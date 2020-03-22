package com.test.gmall.pms.service;

import com.test.gmall.pms.entity.ProductAttribute;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.gmall.vo.PageInfoVo;

/**
 * <p>
 * 商品属性参数表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
public interface ProductAttributeService extends IService<ProductAttribute> {

    PageInfoVo getProductAttributeInfo(Long cid,Integer type, Long pageNum, Long pageSize);
}
