package com.test.gmall.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.pms.entity.Brand;
import com.test.gmall.pms.mapper.BrandMapper;
import com.test.gmall.pms.service.BrandService;
import com.test.gmall.vo.PageInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Slf4j
@com.alibaba.dubbo.config.annotation.Service
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageInfoVo getBrandInfo(String keyword, Long pageNum, Long pageSize) {

        QueryWrapper<Brand> name = null;
        if(!StringUtils.isEmpty(keyword)){
            name = new QueryWrapper<Brand>().like("name", keyword);
        }

        IPage<Brand> page = brandMapper.selectPage(new Page<Brand>(pageNum, pageSize), name);

        PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(), page.getPages(), pageNum, page.getRecords(), page.getCurrent());
        return pageInfoVo;
    }

    @Override
    public Brand getBrandById(Long id) {
        return brandMapper.selectById(id);
    }
}
