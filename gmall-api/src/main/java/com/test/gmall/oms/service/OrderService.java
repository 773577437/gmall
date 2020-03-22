package com.test.gmall.oms.service;

import com.test.gmall.oms.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.gmall.oms.vo.OrderSubmitVo;
import com.test.gmall.oms.vo.OrderVo;

import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
public interface OrderService extends IService<Order> {

    /**
     * 获取确认订单页面数据
     * @param accessToken
     * @param skuId
     * @return
     */
    OrderVo getToOrderVo(String accessToken, Long skuId);

    /**
     * 获取提交订单页面数据并保存订单
     * @param accessToken
     * @param addressId
     * @return
     */
    OrderSubmitVo getToOrderSubmitVo(String orderToken, String accessToken, Long addressId);

    /**
     * 结算订单
     * @param orderSn
     * @param accessToken
     * @return
     */
    String payOrder(String orderSn, String accessToken);

    /**
     * 支付验证
     */
    void payToAsync(Map<String, String> params);
}
