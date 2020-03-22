package com.test.gmall.oms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Verify;
import com.google.common.collect.Ordering;
import com.test.gmall.cart.service.CartService;
import com.test.gmall.cart.vo.Cart;
import com.test.gmall.cart.vo.CartItem;
import com.test.gmall.constant.CartConstant;
import com.test.gmall.constant.OrderConstant;
import com.test.gmall.constant.OrderStatusEnume;
import com.test.gmall.oms.component.MemberComponent;
import com.test.gmall.oms.config.AlipayConfig;
import com.test.gmall.oms.entity.Order;
import com.test.gmall.oms.entity.OrderItem;
import com.test.gmall.oms.mapper.OrderItemMapper;
import com.test.gmall.oms.mapper.OrderMapper;
import com.test.gmall.oms.service.OrderService;
import com.test.gmall.oms.vo.OrderSubmitVo;
import com.test.gmall.oms.vo.OrderVo;
import com.test.gmall.pms.entity.SkuStock;
import com.test.gmall.pms.service.ProductService;
import com.test.gmall.pms.service.SkuStockService;
import com.test.gmall.to.es.EsProduct;
import com.test.gmall.to.es.EsProductSkuInfo;
import com.test.gmall.ums.entity.Member;
import com.test.gmall.ums.entity.MemberReceiveAddress;
import com.test.gmall.ums.service.MemberReceiveAddressService;
import io.shardingjdbc.core.constant.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Queue;
import javax.swing.text.Caret;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Slf4j
@Service
@com.alibaba.dubbo.config.annotation.Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private MemberComponent memberComponent;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Reference
    private CartService cartService;

    @Reference
    private MemberReceiveAddressService addressService;

    @Reference
    private SkuStockService skuStockService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Reference
    private ProductService productService;

    @Autowired
    private Queue queue;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    //线程内共享值
