package com.microyum.config;

import com.alibaba.fastjson.JSON;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 认证失败处理器
 * @author syaka.hong
 */
public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        httpServletResponse.getWriter().write(JSON.toJSONString(new BaseResponseDTO(HttpStatus.UNAUTHORIZED, "login failure")));
    }

}