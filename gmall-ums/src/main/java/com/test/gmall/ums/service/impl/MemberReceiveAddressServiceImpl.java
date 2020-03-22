package com.test.gmall.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.ums.entity.MemberReceiveAddress;
import com.test.gmall.ums.mapper.MemberReceiveAddressMapper;
import com.test.gmall.ums.service.MemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 会员收货地址表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
@com.alibaba.dubbo.config.annotation.Service
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressMapper, MemberReceiveAddress> implements MemberReceiveAddressService {

    @Autowired
    private MemberReceiveAddressMapper addressMapper;

    @Override
    public List<MemberReceiveAddress> getMemberReceiveAddressList(Long id) {
        return addressMapper.selectList(new QueryWrapper<MemberReceiveAddress>().eq("member_id", id));
    }
}
