package com.test.gmall.cart.service;

import com.test.gmall.cart.vo.Cart;
import com.test.gmall.cart.vo.CartResponse;

public interface CartService {
    /**
     * 添加购物车
     * @param accessToken
     * @param num
     * @param skuId
     * @param cartKey
     * @return
     */
    CartResponse addCartItem(String accessToken,Integer num, Long skuId, String cartKey);

    /**
     * 查询并合并临时购物车
     * @param accessToken
     * @param cartKey
     * @return
     */
    CartResponse getToCart(String accessToken, String cartKey);

    /**
     * 修改购物项的数量和选中状态
     * @param accessToken
     * @param num
     * @param skuId
     * @param cartKey
     * @return
     */
    CartResponse updateCartItem(String accessToken, Boolean selected, Integer num, Long skuId, String cartKey);

    /**
     * 删除购物项
     * @param accessToken
     * @param skuId
     * @param cartKey
     */
    void deleteCartItem(String accessToken, Long skuId, String cartKey);

    /**
     * 清空购物车
     * @param accessToken
     * @param cartKey
     */
    void deleteAllCartItem(String accessToken, String cartKey);

    /**
     * 修改全部购物项的选中状态
     * @param accessToken
     * @param selected
     * @param cartKey
     */
    void updateAllCartItem(String accessToken, Boolean selected, String cartKey);

    /**
     * 根据购物项的选中状况，获取购物车信息
     * @param
     * @return
     */
    Cart getCartBySelected(Long id);
}
