package com.linkkids.song.common.model.user;

import java.time.LocalDateTime;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.Data;

@Data
public class UserModel {

    private Long userSeq; // 사용자 일련 번호

    private String id; // 아이디
    private String password; // 비밀번호

    private String email; // 이메일
    private String username; // 이름
    private String birthday; // 생년월일
    private String gender; // 성별
    private String phoneNumber; // 전화번호
    
    private LocalDateTime regDate; // 가입 날짜
    private String status; // 상태 (활성/비활성)
    private String role; // 역할 (사용자/관리자)

    private Long credit; // 크레딧 보유량

    private String sessionId; // 세션 ID



    // 현재 인증된 사용자 정보 가져오기
    public static UserModel getCurrentUser(Boolean isNullCheck) {
        if (isNullCheck &&
            (SecurityContextHolder.getContext() == null ||
             SecurityContextHolder.getContext().getAuthentication() == null ||
             !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserModel))) {
                return null;
             }
             return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
