package com.test.gmall.pms.service.impl;

import com.test.gmall.pms.entity.ProductFullReduction;
import com.test.gmall.pms.mapper.ProductFullReductionMapper;
import com.test.gmall.pms.service.ProductFullReductionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品满减表(只针对同商品) 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class ProductFullReductionServiceImpl extends ServiceImpl<ProductFullReductionMapper, ProductFullReduction> implements ProductFullReductionService {

}
