package com.test.gmall.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.oms.entity.Order;
import com.test.gmall.oms.mapper.OrderMapper;
import com.test.gmall.oms.service.OrderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
