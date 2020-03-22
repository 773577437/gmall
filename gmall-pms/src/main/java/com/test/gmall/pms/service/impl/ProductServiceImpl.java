package com.test.gmall.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.constant.EsConstant;
import com.test.gmall.pms.entity.*;
import com.test.gmall.pms.mapper.*;
import com.test.gmall.pms.vo.PmsProductParam;
import com.test.gmall.pms.vo.PmsProductQueryParam;
import com.test.gmall.pms.service.ProductService;
import com.test.gmall.to.es.EsProduct;
import com.test.gmall.to.es.EsProductAttributeValue;
import com.test.gmall.to.es.EsProductSkuInfo;
import com.test.gmall.vo.PageInfoVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Slf4j
@com.alibaba.dubbo.config.annotation.Service
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    /*商品基本信息表操作对象*/
    @Autowired
    private ProductMapper productMapper;

    /*商品会员信息表操作对象*/
    @Autowired
    private MemberPriceMapper memberPriceMapper;

    /*商品参数信息表操作对象*/
    @Autowired
    private ProductAttributeValueMapper productAttributeValueMapper;

    /*产品满减信息表操作对象*/
    @Autowired
    private ProductFullReductionMapper productFullReductionMapper;

    /*产品阶梯价格信息表操作对象*/
    @Autowired
    private ProductLadderMapper productLadderMapper;

    /*sku的库存信息表操作对象*/
    @Autowired
    private SkuStockMapper skuStockMapper;

    /*es操作对象*/
    @Autowired
    private JestClient jestClient;

    /*当前线程共享同样的数据 product_id，类似map*/
    ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    @Override
    public PageInfoVo productPageInfo(PmsProductQueryParam param) {

        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(param.getKeyword())){
            wrapper.like("name", param.getKeyword());
        }
        if(!StringUtils.isEmpty(param.getProductSn())){
            wrapper.like("product_sn", param.getProductSn());
        }
        if (param.getProductCategoryId() != null){
            wrapper.eq("product_attribute_category_id" ,param.getProductCategoryId());
        }
        if(param.getBrandId() != null){
            wrapper.eq("brand_id", param.getBrandId());
        }
        if(param.getPublishStatus() != null){
            wrapper.eq("publish_status", param.getPublishStatus());
        }
        if(param.getVerifyStatus() != null){
            wrapper.eq("verify_status", param.getVerifyStatus());
        }

        IPage<Product> page = productMapper.selectPage(new Page<Product>(param.getPageNum(), param.getPageSize()),wrapper);

        PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(), page.getPages(), param.getPageSize(), page.getRecords(), page.getCurrent());
        return pageInfoVo;
    }

    /**
     * 事务的传播行为：外部事务存在时，内部方法事务不同级别的反应
     * ·REQUIRED：必须有事务，之前没有就创建一个事务
     * ·SUPPORTS：无论之前有没有事务，都创建新事务，之前的事务暂停
     *  MANDATORY：之前有事务，就事务运行，没有页一样运行
     *  REQUIRES_NEW：必须有事务，之前没有事务，则抛异常
     *  NOT_SUPPORTED：不支持事务内运行，之前有事务，则挂起之前事务
     *  NEVER：不支持在事务内运行，之前有事务，则抛异常
     *  NESTED：开启子事务（mysql不支持）
     * @param productParam
     */
    /*大保存*/
    @Transactional(propagation = Propagation.REQUIRED)  //添加事务
    @Override
    public void SaveProduct(PmsProductParam productParam) {
        /*获取当前类代理对象,达到事务控制的目的*/
        ProductServiceImpl proxy = (ProductServiceImpl) AopContext.currentProxy();

        /*保存 pms_product 商品基本信息表*/
        proxy.saveProduct(productParam);

        /*保存 pms_sku_stock sku的库存信息表*/
        proxy.saveSkuStock(productParam);

        /*捕捉异常，表示当前被调方法出现异常不影响下面方法的运行*/
        try {
            /*保存 pms_member_price 商品会员信息表*/
            proxy.saveMemberPrice(productParam);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            /*保存 pms_product_attribute_value 商品参数信息表*/
            proxy.saveProductAttributeValue(productParam);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            /*保存 pms_product_full_reduction 产品满减信息表*/
            proxy.saveProductFullReduction(productParam);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            /*保存 pms_product_ladder 产品阶梯价格信息表*/
            proxy.saveProductLadder(productParam);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* TODO 商品上架并将商品信息数据整理保存到es模型,下架则删除*/
    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        if(publishStatus == 0){
            /*下架操作*/
            for(Long id : ids){
                /*1、修改数据库中商品的状态*/
                setProductPublishStatus(publishStatus, id);
                /*删除es商品数据*/
                deleteProductToEs(id);
            }
        }else {
            /*上架操作*/
            for (Long id : ids) {
                /*1、修改数据库中商品的状态*/
                setProductPublishStatus(publishStatus, id);
                /*2、保存es的商品数据*/
                saveProductToEs(id);
            }
        }
    }

    public void deleteProductToEs(Long id) {
        Delete build = new Delete.Builder(id.toString())
                .index(EsConstant.PRODUCT_ES_INDEX)
                .type(EsConstant.PRODUCT_INFO_ES_TYPE)
                .build();
        try {
            DocumentResult execute = jestClient.execute(build);
            if(execute.isSucceeded()){
                log.info("es {}商品数据删除成功", id);
            }else{
                log.error("es {}商品数据未删除,重试中。。。。；", id);
//                deleteProductToEs(id);
            }
        } catch (IOException e) {
            log.error("es {}商品数据删除异常：{}" , id, e.getMessage());
//            deleteProductToEs(id);
        }
    }

    /*保存es的商品数据，并查询该商品的sku一起保存*/
    public void saveProductToEs(Long id) {
        /*1）、复制商品基本信息*/
        EsProduct esProduct = new EsProduct();
        /*将商品详情信息复制到es商品信息对象中*/
        Product productInfo = productInfo(id);
        BeanUtils.copyProperties(productInfo, esProduct);

        /*2）、复制sku信息*/
        /*这里就把List<SkuStock>对象的信息复制给List<EsProductSkuInfo>对象，并添加一些其他属性*/
        List<SkuStock> skuStocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", id));
        /*商品所有的sku信息*/
        List<EsProductSkuInfo> esProductSkuInfoList = new ArrayList<>(skuStocks.size());
        /*遍历该商品的每个sku的属性值*/
        for(SkuStock skuStock : skuStocks){
            EsProductSkuInfo esProductSkuInfo = new EsProductSkuInfo();
            /*复制给es商品sku详情对象*/
            BeanUtils.copyProperties(skuStock, esProductSkuInfo);
            /*设置sku的标题,商品名 + 销售属性尺寸、颜色等*/
            String skuTitle = esProduct.getName();
            if(!StringUtils.isEmpty(skuStock.getSp1())){
                skuTitle += " " + skuStock.getSp1();
            }
            if(!StringUtils.isEmpty(skuStock.getSp2())){
                skuTitle += " " + skuStock.getSp2();
            }
            if(!StringUtils.isEmpty(skuStock.getSp3())){
                skuTitle +=" " +  skuStock.getSp3();
            }
            esProductSkuInfo.setSkuTitle(skuTitle);

            /*获取销售属性名称*/
            List<ProductAttribute> saleAttrName = productAttributeValueMapper.selectProductSaleAttrName(id);
            /*设置es销售属性信息对象*/
            List<EsProductAttributeValue> esProductAttributeValueList = new ArrayList<>(saleAttrName.size());
            /*将获取销售属性名称对象复制给es销售属性信息对象，并添加值*/
            for(int i = 0; i < saleAttrName.size(); i++){
                EsProductAttributeValue esAttributeValue = new EsProductAttributeValue();

                esAttributeValue.setName(saleAttrName.get(i).getName());
                esAttributeValue.setProductAttributeId(saleAttrName.get(i).getId());
                esAttributeValue.setProductId(id);
                esAttributeValue.setType(saleAttrName.get(i).getType());

                if(i == 0){
                    esAttributeValue.setValue(skuStock.getSp1());
                }
                if(i == 1){
                    esAttributeValue.setValue(skuStock.getSp2());
                }
                if(i == 2){
                    esAttributeValue.setValue(skuStock.getSp3());
                }
                esProductAttributeValueList.add(esAttributeValue);
            }
            esProductSkuInfo.setAttrValueList(esProductAttributeValueList);
            esProductSkuInfoList.add(esProductSkuInfo);
        }
        esProduct.setSkuInfoList(esProductSkuInfoList);

        /*3)、复制公共属性的信息*/
        List<EsProductAttributeValue> esProductAttributeValue = productAttributeValueMapper.selectProductBaseAttrAndValue(id);
        esProduct.setAttrValueList(esProductAttributeValue);
        /*保存es操作*/
        Index build = new Index.Builder(esProduct)
                .index(EsConstant.PRODUCT_ES_INDEX)
                .type(EsConstant.PRODUCT_INFO_ES_TYPE)
                .id(id.toString())
                .build();
        try {
            /*执行*/
            DocumentResult execute = jestClient.execute(build);
            if(execute.isSucceeded()){
                log.info("es {}商品数据保存成功", id);
            }else{
                log.error("es {}商品数据未保存,重试中。。。。；", id);
//                saveProductToEs(id);
            }
        } catch (IOException e) {
            log.error("es {}商品数据保存异常：{}" , id, e.getMessage());
//            saveProductToEs(id);
        }
    }

    /*修改数据库中商品的状态*/
    public void setProductPublishStatus(Integer publishStatus, Long id) {
        Product product = new Product().setId(id).setPublishStatus(publishStatus);
        /*mybatis plus 自带的更新方法是，哪个属性有值就修改那个字段值*/
        productMapper.updateById(product);
    }

    @Override
    public Product productInfo(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public EsProduct productInfoEsById(Long id) {
        /*获取es检索语句*/
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("id",id));

        return getProduct(sourceBuilder);
    }

    @Override
    public EsProduct productInfoEsSkuById(Long id) {
        /*获取es检索语句*/
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("skuInfoList",
                QueryBuilders.termQuery("skuInfoList.id", id), ScoreMode.None);
        sourceBuilder.query(nestedQuery);
        return getProduct(sourceBuilder);
    }

    /*根据es语句查询商品详情*/
    public EsProduct getProduct( SearchSourceBuilder sourceBuilder) {
        EsProduct esProduct = null;
        Search build = new Search.Builder(sourceBuilder.toString())
                .addIndex(EsConstant.PRODUCT_ES_INDEX)
                .addType(EsConstant.PRODUCT_INFO_ES_TYPE).build();
        try {
            SearchResult result = jestClient.execute(build);
            List<SearchResult.Hit<EsProduct, Void>> hits = result.getHits(EsProduct.class);
            if(hits != null){
                esProduct = hits.get(0).source;
            }
        } catch (IOException e) {

        }
        return esProduct;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSkuStock(PmsProductParam productParam) {
        List<SkuStock> skuStockList = productParam.getSkuStockList();
        for(int i = 1; i <= skuStockList.size(); i++){
            SkuStock skuStock = skuStockList.get(i - 1);
            skuStock.setProductId(threadLocal.get());
            /*判断当前端没有sku编码数据时，自动生成sku编码，生成格式：ProductID_i*/
            if(StringUtils.isEmpty(skuStock.getSkuCode())){
                skuStock.setSkuCode(threadLocal.get() + "_" + i);
            }
        skuStockMapper.insert(skuStock);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductLadder(PmsProductParam productParam) {
        List<ProductLadder> ladderList = productParam.getProductLadderList();
        for(ProductLadder ladder : ladderList){
            ladder.setProductId(threadLocal.get());
            productLadderMapper.insert(ladder);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductFullReduction(PmsProductParam productParam) {
        List<ProductFullReduction> reductionList = productParam.getProductFullReductionList();
        for(ProductFullReduction reduction : reductionList){
            reduction.setProductId(threadLocal.get());
            productFullReductionMapper.insert(reduction);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductAttributeValue(PmsProductParam productParam) {
        List<ProductAttributeValue> valueList = productParam.getProductAttributeValueList();
        for(ProductAttributeValue value : valueList){
            value.setProductId(threadLocal.get());
            productAttributeValueMapper.insert(value);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMemberPrice(PmsProductParam productParam) {
        List<MemberPrice> memberPriceList = productParam.getMemberPriceList();
        for(MemberPrice memberPrice : memberPriceList){
            memberPrice.setProductId(threadLocal.get());
            memberPriceMapper.insert(memberPrice);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProduct(PmsProductParam productParam) {
        Product product = new Product();
        BeanUtils.copyProperties(productParam, product);  //复制功能工具方法，将一个参数对象的数据，复制给第二个参数对象
        productMapper.insert(product);
        /*向共享线程中存入值*/
        threadLocal.set(product.getId());
    }
}
