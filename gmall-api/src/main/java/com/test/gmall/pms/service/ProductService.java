package com.test.gmall.pms.service;

import com.test.gmall.pms.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.gmall.pms.vo.PmsProductParam;
import com.test.gmall.pms.vo.PmsProductQueryParam;
import com.test.gmall.to.es.EsProduct;
import com.test.gmall.vo.PageInfoVo;

import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
public interface ProductService extends IService<Product> {

    /**
     * TODO 根据查询条件返回数据
     * @param productQueryParam
     * @return
     */
    PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam);

    /**
     * TODO 添加保存商品所有信息
     * @param productParam
     */
    void SaveProduct(PmsProductParam productParam);

    /**
     * TODO 批量上下架
     * @param ids
     * @param publishStatus
     */
    void updatePublishStatus(List<Long> ids, Integer publishStatus);

    /**
     * TODO 根据ID获取商品详情
     * @param id
     * @return
     */
    Product productInfo(Long id);

    /**
     * TODO 在es中根据ID获取商品详情
     * @param id
     * @return
     */
    EsProduct productInfoEsById(Long id);

    /**
     * TODO 在es中根据商品sku的ID获取商品详情
     * @param id
     * @return
     */
    EsProduct productInfoEsSkuById(Long id);
}
