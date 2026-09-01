package com.zoo.whale.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.zoo.whale.thread.RedisOperatorThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date: 2025年 06月 17日
 * @Time:19时:59分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
public class FlowRuleManagerUtils {

    private static Set<String> LIMIT_FLOW_PRODUCT_SET = new HashSet<>();

    public static final String HX_LIMITER_REDIS_PREFIX = "hx_global_limit:";
    public static final String HX_RATE_LIMITER_POSTFIX = "-rate-limiter:";

    static {
        reloadProductsFlowRuleThreshValue();
    }

    private static final ExecutorService incrRedisKeyExecutorService = new ThreadPoolExecutor(
            10,
            20,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "incrRedisKey-thread-" + threadNumber.getAndIncrement());
                }
            }
    );


    /**
     * 判断产品是否达到限流阈值
     */
    public static boolean isCanPass(String mealNameAndVersion) {
        //1.不在限流产品集合中，快速返回

        //2.快速失败限流判断（不阻塞请求）
        if (SphO.entry(mealNameAndVersion)) {
            try {
                //异步redis计数(偶发的计数失败，可以忽略，不百分百严格要求)
                incrRedisKeyExecutorService.execute(new RedisOperatorThread(mealNameAndVersion));
            } catch (Exception e) {
                //log.warn("异步redis计数，可以忽略");
            } finally {
                SphO.exit();
            }
            //log.warn("未发生限流");
            return true;
        }
        //log.warn("本地限流器限流发生");
        //redis判断全局是否超限
        if (globalQpsPass(mealNameAndVersion, 10.0)) {
            log.warn("全局未超限");
            //全局未超限
            return true;
        }
        //阻塞限流
        // Step 3. 进入 Sentinel 排队式限流
        //log.warn("进入 Sentinel 排队式限流判断");
        long start = System.currentTimeMillis();
        if (SphO.entry(mealNameAndVersion + "-rate-limiter")) {
            try {
                System.out.println("排队耗时："+(System.currentTimeMillis() - start));
                return true;
            } finally {
                SphO.exit();
            }
        } else {
//            log.warn("排队失败限流触发");
            System.out.println("排队失败限流触发排队耗时："+(System.currentTimeMillis() - start));
            //3.资源访问阻止,记录到exporter指标中
            return false;
        }
    }



    /**
     * 加载配置阈值
     */
    public static void reloadProductsFlowRuleThreshValue() {
        synchronized (FlowRuleManagerUtils.class) {
            ArrayList<FlowRule> flowRules = new ArrayList<>();
            Map<String, Double> productFlowRuleMap = new HashMap<>();
            //MOCK
            productFlowRuleMap.put("ApplyLoanNewSub", 2.0);
            for (Map.Entry<String, Double> entry : productFlowRuleMap.entrySet()) {
                String mealNameAndVersion = entry.getKey();
                Double threshValue = entry.getValue();
                //初始化限流对象
                FlowRule flowRule = getFlowRuleBean(mealNameAndVersion, threshValue, "0");
                flowRules.add(flowRule);

                String mealNameAndVersionLimiter = entry.getKey() + "-rate-limiter";
                //初始化限流对象
                FlowRule flowRuleLimiter = getFlowRuleBean(mealNameAndVersionLimiter, threshValue,"2");

                flowRules.add(flowRuleLimiter);
            }
            //重载限流规则
            FlowRuleManager.loadRules(flowRules);
        }
    }

    /**
     * 初始化资源对象
     */
    private static FlowRule getFlowRuleBean(String mealNameAndVersion, Double threshValue, String controlBehavior) {
        FlowRule flowRule = new FlowRule();
        //限流资源名称
        flowRule.setResource(mealNameAndVersion);
        //限流来源APP,默认所有
        flowRule.setLimitApp(RuleConstant.LIMIT_APP_DEFAULT);
        //限流模式,并发或QPS，默认QPS
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //限流阈值
        flowRule.setCount(threshValue);
        //直接、关联、链路，默认直接及限制资源本身，关联指依赖其他资源时，
        // 其他资源的阈值被触发时，本资源被限流,配合alFr.setRefResource()使用
        //链路指被其他资源依赖时，限流规则只针对制定的依赖源生效,配合alFr.setRefResource()使用
        flowRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        if (StringUtils.equals(controlBehavior, "0")) {
            //流控模式，默认快速失败、
            // 预热，配合alFr.setWarmUpPeriodSec()使用
            // 排队,配合alFr.setMaxQueueingTimeMs()使用
            // 预热排队，配合alFr.setWarmUpPeriodSec()、alsFr.setMaxQueueingTimeMs()使用
            flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        } else if (StringUtils.equals(controlBehavior, "2")) {
            //流控模式，默认快速失败、
            // 预热，配合alFr.setWarmUpPeriodSec()使用
            // 排队,配合alFr.setMaxQueueingTimeMs()使用
            // 预热排队，配合alFr.setWarmUpPeriodSec()、alsFr.setMaxQueueingTimeMs()使用
            flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        }
        //请求在队列中等待的最大时间
        flowRule.setMaxQueueingTimeMs(500);
        return flowRule;
    }


    /**
     * 获取全局限流是否触发
     * */
    public static boolean globalQpsPass(String key, double limitPerSecond) {
        try {
            String redisKey = HX_LIMITER_REDIS_PREFIX + key;
            return RedisClient.isAllowed(redisKey, limitPerSecond);
        } catch (Exception e) {
            log.warn("globalQpsPass error", e);
        }
        //redis执行异常，返回true，暂时不做全局限流
        return true;
    }

}
