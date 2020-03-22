package com.test.gmall.search;

import com.test.gmall.vo.SearchParam;
import com.test.gmall.vo.SearchResponse;

/**
 * es商品信息检索服务
 */
public interface SearchProductService {

    SearchResponse getSearchResponse(SearchParam searchParam);
}
