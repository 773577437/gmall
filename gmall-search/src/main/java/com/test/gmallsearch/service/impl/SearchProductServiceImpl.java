package com.test.gmallsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.test.gmall.constant.EsConstant;
import com.test.gmall.search.SearchProductService;
import com.test.gmall.to.es.EsProduct;
import com.test.gmall.vo.SearchParam;
import com.test.gmall.vo.SearchResponse;
import com.test.gmall.vo.SearchResponseAttrVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@com.alibaba.dubbo.config.annotation.Service
@Service
public class SearchProductServiceImpl implements SearchProductService {

    @Autowired
    private JestClient jestClient;

    @Override
    public SearchResponse getSearchResponse(SearchParam searchParam) {

        /*1、构建检索条件*/
        String dsl = BuildDsl(searchParam);
        System.out.println(dsl);
        Search build = new Search.Builder(dsl)
                .addIndex(EsConstant.PRODUCT_ES_INDEX)
                .addType(EsConstant.PRODUCT_INFO_ES_TYPE)
                .build();
        SearchResponse searchResponse = null;
        try {
            /*2、执行检索*/
            SearchResult execute = jestClient.execute(build);

            /*3、将返回的结果转为SearchResponse对象*/
           searchResponse = buildSearchResponse(execute);

        } catch (IOException e) {
            e.printStackTrace();
        }

        searchResponse.setPageNum(searchParam.getPageNum());
        searchResponse.setPageSize(searchParam.getPageSize());

        return searchResponse;
    }

    private SearchResponse buildSearchResponse(SearchResult execute) {
        SearchResponse searchResponse = new SearchResponse();
        /*1、获取聚合查询结果*/
        MetricAggregation aggregations = execute.getAggregations();

        /*1、1、获取品牌聚合结果*/
        TermsAggregation brand_id_agg = aggregations.getTermsAggregation("brand_id_agg");
        List<String> brandValueList = getValueList(brand_id_agg,"brand_name_agg");
        SearchResponseAttrVo brandVo = new SearchResponseAttrVo();
        brandVo.setName("品牌");
        brandVo.setValue(brandValueList);
        searchResponse.setBrand(brandVo);

        /*1、2、获取分类聚合结果*/
        TermsAggregation category_id_agg = aggregations.getTermsAggregation("Category_id_agg");
        List<String> categoryValueList = getValueList(category_id_agg, "Category_name_agg");
        SearchResponseAttrVo categoryVo = new SearchResponseAttrVo();
        categoryVo.setName("分类");
        categoryVo.setValue(categoryValueList);
        searchResponse.setCatelog(categoryVo);

        /*1、3获取过滤属性聚合结果*/
        List<SearchResponseAttrVo> attrs = new ArrayList<SearchResponseAttrVo>();
        List<TermsAggregation.Entry> buckets = aggregations.getChildrenAggregation("attr_nested")
                .getTermsAggregation("attr_id_agg").getBuckets();
        buckets.forEach((bucket)->{
            SearchResponseAttrVo attrVo = new SearchResponseAttrVo();
            String idString = bucket.getKeyAsString();
            attrVo.setProductAttributeId(Long.parseLong(idString));
            String nameAgg = bucket.getTermsAggregation("attr_name_agg").getBuckets().get(0).getKeyAsString();
            attrVo.setName(nameAgg);
            List<TermsAggregation.Entry> valueAggs = bucket.getTermsAggregation("attr_value_agg").getBuckets();
            List<String> values = new ArrayList<String>();
            valueAggs.forEach((value)->{
                values.add(value.getKeyAsString());
            });
            attrVo.setValue(values);
            attrs.add(attrVo);
        });
        searchResponse.setAttrs(attrs);
        searchResponse.setTotal(execute.getTotal());

        /*2、获取检索商品信息结果*/
        List<EsProduct> products = new ArrayList<EsProduct>();
        List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
        hits.forEach((hit)->{
            EsProduct esProduct = hit.source;
            /*提取高亮标题,设置给商品名*/
            String title = hit.highlight.get("skuInfoList.skuTitle").get(0);
            esProduct.setName(title);
            products.add(esProduct);
        });
        searchResponse.setProducts(products);
        return searchResponse;
    }

    /*提取buckets下的key值*/
    private List<String> getValueList(TermsAggregation attr_id_agg, String nameAgg) {
        List<TermsAggregation.Entry> buckets = attr_id_agg.getBuckets();
        List<String> valueList = new ArrayList<String>();
        buckets.forEach((bucket)->{
//            String idString = bucket.getKeyAsString();
            String nameString = bucket.getTermsAggregation(nameAgg).getBuckets().get(0).getKeyAsString();
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("id", idString);
//            map.put("name", nameString);
//            String jsonString = JSON.toJSONString(map);
            valueList.add(nameString);
        });
        return valueList;
    }

