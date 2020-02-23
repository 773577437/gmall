package com.test.gmall.cms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.cms.entity.HelpCategory;
import com.test.gmall.cms.mapper.HelpCategoryMapper;
import com.test.gmall.cms.service.HelpCategoryService;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 帮助分类表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
@Component
public class HelpCategoryServiceImpl extends ServiceImpl<HelpCategoryMapper, HelpCategory> implements HelpCategoryService {

}
