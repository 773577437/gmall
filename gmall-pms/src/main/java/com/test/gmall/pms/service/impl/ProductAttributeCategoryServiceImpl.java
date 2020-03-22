package com.test.gmall.pms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.pms.entity.ProductAttributeCategory;
import com.test.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.test.gmall.pms.service.ProductAttributeCategoryService;
import com.test.gmall.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@com.alibaba.dubbo.config.annotation.Service
@Service
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {

    @Autowired
    private ProductAttributeCategoryMapper categoryMapper;

    @Override
    public PageInfoVo getAttributeInfo(Long pageNum, Long pageSize) {

        IPage<ProductAttributeCategory> page = categoryMapper.selectPage(new Page<ProductAttributeCategory>(pageNum, pageSize), null);
        PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(), page.getPages(), pageSize, page.getRecords(), page.getCurrent());
        return pageInfoVo;
    }
}
