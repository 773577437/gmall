package com.test.gmall.oms.service.impl;

import com.test.gmall.oms.entity.CartItem;
import com.test.gmall.oms.mapper.CartItemMapper;
import com.test.gmall.oms.service.CartItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {

}
