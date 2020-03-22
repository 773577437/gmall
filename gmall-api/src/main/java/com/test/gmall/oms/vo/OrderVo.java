package com.test.gmall.oms.vo;

import com.test.gmall.cart.vo.Cart;
import com.test.gmall.sms.entity.Coupon;
import com.test.gmall.ums.entity.MemberReceiveAddress;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 确认订单信息页面数据
 */
@Data
public class OrderVo implements Serializable {

    /*购物车信息*/
    private Cart cart;

    /*地址信息*/
    private List<MemberReceiveAddress> addresses = new ArrayList<>();

    /*商品优惠信息*/
    private List<Coupon> coupons = new ArrayList<>();

    /*订单令牌*/
    private String orderToken;
}
