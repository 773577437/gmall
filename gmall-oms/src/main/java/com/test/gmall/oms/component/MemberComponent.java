package com.test.gmall.oms.component;

import com.alibaba.fastjson.JSON;
import com.test.gmall.constant.SysCacheConstant;
import com.test.gmall.ums.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MemberComponent {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据令牌获取用户信息
     * @param accessToken
     * @return
     */
    public Member getMemberByToken(String accessToken){

        String userJson = redisTemplate.opsForValue().get(SysCacheConstant.LOGIN_TOKEN_PREFIX + accessToken);
        Member member = JSON.parseObject(userJson, Member.class);
        return  member;
    }
}
