package com.test.gmall.pms.mapper;

import com.test.gmall.pms.entity.ProductFullReduction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 产品满减表(只针对同商品) Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Repository
public interface ProductFullReductionMapper extends BaseMapper<ProductFullReduction> {

}
