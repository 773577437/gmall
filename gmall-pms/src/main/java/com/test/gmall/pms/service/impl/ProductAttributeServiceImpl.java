package com.test.gmall.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.pms.entity.ProductAttribute;
import com.test.gmall.pms.mapper.ProductAttributeMapper;
import com.test.gmall.pms.service.ProductAttributeService;
import com.test.gmall.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品属性参数表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@com.alibaba.dubbo.config.annotation.Service
@Service
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {

    @Autowired
    private  ProductAttributeMapper attributeMapper;

    @Override
    public PageInfoVo getProductAttributeInfo(Long cid,Integer type, Long pageNum, Long pageSize) {

        QueryWrapper<ProductAttribute> wrapper = new QueryWrapper<ProductAttribute>()
                .eq("product_attribute_category_id",cid)
                .eq("type",type);

        IPage<ProductAttribute> page = attributeMapper.selectPage(new Page<ProductAttribute>(pageNum, pageSize), wrapper);
        PageInfoVo pageInfoVo = PageInfoVo.pageInfoVo(page, pageSize);
        return pageInfoVo;
    }
}
