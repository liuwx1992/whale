package com.zoo.whale.utils.grpc;/*
package com.zoo.whale.utils.grpc;

import com.br.grpc.encodemapping.EncodeMappingGrpc;
import com.br.grpc.encodemapping.EncodeRequest;
import com.br.grpc.encodemapping.ResultBean;

import java.util.concurrent.TimeUnit;

*/
/**
 * @Date: 2022年11月01日
 * @Time: 16时:20分
 * @Author: weixin.liu
 * @Version: 1.0
 *//*

public class EncodeMappingExecutor {


    public static EncodeMappingResultBean encode(String jsonParam) throws Exception {
        //grpc
        EncodeMappingGrpc.EncodeMappingBlockingStub blockingStub = GrpcServerFactory.getEncodeService();
        EncodeRequest request = EncodeRequest.newBuilder().setParam(jsonParam).build();
        ResultBean resultBean = blockingStub.withDeadlineAfter(3, TimeUnit.SECONDS).query(request);
        EncodeMappingResultBean encodeMappingResultBean = new EncodeMappingResultBean(resultBean);
        return encodeMappingResultBean;
    }

}
*/
