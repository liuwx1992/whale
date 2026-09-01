package com.zoo.whale.example.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * @ClassName:HbaseCacheTask
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/3/30
 * @Time:17:17
 * @Version；1.0
 */
public class HbaseCacheTask implements Callable {

    private static final Logger logger = LoggerFactory.getLogger(HbaseCacheTask.class);

    private String result;

    public HbaseCacheTask(String result) {
        this.result = result;
    }

    @Override
    public Integer call() {
        return writeDacCacheToHbase2(result);
    }

    /**
     * 写入HC到Hbase
     */
    private Integer writeDacCacheToHbase2(String result) {
        int ret = 0;
        try {
            if ("0".equals(result)) {
                throw new RuntimeException("存储异常了！");
            } else {
                ret = 0;
            }
        } catch (Exception e) {
            logger.error("海纳大缓存写数据出现异常，请紧急处理！{}", e);
        }
        return ret;
    }

    private Integer writeDacCacheToHbase(String result) {
        int ret = 0;
        if ("0".equals(result)) {
            throw new RuntimeException("存储异常了！");
        } else {
            ret = 0;
        }
        return ret;
    }

    /**
     * 判断是否超过缓存大小
     */
    private boolean ifOverCacheSize(String preResult) {
        int length = preResult.getBytes(StandardCharsets.UTF_8).length;
        if (length > 1024 * 1024) {
            return false;
        }
        return true;
    }
}
