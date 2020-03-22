package com.test.gmall.oms.vo;

import com.test.gmall.oms.entity.CartItem;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 提交订单页面数据
 */
@Data
public class OrderSubmitVo implements Serializable {

    /*订单编号*/
    private String orderSn;
    /*收货地址*/
    private String receiveAddress;
    /*收货人*/
    private String receiveName;
    /*联系方式*/
    private String phoneNumber;
    /*商品名称*/
    private List<String> productNames = new ArrayList<>();
    /*商品总价格*/
    private BigDecimal totalPrice;

    /*验证错误信息*/
    private String error;
}
