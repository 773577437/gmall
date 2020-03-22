package com.test.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.test.gmall.cart.service.CartService;
import com.test.gmall.cart.vo.CartResponse;
import com.test.gmall.to.CommonResult;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
public class CartController {

    @Reference
    private CartService cartService;

    @ApiOperation("查询购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "用户登录令牌"),
            @ApiImplicitParam(name = "cartKey", value = "离线购物车key")
    })
    @PostMapping("/getCart")
    public Object getToCart(@RequestParam(value = "accessToken" ,required = false) String accessToken,
                              @RequestParam(value = "cartKey", required = false) String cartKey){

        CartResponse cartResponse = cartService.getToCart(accessToken, cartKey);
        return  new CommonResult().success(cartResponse);
    }

    @ApiOperation("添加购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "用户登录令牌"),
            @ApiImplicitParam(name = "cartKey", value = "离线购物车key"),
            @ApiImplicitParam(name = "num", value = "购物车单项商品数量"),
            @ApiImplicitParam(name = "skuId", value = "商品skuID")
    })
    @PostMapping("/addCart")
    public Object addCartItem(@RequestParam(value = "accessToken" ,required = false) String accessToken,
                              @RequestParam(value = "num", defaultValue = "1") Integer num,
                              @RequestParam(value = "skuId") Long skuId,
                              @RequestParam(value = "cartKey", required = false) String cartKey){

            CartResponse cartItem = cartService.addCartItem(accessToken, num, skuId, cartKey);
            return  new CommonResult().success(cartItem);
    }

    @ApiOperation("更新购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "用户登录令牌"),
            @ApiImplicitParam(name = "cartKey", value = "离线购物车key"),
            @ApiImplicitParam(name = "num", value = "购物车单项商品数量"),
            @ApiImplicitParam(name = "skuId", value = "商品skuID"),
            @ApiImplicitParam(name = "selected", value = "商品选中状态")
    })
    @PostMapping("/updateCartItem")
    public Object updateCartItem(@RequestParam(value = "accessToken" ,required = false) String accessToken,
                              @RequestParam(value = "num", defaultValue = "1") Integer num,
                              @RequestParam(value = "selected", defaultValue = "true") Boolean selected,
                              @RequestParam(value = "skuId") Long skuId,
                              @RequestParam(value = "cartKey", required = false) String cartKey){

        CartResponse cartItem = cartService.updateCartItem(accessToken, selected, num, skuId, cartKey);
        return  new CommonResult().success(cartItem);
    }

    @ApiOperation("删除购物项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "用户登录令牌"),
            @ApiImplicitParam(name = "cartKey", value = "离线购物车key"),
            @ApiImplicitParam(name = "skuId", value = "商品skuID")
    })
    @DeleteMapping("/deleteCartItem")
    public Object deleteCartItem(@RequestParam(value = "accessToken" ,required = false) String accessToken,
                                 @RequestParam(value = "skuId") Long skuId,
                                 @RequestParam(value = "cartKey", required = false) String cartKey){

        cartService.deleteCartItem(accessToken, skuId, cartKey);
        return  new CommonResult().getMessage();
    }


    @ApiOperation("清空全部购物项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "用户登录令牌"),
            @ApiImplicitParam(name = "cartKey", value = "离线购物车key")
    })
    @DeleteMapping("/deleteCart")
    public Object deleteAllCartItem(@RequestParam(value = "accessToken" ,required = false) String accessToken,
                             @RequestParam(value = "cartKey", required = false) String cartKey){

        cartService.deleteAllCartItem(accessToken, cartKey);
        return  new CommonResult().getMessage();
    }

    @ApiOperation("选中/取消全部购物项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "用户登录令牌"),
            @ApiImplicitParam(name = "cartKey", value = "离线购物车key"),
            @ApiImplicitParam(name = "selected", value = "商品选中状态")
    })
    @PostMapping("/updateCart")
    public Object updateAllCartItem(@RequestParam(value = "accessToken" ,required = false) String accessToken,
                                    @RequestParam(value = "selected", defaultValue = "true") Boolean selected,
                                    @RequestParam(value = "cartKey", required = false) String cartKey){

        cartService.updateAllCartItem(accessToken, selected, cartKey);
        return  new CommonResult().getMessage();
    }

}
