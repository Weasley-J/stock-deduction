package cn.alphahub.mall.stock.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品库存表
 *
 * @author Weasley J
 * @email 1432689025@qq.com
 * @date 2021-02-20 00:35:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_product_stock")
public class ProductStock implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 进货价格
     */
    private BigDecimal purchasePrice;

    /**
     * 价格单位 (默认: 分  )
     */
    private String priceUnit;

    /**
     * 库存存量
     */
    private Long stockQuantity;

    /**
     * 库存单位 ( 默认: 个 )
     */
    private String stockUnit;

    /**
     * 删除状态（0：未删，1：已删，默认：未删除）
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 备注信息
     */
    private String remark;

    public void setPriceUnit(String priceUnit) {
        if (StringUtils.isBlank(priceUnit)) {
            this.priceUnit = "分";
        } else {
            this.priceUnit = priceUnit;
        }
    }

    public void setStockUnit(String stockUnit) {
        if (StringUtils.isBlank(stockUnit)) {
            this.stockUnit = "个";
        } else {
            this.stockUnit = stockUnit;
        }
    }
}
