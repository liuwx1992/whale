package com.zoo.whale.utils.grpc;/*
package com.zoo.whale.utils.grpc;

import com.br.grpc.encodemapping.EncodeMappingGrpc;
import com.br.grpc.utils.BrGrpcUtils;
import com.br.monitor.grpc.EnvUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

*/
/**
 * @Date: 2022年10月31日
 * @Time: 19时:42分
 * @Author: weixin.liu
 * @Version: 1.0
 *//*

public class GrpcServerFactory {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServerFactory.class);

    private static final String ENCODE_MAPPING_SERVICE;

    // GRPC_ENV 该环境变量不用加入 sre 平台或 启动脚本中。仅用于方便本地启动调试，本地启动时需配置环境变量 GRPC_ENV。
    static {
        String grpcEnv = EnvUtil.getProperties("GRPC_ENV");
        if (StringUtils.isEmpty(grpcEnv)) {
            ENCODE_MAPPING_SERVICE = "grpc-encode-mapping-service";
        } else {
            ENCODE_MAPPING_SERVICE = "grpc.brapp.com";
        }
    }

    */
/**
     * 初始化grpc客户端
     * *//*

    public static void initGrpcClient(){
        int k = 1;
        while (k <= 5) {
            try {
                //k8s集群内初始化方式：（内置healthcheck探针，自动探测识别）
                BrGrpcUtils.initChannels(ENCODE_MAPPING_SERVICE);
                logger.info("启动时初始化加解密grpc服务连接成功");
                break;
            } catch (Exception e) {
                logger.warn("启动时初始化加解密grpc服务连接出错第 {} 次,异常:", k, e);
                if (k == 5) {
                    throw new RuntimeException("启动时初始化加解密grpc服务连接出错5次，终止启动！", e);
                }
                k++;
            }
        }
    }


    */
/**
     * 获取加解密GRPC服务
     *//*

    public static EncodeMappingGrpc.EncodeMappingBlockingStub getEncodeService() throws Exception {
        EncodeMappingGrpc.EncodeMappingBlockingStub blockingStub = (EncodeMappingGrpc.EncodeMappingBlockingStub)
                BrGrpcUtils.newBlockStub(ENCODE_MAPPING_SERVICE, EncodeMappingGrpc.class);
        return blockingStub;
    }




}
*/
