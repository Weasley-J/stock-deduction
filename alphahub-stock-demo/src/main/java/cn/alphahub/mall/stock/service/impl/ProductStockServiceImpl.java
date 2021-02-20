package cn.alphahub.mall.stock.service.impl;

import cn.alphahub.common.core.page.PageDomain;
import cn.alphahub.common.core.page.PageResult;
import cn.alphahub.mall.stock.domain.ProductStock;
import cn.alphahub.mall.stock.mapper.ProductStockMapper;
import cn.alphahub.mall.stock.service.ProductStockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品库存表Service业务层处理
 *
 * @author Weasley J
 * @email 1432689025@qq.com
 * @date 2021-02-20 00:35:35
 */
@Slf4j
@Service
public class ProductStockServiceImpl extends ServiceImpl<ProductStockMapper, ProductStock> implements ProductStockService {
    @Resource
    private RedissonClient redissonClient;
    private Long requestCount = 0L;

    /**
     * 查询产品库存表分页列表
     *
     * @param pageDomain   分页数据
     * @param productStock 分页对象
     * @return 产品库存表分页数据
     */
    @Override
    public PageResult<ProductStock> queryPage(PageDomain pageDomain, ProductStock productStock) {
        pageDomain.startPage();
        QueryWrapper<ProductStock> wrapper = new QueryWrapper<>(productStock);

        // 这里可编写自己的业务查询wrapper，如果需要。
        // ...

        return getPageResult(wrapper);
    }

    /**
     * 根据查询构造器条件查询分页查询结果
     *
     * @param wrapper <b>产品库存表<b/>实体对象封装操作类
     * @return 实体对象分页查询结果
     */
    private PageResult<ProductStock> getPageResult(QueryWrapper<ProductStock> wrapper) {
        List<ProductStock> list = this.list(wrapper);
        PageInfo<ProductStock> pageInfo = new PageInfo<>(list);
        return PageResult.<ProductStock>builder()
                .totalCount(pageInfo.getTotal())
                .totalPage((long) pageInfo.getPages())
                .items(pageInfo.getList())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String decreaseStockQuantity(Long id, Long orderQuantity) {
        String key = "DEC_STOCK_LOCK" + id, msg = "";

        //单体时计算请求次数
        requestCount += 1;
        Long stockQuantity = 0L;

        // 加锁 操作很类似Java的ReentrantLock机制
        RLock lock = redissonClient.getLock(key);
        lock.lock();

        try {
            log.info("=============== 获取分布式锁成功 ================");

            ProductStock stock = this.getById(id);
            stockQuantity = stock.getStockQuantity();

            if (ObjectUtils.isNull(stock)) {
                msg = "商品不存在！！！";
                log.warn("{}", msg);
                return msg;
            }
            // 简单减库存操作 没有重新写其他接口了
            if (orderQuantity > 1) {
                msg = "限购一件！！！";
                log.warn("{}", msg);
                return msg;
            }
            if (stockQuantity == 0) {
                msg = "当前库存量为 0 ，不能再减库存了！！！";
                log.warn("{}", msg);
                return msg;
            }
            stockQuantity -= orderQuantity;
            stock.setStockQuantity(stockQuantity);
            // 减库存同步修改db
            this.updateById(stock);

            msg = "当前库存量为: ";
            log.warn(msg + stockQuantity);

        } catch (Exception e) {
            log.error("查询数据异常{}, 异常栈：{}", e.getMessage(), e);
        } finally {
            // 释放锁
            lock.unlock();
            // log.info("================= 分布式锁已释放，接受：" + requestCount + " 请求\n");
            log.info("================= 分布式锁已释放  ========\n");
        }

        return msg + stockQuantity;
    }
}
