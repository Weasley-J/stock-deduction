package cn.alphahub.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 库存服务
 *
 * @author liuwenjing
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(value = {"cn.**.dao", "cn.**.mapper"})
public class StockDecreaseDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockDecreaseDemoApplication.class, args);
    }

}
