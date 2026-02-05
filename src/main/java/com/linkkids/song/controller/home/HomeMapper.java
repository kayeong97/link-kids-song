package com.linkkids.song.controller.home;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.linkkids.song.dto.song.SongListDTO;
import com.linkkids.song.dto.video.VideoListDTO;

@Mapper
public interface HomeMapper {

    // 생성 대기중 노래 목록 조회
    public List<String> getGenerationSongReadyList(@Param("userSeq") Long userSeq);

    // 최근 노래 목록 조회
    public List<SongListDTO> getRecentSongs(@Param("userSeq") Long userSeq, @Param("count") int count);

    // 최근 영상 목록 조회
    public List<VideoListDTO> getRecentVideos(@Param("userSeq") Long userSeq, @Param("count") int count);
}
