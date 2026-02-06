package com.linkkids.song.controller.media;

import org.apache.ibatis.annotations.Mapper;

import com.linkkids.song.common.model.media.MediaModel;

@Mapper
public interface MediaMapper {

    // 미디어 저장
    void saveMedia(MediaModel mediaModel);

    // 미디어 ID로 조회
    MediaModel getMediaById(Long mediaId);

    // 미디어 경로 조회
    String getMediaPathById(Long mediaId);

    // 미디어 삭제
    void deleteMedia(Long mediaId);

    // 미디어 권한 확인
    Long checkUserMediaAccess(Long userSeq, Long mediaId);
}