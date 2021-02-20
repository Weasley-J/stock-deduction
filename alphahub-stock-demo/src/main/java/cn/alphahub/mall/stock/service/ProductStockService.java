package cn.alphahub.mall.stock.service;

import cn.alphahub.common.core.service.PageService;
import cn.alphahub.mall.stock.domain.ProductStock;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 产品库存表Service接口
 *
 * @author Weasley J
 * @email 1432689025@qq.com
 * @date 2021-02-20 00:35:35
 */
public interface ProductStockService extends IService<ProductStock>, PageService<ProductStock> {

    /**
     * 减库存
     *
     * @param id              库存id
     * @param orderQuantity 下单数量
     * @return 购买提示
     */
    String decreaseStockQuantity(Long id, Long orderQuantity);
}
