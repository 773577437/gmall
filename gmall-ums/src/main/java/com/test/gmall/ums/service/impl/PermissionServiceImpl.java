package com.test.gmall.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.ums.entity.Permission;
import com.test.gmall.ums.mapper.PermissionMapper;
import com.test.gmall.ums.service.PermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户权限表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
