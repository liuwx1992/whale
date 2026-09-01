# speed 配置缓存更新流程

> **文档版本**: v1.0
> **创建日期**: 2026-08-28
> **编写人**: weixin.liu
> **用途**: 记录 al-service 定制化产品配置从 speed 推送到缓存生效的完整链路，供后续大模型阅读

---

## 1. 涉及组件

| 组件 | 说明 |
|---|---|
| sigma | 配置管理界面（web），产品在此配置，推送变更到 speed |
| speed | 配置中心，存储配置文件，监听变更并回调 al-service |
| al-service | 业务服务，通过 @SpeedFile 注解订阅 speed 配置文件变更 |

speed 配置文件名：`al_customized_product.properties`
speed topic：`model_dependence`

---

## 2. 完整链路总览

```
sigma 推送配置
  -> speed 写入 al_customized_product.properties (topic=model_dependence)
  -> speed 客户端监听到文件变更
  -> 回调 UpdatePipelineCallback.reloadSpeedFile(event, key, filePath, appContext)
  -> key = "al_customized_product.properties"
  -> InjectUtils.setGeneralConfig(configBean, new File(filePath))  // 文件内容灌入 data 字段
  -> ConfigService.refresh(configBean)  // push ConfigureTask 到 BlockingDeque，立即返回
  -> 后台单线程 configSyncThread take 出 ConfigureTask 执行 run()
  -> run() 内：new CacheDb() -> cacheExchange 填充 -> refreshCache 原子引用切换
  -> 全局 cInfo 的 alCustProductConfig 引用切换到新 map
```

---

## 3. 分步详解

### 3.1 speed 监听与回调

`AlCustomizedProductConfig` 类标注：

```java
@SpeedFile(filename = "al_customized_product.properties", topic = "model_dependence")
public class AlCustomizedProductConfig extends AbstractDataConfig { ... }
```

speed 客户端监听到该文件变更后，回调 `UpdatePipelineCallback.reloadSpeedFile`。

该方法按 key 分发，`al_customized_product.properties` 分支：

```java
case "al_customized_product.properties": {
    AlCustomizedProductConfig config = applicationContext.getBean(AlCustomizedProductConfig.class);
    InjectUtils.setGeneralConfig(config, new File(filePath));  // 文件内容 -> data 字段
    configureService.refresh(config);  // 入队，立即返回
    break;
}
```

关键点：`InjectUtils.setGeneralConfig` 把文件内容读入 configBean 的 `data` 字段（String）。此时不触发缓存刷新，只更新数据源。

### 3.2 入队

`ConfigService.refresh` 只做一件事：把 `ConfigureTask` push 进 `BlockingDeque<Runnable>`，立即返回，不阻塞 speed 回调线程。

```java
public void refresh(AbstractDataConfig abstractDataConfig) {
    configureTasks.push(new ConfigureTask(abstractDataConfig));
}
```

### 3.3 后台单线程执行

`ConfigService.initMethod`（@PostConstruct）启动单线程 `configSyncThread`，循环 `configureTasks.take()` 取出任务执行。

```java
@PostConstruct
public void initMethod() {
    Runnable runnable = () -> {
        while (true) {
            Runnable event = configureTasks.take();  // 阻塞等待
            event.run();
        }
    };
    Thread thread = new Thread(runnable, "configSyncThread");
    thread.start();
}
```

单线程串行执行保证配置刷新不会并发。

### 3.4 ConfigureTask.run() 核心刷新逻辑

```java
public void run() {
    String data = abstractDataConfig.getData();        // 配置内容字符串
    JsonNode dataNode = mapper.readTree(data);
    ArrayNode rows = ...                                // 解析为 JSON 数组

    CacheDataInterface cacheDB = new CacheDb();         // 新建临时空 CacheDb
    abstractDataConfig.cacheExchange(cacheDB, rows);    // 在临时对象上逐行装配
    CacheFactory.getInstance().refreshCache(cacheDB, abstractDataConfig);  // 整体替换
}
```

三步：
1. `new CacheDb()` 创建一个**临时**的空缓存对象（不是全局的那个）
2. `cacheExchange` 在这个临时对象上逐行解析、校验、装配
3. `refreshCache` 做整体替换

### 3.5 cacheExchange 逐行解析校验装配

