# Kubernetes 整体架构和深入理解

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **用途**: 从整体架构到控制面/数据面/调度/网络/存储/控制器/声明式机制全面拆解 Kubernetes，供后续大模型阅读与排障

---

## 0. 一句话定位

Kubernetes（K8s）是一个**声明式、可自愈、可扩展的容器编排系统**。核心思想：你声明"期望状态"（多少副本、什么镜像），K8s 持续**调谐（reconcile）**让实际状态逼近期望状态。它是 Borg 的开源继承者。

---

## 1. 适用场景

| 维度 | 说明 |
|---|---|
| 适用 | 微服务容器编排、自动扩缩容、滚动/灰度发布、故障自愈、混合云、批处理任务 |
| 不适用 | 单体小应用（过重）、强状态有状态服务的复杂管理（需 Operator）、低延迟裸机场景 |
| 价值 | 声明式、自愈、滚动发布、资源调度、生态（CRD/Operator/Helm） |

---

## 2. 整体架构总览

```
┌──────────────────────────── Control Plane(控制面/Master) ────────────────────────────┐
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ kube-apiserver│ │ etcd   │  │kube-scheduler│ │controller-mgr│ │ cloud-mgr  │             │
│  │ (唯一入口, REST)│ │(状态存储)│ │ (Pod 调度)  │ │(调谐控制器群) │ │(云厂商集成) │             │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘              │
│                所有组件经 apiserver 读写 etcd, 不直连 etcd                              │
└───────────────────────────────┬──────────────────────────────────────────────────────┘
                                 │ watch/list
        ┌────────────────────────┼────────────────────────────────┐
        ▼                         ▼                                  ▼
┌────────────── Node(数据面) ──────────┐    ┌────────────── Node ──────────┐
│  ┌──────────────┐  ┌──────────────┐ │    │  kubelet  / kube-proxy / CRI │
│  │   kubelet    │  │  kube-proxy  │ │    │  / CNI / CSI / Pod×N          │
│  │(管 Pod 生命周期)│ │(网络/Service)│ │    └────────────────────────────┘
│  └──────────────┘  └──────────────┘ │
│  ┌──────────────┐  ┌──────────────┐ │
│  │ CRI(容器运行时)│ │ CNI(网络)     │ │    Pod: 最小调度单元, 含1~N容器
│  │  containerd   │ │  Calico/Flannel│ │    共享 network/存储 namespace
│  └──────────────┘  └──────────────┘ │
│  ┌──────────────┐                  │
│  │ CSI(存储)     │  Pod×N          │
│  └──────────────┘                  │
└────────────────────────────────────┘
```

控制面（Master）组件：

| 组件 | 职责 |
|---|---|
| **kube-apiserver** | 唯一入口，REST API，认证/鉴权/准入/etcd 读写。所有组件都经它 |
| **etcd** | 强一致（Raft）KV 存储，存集群全部状态（期望+实际）。唯一持久存储 |
| **kube-scheduler** | 调度：为新 Pod 选合适 Node（资源/亲和/污点/优先级） |
| **controller-manager** | 运行一组**控制器**，做调谐（副本/节点/端点/任务等） |
| **cloud-controller-manager** | 云厂商集成（LB/路由/存储），分离云特有逻辑 |

数据面（Node）组件：

| 组件 | 职责 |
|---|---|
| **kubelet** | Pod 生命周期管理者：按 PodSpec 调 CRI 起容器、健康检查、状态上报 |
| **kube-proxy** | Service 网络代理：每节点维护 iptables/ipvs 规则做负载均衡 |
| **CRI** | 容器运行时接口（containerd/CRI-O），跑容器 |
| **CNI** | 容器网络接口（Calico/Flannel/Cilium），Pod 间组网 |
| **CSI** | 容器存储接口，挂载 PV |

---

## 3. 核心概念（必须先理清）

### 3.1 Pod：最小调度单元

- Pod 是 K8s **最小可调度/部署单元**，内含 1~N 容器，**共享 network namespace（同 IP 同端口空间）和存储卷**。
- 容器间通过 localhost 通信。
- Pod 是短暂的（会重建），IP 会变，所以不直连 Pod IP，靠 **Service** 稳定寻址。

### 3.2 声明式 vs 命令式

- 命令式：告诉系统"做什么"（kubectl run、run this）。
- **声明式**：告诉系统"想要什么状态"（YAML：replicas=3, image=x）。K8s 自己调谐到该状态。
- `kubectl apply -f` 幂等声明，`kubectl create` 命令式。

### 3.3 期望状态与调谐（Reconcile Loop）

控制器持续循环：**读取期望状态（etcd 里的 spec）→ 对比实际状态（运行的 Pod）→ 不一致就操作使其一致**。这是自愈的根：Pod 挂了，副本控制器发现少了，自动重建一个。

---

## 4. 控制器与工作负载

