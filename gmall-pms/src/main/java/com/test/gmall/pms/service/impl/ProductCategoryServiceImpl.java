package com.test.gmall.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.constant.SysCacheConstant;
import com.test.gmall.pms.entity.ProductCategory;
import com.test.gmall.pms.mapper.ProductCategoryMapper;
import com.test.gmall.pms.service.ProductCategoryService;
import com.test.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Slf4j
@com.alibaba.dubbo.config.annotation.Service
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public List<PmsProductCategoryWithChildrenItem> listCategoryWithChildren(Integer i) {

        List<PmsProductCategoryWithChildrenItem> list = null;
        Object cache = redisTemplate.opsForValue().get(SysCacheConstant.CATEGORY_MENU_CACHE_KEY);
        /*判断redis缓存中是否有该对象*/
        if(cache != null){
            log.debug("从redis缓存中读取列表数据");
            list = (List<PmsProductCategoryWithChildrenItem>) cache;
        }else{
            list = productCategoryMapper.listCategoryWithChildren(i);
            /*将常用的数据放入分布式缓存redis中*/
            redisTemplate.opsForValue().set(SysCacheConstant.CATEGORY_MENU_CACHE_KEY,list);
        }
        return list;
    }
}
