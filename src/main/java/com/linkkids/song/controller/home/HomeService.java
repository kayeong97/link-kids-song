package com.linkkids.song.controller.home;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linkkids.song.dto.song.SongListDTO;
import com.linkkids.song.dto.video.VideoListDTO;

@Service
public class HomeService {

    @Autowired
    HomeMapper homeMapper;

    // 생성 대기중 노래 목록 조회
    public List<String> getGenerationSongReadyList(Long userSeq) {
        return homeMapper.getGenerationSongReadyList(userSeq);
    }

    // 최근 노래 목록 조회
    public List<SongListDTO> getRecentSongs(Long userSeq, int count) {
        return homeMapper.getRecentSongs(userSeq, count);
    }

    // 최근 영상 목록 조회
    public List<VideoListDTO> getRecentVideos(Long userSeq, int count) {
        return homeMapper.getRecentVideos(userSeq, count);
    }
}
