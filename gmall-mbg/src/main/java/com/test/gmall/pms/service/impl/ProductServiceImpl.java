package com.test.gmall.pms.service.impl;

import com.test.gmall.pms.entity.Product;
import com.test.gmall.pms.mapper.ProductMapper;
import com.test.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
