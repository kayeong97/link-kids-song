package com.linkkids.song.controller.lipsyncVideo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.linkkids.song.dto.song.SongListDTO;
import com.linkkids.song.dto.video.VideoListDTO;

@Mapper
public interface LipsyncMapper {
    // 미디어 아이디 가져오기
    Long getFullMediaId(Long songId);

    Long getVocalMediaId(Long songId);

    // 동영상 저장
    void saveLipsync(Map<String, Object> params);

    // 사용자 노래 리스트 조회
    List<SongListDTO> getUserSongs(Long userSeq);

    // mediaId로 파일 경로 조회
    String getVideoFilePath(Long mediaId);

    // 6개월 이전 동영상 리스트 조회
    List<VideoListDTO> getPreviousVideos(Long userSeq);

    // 달별 동영상 리스트 조회
    List<VideoListDTO> getVideosByMonth(Long userSeq, int month);

    // 노래별 동영상 리스트 조회
    List<VideoListDTO> getVideosBySong(Long songId);

    // 동영상 삭제
    void deleteVideo(Long videoId);

    // 동영상 접근 권한 확인
    Long checkUserVideoAccess(Long userSeq, Long videoId);
}
