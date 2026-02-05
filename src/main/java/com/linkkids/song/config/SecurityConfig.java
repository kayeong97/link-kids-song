package com.linkkids.song.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.linkkids.song.common.handler.LoginFailureHandler;
import com.linkkids.song.common.handler.LoginSuccessHandler;
import com.linkkids.song.security.JsonAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Autowired
    private JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

    // Bean : 스프링에서 관리하게 되는 객체 @Controller, @Service, @Repository 등과 유사
    // Order : 우선순위

    @Bean
    @Order(1)
    public SecurityFilterChain filterCain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())

                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests

                        // 자료 접근 관련
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // 페이지 접근 관련
                        .requestMatchers("/signup").permitAll()
                        .requestMatchers("/home").authenticated()
                        .requestMatchers("/song/**").authenticated()
                        .requestMatchers("/media/**").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/video/**").authenticated()

                        // login/out
                        .requestMatchers("/errorPage").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/").permitAll()

                        .requestMatchers("/user/**").permitAll()

                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .defaultSuccessUrl("/home", true))
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/"))
                .exceptionHandling(handling -> handling.accessDeniedPage("/")
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint));

        return http.build();
    }

    // 비밀번호 해싱 빈 등록
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 세션 조회 빈 등록
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // 세션 이벤트 리스너 빈 등록
    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    // 인증 관리 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
