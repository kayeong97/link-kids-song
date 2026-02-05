package com.linkkids.song.common.model.song;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SongModel {
    public enum SongStatus {
        COMPLETED, READY, FAILED
    }

    private Long songId; // 노래 ID
    private Long userSeq; // 사용자 ID
    private String jobId; // 외부 API 작업 ID
    private String title; // 제목
    private String subject; // 주제
    private String mood; // 분위기
    private String vocalGender; // 보컬 성별
    private String audioLength; // 오디오 길이
    private String categories; // 카테고리
    private String lyrics; // 가사
    private Long coverMediaId; // 커버 이미지
    private Long fullAudioMediaId; // mix 오디오
    private Long vocalAudioMediaId; // 보컬만 있는 오디오
    private Long instAudioMediaId; // 반주만 있는 오디오
    private SongStatus status; // 노래 상태 (생성중/준비(queue)/실패)
    private LocalDateTime createdAt; // 생성 일시
}
