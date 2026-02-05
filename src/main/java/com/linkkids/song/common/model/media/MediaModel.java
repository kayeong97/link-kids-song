package com.linkkids.song.common.model.media;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MediaModel {
    public enum MediaType {
        IMAGE, AUDIO, VIDEO
    }

    // 노래, 이미지, 비디오 저장
    private Long songId; // 노래 ID
    private Long mediaId; // 미디어 ID
    private Long userSeq; // 사용자 ID
    private MediaType type; // 미디어 타입(노래, 이미지, 비디오)
    private String url; // 저장된 위치
    private String mimeType; // 저장 타입
    private Long sizeBytes; // 용량
    private LocalDateTime createdAt; // 생성일시
}