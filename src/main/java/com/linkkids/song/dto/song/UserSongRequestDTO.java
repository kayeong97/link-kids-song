package com.linkkids.song.dto.song;

import lombok.Data;

@Data
public class UserSongRequestDTO {
    
    private Long userSeq; // 사용자 아이디

    private String title; // 제목
    private String subject; // 주제
    private String mood; // 분위기
    private String category; // 카테고리
    private String gender; // 아이 목소리 성별
    private String lyrics; // 가사
    private String run_n_segments; // 총 길이
    private int verseNo; // 절 수
    private Long coverMediaId; // 커버 이미지 미디어 ID
}
