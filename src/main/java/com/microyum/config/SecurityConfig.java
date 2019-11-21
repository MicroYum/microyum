package com.microyum.config;

import com.microyum.filter.JSONLoginFilter;
import com.microyum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    private static String[] antPatterns = new String[]{"/", "/favicon.ico", "/index", "/wealth", "/message", "/album", "/about",
            "/list/**/blog", "/list/article/type", "/blog/**/detail", "/blog/image/**/**", "/album/**/detail",
            "/user/captcha", "/user/login", "/stock/**", "/stock/**/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(antPatterns).permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/management/login").permitAll()
                .and().logout().permitAll()
                .and().csrf().disable()
                .headers().frameOptions().sameOrigin();

        http.addFilterAt(customJSONLoginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 自定义认证过滤器
     */
    private JSONLoginFilter customJSONLoginFilter() {
        JSONLoginFilter loginFilter = new JSONLoginFilter("/user/login", userService);
        loginFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler());
        loginFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler());
        return loginFilter;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/static/**");
    }
}
