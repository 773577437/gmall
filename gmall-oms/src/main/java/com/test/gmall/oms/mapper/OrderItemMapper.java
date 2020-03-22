package com.test.gmall.oms.mapper;

import com.test.gmall.oms.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单中所包含的商品 Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Repository
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}
