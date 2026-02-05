package com.linkkids.song.dto.video;

import lombok.Data;

@Data
public class UserVideoRequestDTO {
    String title; // 제목
    Long inputImageMediaId; // 업로드 이미지 (아이디)
    Long songId; // 배경 노래 (아이디)
}
