package com.linkkids.song.controller.song;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linkkids.song.common.model.song.SongModel;
import com.linkkids.song.dto.song.SongListDTO;

@Mapper
public interface SongMapper {

    // 준비중인 노래 개수 조회
    int getReadySongCount(Long userSeq);

    // 준비중인 노래 리스트 조회
    List<Integer> getReadyJobIds(Long userSeq);

    // 모든 READY 상태의 노래 조회
    List<SongModel> getReadySongs();

    // 사용자별 노래 리스트(페이지) - 최신순
    List<SongListDTO> getSongsNewSort(Long userSeq, int offset, int limit);

    // 사용자별 노래 리스트(페이지) - 오래된순
    List<SongListDTO> getSongsOldSort(Long userSeq, int offset, int limit);

    // 사용자별 노래 리스트(페이지) - 제목순
    List<SongListDTO> getSongsTitleSort(Long userSeq, int offset, int limit);

    // 노래 저장(생성 정보 저장)
    void saveSong(SongModel songModel);

    // 제작된 노래 저장
    void saveSongMedia(HashMap<String, Object> params);

    // 전체 노래 개수 조회
    int getTotalSongCount(Long userSeq);

    // 가사 저장
    boolean saveLyrics(HashMap<String, Object> params);

    // 노래 삭제
    void deleteSong(Long songId);

    // 노래 파일 경로 조회
    String getSongFilePath(Long songId);

    // 노래 가사 조회
    String getSongLyrics(Long songId);

    // 노래 한 개 조회
    SongListDTO getSongInfo(Long songId);
}
