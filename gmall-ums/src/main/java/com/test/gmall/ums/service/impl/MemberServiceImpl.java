package com.test.gmall.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.ums.entity.Member;
import com.test.gmall.ums.mapper.MemberMapper;
import com.test.gmall.ums.service.MemberService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

}
