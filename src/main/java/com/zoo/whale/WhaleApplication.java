package com.zoo.whale;

import com.zoo.whale.listener.StartListener;
import com.zoo.whale.utils.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class WhaleApplication {


    /*@Bean
    RestTemplate restTemplateIstio() {
        RestTemplate restTemplate = new RestTemplate(HttpClientUtils.getClientHttpRequestFactory());
        return restTemplate;
    }*/

    public static void main(String[] args) {

        SpringApplication.run(WhaleApplication.class, args);

        //GrpcServerFactory.initGrpcClient();

        int ret = RedisClient.init();
        if (ret != 0) {
            log.error("缓存初始化失败！");
            System.exit(-1);
        }

        StartListener.init();

        log.warn("service whale started...");

    }

}
