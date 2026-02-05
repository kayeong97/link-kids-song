package com.linkkids.song.controller.user;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.linkkids.song.common.model.user.UserModel;

@Mapper
public interface UserMapper {
    
    // ID로 사용자 정보 가져오기
    public UserModel getUserById(@Param("id") String id);

    // 이메일로 사용자 정보 가져오기
    public UserModel getUserByEmail(@Param("email") String email);

    // 세션 ID 설정
    public int setSessionId(HashMap<String, Object> params);

    // 회원가입
    public int insertUser(UserModel user);
}
