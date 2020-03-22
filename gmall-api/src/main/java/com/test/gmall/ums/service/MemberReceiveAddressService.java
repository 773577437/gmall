package com.test.gmall.ums.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.test.gmall.ums.entity.MemberReceiveAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 会员收货地址表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddress> {

    /**
     * 根据会员id获取会员收货地址信息
     * @param id
     * @return
     */
    List<MemberReceiveAddress> getMemberReceiveAddressList(Long id);
}
