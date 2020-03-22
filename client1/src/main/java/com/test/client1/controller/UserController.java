package com.test.client1.controller;

import com.test.client1.config.SsoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private SsoProperties ssoProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(value = "token",required = false) String ssoCookie,
                        @CookieValue(value = "sso_user", required = false) String userCookie) throws IOException {

        //判断之前是否认证中心登录后重定向过来的且redis缓存中是否有对应key值
        if(!StringUtils.isEmpty(ssoCookie) && redisTemplate.hasKey(ssoCookie)){
            //添加cookie
            response.addCookie(new Cookie("sso_user", ssoCookie));
            Map<String,String> userJson = (Map<String, String>) redisTemplate.opsForValue().get(ssoCookie);
            model.addAttribute("username1",userJson.get("username"));
            model.addAttribute("ssoUrl",getSsoUrl(request)+ "&exit=true");
            return "index";
        }

        //判断是否登录
        if(StringUtils.isEmpty(userCookie)){
            //重定向认证中心
            return "redirect:" + getSsoUrl(request);
        }

        //判断redis是否有缓存，有则返回首页
        if(redisTemplate.hasKey(userCookie)){
            Map<String,String> userJson = (Map<String, String>) redisTemplate.opsForValue().get(userCookie);
            model.addAttribute("username1",userJson.get("username"));
            model.addAttribute("ssoUrl",getSsoUrl(request) + "&exit=true");
            return "index";
        }else{  //否则删除cookie必能返回认证中心
            Cookie cookie = new Cookie("sso_user", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "redirect:" + getSsoUrl(request);
        }
    }

    //重定向认证中心
    public String getSsoUrl(HttpServletRequest request) {
        //当前的action绝对路径
        StringBuffer requestURL = request.getRequestURL();
        String ssoUrl = ssoProperties.getUrl() + ssoProperties.getLoginPath() + "?redirect_url=" + requestURL;
        return ssoUrl;
    }
}