| 控制器 | 用途 |
|---|---|
| **Deployment** | 无状态应用，管理 ReplicaSet，滚动/回滚发布 |
| **ReplicaSet** | 维持副本数（被 Deployment 管） |
| **StatefulSet** | 有状态应用，稳定网络标识+持久卷+顺序，适合 DB/MQ |
| **DaemonSet** | 每节点跑一个（日志/监控/网络插件） |
| **Job/CronJob** | 批处理/定时任务，跑完退出 |
| **HPA** | 水平自动扩缩容（按 CPU/自定义指标） |

### 4.1 Deployment 滚动发布

更新镜像时，Deployment 创建**新 ReplicaSet** 逐步增副本，旧 ReplicaSet 逐步减副本，按 `maxSurge/maxUnavailable` 控制节奏，实现**零停机滚动**。回滚 = 切回旧 ReplicaSet。

### 4.2 StatefulSet 为何适合有状态

- 稳定网络标识：`pod-name-0/1/2...`，DNS 解析稳定。
- 顺序启停：0→1→2 启，反序停。
- 稳定持久卷：每 Pod 绑定独立 PVC，重建后挂回同卷，数据不丢。
- 适合 MySQL/Redis/ES/ZK 这类需稳定身份与数据的服务。

---

## 5. Service：稳定寻址与负载均衡

Pod IP 易变，Service 提供稳定**ClusterIP + DNS 名** + 负载均衡。

| 类型 | 说明 |
|---|---|
| **ClusterIP**（默认） | 集群内虚拟 IP + DNS，集群内访问 |
| **NodePort** | 每节点开端口，集群外可访问 |
| **LoadBalancer** | 云厂商 LB，公网入口 |
| **ExternalName/Headless** | DNS 别名/无 IP 直连 Pod（StatefulSet 常用） |

### 5.1 kube-proxy 与 iptables/ipvs

- kube-proxy **watch** Service+Endpoint 变化，在每节点写 **iptables/ipvs 规则**。
- 访问 ClusterIP 时，iptables/ipvs 规则把流量 DNAT 到某个后端 Pod IP。
- **ipvs** 支持更多 LB 算法、性能更好（大量 Service 时），生产推荐。

### 5.2 Endpoint / EndpointSlice

Service 的后端 Pod 列表。kube-proxy 据此写规则。EndpointSlice 是分片版本，大集群减少更新开销。

### 5.3 Service vs Ingress

- Service 是 L4（TCP/UDP），稳定 IP+LB。
- **Ingress** 是 L7（HTTP/HTTPS），基于域名/路径路由到不同 Service，由 Ingress Controller（nginx-ingress/traefik）实现。一条 LB 暴露多服务靠 Ingress。

---

## 6. 调度（kube-scheduler）

新 Pod 创建后处于 Pending，scheduler 选 Node：

1. **预选（Filter）**：排除不满足的 Node（资源不足、污点不容忍、亲和不匹配、端口冲突）。
2. **优选（Score）**：对剩余 Node 打分（资源均衡、亲和优先、反亲和分散）。
3. **绑定（Bind**：选最高分 Node，写 PodSpec.nodeName。

关键机制：
- **资源 requests/limits**：调度按 requests 算（不是 limits）。
- **节点亲和/反亲和**：硬（required）/软（preferred）。
- **污点与容忍（Taint/Toleration）**：污点让 Pod 不调度到某 Node（如专用节点），容忍才能上。
- **优先级与抢占**：高优 Pod 抢低优 Pod 资源。

---

## 7. 存储

### 7.1 卷体系

```
PersistentVolume(PV)        集群级存储资源(管理员/动态供给)
PersistentVolumeClaim(PVC)  用户对存储的申请
StorageClass(SC)            动态供给模板(按 PVC 自动建 PV)
```

- **静态供给**：管理员预先建 PV。
- **动态供给**：PVC 引用 StorageClass，自动按需创建 PV（云盘/NFS/Ceph）。

### 7.2 卷类型

- **emptyDir**：Pod 内临时共享卷，Pod 删则没。
- **hostPath**：挂载 Node 目录（慎用，耦合节点）。
- **PV/PVC**：持久存储，Pod 重建后数据在。
- **configMap/secret**：配置/敏感数据以卷形式挂载。

### 7.3 CSI

容器存储接口，存储驱动标准化，PV 由 CSI 驱动 attach/mount 到 Pod。

---

## 8. 配置与敏感信息

- **ConfigMap**：非敏感配置，可做卷挂载或环境变量注入。热更新（卷挂载方式会自动更新，env 注入不自动）。
- **Secret**：敏感信息（base64 编码，非加密），同 ConfigMap 用法。生产配合 RBAC + 加密 etcd（KMS）。

---

## 9. 网络模型

K8s 网络四模型：
1. **Pod 内容器**：localhost 通信（共享 netns）。
2. **Pod 间**：每 Pod 独立 IP，Pod 间可直接路由（CNI 实现，扁平网络，无 NAT）。
3. **Pod 与 Service**：ClusterIP（虚拟，iptables/ipvs DNAT）。
4. **外部访问**：NodePort/LB/Ingress。

