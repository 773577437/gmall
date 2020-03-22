package com.test.gmall.pms.mapper;

import com.test.gmall.pms.entity.ProductAttribute;
import com.test.gmall.pms.entity.ProductAttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.gmall.to.es.EsProductAttributeValue;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 存储产品参数信息的表 Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Repository
public interface ProductAttributeValueMapper extends BaseMapper<ProductAttributeValue> {

    /**
     * 查询商品公共属性的信息
     * @param id
     */
    List<EsProductAttributeValue> selectProductBaseAttrAndValue(Long id);

    /**
     * 查询商品的销售属性名称
     * @param id
     * @return
     */
    List<ProductAttribute> selectProductSaleAttrName(Long id);
}
