package com.linkkids.song.common.handler;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.linkkids.song.common.model.user.UserModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration 
public class LoginSuccessHandler implements AuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        System.out.println("성공");
        HttpSession session = request.getSession();
        
        String userId = request.getParameter("name");
        UserModel user = authentication.getPrincipal() instanceof UserModel
            ? (UserModel) authentication.getPrincipal()
            : null;
        String sessionId = session.getId();

        if (user != null) {
            System.out.println("성공");
            response.sendRedirect("/home");
            return;
        }

        response.sendRedirect("/?error=true");
    }

}
