package com.linkkids.song.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 인증되지 않은 사용자가 로그인이 필요한 곳에 접근하려고 할 때 필요한 함수
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        
        System.out.println(authException.getMessage());
        
        String uri = request.getRequestURI();

        if (uri.matches(".*/process/.*")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            PrintWriter writer = response.getWriter();
            writer.write("{\"error\": \"Unauthorized\", \"message\": \" 로그인이 필요합니다. \"}");
            writer.flush();
        } else {
            response.sendRedirect("/");
        }

    }
    
}
