package cn.alphahub.mall.stock.controller;

import cn.alphahub.common.constant.HttpStatus;
import cn.alphahub.common.core.controller.BaseController;
import cn.alphahub.common.core.domain.BaseResult;
import cn.alphahub.common.core.page.PageDomain;
import cn.alphahub.common.core.page.PageResult;
import cn.alphahub.mall.stock.domain.ProductStock;
import cn.alphahub.mall.stock.service.ProductStockService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品库存表Controller
 *
 * @author Weasley J
 * @email 1432689025@qq.com
 * @date 2021-02-20 00:35:35
 */
@RestController
@RequestMapping("stock/product")
public class ProductStockController extends BaseController {
    @Autowired
    private ProductStockService productStockService;

    /**
     * 查询产品库存表列表
     *
     * @param page         当前页码,默认第1页
     * @param rows         显示行数,默认10条
     * @param orderColumn  排序排序字段,默认不排序
     * @param isAsc        排序方式,desc或者asc
     * @param productStock 产品库存表, 查询字段选择性传入, 默认为等值查询
     * @return 产品库存表分页数据
     */
    @GetMapping("/list")
    public BaseResult<PageResult<ProductStock>> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "10") Integer rows,
            @RequestParam(value = "orderColumn", defaultValue = "") String orderColumn,
            @RequestParam(value = "isAsc", defaultValue = "") String isAsc,
            ProductStock productStock
    ) {
        PageDomain pageDomain = new PageDomain(page, rows, orderColumn, isAsc);
        PageResult<ProductStock> pageResult = productStockService.queryPage(pageDomain, productStock);
        if (ObjectUtils.isNotEmpty(pageResult.getItems())) {
            return BaseResult.ok(pageResult);
        }
        return BaseResult.fail(HttpStatus.NOT_FOUND, "查询结果为空");
    }

    /**
     * 获取产品库存表详情
     *
     * @param id 产品库存表主键id
     * @return 产品库存表详细信息
     */
    @GetMapping("/info/{id}")
    public BaseResult<ProductStock> info(@PathVariable("id") Long id) {
        ProductStock productStock = productStockService.getById(id);
        return ObjectUtils.anyNotNull(productStock) ? BaseResult.ok(productStock) : BaseResult.fail();
    }

    /**
     * 修改产品库存表
     *
     * @param productStock 产品库存表, 根据id选择性更新
     * @return 成功返回true, 失败返回false
     */
    @PutMapping("/update")
    public BaseResult<Boolean> update(@RequestBody ProductStock productStock) {
        boolean update = productStockService.updateById(productStock);
        return toOperationResult(update);
    }

    /**
     * 下单抢购
     *
     * @param id            主键id
     * @param orderQuantity 下单数量
     * @return 抢购结果提示信息
     */
    @GetMapping("/decreaseStock/{id}/{orderQuantity}")
    public BaseResult<String> decreaseStock(@PathVariable("id") Long id, @PathVariable("orderQuantity") Long orderQuantity) {
        /**
         终端测试说明：
         ab -n 300 -c 300 请求地址
         -n 你做多少个请求
         -c 有多少个用户并发请求
         -H 请求的Header头文件
         ab -c 9000 -n 9000 -H "Authorization:0f8a-477d-bcaf-1234" "http://127.0.0.1:8080/stock/product/decreaseStock/1/1"
         */
        String msg = productStockService.decreaseStockQuantity(id, orderQuantity);
        return StringUtils.isNotBlank(msg) ? BaseResult.ok("下单成功", msg) : BaseResult.fail("下单抢购减库存失败！");
    }
}
