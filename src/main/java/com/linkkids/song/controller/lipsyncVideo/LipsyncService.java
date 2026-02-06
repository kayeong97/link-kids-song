package com.linkkids.song.controller.lipsyncVideo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.linkkids.song.common.model.user.UserModel;
import com.linkkids.song.controller.media.MediaService;
import com.linkkids.song.dto.song.SongListDTO;
import com.linkkids.song.dto.video.CheckFaceResponseDTO;
import com.linkkids.song.dto.video.CheckFaceRequestDTO;
import com.linkkids.song.dto.video.GenerateVideoRequestDTO;
import com.linkkids.song.dto.video.GenerateVideoResponseDTO;
import com.linkkids.song.dto.video.VideoListDTO;

@Service
public class LipsyncService {

    @Autowired
    private LipsyncMapper lipsyncMapper;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${generate.api}")
    private String api;
    @Value("${video.api}")
    private String videoApi;

    // *** 동영상 생성 관련 ***/

    // 영상 얼굴 확인
    // 확인 후 생성 요청 DTO 반환
    public GenerateVideoRequestDTO checkFace(CheckFaceRequestDTO checkFaceDTO, Long vocalMediaId, Long bgmMediaId) {
        ResponseEntity<CheckFaceResponseDTO> response = restTemplate.postForEntity(
                videoApi + "/check_face",
                checkFaceDTO, CheckFaceResponseDTO.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            CheckFaceResponseDTO responseBody = response.getBody();

            CheckFaceResponseDTO.Info info = responseBody.getInfo();
            CheckFaceResponseDTO.Data data = responseBody.getData();

            GenerateVideoRequestDTO generateVideoRequestDTO = new GenerateVideoRequestDTO();
            generateVideoRequestDTO.setUserId(info.getUserId());
            generateVideoRequestDTO.setWorkId(info.getWorkId());
            generateVideoRequestDTO.setCharacterType("human");
            generateVideoRequestDTO.setCropInfo(data.getCropInfo());
            generateVideoRequestDTO.setVoice(api + mediaService.getMediaPathById(vocalMediaId));
            generateVideoRequestDTO.setBgm(api + mediaService.getMediaPathById(bgmMediaId));
            generateVideoRequestDTO.setSourcePath(data.getSourcePath());
            return generateVideoRequestDTO;
        }
        return null;
    }

    // 동영상 생성
    public String createVideo(GenerateVideoRequestDTO generateVideoRequestDTO) {
        ResponseEntity<GenerateVideoResponseDTO> response = restTemplate.postForEntity(
                videoApi + "/generate_single_video", generateVideoRequestDTO,
                GenerateVideoResponseDTO.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            GenerateVideoResponseDTO responseBody = response.getBody();
            GenerateVideoResponseDTO.Data data = responseBody.getData();
            return data.getVideoUrl();
        }
        return null;
    }

    // 동영상 다운로드
    public void downloadVideo(String videoUrl, Long songId, Long userSeq, Long inputImageMediaId, String title) {
        // 동영상 다운로드 (media)
        Long outputVideoMediaId = mediaService.saveVideo(videoUrl, title);

        Map<String, Object> params = new HashMap<>();

        params.put("userSeq", userSeq);
        params.put("title", title);
        params.put("songId", songId);
        params.put("inputImageMediaId", inputImageMediaId);
        params.put("outputVideoMediaId", outputVideoMediaId);

        // 립싱크 정보 저장 (lipsync)
        lipsyncMapper.saveLipsync(params);
    }

    // 미디어 아이디 가져오기
    public Long getFullMediaId(Long songId) {
        return lipsyncMapper.getFullMediaId(songId);
    }

    public Long getVocalMediaId(Long songId) {
        return lipsyncMapper.getVocalMediaId(songId);
    }

    // 사용자 노래 리스트 가져오기
    public List<SongListDTO> getUserSongs(Long userSeq) {
        return lipsyncMapper.getUserSongs(userSeq);
    }

    // 미디어 아이디로 파일 경로 가져오기
    public String getVideoFilePath(Long mediaId) {
        return lipsyncMapper.getVideoFilePath(mediaId);
    }

    // *** 동영상 리스트 관련 *** //

    // monthList
    public Map<String, Long> getMonthList() {
        Map<String, Long> monthList = new LinkedHashMap<>();
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        for (int index = 0; index < 6; index++) {
            int month = currentMonth - index;
            if (month <= 0) {
                currentYear -= 1;
                currentMonth += 12;
                month = 12;
            }
            monthList.put(String.format("%04d-%02d", currentYear, month), (long) month);
        }
        monthList.put("이전", -1L);
        return monthList;
    }

    // 월별 동영상 리스트 가져오기
    public List<VideoListDTO> getVideosByMonth(Long userSeq, int month) {
        if (month == 0) {
            return lipsyncMapper.getPreviousVideos(userSeq);
        } else {
            return lipsyncMapper.getVideosByMonth(userSeq, month);
        }
    }

    // songTitleList
    public Map<String, Long> getSongTitleList(Long userSeq) {
        List<SongListDTO> userSongs = lipsyncMapper.getUserSongs(userSeq);
        Map<String, Long> songTitleList = new LinkedHashMap<>();
        for (SongListDTO song : userSongs) {
            songTitleList.put(song.getTitle(), song.getSongId());
        }
        return songTitleList;
    }

    // 노래별 동영상 리스트 가져오기
    public List<VideoListDTO> getVideosBySong(Long songId) {
        return lipsyncMapper.getVideosBySong(songId);
    }

    // 동영상 삭제하기
    public void deleteVideo(Long videoId) {
        lipsyncMapper.deleteVideo(videoId);
    }

    // *** 기타 ***//
    public boolean checkUserVideoAccess(Long videoId) {
        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        Long count = lipsyncMapper.checkUserVideoAccess(userSeq, videoId);
        return count != null && count > 0;
    }
}
