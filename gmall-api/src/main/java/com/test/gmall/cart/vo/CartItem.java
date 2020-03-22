package com.test.gmall.cart.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物项
 */
@Setter
public class CartItem implements Serializable {

    /*商品id*/
    @Getter
    private Long productId;

    /*商品名*/
    @Getter
    private  String name;

    /*skuId*/
    @Getter
    private Long id;

    /*库存*/
    @Getter
    private Integer stock;

    /*sku属性值*/
    @Getter
    private String sp1;

    /*sku属性值*/
    @Getter
    private String sp2;

    /*sku属性值*/
    @Getter
    private String sp3;

    /*sku偏码*/
    @Getter
    private String skuCode;

    /*图片*/
    @Getter
    private String pic;

    /*商品价格*/
    @Getter
    private BigDecimal price;

    /*促销价格*/
    @Getter
    private BigDecimal promotionPrice;

    /*选中状态*/
    @Getter
    private Boolean selected = true;

    /*购买数量*/
    @Getter
    private Integer count;

    /*当前购物项总价格*/
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        if(price != null){
            //商品价格 * 购买数量
            BigDecimal multiply = price.multiply(new BigDecimal(count.toString()));
            return multiply;
        }else{
            return new BigDecimal("0");
        }

    }



}
