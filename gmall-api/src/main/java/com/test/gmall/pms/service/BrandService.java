package com.test.gmall.pms.service;

import com.test.gmall.pms.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.gmall.vo.PageInfoVo;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
public interface BrandService extends IService<Brand> {

    PageInfoVo getBrandInfo(String keyword, Long pageNum, Long pageSize);

    Brand getBrandById(Long id);
}
