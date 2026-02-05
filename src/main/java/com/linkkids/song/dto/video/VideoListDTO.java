package com.linkkids.song.dto.video;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VideoListDTO {
    private Long lipsyncId; // 립싱크 ID
    private String title; // 영상 제목

    // Media 경로
    private String inputImageMediaUrl; // 입력 이미지(썸네일) URL

    private String outputVideoMediaId; // 비디오 ID
    private LocalDateTime createdAt; // 비디오 생성일시
}
