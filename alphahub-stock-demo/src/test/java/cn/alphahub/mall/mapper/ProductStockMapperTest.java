package cn.alphahub.mall.mapper;

import cn.alphahub.mall.stock.domain.ProductStock;
import cn.alphahub.mall.stock.mapper.ProductStockMapper;
import cn.alphahub.mall.stock.service.ProductStockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Slf4j
@SpringBootTest
class ProductStockMapperTest {

    @Resource
    private ProductStockMapper productStockMapper;

    @Resource
    private ProductStockService productStockService;

    @Test
    void insert() {
        ProductStock stock1 = ProductStock.builder()
                .name("苏打水")
                .categoryId(12L)
                .brandId(245L)
                .stockQuantity(86L)
                .purchasePrice(new BigDecimal(200L))
                .priceUnit("分")
                .stockUnit("个")
                .createBy("admin")
                .createTime(new Date())
                .deleted(0)
                .build();
        ProductStock stock2 = ProductStock.builder()
                .name("农夫山泉")
                .categoryId(31L)
                .brandId(195L)
                .purchasePrice(new BigDecimal(600L))
                .priceUnit("分")
                .stockQuantity(365L)
                .stockUnit("个")
                .createBy("admin")
                .createTime(new Date())
                .deleted(0)
                .build();
        ProductStock stock3 = ProductStock.builder()
                .name("雪碧")
                .categoryId(12L)
                .brandId(245L)
                .purchasePrice(new BigDecimal(300L))
                .priceUnit("分")
                .stockQuantity(86L)
                .stockUnit("个")
                .createBy("admin")
                .createTime(new Date())
                .deleted(0)
                .build();
        int insert = 0;
        //insert = productStockMapper.insert(stock1);
        List<ProductStock> stocks = Arrays.asList(stock1, stock2, stock3);
        boolean batch = productStockService.saveBatch(stocks);

        /*for (ProductStock stock : stocks) {
            insert = productStockMapper.insert(stock);
            log.info("影响行数：{}", insert);
        }*/
    }

    @Test
    void delete() {
        int i = productStockMapper.deleteBatchIds(Arrays.asList(1, 2, 3));
        System.out.println("i = " + i);
    }
}
