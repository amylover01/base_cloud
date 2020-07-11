package com.amylover.common.utils;

import com.amylover.common.config.RSAProperties;
import com.amylover.common.utils.bean.PlayLoad;
import io.jsonwebtoken.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static com.sun.xml.internal.ws.util.xml.XMLReaderComposite.State.Payload;

/**
 * 功能描述：TODO//
 *
 * @Title: JwtUtils
 * @Author: zhangbin
 * @Date: 2020/6/25
 */
@Component
public class JwtUtils {
    @Autowired
    private RSAProperties rsaProperties;
    private String JWT_PLAYLOAD_USER_KEY = "USER";


    /**
     * 功能描述： TODO//私钥加密token
     *
     * @param userInfo 用户信息
     * @param expire   过期时间 秒
     * @Return: java.lang.String
     * @Author: zhangbin
     * @Data: 2020/6/25
     */
    public <T> String generateTokenExpireInSeconds(T userInfo, int expire) {
        try {
            return Jwts.builder().claim(JWT_PLAYLOAD_USER_KEY, JsonUtils.toString(userInfo))
                    .setId(createJTI())
                    .setExpiration(DateTime.now().plusMinutes(expire).toDate())
                    .signWith(RSAUtils.getPrivateKey(rsaProperties.getPrivateKey()), SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createJTI() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }

    public Claims parseToken(String token) {
        //  return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJwt(token).getBody();
        try {
            return Jwts.parser().setSigningKey(RSAUtils.getPublicKey(rsaProperties.getPublicKey())).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> PlayLoad<T> getInfoFromToken(String token, Class<T> tClass) {
        Claims body = parseToken(token);
        PlayLoad<T> playLoad = new PlayLoad<>();
        playLoad.setId(body.getId());
        playLoad.setData(JsonUtils.toBean(body.get(JWT_PLAYLOAD_USER_KEY).toString(), tClass));
        playLoad.setExpireTime(body.getExpiration());
        return playLoad;
    }

    public <T> PlayLoad<T> getInfoFromToken(String token) {
        Claims body = parseToken(token);
        PlayLoad<T> claims = new PlayLoad<>();
        claims.setId(body.getId());
        claims.setExpireTime(body.getExpiration());
        return claims;
    }

    public void main(String[] args) throws Exception {
        Map<String, String> map = RSAUtils.initKey();
        String pu = map.get("pu");
        String pr = map.get("pr");
        //PublicKey publicKey = RSAUtils.getPublicKey(publicKey1);
        //PrivateKey privateKey = RSAUtils.getPrivateKey(privateKey1);
        //String expireInSeconds = generateTokenExpireInSeconds("123", privateKey, 200000);
        //Claims claims = parseToken(expireInSeconds, publicKey);
        //System.out.println("123");
    }

}
