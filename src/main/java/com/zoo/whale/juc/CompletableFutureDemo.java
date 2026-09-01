package com.zoo.whale.juc;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.concurrent.CompletableFuture;

/**
 * @Date: 2024年 02月 05日
 * @Time:10时:32分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class CompletableFutureDemo {
    /*private ArrayNode dealEsMTTR(String clusterName, String index, String type, String jsonBody)
            throws Exception {
        ArrayNode arrayNode = null;
        if (clusterName.startsWith(EnumIdcRequest.SINGLE_IDC.name())) {
            arrayNode = CompletableFuture.supplyAsync(() ->
                            getEsData(IdcDealUtils.getReqSingleIdc(clusterName), index, type, jsonBody),
                    BrExecutors.ES_LOCAL_DATA_POOL).get(IdcEnvConfigBean.getThreadToalTimeOut(), TimeUnit.MILLISECONDS);
        } else if (clusterName.startsWith(EnumIdcRequest.BOTH_IDC.name())) {
            CompletableFuture<ArrayNode> localFuture = null;
            try {
                localFuture = CompletableFuture.supplyAsync(() ->
                                getEsData(IdcDealUtils.getReqBothLocalIdc(clusterName), index, type, jsonBody),
                        BrExecutors.ES_LOCAL_DATA_POOL);
                arrayNode = (ArrayNode) localFuture.get(IdcEnvConfigBean.getThreadTimeOut(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.warn("es error,idc cluster:{},error:{}", IdcDealUtils.getReqBothLocalIdc(clusterName),
                        e);
                CompletableFuture<ArrayNode> remoteFuture = null;
                CompletableFuture<Object> completableFuture = null;
                try {
                    remoteFuture = CompletableFuture.supplyAsync(() ->
                                    getEsData(IdcDealUtils.getReqBothRemoteIdc(clusterName), index, type, jsonBody),
                            BrExecutors.ES_REMOTE_DATA_POOL);
                    completableFuture = CompletableFuture.anyOf(localFuture, remoteFuture);
                    try {
                        arrayNode = (ArrayNode) completableFuture.get(
                                IdcEnvConfigBean.getThreadToalTimeOut() - IdcEnvConfigBean.getThreadTimeOut(),
                                TimeUnit.MILLISECONDS);
                    } catch (Exception ee) {
                        throw new BrException(BrException.getExceptionMsg(ee));
                    }
                }  catch (Exception ex) {
                    if (localFuture != null && remoteFuture != null && localFuture.isDone() && !remoteFuture.isDone()) {
                        arrayNode = remoteFuture.get(
                                IdcEnvConfigBean.getThreadToalTimeOut() - IdcEnvConfigBean.getThreadTimeOut(),
                                TimeUnit.MILLISECONDS);
                    } else if (localFuture != null && remoteFuture != null && !localFuture.isDone()
                            && remoteFuture.isDone()) {
                        arrayNode = localFuture.get(IdcEnvConfigBean.getThreadToalTimeOut(), TimeUnit.MILLISECONDS);
                    } else {
                        throw ex;
                    }
                } finally {
                    try {
                        if (remoteFuture != null) {
                            remoteFuture.cancel(true);
                        }
                        if (localFuture != null) {
                            localFuture.cancel(true);
                        }
                        if (completableFuture != null) {
                            completableFuture.cancel(true);
                        }
                    } catch (Exception ex) {
                        logger.error("hbase ComletableFuture cancle error,", ex);
                    }
                }
            }
        } else {
            logger.error("es no this cluserName:{}", clusterName);
        }
        return arrayNode;
    }*/
}
