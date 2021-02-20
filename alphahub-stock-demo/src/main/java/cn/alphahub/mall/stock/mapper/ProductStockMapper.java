package cn.alphahub.mall.stock.mapper;

import cn.alphahub.mall.stock.domain.ProductStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品库存表
 *
 * @author Weasley J
 * @email 1432689025@qq.com
 * @date 2021-02-20 00:35:35
 */
@Mapper
public interface ProductStockMapper extends BaseMapper<ProductStock> {

}