//    private ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    @Override
    public OrderVo getToOrderVo(String accessToken, Long skuId) {
        Member member = memberComponent.getMemberByToken(accessToken);
        //判断用户登录验证
        if(member != null){
            OrderVo orderVo = new OrderVo();
            //规定当没有传skuId时，是有加入购物车，然后确认订单
//            if(StringUtils.isEmpty(skuId.toString())){
                //获取会员购物车信息
                Cart cart = cartService.getCartBySelected(member.getId());
                orderVo.setCart(cart);
                //获取会员收货地址信息
                orderVo.setAddresses(addressService.getMemberReceiveAddressList(member.getId()));
                //商品优惠信息
                orderVo.setCoupons(null);
                //设置订单令牌
                String token = UUID.randomUUID().toString();
                //令牌规则：token + "_" + 系统当前时间 + "_" +  固定时间
                token += token + "_" +  System.currentTimeMillis() + "_" + 10*60;
                orderVo.setOrderToken(token);
                //将令牌以map的方式放入缓存
                redisTemplate.opsForSet().add(OrderConstant.ORDER_TOKEN , token);

                return orderVo;
//            }else{  //当有传skuId时，是没有加入购物车，然后确认订单
//
//            }
        }
        //没有登录就返回null
        return null;
    }

    @Override
    @Transactional
    public OrderSubmitVo getToOrderSubmitVo(String orderToken, String accessToken, Long addressId) {

        OrderSubmitVo orderSubmitVo = new OrderSubmitVo();
        //验证令牌
        verifyOrderToken(orderToken, orderSubmitVo);

        //获取会员信息
        Member member = memberComponent.getMemberByToken(accessToken);
        //获取会员购物车信息
        Cart cart = cartService.getCartBySelected(member.getId());
        //收货地址
        MemberReceiveAddress address = addressService.getById(addressId);

        //自动生成唯一id
        String orderSn = IdWorker.getTimeId();
        orderSubmitVo.setOrderSn(orderSn);
        //获取提交订单页数据
        getOrderSubmitVo(orderSubmitVo, cart, address);

        //保存订单信息
        Order order = getOrder(orderSn, member, address, cart.getTotalPrice());
        orderMapper.insert(order);

        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem cartItem : cartItems) {
            //保存订单商品信息
            OrderItem orderItem = getOrderItem(cartItem, orderSn, order.getId());
            orderItemMapper.insert(orderItem);
        }
        //删除redis缓存中下单商品
        String userCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();
        RMap<String, String> map = redissonClient.getMap(userCartKey);
        map.forEach((key, value) -> {
            CartItem cartItem = JSON.parseObject(value, CartItem.class);
            if(cartItem.getSelected()){
                map.remove(key);
            }
        });

        return orderSubmitVo;
    }

    @Override
    public String payOrder(String orderSn, String accessToken) {

        String result = null;
        //验证用户登录令牌
        Member member = memberComponent.getMemberByToken(accessToken);
        if(member != null){
            Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_sn", orderSn));

            List<OrderItem> orderItems = orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_sn", orderSn));
            StringBuffer body = new StringBuffer();   //相比String，线程安全的
            for (OrderItem orderItem : orderItems) {
                body.append(orderItem.getProductName()).append("<br/>");
            }
            //支付
            result = payOrder(orderSn, order.getPayAmount().toString(), "[谷粒商城]-梁海滔结算界面", body.toString());
        }

        return result;
    }

    @Override
    public void payToAsync(Map<String, String> params){

        boolean signVerified = true;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset,
                    AlipayConfig.sign_type);
            System.out.println("验签：" + signVerified);

        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
        }
        // 商户订单号
        String out_trade_no = params.get("out_trade_no");
        // 支付宝流水号
        String trade_no = params.get("trade_no");
        // 交易状态
        String trade_status = params.get("trade_status");
        if (trade_status.equals("TRADE_FINISHED")) {
            log.debug("订单【{}】,已经完成...不能再退款。数据库都改了", trade_no);
        } else if (trade_status.equals("TRADE_SUCCESS")) {
            //修改订单状态
            Order order = new Order();
            order.setPayType(OrderStatusEnume.PAYED.getCode());
            orderMapper.update(order, new UpdateWrapper<Order>().eq("order_sn", out_trade_no));
            //发送消息，通知商品库存更新
            List<OrderItem> orderItems = orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_sn", out_trade_no));
            for (OrderItem item : orderItems) {
                jmsMessagingTemplate.convertAndSend(queue, JSON.toJSONString(item));
            }

            log.debug("订单【{}】,已经支付成功...可以退款。数据库都改了", out_trade_no);
        }
    }

    //获取提交订单页数据
    private void getOrderSubmitVo(OrderSubmitVo orderSubmitVo, Cart cart, MemberReceiveAddress address) {

        List<String> productNames = new ArrayList<>();
        if(cart.getCartItems() != null){
            cart.getCartItems().forEach((item) -> {
                //获取购物项商品名称 + 数量
                productNames.add(item.getName() + "  * " + item.getCount());
                //验证购物车商品价格和数据库最新价格是否一致，true则不一致
                Boolean verify = verifyPrice(item.getId(), item.getPrice());
                if(verify){
                    throw new RuntimeException("购物车商品已过期！");
                }
            });
        }
        orderSubmitVo.setProductNames(productNames);
        orderSubmitVo.setTotalPrice(cart.getTotalPrice());

        //收货地址
        String receiveAddress = address.getProvince() + address.getCity() + address.getRegion() + address.getDetailAddress();
        orderSubmitVo.setReceiveAddress(receiveAddress);
        orderSubmitVo.setPhoneNumber(address.getPhoneNumber());
        orderSubmitVo.setReceiveName(address.getName());
    }

    //验证令牌
    public void verifyOrderToken(String orderToken, OrderSubmitVo orderSubmitVo) {

        //验证令牌
        if(StringUtils.isEmpty(orderToken)){
            orderSubmitVo.setError("订单令牌错误！");
        }

        //令牌规则：token + "_" + 系统当前时间 + "_" +  固定时间
        String[] strings = orderToken.split("_");
        if(strings.length != 3){
            orderSubmitVo.setError("订单令牌错误！");
        }

        //创建令牌时间、过期时间、当前时间
        long createTime = Long.parseLong(strings[1]);
        long timeout = Long.parseLong(strings[2]);
        long currentTime = System.currentTimeMillis();
        if(createTime + timeout > currentTime){
            orderSubmitVo.setError("订单响应超时！");
        }

        //令牌删除和防止重复提交
        Long remove = redisTemplate.opsForSet().remove(OrderConstant.ORDER_TOKEN, orderToken);
        //判断redis是否有对应令牌
        if(remove == null || remove == 0){
            orderSubmitVo.setError("订单令牌错误或订单重复提交！");
        }
    }

    //获取订单商品信息
    private OrderItem getOrderItem(CartItem item, String orderSn, Long orderId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setOrderSn(orderSn);
        orderItem.setProductId(item.getProductId());
        orderItem.setProductPic(item.getPic());
        orderItem.setProductName(item.getName());
        orderItem.setProductQuantity(item.getCount());
        orderItem.setProductPrice(item.getPrice());
        orderItem.setProductSkuId(item.getId());
        orderItem.setProductSkuCode(item.getSkuCode());
        orderItem.setSp1(item.getSp1());
        orderItem.setSp2(item.getSp2());
        orderItem.setSp3(item.getSp3());

        EsProduct esProduct = productService.productInfoEsSkuById(item.getId());
        orderItem.setProductBrand(esProduct.getBrandName());
        orderItem.setProductCategoryId(esProduct.getProductCategoryId());
        List<EsProductSkuInfo> skuInfoList = esProduct.getSkuInfoList();
        for (EsProductSkuInfo esProductSkuInfo : skuInfoList) {
            //判断商品所有销售属性中，skuId对应的销售属性
            if(esProductSkuInfo.getId().equals(item.getId())){
                orderItem.setProductAttr(JSON.toJSONString(esProductSkuInfo.getAttrValueList()));
            }
        }

        return orderItem;
    }

    //验证购物车商品价格和数据库最新价格是否一致,true则不一致
    private Boolean verifyPrice(Long id, BigDecimal price) {
        //获取数据库最新价格
        SkuStock skuStock = skuStockService.getById(id);
        int compareTo = skuStock.getPrice().compareTo(price);
        return compareTo != 0;
    }

    //获取订单信息
    private Order getOrder(String orderSn ,Member member, MemberReceiveAddress address, BigDecimal totalPrice) {
        Order order = new Order();
        order.setOrderSn(orderSn);
        order.setMemberId(member.getId());
        order.setMemberUsername(member.getUsername());
        order.setCreateTime(new Date());
        order.setAutoConfirmDay(7);
        //订单金额
        order.setTotalAmount(totalPrice);
        order.setPayAmount(totalPrice);
        order.setFreightAmount(new BigDecimal("0"));
        order.setStatus(OrderStatusEnume.UNPAY.getCode());
        order.setOrderType(0);
        order.setAutoConfirmDay(7);
        //收货人信息
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverDetailAddress(address.getDetailAddress());
        order.setReceiverRegion(address.getRegion());
        return order;
    }

    // 支付
    private String payOrder(String out_trade_no, String total_amount, String subject, String body) {
        // 1、创建支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id,
                AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key,
                AlipayConfig.sign_type);

        // 2、创建一次支付请求
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        // 商户订单号，商户网站订单系统中唯一订单号，必填
        // 付款金额，必填
        // 订单名称，必填
        // 商品描述，可空

        // 3、构造支付请求数据
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"total_amount\":\"" + total_amount
                + "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = "";
        try {
            // 4、请求
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;// 支付跳转页的代码

    }

}
