package com.linkkids.song.dto.song;

import java.time.LocalDateTime;

import lombok.Data;

// 노래 목록 DTO
@Data
public class SongListDTO {
    private Long songId; // 노래 ID
    private String title; // 노래 제목
    
    // Media 경로
    private String coverMediaUrl; // 커버 이미지 저장 경로
    private String fullAudioUrl; // full 오디오 경로
    private String vocalAudioUrl; // vocal 오디오 경로
    private String instAudioUrl; // inst 오디오 경로
    
    private String status; // 노래 상태
    private LocalDateTime createdAt; // 노래 생성일시
    private String jobId; // 외부 API 작업 ID
}
