package com.test.gmall.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.oms.entity.OrderItem;
import com.test.gmall.oms.mapper.OrderItemMapper;
import com.test.gmall.oms.service.OrderItemService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单中所包含的商品 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

}
