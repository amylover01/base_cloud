package com.amylover.security.news.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 功能描述：TODO//
 *
 * @Title: JwtConstant
 * @Author: zhangbin
 * @Date: 2020/6/29
 */
@Configuration
@EnableConfigurationProperties(JwtConstant.class)
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConstant {

    private String tokenHeaderKey; //token响应头Key
    private String tokenPrefix; //token前缀
    private String tokenSecret; //token秘钥
    private Long tokenExpiration; //token过期时间
}
