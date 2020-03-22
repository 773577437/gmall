package com.test.gmall.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.ums.entity.Member;
import com.test.gmall.ums.mapper.MemberMapper;
import com.test.gmall.ums.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@com.alibaba.dubbo.config.annotation.Service
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public Member toLogin(String username, String password) {
        /*密码加密*/
        String digest = DigestUtils.md5DigestAsHex(password.getBytes());
        QueryWrapper<Member> wrapper = new QueryWrapper<Member>().eq("username", username).eq("password", digest);
        Member member = memberMapper.selectOne(wrapper);

        return member;
    }
}
