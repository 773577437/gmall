package com.test.sso;

import org.assertj.core.internal.DigestDiff;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class GmallSsoWebApplicationTests {

    /**
     * 测试加密
     */
    @Test
    void contextLoads() {
        String s = DigestUtils.md5DigestAsHex("123".getBytes());
        System.out.println(s);
    }

}
