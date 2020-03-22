package com.test.gmall.vo;

import lombok.Data;

/**
 * 登录返回信息
 */
@Data
public class SsoLoginVo {

    /*会员等级*/
    private Long memberLevelId;
    /*用户名*/
    private  String username;
    /*呢称*/
    private String nickname;
    /*头像*/
    private String icon;
    /*手机号码*/
    private String phone;
    /*令牌*/
    private String accessToken;
}
