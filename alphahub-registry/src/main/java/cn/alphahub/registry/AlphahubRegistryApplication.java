package cn.alphahub.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 注册中心
 *
 * @author liuwenjing
 */
@EnableEurekaServer
@SpringBootApplication
public class AlphahubRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlphahubRegistryApplication.class, args);
    }

}
