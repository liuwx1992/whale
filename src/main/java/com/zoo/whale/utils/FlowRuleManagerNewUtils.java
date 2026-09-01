package com.zoo.whale.utils;

import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @Date: 2025年 06月 18日
 * @Time:11时:18分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
public class FlowRuleManagerNewUtils {


    static {
//        reloadProductsFlowRuleThreshValue();
        reloadProductsFlowRuleThreshValueDefault();

    }


    /**
     * 判断产品是否达到限流阈值
     */
    public static boolean isCanPass(String mealNameAndVersion) {
        //1.不在限流产品集合中，快速返回

        //2.限流判断
        long start = System.currentTimeMillis();
        if (SphO.entry(mealNameAndVersion)) {
            try {
                //log.warn("排队成功触发");
                System.out.println("排队耗时："+(System.currentTimeMillis() - start));
                return true;
            } finally {
                SphO.exit();
            }
        } else {
            //log.warn("限流发生");
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
            productFlowRuleMap.put("ApplyLoanNewSub-rate-limiter", 2.0);
            HashSet<String> limitFlowProductSetTmp = new HashSet<>();
            for (Map.Entry<String, Double> entry : productFlowRuleMap.entrySet()) {
                String mealNameAndVersion = entry.getKey();
                Double threshValue = entry.getValue();
                //初始化限流对象
                FlowRule flowRule = getFlowRuleBean(mealNameAndVersion, threshValue);
                flowRules.add(flowRule);
                limitFlowProductSetTmp.add(mealNameAndVersion);
            }
            //重载限流规则
            FlowRuleManager.loadRules(flowRules);
        }
    }

    /**
     * 初始化资源对象
     */
    private static FlowRule getFlowRuleBean(String mealNameAndVersion, Double threshValue) {
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
        //流控模式，默认快速失败、
        // 预热，配合alFr.setWarmUpPeriodSec()使用
        // 排队,配合alFr.setMaxQueueingTimeMs()使用
        // 预热排队，配合alFr.setWarmUpPeriodSec()、alsFr.setMaxQueueingTimeMs()使用
        flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        //请求在队列中等待的最大时间
        flowRule.setMaxQueueingTimeMs(500);
        return flowRule;
    }

    /**
     * 加载配置阈值
     */
    public static void reloadProductsFlowRuleThreshValueDefault() {
        synchronized (FlowRuleManagerUtils.class) {
            ArrayList<FlowRule> flowRules = new ArrayList<>();
            Map<String, Double> productFlowRuleMap = new HashMap<>();
            //MOCK
            productFlowRuleMap.put("ApplyLoanNewSub", 2.0);
            for (Map.Entry<String, Double> entry : productFlowRuleMap.entrySet()) {
                String mealNameAndVersion = entry.getKey();
                Double threshValue = entry.getValue();
                //初始化限流对象
                FlowRule flowRule = getFlowRuleBeanDefault(mealNameAndVersion, threshValue);
                flowRules.add(flowRule);
            }

            for (Map.Entry<String, Double> entry : productFlowRuleMap.entrySet()) {
                String mealNameAndVersion = entry.getKey() + "-rate-limiter";
                Double threshValue = entry.getValue();
                //初始化限流对象
                FlowRule flowRule = getFlowRuleBean(mealNameAndVersion, threshValue);
                flowRules.add(flowRule);
            }
            //重载限流规则
            FlowRuleManager.loadRules(flowRules);
        }
    }

    /**
     * 初始化资源对象
     */
    private static FlowRule getFlowRuleBeanDefault(String mealNameAndVersion, Double threshValue) {
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
        //流控模式，默认快速失败、
        // 预热，配合alFr.setWarmUpPeriodSec()使用
        // 排队,配合alFr.setMaxQueueingTimeMs()使用
        // 预热排队，配合alFr.setWarmUpPeriodSec()、alsFr.setMaxQueueingTimeMs()使用
        flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        //请求在队列中等待的最大时间
        flowRule.setMaxQueueingTimeMs(500);
        return flowRule;
    }

}
