package com.amylover.security.news.config;

import com.amylover.security.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Andon
 * @date 2019/12/13
 * <p>
 * 登录拦截全局配置
 */
@Configuration
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConstant jwtConstant;
    @Autowired
    private UserService userService;

    @Resource
    private SelfUserDetailsService selfUserDetailsService;

    @Resource
    private UrlAuthenticationEntryPoint authenticationEntryPoint; //自定义未登录时：返回状态码401


    @Resource
    private UrlAccessDeniedHandler accessDeniedHandler; //自定义权限不足处理器：返回状态码403

    @Resource
    private UrlLogoutSuccessHandler logoutSuccessHandler; //自定义注销成功处理器：返回状态码200


    @Resource
    private SelfFilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource; //动态获取url权限配置

    @Resource
    private SelfAccessDecisionManager accessDecisionManager; //自定义权限判断管理器


    @Resource
    private JwtAuthorizationTokenFilter authorizationTokenFilter; //JwtToken解析并生成authentication身份信息过滤器

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/common/**"); //无条件允许访问
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {

        auth.authenticationProvider(new JwtAuthenticationProvider(selfUserDetailsService)); //自定义登录认证
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 关闭csrf验证(防止跨站请求伪造攻击)
        http.csrf().disable();

        // JwtToken解析并生成authentication身份信息过滤器
        http.addFilterBefore(authorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 未登录时：返回状态码401
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        // 无权访问时：返回状态码403
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);

        // url权限认证处理
        http.antMatcher("/**").authorizeRequests()
//                .antMatchers("/security/user/**").hasRole("ADMIN") //需要ADMIN角色才可以访问
//                .antMatchers("/connect").hasIpAddress("127.0.0.1") //只有ip[127.0.0.1]可以访问'/connect'接口
                .anyRequest() //其他任何请求
                .authenticated() //都需要身份认证
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setSecurityMetadataSource(filterInvocationSecurityMetadataSource); //动态获取url权限配置
                        o.setAccessDecisionManager(accessDecisionManager); //权限判断
                        return o;
                    }
                });

        // 将session策略设置为无状态的,通过token进行登录认证
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 开启自动配置的登录功能

        // 开启自动配置的注销功能
        http.logout() //用户注销, 清空session
                .logoutUrl("/nonceLogout") //自定义注销请求路径
//                .logoutSuccessUrl("/bye") //注销成功后的url(前后端不分离)
                .logoutSuccessHandler(logoutSuccessHandler); //注销成功处理器(前后端分离)：返回状态码200
        http.addFilterBefore(new JwtLoginFilter(authenticationManager(), selfUserDetailsService, userService, jwtConstant), UsernamePasswordAuthenticationFilter.class);
    }

}
