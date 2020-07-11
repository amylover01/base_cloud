package com.amylover.common.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：TODO//
 *
 * @Title: RSAProperties
 * @Author: zhangbin
 * @Date: 2020/6/27
 */
@Configuration
@ConfigurationProperties(prefix = "rsa.key", ignoreUnknownFields = true)
public class RSAProperties {
    private String privateKey;
    private String publicKey;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
