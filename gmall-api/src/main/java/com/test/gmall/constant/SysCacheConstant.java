package com.test.gmall.constant;

/**
 * 系统中使用的常量
 */
public class SysCacheConstant {

    /*系统菜单 */
    public static final String CATEGORY_MENU_CACHE_KEY = "sys_category";

    /*登录令牌前缀；规则：login：member：token={userObj}*/
    public static final String LOGIN_TOKEN_PREFIX = "login:member:";
    /*令牌过期时间*/
    public static final Long LOGIN_TOKEN_TIMEOUT = 30L;
}
