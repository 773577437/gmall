package com.test.sso.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.test.gmall.constant.SysCacheConstant;
import com.test.gmall.to.CommonResult;
import com.test.gmall.ums.entity.Member;
import com.test.gmall.ums.service.MemberService;
import com.test.gmall.vo.SsoLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class SsoLoginController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Reference
    private MemberService memberService;

//    @GetMapping("/login")
//    public String login(@RequestParam(value = "redirect_url", required = false) String redirect_url, HttpSession session,
//                        @CookieValue(value = "sso_user",required = false) String userCookie,
//                        @RequestParam(value = "exit",required = false) String exit, HttpServletResponse response){
//        //判断是否是退出重定向到认证中心
//        if(!StringUtils.isEmpty(exit)){
//            //去除cookie
//            Cookie cookie = new Cookie("sso_user", null);
//            cookie.setMaxAge(0);
//            response.addCookie(cookie);
//            //去除redis
//            redisTemplate.delete(userCookie);
//            session.setAttribute("redirect_url", redirect_url);
//            return "login";
//        }
//
//        //判断用户是否有登录
//        if(!StringUtils.isEmpty(userCookie)){
//            String url = redirect_url + "?token=" + userCookie;
//            return "redirect:" + url;
//        }else{
//            session.setAttribute("redirect_url", redirect_url);
//            return "login";
//        }
//    }

    @PostMapping("/doLogin")
    public Object doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password){

        Member member = memberService.toLogin(username, password);
        //登录成功
        if(member != null){
            SsoLoginVo loginVo = new SsoLoginVo();
            BeanUtils.copyProperties(member, loginVo);
            String token = UUID.randomUUID().toString().replace("-","");
            loginVo.setAccessToken(token);

//            redisTemplate.opsForValue().set(SysCacheConstant.LOGIN_TOKEN_PREFIX + token, JSON.toJSONString(member),
//                    SysCacheConstant.LOGIN_TOKEN_TIMEOUT, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(SysCacheConstant.LOGIN_TOKEN_PREFIX + token, JSON.toJSONString(member));
            return new CommonResult().success(loginVo);
        }else{ //登录失败
            CommonResult result = new CommonResult().failed();
            result.setMessage("账号密码不匹配，请重新输入");
            return result;
        }
    }

    @GetMapping("/userInfo")
    public Object userInfo(@RequestParam("accessToken") String accessToken){

        String redisKey = SysCacheConstant.LOGIN_TOKEN_PREFIX + accessToken;
        //从redis获取令牌对应的member对象
        String memberJson =  redisTemplate.opsForValue().get(redisKey);
        //把json数据解析成对象
        Member member = JSON.parseObject(memberJson, Member.class);
        //将关键信息过滤
        member.setId(null);
        member.setPassword(null);
        return new CommonResult().success(member);
    }

}
