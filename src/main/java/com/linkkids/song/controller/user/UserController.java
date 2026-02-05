package com.linkkids.song.controller.user;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.core.JacksonException;

import com.linkkids.song.common.model.user.UserModel;
import com.linkkids.song.dto.user.SignupRequestDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class UserController {
    @Autowired
    UserService userService;

    // 로그인 페이지
    @GetMapping("/")
    public ModelAndView login(@RequestParam(value="duplicated", required = false) String duplicated,
            @RequestParam(value="error", required = false) String error,
            Authentication authentication) {
                // duplicate : 중복 로그인
                // error : 로그인 실패
        ModelAndView model = new ModelAndView();
        model.setViewName("login");

        if (authentication != null && authentication.isAuthenticated()) {
            model.setViewName("redirect:/home");
            return model;
        }
        else if (duplicated != null && duplicated.equals("true")) {
            model.addObject("duplicated", true);
        }
        else if (error != null && error.equals("true")) {
            model.addObject("error", true);
        }

        return model;
    }
    
    // 로그인 처리
    @PostMapping("/user/login")
    public ResponseEntity<Boolean> login(@RequestParam HashMap<String, Object> params, HttpServletRequest request) 
            throws JacksonException {
        boolean res = false;

        String id = (String) params.get("id");
        String password = (String) params.get("password");

        UserModel dbUser = userService.loadUserById(id);
        
        if (dbUser != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean passwordMatch = passwordEncoder.matches(password, dbUser.getPassword());

            if (passwordMatch) {
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(dbUser, null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 세션 ID 설정
                HttpSession session = request.getSession(true);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
                        , SecurityContextHolder.getContext());
                
                res = true;
            }
        }

        return ResponseEntity.ok().body(res);
    }

    // 회원가입 페이지
    @GetMapping("/signup")
    public ModelAndView signup(){
        ModelAndView model = new ModelAndView();
        model.setViewName("signup");
        return model;
    }

    // 회원가입 처리
    @PostMapping("/user/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO signupRequest) 
            throws JacksonException {
        
        // 입력 체크
        if (signupRequest.getId() == null || signupRequest.getId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID는 필수입니다.");
        }
        if (signupRequest.getPassword() == null || signupRequest.getPassword().length() < 8 || signupRequest.getPassword().length() > 20) {
            return ResponseEntity.badRequest().body("비밀번호는 8자 이상 20자 이하이어야 합니다.");
        }
        if (signupRequest.getEmail() == null || !signupRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body("올바른 이메일 형식이 아닙니다.");
        }
        
        // ID 중복 체크
        UserModel existingUser = userService.loadUserById(signupRequest.getId());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("이미 사용 중인 ID입니다.");
        }

        existingUser = userService.loadUserByEmail(signupRequest.getEmail());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
        }
        
        // 비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        signupRequest.setPassword(encodedPassword);

        // 회원가입 처리
        try {
            int insertCount = userService.registerUser(signupRequest);
            if (insertCount > 0) {
                return ResponseEntity.ok().body("success");
            }
            return ResponseEntity.status(500).body("회원가입에 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // ID 중복 체크
    @GetMapping("/user/signup/id-dup-check")
    public ResponseEntity<String> dupCheckId(@RequestParam String id) throws JacksonException {
        UserModel existingUser = userService.loadUserById(id);
        if(existingUser != null) {
            return ResponseEntity.ok().body("duplicated");
        } else {
            return ResponseEntity.ok().body("available");
        }
    }
    
    // 이메일 중복 체크
    @GetMapping("/user/signup/email-dup-check")
    public ResponseEntity<String> dupCheckEmail(@RequestParam String email) {
        UserModel existingUser = userService.loadUserByEmail(email);
        if (existingUser != null) {
            return ResponseEntity.ok().body("duplicated");
        } else {
            return ResponseEntity.ok().body("available");
        }
    }
}