package com.test.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/login")
    public String login(@RequestParam(value = "redirect_url", required = false) String redirect_url, HttpSession session,
                        @CookieValue(value = "sso_user",required = false) String userCookie,
                        @RequestParam(value = "exit",required = false) String exit, HttpServletResponse response){
        //判断是否是退出重定向到认证中心
        if(!StringUtils.isEmpty(exit)){
            //去除cookie
            Cookie cookie = new Cookie("sso_user", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            //去除redis
            redisTemplate.delete(userCookie);
            session.setAttribute("redirect_url", redirect_url);
            return "login";
        }

        //判断用户是否有登录
        if(!StringUtils.isEmpty(userCookie)){
            String url = redirect_url + "?token=" + userCookie;
            return "redirect:" + url;
        }else{
            session.setAttribute("redirect_url", redirect_url);
            return "login";
        }
    }

    @PostMapping("/doLogin")
    public String doLogin(String userName, String password, HttpSession session , HttpServletResponse response)
            throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("username", userName);
        map.put("password",password);
        String redirect_url = (String) session.getAttribute("redirect_url");
        //登录验证
        if(userName.contains("admin") && password.contains("123456")){
            System.out.println(userName + password + redirect_url );

            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(token, map);
            //添加cookie
            response.addCookie(new Cookie("sso_user", token));
            String url = redirect_url + "?" + "token=" + token;
//            response.sendRedirect(url);
            return "redirect:" + url;
        }else{
            return "login";
        }
    }
}
