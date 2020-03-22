package com.test.gmall.cart.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车
 */
@Setter
public class Cart implements Serializable {

    /*全部购物项*/
    @Getter
    private List<CartItem> cartItems = new ArrayList<>();

    /*以选中商品总价格*/
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        if(cartItems != null){
            totalPrice = new BigDecimal("0");
            cartItems.forEach((cartItem)->{
                if(cartItem.getSelected()){  //判断当前商品是否选中状态
                    //等同 i += i + 1;
                    totalPrice = totalPrice.add(cartItem.getTotalPrice());
                }
            });
            return totalPrice;
        }else{
            return new BigDecimal("0");
        }

    }


    /*以选中商品总数*/
    private Integer totalCount;

    public Integer getTotalCount() {
        if(cartItems != null){
            totalCount = 0;
            cartItems.forEach((cartItem)->{
                if(cartItem.getSelected()){  //判断当前商品是否选中状态
                    totalCount += cartItem.getCount();
                }
            });
            return totalCount;
        }else{
            return 0;
        }

    }

}