`AlCustomizedProductConfig.cacheExchange(cacheDB, rows)` 遍历每一行 JSON：

1. 逐字段读取 + 校验（code/name/status/data_v/productShortName/version/inputParamType/retainDay/groupType/orgNumCountType/filter/distinct/timeSlices/orgGroups/fields）
2. 校验不过的行 `continue` 跳过，记 warn 日志
3. 全部通过后装配成 `AlCustProductConfigBean`
4. 派生 subject（从 data_v 提取，如 H:ApplyLoan -> ApplyLoan）
5. 计算 cacheKey（out_v 为空时 key=code，否则 key=code_out_v）
6. `productConfigMap.put(cacheKey, bean)` 写入临时 cacheDB 的 map

整个过程在临时 cacheDB 上操作，外界完全不可见。

### 3.6 refreshCache 原子引用切换

`CacheFactory.refreshCache` 对 `AlCustomizedProductConfig` 实例：

```java
} else if (abstractDataConfig instanceof AlCustomizedProductConfig) {
    cInfo.setAlCustProductConfig(cacheDB.getAlCustProductConfig());
}
```

`cInfo` 是 `CacheFactory` 持有的全局单例 `CacheDataInterface`（CacheDb 实例，在 init 时注入，全局唯一）。

`CacheDb.setAlCustProductConfig`：

```java
public void setAlCustProductConfig(Map<String, AlCustProductConfigBean> map) {
    this.alCustProductConfig = map;  // 引用赋值，原子操作
}
```

将全局 `cInfo` 的 `alCustProductConfig` 字段指向临时 cacheDB 构建好的新 map。

---

## 4. 安全性分析

### 4.1 不存在"清空-重建"窗口

临时 cacheDB 和全局 cInfo 是**两个不同对象**。临时 cacheDB 从头到尾构建完整 map，全局 cInfo 不经历清空。读侧拿到的要么是旧 map 要么是新 map，不会拿到空 map 或半截 map。

引用赋值对引用类型是原子的（JVM 规范），切换瞬间读侧看到旧或新，不会看到中间态。

### 4.2 残留风险（P2，理论级别）

`setAlCustProductConfig` 直接赋引用，没有包成不可变集合。如果读侧拿到 map 引用后做 put/remove，会脏。正常读侧只读不改，属误用风险，非正常流程触发。

### 4.3 status 字段当前行为

cacheExchange 校验 status 合法值（0 或 1），status=0 的产品**仍会被加载**到 map（0 是合法值，不 skip）。

路由层 `ConfigurableCustProdCommand` 不检查 status，status=0 的产品仍可被调用。即当前 status 字段**没有运行时下线效果**，仅作为数据字段存在。

---

## 5. 涉及类清单

| 类 | 路径 | 职责 |
|---|---|---|
| AlCustomizedProductConfig | config/ | @SpeedFile 订阅 + cacheExchange 解析校验装配 |
| AbstractDataConfig | bean/ | 抽象基类，定义 cacheExchange 抽象方法 + data 字段 |
| UpdatePipelineCallback | config/callbacks/ | speed 回调入口，按 key 分发到对应 Config |
| ConfigService | service/ | 队列管理 + ConfigureTask + 后台执行线程 |
| CacheFactory | cache/ | 单例，持有全局 cInfo，refreshCache 做引用切换 |
| CacheDb | cache/ | 缓存实现，ConcurrentHashMap 存储，set 方法做引用赋值 |
| CacheDataInterface | cache/ | 缓存接口，定义 get/set 契约 |
| ConfigurableCustProdCommand | processor/impl/ | 路由层，按 mealName 从缓存取配置执行 |
| InjectUtils | util/ | setGeneralConfig 读文件灌入 data 字段 |

---

## 6. 读侧如何使用缓存

`ConfigurableCustProdCommand` 路由逻辑：

```java
String mealName = pm.getKey();                    // 产品 code（调用方传入）
String processorName = mealName + "_" + outVersion; // 拼出缓存 key
Map<String, AlCustProductConfigBean> config = CacheFactory.getInstance().getAlCustProductConfig();
AlCustProductConfigBean bean = config.get(processorName);  // 按缓存 key 查找
```

每次请求实时从全局 cInfo 取当前 map 引用，按 processorName 查找。刷新期间读到的可能是旧 map 或新 map，取决于请求发生在引用切换前还是后。
