package com.linkkids.song.controller.user;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linkkids.song.common.model.user.UserModel;
import com.linkkids.song.dto.user.SignupRequestDTO;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;

    // ID로 사용자 정보 가져오기
    public UserModel loadUserById(String userId) {
        UserModel user = userMapper.getUserById(userId);
        return user;
    }

    // 이메일로 사용자 정보 가져오기
    public UserModel loadUserByEmail(String email) {
        UserModel user = userMapper.getUserByEmail(email);
        return user;
    }

    // 세선 ID 설정
    public int setSessionId(Long userSeq, String sessionId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userSeq", userSeq);
        params.put("sessionId", sessionId);
        return userMapper.setSessionId(params);
    }

    // 회원가입
    public int registerUser(SignupRequestDTO signupUser) {
        UserModel user = new UserModel();

        user.setId(signupUser.getId());
        user.setPassword(signupUser.getPassword());
        user.setEmail(signupUser.getEmail());
        user.setUsername(signupUser.getUsername());
        user.setGender(signupUser.getGender());
        user.setBirthday(signupUser.getBirthday());
        user.setPhoneNumber(signupUser.getPhoneNumber());

        user.setStatus("ACTIVE"); // 기본 상태 설정
        user.setRole("USER"); // 기본 역할 설정
        user.setCredit(0L); // 초기 크레딧 설정

        user.setRegDate(java.time.LocalDateTime.now()); // 가입 날짜 설정

        return userMapper.insertUser(user);
    }
}
