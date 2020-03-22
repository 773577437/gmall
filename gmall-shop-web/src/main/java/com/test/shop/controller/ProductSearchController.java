package com.test.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.test.gmall.pms.entity.Comment;
import com.test.gmall.search.SearchProductService;
import com.test.gmall.to.CommonResult;
import com.test.gmall.vo.SearchParam;
import com.test.gmall.vo.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "检索功能")
@CrossOrigin
@RestController
public class ProductSearchController {

    @Reference
    private SearchProductService searchProductService;

    /**
     * 商品检索
     * @param searchParam
     * @return
     */
    @ApiOperation("商品检索")
    @GetMapping("/search")
    public Object searchResponse(SearchParam searchParam){
        SearchResponse searchResponse = searchProductService.getSearchResponse(searchParam);
        return searchResponse;
    }
}