    /*构建es语句*/
    private String BuildDsl(SearchParam searchParam) {

        SearchSourceBuilder builder = new SearchSourceBuilder();
        /*1、查询*/
        /*1）、构建bool*/
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        /*2）、构建must*/
        List<QueryBuilder> must = boolQueryBuilder.must();
        /*1、1、检索*/
        if(!StringUtils.isEmpty(searchParam.getKeyword())){   //判断检索关键字是否为空
            /*构建match*/
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("skuInfoList.skuTitle", searchParam.getKeyword());
            /*构建nested*/
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("skuInfoList", matchQuery, ScoreMode.None);
            must.add(nestedQuery);
        }

        /*1、2、过滤*/
        /*1、2、2、按照三级分类条件检索*/
        if(searchParam.getCatelog3() != null && searchParam.getCatelog3().length > 0){  //判断三级分类数组不为空
            /*构建filter-terms*/
            boolQueryBuilder.filter(QueryBuilders.termsQuery("productCategoryName.keyword", searchParam.getCatelog3()));
        }
        /*1、2、3、按照品牌条件检索*/
        if(searchParam.getBrand() != null && searchParam.getBrand().length > 0){  //判断品牌数组不为空
            /*构建filter-terms*/
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandName.keyword", searchParam.getBrand()));
        }
        /*1、2、3、按照spu属性添加过滤*/
        if(searchParam.getProps() != null && searchParam.getProps().length >0 ){  //判断spu属性数组不为空
            String[] props = searchParam.getProps();
            /*遍历数组，并把每个值按照规则拆分*/
            for(String prop : props){
                /*规则：属性id：值-值*/
                String[] strings = prop.split(":");
                /*2）、构建bool*/
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                /*3)、构建must*/
                List<QueryBuilder> must1 = query.must();
                    must1.add(QueryBuilders.matchQuery("attrValueList.productAttributeId", strings[0]));
                    must1.add(QueryBuilders.termsQuery("attrValueList.value.keyword", strings[1].split("-")));
                /*1)、构建nested*/
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrValueList", query, ScoreMode.None);
                boolQueryBuilder.filter(nestedQuery);
            }
        }
        /*1、2、4、按照价格区间过滤*/
        if(searchParam.getPriceFrom() != null || searchParam.getPriceTo() != null){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            if(searchParam.getPriceFrom() != null){
                rangeQuery.gte(searchParam.getPriceFrom());
            }
            if(searchParam.getPriceTo() != null){
                rangeQuery.lte(searchParam.getPriceTo());
            }
            boolQueryBuilder.filter(rangeQuery);
        }
        builder.query(boolQueryBuilder);

        /*2、聚合*/
        /*2、1、按照品牌聚合,获取品牌名和ID*/
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_id_agg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName.keyword"));
        builder.aggregation(brandAgg);
        /*2、2、按照分类聚合，获取分类名和ID*/
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("Category_id_agg").field("productCategoryId")
                .subAggregation(AggregationBuilders.terms("Category_name_agg").field("productCategoryName.keyword"));
        builder.aggregation(categoryAgg);
        /*2、3、按照属性聚合,获取属性名、属性值、ID*/
        /*ID聚合套属性名子聚合和属性值子聚合*/
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrValueList.productAttributeId")
                .subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrValueList.name.keyword"))
                .subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrValueList.value.keyword"));
        /*内嵌聚合套ID子聚合*/
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_nested", "attrValueList")
                .subAggregation(attrIdAgg);
        builder.aggregation(attrAgg);
        /*3、高亮*/
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder()
                    /*高亮字段*/
                    .field("skuInfoList.skuTitle")
                    /*格式开头*/
                    .preTags("<b style='color:red'>")
                    /*格式结尾*/
                    .postTags("</b>");
            builder.highlighter(highlightBuilder);
        }
        /*4、排序*/
        /*当有定义排序规则时，默认是综合排序*/
        if(!StringUtils.isEmpty(searchParam.getOrder())){
            /*规则：代号：asc/desc ；代号：0：综合排序  1：销量  2：价格*/
            String[] strings = searchParam.getOrder().split(":");
            /*综合排序默认*/
            if(strings[0].equals("0")){}
            /*销量排序*/
            if(strings[0].equals("1")){
                FieldSortBuilder sale = SortBuilders.fieldSort("sale");
                if(strings[1].equalsIgnoreCase("asc")){
                    sale.order(SortOrder.ASC);
                }
                if(strings[1].equalsIgnoreCase("desc")){
                    sale.order(SortOrder.DESC);
                }
                builder.sort(sale);
            }
            /*价格排序*/
            if(strings[0].equals("2")){
                FieldSortBuilder price = SortBuilders.fieldSort("price");
                if(strings[1].equalsIgnoreCase("asc")){
                    price.order(SortOrder.ASC);
                }
                if(strings[1].equalsIgnoreCase("desc")){
                    price.order(SortOrder.DESC);
                }
                builder.sort(price);
            }
        }
        /*5、分页*/
        builder.from((searchParam.getPageNum() - 1) * searchParam.getPageSize());
        builder.size(searchParam.getPageSize());

        String toString = builder.toString();
        return toString;
    }
}
