package com.test.gmallcart;

import com.alibaba.fastjson.JSON;
import com.test.gmall.cart.vo.Cart;
import com.test.gmall.cart.vo.CartItem;
import com.test.gmall.constant.OrderConstant;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class GmallCartApplicationTests {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    void test(){
        redisTemplate.expire("123",30, TimeUnit.SECONDS);
        RMap<Object, Object> map = redissonClient.getMap("123");
        map.put("asd","asd");
    }

    @Test
    void userRedisson(){
        RMap<Object, Object> cart = redissonClient.getMap("cart");
        CartItem cartItem = new CartItem();
        cartItem.setPrice(new BigDecimal("999.9"));
        cartItem.setCount(3);
        cart.put("1", JSON.toJSONString(cartItem));

        CartItem cartItem1 = new CartItem();
        cartItem1.setPrice(new BigDecimal("0.1"));
        cartItem1.setCount(3);
        cart.put(2, JSON.toJSONString(cartItem1));
    }

    @Test
    void contextLoads() {
        CartItem cartItem = new CartItem();
        cartItem.setPrice(new BigDecimal("999.9"));
        cartItem.setCount(3);

        CartItem cartItem1 = new CartItem();
        cartItem1.setPrice(new BigDecimal("0.1"));
        cartItem1.setCount(3);

        Cart cart = new Cart();
        ArrayList<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cartItems.add(cartItem1);
        cart.setCartItems(cartItems);

        System.out.println(cartItem.getTotalPrice() + "=====>数量" + cart.getTotalCount() + "======>价格" + cart.getTotalPrice());
    }

}
