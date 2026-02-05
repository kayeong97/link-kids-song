package com.linkkids.song.dto.user;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String id;
    private String password;
    private String email;
    private String username;
    private String birthday;
    private String gender;
    private String phoneNumber;
}
