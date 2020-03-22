package com.test.gmall.cart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartResponse implements Serializable {

    /*整个购物车*/
    private Cart cart;
    /*单个购物项*/
    private CartItem cartItem;

    private String cartKey;
}
