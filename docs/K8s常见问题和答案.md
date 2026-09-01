# Kubernetes 常见问题和答案

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **用途**: 系统梳理 K8s 高频面试题与标准答案，每题给"要点速答 + 深入展开"，配合 [K8s整体架构和深入理解.md](K8s整体架构和深入理解.md) 阅读

---

## 目录

- [一、基础与架构](#一基础与架构)
- [二、Pod 与工作负载](#二pod-与工作负载)
- [三、Service 与网络](#三service-与网络)
- [四、调度](#四调度)
- [五、存储](#五存储)
- [六、安全与配置](#六安全与配置)
- [七、扩展与生态](#七扩展与生态)
- [八、高可用与运维](#八高可用与运维)
- [九、对比与进阶](#九对比与进阶)

---

## 一、基础与架构

### Q1. K8s 是什么？核心理念？

**要点速答**：声明式、可自愈、可扩展的容器编排系统。核心理念：**声明式**（声明期望状态，系统调谐达成）+ **自愈**（实际偏离期望自动修复）+ **可扩展**（CRD/Operator）。

---

### Q2. K8s 控制面和数据面分别有哪些组件？

**要点速答**：
- **控制面（Master）**：kube-apiserver（唯一入口）、etcd（状态存储）、kube-scheduler（调度）、controller-manager（调谐控制器群）、cloud-controller-manager（云集成）。
- **数据面（Node）**：kubelet（Pod 生命周期）、kube-proxy（Service 网络）、CRI（容器运行时）、CNI（网络）、CSI（存储）。

---

### Q3. kube-apiserver 为什么是唯一入口？

**要点速答**：所有组件（scheduler/controller/kubelet/kube-proxy）和用户都经 apiserver 读写集群状态，apiserver 再读写 etcd。**不直连 etcd**，便于统一认证/鉴权/准入/审计/缓存。etcd 不直接对外暴露。

---

### Q4. etcd 的作用？为什么选 etcd？

**要点速答**：etcd 是强一致（Raft）的 KV 存储，存集群全部状态（期望 spec + 实际状态）。选它因为强一致、watch 机制（变更通知，驱动调谐）、高可用多副本、轻量。K8s 依赖 etcd 的 watch 让控制器实时感知变更。

---

### Q5. 声明式和命令式区别？

**要点速答**：命令式告诉系统"做什么"（kubectl run）。声明式告诉系统"想要什么状态"（YAML replicas=3）。声明式幂等、可 diff、可自愈——K8s 控制器持续调谐让实际=期望。`kubectl apply` 声明式。

---

### Q6. 什么是调谐（Reconcile）？

**要点速答**：控制器持续循环：读期望状态（spec）→ 对比实际状态（运行的 Pod）→ 不一致就操作使其一致。Pod 挂了副本数变少，副本控制器自动重建——自愈的本质。详见架构文档 §3.3。

---

## 二、Pod 与工作负载

### Q7. 什么是 Pod？为什么是最小调度单元？

**要点速答**：Pod 是 K8s 最小可调度单元，含 1~N 容器，共享 network namespace（同 IP 同端口）和存储卷，容器间 localhost 通信。不直接调度容器是因为一组紧密协作的容器（主+sidecar）需共享资源、同生命周期，Pod 把它们打包。

---

### Q8. Pod 内容器怎么通信？Pod 间呢？

**要点速答**：
- **Pod 内**：localhost（共享 netns）。
- **Pod 间**：每 Pod 独立 IP，扁平网络无 NAT，可直接路由（CNI 实现）。

---

### Q9. Deployment、StatefulSet、DaemonSet、Job 区别？

**要点速答**：

| 控制器 | 用途 | 特点 |
|---|---|---|
| Deployment | 无状态应用 | 滚动/回滚，管理 ReplicaSet |
| StatefulSet | 有状态应用 | 稳定网络标识+PVC+顺序 |
| DaemonSet | 每节点一个 | 日志/监控/网络插件 |
| Job/CronJob | 批处理/定时 | 跑完退出 |

---

### Q10. StatefulSet 为什么适合有状态服务？

**要点速答**：① 稳定网络标识 `pod-0/1/2`，DNS 稳定；② 顺序启停（0→1→2 启，反序停）；③ 每 Pod 绑独立 PVC，重建挂回同卷数据不丢。适合 MySQL/Redis/ES/ZK 这类需稳定身份与持久数据的服务。

---

### Q11. Deployment 滚动发布原理？

**要点速答**：更新镜像 → 新建新 ReplicaSet 逐步增副本（maxSurge），旧 ReplicaSet 逐步减副本（maxUnavailable），新老交替直到全切新。零停机。回滚 = 切回旧 ReplicaSet（保留历史版本数 `revisionHistoryLimit`）。

---

### Q12. HPA 怎么工作？

**要点速答**：水平 Pod 自动扩缩容（Horizontal Pod Autoscaler）。按 CPU/内存/自定义指标（Prometheus）阈值，周期调 Deployment replicas。指标高扩，低缩。需 metrics-server/自定义指标适配器。

---

### Q13. 健康检查 liveness/readiness/startup 区别？

**要点速答**：
- **livenessProbe**：存活探针，失败重启容器。
- **readinessProbe**：就绪探针，失败从 Service Endpoints 摘除（不接流量）但不重启。
- **startupProbe**：启动探针，启动慢应用期间禁用其他探针，成功后才生效。

---

## 三、Service 与网络

### Q14. Service 是什么？为什么需要？

**要点速答**：Service 是稳定 ClusterIP + DNS 名 + 负载均衡，屏蔽 Pod IP 易变。Pod 重建 IP 变，靠 Service 稳定寻址。kube-proxy watch Service/Endpoint 在每节点写 iptables/ipvs 规则做 LB。

---

### Q15. Service 四种类型？

**要点速答**：
- **ClusterIP**（默认）：集群内虚拟 IP + DNS。
- **NodePort**：每节点开端口，集群外访问。
- **LoadBalancer**：云厂商 LB，公网入口。
- **ExternalName/Headless**：DNS 别名 / 无 IP 直连 Pod（StatefulSet 常用 Headless）。

---

### Q16. kube-proxy 的 iptables 和 ipvs 模式区别？

**要点速答**：
- **iptables**：靠规则链 DNAT，规则多时线性匹配性能下降。
- **ipvs**：基于内核哈希，支持更多 LB 算法、大量 Service 性能更好。生产推荐 ipvs 模式（`--proxy-mode=ipvs`）。

---

### Q17. Service 和 Ingress 区别？

**要点速答**：Service 是 L4（TCP/UDP）稳定 IP+LB。Ingress 是 L7（HTTP/HTTPS），基于域名/路径路由到不同 Service，由 Ingress Controller（nginx-ingress/traefik）实现。一条 LB 暴露多服务靠 Ingress + 域名/路径路由。

---

### Q18. K8s 网络模型要求？

**要点速答**：① Pod 内容器 localhost 共享；② Pod 间无 NAT 直接路由（每 Pod 独立 IP，扁平网络）；③ 节点与 Pod 间无 NAT（节点能直连 Pod）；④ Service 提供虚拟 IP LB。CNI（Calico/Flannel/Cilium）实现 Pod 间组网。

---

### Q19. CNI 实现选型？

**要点速答**：
- **Flannel**：简单 Overlay/VXLAN，适合入门。
- **Calico**：BGP 路由性能好，支持 NetworkPolicy。
- **Cilium**：eBPF 高性能+可观测+策略，新项目首选。
- Overlay 封装有开销，Underlay/BGP 性能好但要求底层网络支持。

---

## 四、调度

### Q20. kube-scheduler 调度过程？

**要点速答**：新 Pod Pending → ① **预选（Filter）**排除不满足 Node（资源/污点/亲和/端口）；② **优选（Score）**对剩余打分（资源均衡/亲和）；③ **绑定（Bind）**选最高分 Node 写 nodeName。详见架构文档 §6。

---

### Q21. 调度按 requests 还是 limits？

**要点速答**：按 **requests**（调度依据，保证节点不超分）。limits 是上限（cgroup 限制，超则限流/OOM）。没设 requests 默认用 limits 值。requests 是"保底"，limits 是"上限"。

---

### Q22. 亲和性 Affinity 和 污点 Taint/Toleration 区别？

**要点速答**：
- **Affinity（节点亲和/反亲和）**：从 Pod 视角"我想去/不去哪些 Node"。
- **Taint/Toleration**：从 Node 视角"我排斥 Pod，除非它容忍我"。常用于专用节点（GPU/专用节点打污点，只有容忍的 Pod 能上）。
- **Pod 反亲和**：把副本分散到不同 Node/AZ，高可用。

---

### Q23. 优先级与抢占？

**要点速答**：高优先级 Pod 在资源不足时，**抢占**低优先级 Pod（驱逐部分低优 Pod 腾资源）让自己调度。priorityClass 定义优先级。用于关键服务优先保障。

---

## 五、存储

### Q24. PV/PVC/StorageClass 关系？

**要点速答**：
- **PV**：集群级存储资源（管理员建或动态生成）。
- **PVC**：用户对存储的申请（大小/访问模式）。
- **StorageClass**：动态供给模板，PVC 引用 SC 自动按需创建 PV（云盘/NFS）。
PVC 绑定 PV，Pod 挂载 PVC。

---

### Q25. 静态供给和动态供给？

**要点速答**：
- **静态供给**：管理员预先建好 PV，PVC 匹配绑定。
- **动态供给**：PVC 引用 StorageClass，CSI 驱动按需自动创建 PV。生产主流，按需即用。

---

### Q26. emptyDir、hostPath、PV 区别？

**要点速答**：
- **emptyDir**：Pod 内临时共享卷，Pod 删则没。
- **hostPath**：挂 Node 目录，耦合节点慎用。
- **PV/PVC**：持久存储，Pod 重建数据在。StatefulSet 用 PV 保证数据持久。

---

## 六、安全与配置

### Q27. RBAC 怎么工作？

**要点速答**：Role/ClusterRole 定义权限（可对哪些资源做哪些动作），RoleBinding/ClusterRoleBinding 把 Role 绑到 Subject（User/Group/ServiceAccount）。Pod 用 ServiceAccount 身份访问 apiserver。最小权限原则。

---

### Q28. apiserver 三道关卡？

**要点速答**：① **认证**（Authentication，你是谁，证书/token/oidc）；② **鉴权**（Authorization，RBAC，你能不能做）；③ **准入**（Admission，能不能这么改，配额/默认值/PodSecurity/Webhook 注入如 Istio sidecar）。

---

### Q29. ConfigMap 和 Secret 区别？

**要点速答**：ConfigMap 存非敏感配置，Secret 存敏感信息（base64 编码，非真加密）。都可做卷挂载或 env 注入。Secret 生产应配合 etcd 加密（KMS）、RBAC 严格管控。卷挂载方式 ConfigMap 可热更新，env 注入不自动更新。

---

### Q30. ServiceAccount 是什么？

**要点速答**：Pod 在集群内的身份。每个 Pod 自动挂载其 ServiceAccount 的 token，用它访问 apiserver。配合 RBAC 控制该 SA 能操作什么。给应用最小权限的 SA 而非默认。

---

## 七、扩展与生态

### Q31. CRD 和 Operator 是什么？

**要点速答**：
- **CRD**：自定义资源定义，扩展 K8s API（如定义 `MySQL` 资源类型）。
- **Operator**：配合 CRD 的自定义控制器，把某服务（如 MySQL/etcd）的运维知识（备份/扩容/故障恢复/版本升级）编码成自动化逻辑。让 K8s 管理复杂有状态服务。

---

### Q32. Helm 是什么？

**要点速答**：K8s 包管理工具。Chart 是一组资源模板（YAML + 模板变量），helm install 一键部署多资源、版本管理、回滚。相当于 K8s 的"包管理器"，简化复杂应用部署。

---

### Q33. Admission Webhook 作用？

**要点速答**：准入阶段注入自定义逻辑。Mutating（可改对象，如 Istio 自动注入 sidecar、加默认标签）、Validating（校验，如配额/安全策略）。无需改 K8s 源码扩展准入控制。

---

### Q34. 服务网格 Istio 做什么？

**要点速答**：Sidecar 模式（Envoy）接管 Pod 流量，提供 mTLS、流量治理（熔断/重试/灰度）、可观测、策略。K8s Service 只管 L4 LB，Istio 管细粒度 L7 流量治理。代价是 sidecar 开销与复杂度。

---

## 八、高可用与运维

### Q35. 控制面怎么做高可用？

**要点速答**：多 Master 节点，etcd Raft 多副本强一致，apiserver 多实例前置 LB，scheduler 和 controller-manager **leader 选举**（同一时刻只有一个工作避免双写）。客户端/etcd 任一挂不影响（多数 etcd 存活即可用）。

---

### Q36. 应用高可用怎么做？

**要点速答**：① 多副本 Deployment；② **Pod 反亲和**分散到不同 Node/AZ（单节点/可用区故障不影响）；③ liveness/readiness 健康检查；④ PDB 保自愿驱逐时最少可用副本；⑤ 跨可用区拓扑分布（topologySpreadConstraints）。

---

### Q37. PDB 是什么？

**要点速答**：PodDisruptionBudget，自愿驱逐（滚动更新/节点维护 drain）时保证最少可用副本数，防止全部 Pod 被同时驱逐导致服务不可用。非自愿驱逐（节点宕机）不受 PDB 限制。

---

### Q38. Pod 持续重启或 Pending 怎么排查？

**要点速答**：
- `kubectl describe pod` 看 Events（调度失败/OOM/镜像拉取失败）。
- CrashLoopBackOff：看 `kubectl logs`，常见是应用启动失败/探针配错/OOMKilled（`kubectl describe` 看 Last State）。
- Pending：scheduler 找不到合适 Node（资源不足/污点/亲和），看 Events 的 FailedScheduling。
- `kubectl get events --sort-by=.lastTimestamp`。

---

### Q39. 怎么保证滚动发布零停机？

**要点速答**：① readinessProbe 配好（就绪才接流量）；② maxUnavailable=0 + 合理 maxSurge；③ 有状态会话用外部存储或平滑；④ 资源 requests 充足；⑤ 优雅终止（preStop hook + terminationGracePeriodSeconds，等连接排空）。

---

### Q40. 镜像和发布最佳实践？

**要点速答**：小镜像（distroless/alpine，多阶段构建）、**固定 tag 别用 latest**（不可变、可回滚）、私有仓库、拉取策略 IfNotPresent/Always 按需、CI 构建推送 + GitOps（ArgoCD）部署。

---

## 九、对比与进阶

### Q41. 容器 vs 虚拟机？

**要点速答**：容器共享宿主内核，轻量、秒启、密度高；虚拟机独立内核，隔离强但重。容器适合微服务高密度部署，虚拟机适合强隔离/异构内核。K8s 编排容器，也可管 VM（KubeVirt）。

---

### Q42. K8s vs Docker Swarm？

**要点速答**：Docker Swarm 简单、易上手但功能弱（网络/存储/扩展有限）。K8s 功能强大、生态丰富（CRD/Operator/Helm/服务网格）、大规模生产首选，但学习曲线陡。新项目选 K8s。

---

### Q43. 为什么有状态服务用 Operator 而非裸 StatefulSet？

**要点速答**：StatefulSet 只给稳定身份+存储，不懂数据库的运维（备份/主从切换/扩容/故障恢复/版本升级兼容）。Operator 把这些**领域运维知识**编码进控制器，自动化管理有状态服务的全生命周期，比裸 StatefulSet 省心可靠。

---

### Q44. etcd 挂了集群还能用吗？

**要点速答**：etcd 多数派 Raft，少数挂仍可用。多数挂则**只能读不能写**（新变更无法持久化），apiserver 写操作失败，但已运行的 Pod/Service 在 Node 上仍工作（kubelet 有缓存），属于降级。修 etcd 恢复多数派即恢复。

---

### Q45. Pod 之间通信全链路？

**要点速答**：Pod A 访问 Service → DNS 解析 Service 名到 ClusterIP → kube-proxy 的 iptables/ipvs 规则 DNAT 到后端 Pod B IP → CNI 网络（Calico/Cilium）路由到 Pod B 所在 Node → Node 上 veth pair 送到 Pod B 容器。Pod 间扁平无 NAT。

---

## 附：一句话速答卡

| 问 | 答 |
|---|---|
| 核心理念 | 声明式+自愈+可扩展 |
| 控制面 | apiserver/etcd/scheduler/controller-mgr |
| 数据面 | kubelet/kube-proxy/CRI/CNI/CSI |
| 调谐 | watch spec→对比实际→使其一致 |
| Pod | 最小调度单元,共享netns |
| Service | 稳定IP+DNS+LB(iptables/ipvs) |
| Deployment | 无状态滚动 |
| StatefulSet | 有状态稳定身份+PVC |
| 调度 | 预选+优选,按requests |
| Service vs Ingress | L4 vs L7 |
| 存储 | PVC→SC→CSI→PV |
| RBAC | Role+Binding绑定SA |
| Operator | CRD+控制器,管有状态 |
| HA | 多Master+leader选举+Pod反亲和 |

---

> 配套架构文档见 [K8s整体架构和深入理解.md](K8s整体架构和深入理解.md)。