CNI 实现：
- **Flannel**：简单 Overlay/VXLAN。
- **Calico**：BGP 路由，性能好，支持网络策略。
- **Cilium**：eBPF，高性能+可观测+网络策略。

### 9.1 网络策略 NetworkPolicy

L3/L4 级别的 Pod 间访问控制（哪些 Pod 能访问哪些）。需 CNI 支持（Calico/Cilium）。

### 9.2 CNI 网络模式

- **Overlay（VXLAN/IPinIP）**：跨子网封装，简单但封装开销。
- **BGP/Underlay**：节点路由直通，性能好但要求底层网络支持。
- **eBPF**：内核态，无 iptables 开销，最优性能。

---

## 10. 服务发现与 DNS

- **CoreDNS**：集群 DNS，自动为 Service 生成 `svc.ns.svc.cluster.local` 记录。
- Pod 可通过 Service 名解析到 ClusterIP。
- Headless Service（ClusterIP=None）解析到 Pod IP 列表（StatefulSet 用，`pod.svc.ns` 稳定）。

---

## 11. 安全：RBAC 与 ServiceAccount

- **RBAC**：Role/ClusterRole（权限） + RoleBinding/ClusterRoleBinding（绑定到 Subject）。
- **ServiceAccount**：Pod 的身份，挂载 token，访问 apiserver 用。
- **认证/鉴权/准入**：apiserver 三道关卡——认证（谁）、鉴权（能不能）、准入（能不能这么改，如配额/默认值/安全策略）。

---

## 12. 扩展机制

- **CRD（Custom Resource Definition）**：自定义资源，扩展 K8s API（如定义一个 `MySQL` 资源）。
- **Controller/Operator**：配合 CRD 的控制器，编码运维知识（备份/扩容/故障恢复），把有状态服务管理自动化。如 Prometheus Operator、Etcd Operator。
- **Admission Webhook**：准入阶段注入逻辑（如 Istio sidecar 注入、配额校验）。
- **Scheduler Framework**：自定义调度插件。
- **Helm**：应用打包/模板化部署（Chart）。

---

## 13. 生态与运维

- **Helm**：包管理，Chart 模板化部署多资源。
- **ArgoCD/Flux**：GitOps 持续部署。
- **Prometheus + Grafana**：监控。
- **EFK/loki**：日志。
- **Istio/Linkerd**：服务网格，sidecar 治理流量（mTLS/熔断/可观测）。

---

## 14. 高可用与故障域

- **控制面高可用**：多 Master，etcd Raft 多副本，apiserver 前置 LB，scheduler/controller 选举 leader（同一时刻只有一个工作，避免双写）。
- **数据面**：Pod 多副本 + 反亲和分散到不同 Node/可用区，单节点/可用区故障不影响。
- **PDB（PodDisruptionBudget）**：自愿驱逐（滚动/维护）时保最少可用副本数。
- **健康检查**：livenessProbe（存活，失败重启容器）、readinessProbe（就绪，失败从 Service 摘除）、startupProbe（启动慢应用）。

---

## 15. 性能与运维要点

| 关注点 | 做法 |
|---|---|
| 资源 requests/limits | 必设 requests（调度依据），limits 防超用 |
| 健康检查 | liveness+readiness 都配 |
| 副本与反亲和 | 多副本散到多 Node/AZ |
| 镜像 | 小镜像、固定 tag 别 latest、私有仓库 |
| 配置 | ConfigMap/Secret 而非打镜像 |
| 有状态 | StatefulSet + PVC，别用 Deployment 跑 DB（除非有 Operator） |
| 大集群 | ipvs 模式、EndpointSlice、etcd 性能、控制面多副本 |
| 网络 | 生产用 Calico/Cilium，开 NetworkPolicy |
| 安全 | RBAC 最小权限、Pod Security、etcd 加密、Secret 管控 |
| 监控 | Prometheus 抓 kubelet/apiserver/etcd |
| 升级 | 滚动升级控制面，先测兼容性 |

---

## 16. 一张图串起核心概念

```
声明式 YAML(期望状态) -> apiserver -> etcd
   控制器 watch spec -> 调谐(reconcile)使实际==期望 -> 自愈
   scheduler 预选+优选选 Node -> bind
   kubelet 调 CRI 起 Pod, 上报状态, 健康检查
   kube-proxy watch Service/Endpoint -> iptables/ipvs 规则 -> LB
   Pod(最小单元,共享netns) -> Service(稳定IP+DNS) -> Ingress(L7)
   存储: PVC -> StorageClass -> CSI -> PV
   扩展: CRD + Operator(运维知识自动化)
   网络: CNI(Calico/Cilium) 扁平无NAT Pod间直连
```

记住这条链，就抓住 K8s 全部骨架：**声明式调谐、控制面/数据面、Pod/Service/Deployment、调度、CNI/CSI/CRI、RBAC、CRD/Operator**。
