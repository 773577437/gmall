package com.test.gmallcart.Service.Impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.test.gmall.cart.service.CartService;
import com.test.gmall.cart.vo.Cart;
import com.test.gmall.cart.vo.CartItem;
import com.test.gmall.cart.vo.CartResponse;
import com.test.gmall.constant.CartConstant;
import com.test.gmall.pms.entity.Product;
import com.test.gmall.pms.service.ProductService;
import com.test.gmall.pms.service.SkuStockService;
import com.test.gmall.ums.entity.Member;
import com.test.gmallcart.component.MemberComponent;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@com.alibaba.dubbo.config.annotation.Service
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private MemberComponent memberComponent;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Reference
   private SkuStockService skuStockService;

    @Reference
   private ProductService productService;

    @Override
    public CartResponse addCartItem(String accessToken,Integer num, Long skuId, String cartKey) {

        Member member = memberComponent.getMemberByToken(accessToken);
        String finalCartKey = "";
        CartResponse cartResponse = new CartResponse();
        Cart cart = new Cart();

        //1、用户登录状态,没有添加购物车记录，创建临时购物车，并添加购物车
        if(member != null){
            finalCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();
            CartItem cartItem = addToCart(skuId, num, finalCartKey);

            cart.setCartItems(Collections.singletonList(cartItem));
            cartResponse.setCart(cart);;
            return  cartResponse;
        }
        //2、有添加购物车记录，离线状态加临时购物车
        if(!StringUtils.isEmpty(cartKey)){
            finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;
            //设置缓存存放30天不访问自动到期
            redisTemplate.expire(finalCartKey, CartConstant.CART_KEY_TIMEOUT, TimeUnit.DAYS);
            CartItem cartItem = addToCart(skuId, num, finalCartKey);

            cart.setCartItems(Collections.singletonList(cartItem));
            cartResponse.setCart(cart);
            cartResponse.setCartKey(cartKey);
            return  cartResponse;
        }
        //3、没有添加购物车记录，离线状态，创建临时购物车，并添加
            String newCartKey = UUID.randomUUID().toString();
            finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + newCartKey;
            //设置缓存存放30天不访问自动到期
            redisTemplate.expire(finalCartKey, CartConstant.CART_KEY_TIMEOUT, TimeUnit.DAYS);
            CartItem cartItem = addToCart(skuId, num, finalCartKey);

            cart.setCartItems(Collections.singletonList(cartItem));
            cartResponse.setCart(cart);;
            cartResponse.setCartKey(newCartKey);
            return  cartResponse;
    }

    @Override
    public CartResponse getToCart(String accessToken, String cartKey) {
        //检验登录令牌
        Member member = memberComponent.getMemberByToken(accessToken);
        CartResponse cartResponse = new CartResponse();
        //判断是否登录查询
        if(member != null){
            Cart cart = cartMerge(member.getId(), cartKey);
            //返回合并的购物车
            cartResponse.setCart(cart);
        }else if(!StringUtils.isEmpty(cartKey)){  //当只有临时令牌时，获取临时购物车
            String tempCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;
            redisTemplate.expire(tempCartKey, CartConstant.CART_KEY_TIMEOUT, TimeUnit.DAYS);  //设置缓存存放30天不访问自动到期
            RMap<String , String> map = redissonClient.getMap(tempCartKey);
            Cart cart = getCart(map);
            cartResponse.setCart(cart);
        }else{
            //没有用户令牌和临时令牌，返回空页
            return null;
        }

        return cartResponse;
    }

    @Override
    public CartResponse updateCartItem(String accessToken, Boolean selected, Integer num, Long skuId, String cartKey) {
        //检验登录令牌
        Member member = memberComponent.getMemberByToken(accessToken);
        CartResponse cartResponse = new CartResponse();
        String finalCartKey = "";
        Cart cart = new Cart();

        //1、用户登录状态,修改购物项数量
        if(member != null){
            finalCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();
            return getCartResponse(selected,num, skuId, cartResponse, finalCartKey, cart);
        }

        //2、临时购物车,修改购物项数量
        finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;
        return  getCartResponse(selected, num, skuId, cartResponse, finalCartKey, cart);
    }

    @Override
    public void deleteCartItem(String accessToken, Long skuId, String cartKey) {
        //检验登录令牌
        Member member = memberComponent.getMemberByToken(accessToken);
        String finalCartKey = "";

        //1、用户登录状态,删除购物项
        if(member != null){
            finalCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();
            RMap<String, String> map = redissonClient.getMap(finalCartKey);
            map.remove(skuId.toString());
        }else{
            //2、临时购物车,删除购物项
            finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;
            RMap<String, String> map = redissonClient.getMap(finalCartKey);
            map.remove(skuId.toString());
        }
    }

    @Override
    public void deleteAllCartItem(String accessToken, String cartKey) {
        //检验登录令牌
        Member member = memberComponent.getMemberByToken(accessToken);
        String finalCartKey = "";

        //1、用户登录状态,清空购物车
        if(member != null){
            finalCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();
            RMap<String, String> map = redissonClient.getMap(finalCartKey);
            map.clear();
        }else{
            //2、临时购物车,清空购物车
            finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;
            RMap<String, String> map = redissonClient.getMap(finalCartKey);
            map.clear();
        }
    }

    @Override
    public void updateAllCartItem(String accessToken, Boolean selected, String cartKey) {
        //检验登录令牌
        Member member = memberComponent.getMemberByToken(accessToken);
        String finalCartKey = "";

        //1、用户登录状态,清空购物车
        if(member != null){
            finalCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();
            updateSelected(selected, finalCartKey);
        }else{
            //2、临时购物车,清空购物车
            finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;
            updateSelected(selected, finalCartKey);
        }
    }

    @Override
    public Cart getCartBySelected(Long id) {
        //用户购物车
        String userCartKey = CartConstant.USER_CART_KEY_PREFIX + id;
        RMap<String, String> userMap = redissonClient.getMap(userCartKey);
        Cart cart = new Cart();
        ArrayList<CartItem> cartItems = new ArrayList<CartItem>();
        userMap.forEach((Key, Value) -> {
            CartItem cartItem = JSON.parseObject(Value, CartItem.class);
            //判断购物项是否是选中状态
            if(cartItem.getSelected()){
                cartItems.add(cartItem);
            }
        });
        cart.setCartItems(cartItems);
        return cart;
    }

    private void updateSelected(Boolean selected, String finalCartKey) {
        RMap<String, String> map = redissonClient.getMap(finalCartKey);
        map.forEach((key, value) -> {
            String itemJson = map.get(key);
            CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
            cartItem.setSelected(selected);
            map.put(key, JSON.toJSONString(cartItem));
        });
    }

    //修改购物项数量
    private CartResponse getCartResponse(Boolean selected, Integer num, Long skuId, CartResponse cartResponse, String finalCartKey, Cart cart) {

        RMap<String, String> map = redissonClient.getMap(finalCartKey);
        String itemJson = map.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
        cartItem.setCount(num);
        cartItem.setSelected(selected);
        map.put(skuId.toString(), JSON.toJSONString(cartItem));
        cart.setCartItems(Collections.singletonList(cartItem));
        cartResponse.setCart(cart);

        return  cartResponse;
    }

    //用户购物车合并临时购物车
    private Cart cartMerge(Long id, String cartKey) {

        //用户购物车
        String userCartKey = CartConstant.USER_CART_KEY_PREFIX + id;
        //临时购物车
        String tempCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;

        RMap<String, String> userMap = redissonClient.getMap(userCartKey);
        RMap<String, String> tempMap = redissonClient.getMap(tempCartKey);
        //判断是否有临时购物车
        if(tempMap != null){
            tempMap.forEach((tempKey, tempValue) -> {
                CartItem tempCartItem = JSON.parseObject(tempValue, CartItem.class);
                //将临时购物车的购物项加入用户购物车
                addToCart(Long.parseLong(tempKey), tempCartItem.getCount(), userCartKey);
            });
            //清空临时购物车
            tempMap.clear();
        }
        //从缓存中获取数据返回
        return getCart(userMap);
    }

    //从缓存中获取数据返回
    private Cart getCart(RMap<String, String> map) {
        Cart cart = new Cart();
        ArrayList<CartItem> cartItems = new ArrayList<CartItem>();
        map.forEach((Key, Value) -> {
            CartItem cartItem = JSON.parseObject(Value, CartItem.class);
            //添加或覆盖购物项
            cartItems.add(cartItem);
        });
        cart.setCartItems(cartItems);
        return cart;
    }

    //添加购物项
    private CartItem addToCart(Long skuId, Integer num, String finalCartKey){

        //规则：k-v ;k=sukId, v=cartItem对象
        RMap<String, String> map = redissonClient.getMap(finalCartKey);
        String itemJson = map.get(skuId.toString());
        CartItem item = JSON.parseObject(itemJson, CartItem.class);
        //判断当前购物项，之前是否是新增
        if(item != null){
            //不是新增，则之前数量加一即可
            item.setCount(item.getCount() + num);
            map.put(skuId.toString(), JSON.toJSONString(item));
            return item;
        }else{  //新增购物项
            CompletableFuture<CartItem> newCartItem = CompletableFuture.supplyAsync(() -> {  //异步
                //获取sku的信息
                return skuStockService.getById(skuId);
            }).thenApplyAsync((sku) -> {
                CartItem finalItem = new CartItem();
                Product product = productService.getById(sku.getProductId());
                BeanUtils.copyProperties(sku, finalItem);
                finalItem.setCount(num);
                finalItem.setName(product.getName());
                return finalItem;
            });
            try {
                item = newCartItem.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(item != null){
                map.put(skuId.toString(), JSON.toJSONString(item));
            }

            return item;
        }
    }
}
