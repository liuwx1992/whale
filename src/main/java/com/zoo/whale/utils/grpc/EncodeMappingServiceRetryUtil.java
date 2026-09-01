package com.zoo.whale.utils.grpc;/*
package com.zoo.whale.utils.grpc;

import net.sf.json.JSONObject;

import java.util.Objects;

public class EncodeMappingServiceRetryUtil {

    public static EncodeMappingResultBean queryRetryOnceIfTimeOut(JSONObject jsonParam) throws Exception {
        int count = 0;
        EncodeMappingResultBean bean;
        do {
            //解密
            bean = EncodeMappingExecutor.encode(jsonParam.toString());
            if (Objects.nonNull(bean) && "0".equals(bean.getStatus())) {
                return bean;
            }
            count++;
        } while (count <= 1);
        return bean;
    }

}
*/
