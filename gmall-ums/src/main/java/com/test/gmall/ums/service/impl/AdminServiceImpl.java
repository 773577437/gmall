package com.test.gmall.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.ums.entity.Admin;
import com.test.gmall.ums.mapper.AdminMapper;
import com.test.gmall.ums.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@com.alibaba.dubbo.config.annotation.Service
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    /**
     * 验证用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public Admin login(String username, String password) {
        //转化为加密密码
        String s = DigestUtils.md5DigestAsHex(password.getBytes());

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<Admin>().eq("username", username).eq("password", s);

        Admin admin = adminMapper.selectOne(queryWrapper);

        return admin;
    }

    /**
     * 获取用户详情
     * @param userName
     * @return
     */
    @Override
    public Admin getUserInfo(String userName) {
        /**
         * 自动生成的数据库操作对象带有泛型，远程调用报错
         */
        Admin admin = getOne(new QueryWrapper<Admin>().eq("username", userName));
        return admin;
    }
}
