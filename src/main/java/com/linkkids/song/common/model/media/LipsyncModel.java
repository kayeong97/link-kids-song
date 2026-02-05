package com.linkkids.song.common.model.media;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LipsyncModel {
    public enum LipsyncStatus {
        COMPLETED, READY, FAILED
    }

    private Long lipsyncId; // 립싱크 ID
    private Long userSeq; // 사용자 ID
    private String title; // 제목
    private Long sourceSongId; // 배경 노래 ID
    private Long inputImageMediaId; // 입력 이미지 미디어 ID
    private Long outputVideoMediaId; // 출력 비디오 미디어 ID
    private LipsyncStatus status; // 립싱크 상태 (완료/준비(queue)/실패)
    private LocalDateTime createdAt; // 생성 일시
}