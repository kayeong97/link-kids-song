package com.linkkids.song.common.handler;

import java.io.IOException;

// 로그인 실패 처리 핸들러
import org.springframework.context.annotation.Configuration; // @Configuration
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler; // spring에서 제공하는 로그인 실패 핸들러

//
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest; // 클라이언트 요청 객체
import jakarta.servlet.http.HttpServletResponse; // 서버 응답 객체

@Configuration
public class LoginFailureHandler implements AuthenticationFailureHandler {
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        response.sendRedirect("/?error=true");
    }
}
