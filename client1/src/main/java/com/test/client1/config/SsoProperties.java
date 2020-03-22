package com.test.client1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("sso.server")
public class SsoProperties {

    private String url;
    private String loginPath;


}
