package com.test.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.oms.entity.OrderItem;
import com.test.gmall.pms.entity.SkuStock;
import com.test.gmall.pms.mapper.SkuStockMapper;
import com.test.gmall.pms.service.SkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@com.alibaba.dubbo.config.annotation.Service
@Service
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements SkuStockService {

    @Autowired
    private SkuStockMapper skuStockMapper;

    /**
     * 接收已经完成的订单号，并处理
     * @param
     */
    @JmsListener(destination = "order_queue")
    public void receiveOrderQueue(String orderItem) {

        System.out.println(orderItem);
        OrderItem item = JSON.parseObject(orderItem, OrderItem.class);
        //获取数据库对应skuId的库存
        SkuStock stock = skuStockMapper.selectById(item.getProductSkuId());

        //修改对应skuId的库存
        SkuStock skuStock = new SkuStock();
        skuStock.setStock(stock.getStock() - item.getProductQuantity());
        int update = skuStockMapper.update(skuStock, new UpdateWrapper<SkuStock>().eq("id", item.getProductSkuId()));
        if(update != 0){
            System.out.println("===============" +  item.getProductSkuId() + "修改成功");
        }
    }
}
