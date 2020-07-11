package com.amylover.security.news.config;

import com.amylover.security.news.service.UserService;
import com.amylover.security.news.util.GsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述：TODO//
 *
 * @Title: JwtLoginFilter
 * @Author: zhangbin
 * @Date: 2020/6/29
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {


    private SelfUserDetailsService selfUserDetailsService;
    private UserService userService;
    private JwtConstant jwtConstant;

    public JwtLoginFilter(AuthenticationManager authenticationManager,
                          SelfUserDetailsService selfUserDetailsService,
                          UserService userService,
                          JwtConstant jwtConstant) {
        this.selfUserDetailsService = selfUserDetailsService;
        this.userService = userService;
        this.jwtConstant = jwtConstant;
        setAuthenticationManager(authenticationManager);
    }

    /**
     * 功能描述： TODO//
     *
     * @param req
     * @param res
     * @param chain
     * @Return: void
     * @Author: zhangbin
     * @Data: 2020/6/29
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        super.doFilter(req, res, chain);
    }

    @Override
    public void setPostOnly(boolean postOnly) {
        super.setPostOnly(postOnly);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            SelfUserDetails selfUserDetails;
            try {
                selfUserDetails = new ObjectMapper().readValue(request.getInputStream(), SelfUserDetails.class);
            } catch (IOException e) {
                throw new AuthenticationServiceException("login params not match: " + request.getMethod());
            }
            String username = selfUserDetails.getUsername();
            String password = selfUserDetails.getPassword();
            username = username.trim();
            UserDetails userInfo = selfUserDetailsService.loadUserByUsername(username);

            boolean matches = new BCryptPasswordEncoder().matches(password, userInfo.getPassword()); //校验用户名密码
            if (!matches) {
                throw new BadCredentialsException("The password is incorrect!!");

            }
            return new UsernamePasswordAuthenticationToken(username, userInfo.getPassword(), userInfo.getAuthorities());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        UrlResponse response = new UrlResponse();
        response.setSuccess(true);
        response.setCode("200");
        response.setMessage("Login Success!");

        String username = (String) authResult.getPrincipal(); //表单输入的用户名
        Map<String, Object> userInfo = userService.findMenuInfoByUsername(username, response); //用户可访问的菜单信息
        response.setData(userInfo);

        // 生成token并设置响应头
        Claims claims = Jwts.claims();
        claims.put("role", authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username) //设置用户名
                .setExpiration(new Date(System.currentTimeMillis() + jwtConstant.getTokenExpiration())) //设置token过期时间
                .signWith(SignatureAlgorithm.HS512, jwtConstant.getTokenSecret()).compact(); //设置token签名算法及秘钥
        httpServletResponse.addHeader(jwtConstant.getTokenHeaderKey(), jwtConstant.getTokenPrefix() + " " + token); //设置token响应头

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("text/html;charset=UTF-8");
        httpServletResponse.getWriter().write(GsonUtil.GSON.toJson(response));
    }

    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse, AuthenticationException failed) throws IOException, ServletException {
        UrlResponse response = new UrlResponse();
        response.setSuccess(false);
        response.setCode("402");
        response.setMessage(failed.getMessage());
        response.setData(null);

        httpServletResponse.setStatus(402);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("text/html;charset=UTF-8");
        httpServletResponse.getWriter().write(GsonUtil.GSON.toJson(response));
    }

    public SelfUserDetailsService getSelfUserDetailsService() {
        return selfUserDetailsService;
    }

    public void setSelfUserDetailsService(SelfUserDetailsService selfUserDetailsService) {
        this.selfUserDetailsService = selfUserDetailsService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public JwtConstant getJwtConstant() {
        return jwtConstant;
    }

    public void setJwtConstant(JwtConstant jwtConstant) {
        this.jwtConstant = jwtConstant;
    }
}
