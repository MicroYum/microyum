package com.microyum.filter;

import com.microyum.model.common.MyUser;
import com.microyum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JSONLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final UserService userService;

    public JSONLoginFilter(String defaultFilterProcessesUrl, UserService userService) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl, HttpMethod.POST.name()));
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        validateUsernameAndPassword(request);
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority("USER"));

        MyUser myUser = userService.getUserByName(request.getParameter("userName"));
        return new UsernamePasswordAuthenticationToken(myUser, request.getParameter("password"), simpleGrantedAuthorities);
    }

    private void validateUsernameAndPassword(HttpServletRequest request) throws AuthenticationException {
        String username = request.getParameter("userName");
        String password = request.getParameter("password");
        String captcha = request.getParameter("captcha");

        HttpSession session = request.getSession();
        if (!StringUtils.equals(session.getAttribute("captcha").toString(), captcha)) {
            throw new AuthenticationServiceException("captcha not match.");
        }

        boolean result = userService.checkUserLogin(username, password);
        if (!result) {
            throw new UsernameNotFoundException("username password error.");
        }
    }
}
