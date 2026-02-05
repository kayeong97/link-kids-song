package com.linkkids.song.controller.song;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.linkkids.song.common.model.song.SongModel;
import com.linkkids.song.common.model.user.UserModel;
import com.linkkids.song.common.util.CategoryUtil;
import com.linkkids.song.common.util.MoodUtil;
import com.linkkids.song.common.model.media.MediaModel;
import com.linkkids.song.common.model.media.MediaModel.MediaType;
import com.linkkids.song.controller.media.MediaService;
import com.linkkids.song.dto.song.SongListDTO;
import com.linkkids.song.dto.song.UserSongRequestDTO;

@Service
public class SongService {

    @Value("${song.api}")
    private String songApi;

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${generate.api}")
    private String api;

    // *** 노래 리스트 관련 *** //

    // 전체 노래 개수 가져오기
    public int getAllSongCount(Long userSeq) {
        return songMapper.getTotalSongCount(userSeq);
    }

    // 노래 삭제하기
    public void deleteSong(Long songId) {
        songMapper.deleteSong(songId);
    }

    // 노래 5개씩 가져오기
    public List<SongListDTO> getSongsByPage(int page) {
        return getSongsByPage(page, "newest");
    }

    public List<SongListDTO> getSongsByPage(int page, String sortType) {
        int pageSize = 5; // 한 페이지당 개수

        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();

        int songCount = songMapper.getTotalSongCount(userSeq);
        int offset = (page - 1) * pageSize;
        int limit = (pageSize > songCount - offset) ? songCount - offset : pageSize;

        // hashmap parameter
        HashMap<String, Long> params = new HashMap<>();
        params.put("userSeq", userSeq);
        params.put("offset", (long) offset);
        params.put("limit", (long) limit);

        List<SongListDTO> songs = null;
        if ("newest".equals(sortType)) {
            songs = songMapper.getSongsNewSort(userSeq, offset, limit);
        } else if ("oldest".equals(sortType)) {
            songs = songMapper.getSongsOldSort(userSeq, offset, limit);
        } else if ("title".equals(sortType)) {
            songs = songMapper.getSongsTitleSort(userSeq, offset, limit);
        }

        // \ -> / 변환
        if (songs != null) {
            songs.forEach(song -> {
                if (song.getCoverMediaUrl() != null) {
                    song.setCoverMediaUrl(song.getCoverMediaUrl().replace("\\", "/"));
                }
                if (song.getFullAudioUrl() != null) {
                    song.setFullAudioUrl(song.getFullAudioUrl().replace("\\", "/"));
                }
                if (song.getVocalAudioUrl() != null) {
                    song.setVocalAudioUrl(song.getVocalAudioUrl().replace("\\", "/"));
                }
                if (song.getInstAudioUrl() != null) {
                    song.setInstAudioUrl(song.getInstAudioUrl().replace("\\", "/"));
                }
            });
        }

        return songs;
    }

    // 노래 경로 가져오기
    public String getSongFilePath(Long songId) {
        return songMapper.getSongFilePath(songId);
    }

    // *** 노래 생성 관련 *** //

    // 노래 생성 요청 보내기
    public String createSong(UserSongRequestDTO userSongRequestDTO, Long userSeq) {

        SongModel songModel = new SongModel();
        HashMap<String, Object> requestJson = new HashMap<>();

        // 노래 정보 설정
        songModel.setUserSeq(userSeq);
        songModel.setTitle(userSongRequestDTO.getTitle());
        songModel.setSubject(userSongRequestDTO.getSubject());
        songModel.setMood(MoodUtil.getMoodEng(userSongRequestDTO.getMood()));
        songModel.setVocalGender(userSongRequestDTO.getGender());
        songModel.setAudioLength(userSongRequestDTO.getRun_n_segments());
        songModel.setCategories(CategoryUtil.getCategoryEng(userSongRequestDTO.getCategory()));
        songModel.setLyrics(userSongRequestDTO.getLyrics());
        songModel.setCoverMediaId(userSongRequestDTO.getCoverMediaId());

        StringBuilder genreBuilder = new StringBuilder();

        // 프롬프트 생성
        genreBuilder.append(MoodUtil.getMoodEng(userSongRequestDTO.getMood()))
                .append(" children's song ")
                .append(CategoryUtil.getCategoryEng(userSongRequestDTO.getCategory()))
                .append(" Xylophone piano bright playful")
                .append(userSongRequestDTO.getGender())
                .append(" teenager child voice childeren");
        String genre = genreBuilder.toString();

        // 요청 JSON
        requestJson.put("genre", genre);
        requestJson.put("lyrics", userSongRequestDTO.getLyrics());
        requestJson.put("run_n_segments", userSongRequestDTO.getRun_n_segments());
        requestJson.put("max_new_tokens", "3000");
        requestJson.put("repetition_penalty", "1.1");
        requestJson.put("stage2_batch_size", "4");
        requestJson.put("separate_tracks", "true");
        requestJson.put("nomalize_vocals", "true");
        requestJson.put("vocal_sr", "16000");

        // 요청 보내기
        Map<String, Object> response = new HashMap<>();
        response = restTemplate.postForObject(songApi + "/jobs", requestJson, Map.class);

        songModel.setJobId(response.get("job_id").toString());
        songMapper.saveSong(songModel);
        return response.get("job_id").toString();
    }

    // 대기 중인 노래 list 가져오기
    public List<Integer> getReadyJobs(Long userSeq) {
        return songMapper.getReadyJobIds(userSeq);
    }

    // 모든 READY 상태의 노래 가져오기 (스케줄러용)
    public List<SongModel> getReadySongs() {
        return songMapper.getReadySongs();
    }

    // 노래 완료 처리
    public void completeSong(Long songId, Long userSeq, String fullAudioUrl, String vocalAudioUrl,
            String instAudioUrl) {
        try {
            // mix 오디오 저장
            MediaModel fullAudioMedia = new MediaModel();
            fullAudioMedia.setUserSeq(userSeq);
            fullAudioMedia.setType(MediaType.AUDIO);
            fullAudioMedia.setUrl(fullAudioUrl);
            fullAudioMedia.setMimeType("audio/mpeg");
            fullAudioMedia.setSizeBytes(Files.size(Path.of(fullAudioUrl)));
            mediaService.saveMedia(fullAudioMedia);

            // 보컬만 있는 오디오 저장
            MediaModel vocalAudioMedia = new MediaModel();
            vocalAudioMedia.setUserSeq(userSeq);
            vocalAudioMedia.setType(MediaType.AUDIO);
            vocalAudioMedia.setUrl(vocalAudioUrl);
            vocalAudioMedia.setMimeType("audio/mpeg");
            vocalAudioMedia.setSizeBytes(Files.size(Path.of(vocalAudioUrl)));
            mediaService.saveMedia(vocalAudioMedia);

            // 반주만 있는 오디오 저장
            MediaModel instAudioMedia = new MediaModel();
            instAudioMedia.setUserSeq(userSeq);
            instAudioMedia.setType(MediaType.AUDIO);
            instAudioMedia.setUrl(instAudioUrl);
            instAudioMedia.setMimeType("audio/mpeg");
            instAudioMedia.setSizeBytes(Files.size(Path.of(instAudioUrl)));
            mediaService.saveMedia(instAudioMedia);

            // song 업데이트
            HashMap<String, Object> params = new HashMap<>();
            params.put("songId", songId);
            params.put("fullAudioMediaId", fullAudioMedia.getMediaId());
            params.put("vocalAudioMediaId", vocalAudioMedia.getMediaId());
            params.put("instAudioMediaId", instAudioMedia.getMediaId());
            params.put("status", "COMPLETED");

            songMapper.saveSongMedia(params);
        } catch (Exception e) {
            System.err.println("노래 완료 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 노래 실패 처리
    public void failSong(Long songId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("songId", songId);
        params.put("status", "FAILED");

        songMapper.saveSongMedia(params);
    }

    // *** 노래 크게 재생 관련 *** //
    // 가사 조회
    public String getSongLyrics(Long songId) {
        return songMapper.getSongLyrics(songId);
    }

    // 노래 한 개 불러오기
    public SongListDTO getSongInfo(Long songId) {
        SongListDTO song = songMapper.getSongInfo(songId);
        // \ -> / 변환
        if (song != null) {
            if (song.getCoverMediaUrl() != null) {
                song.setCoverMediaUrl(song.getCoverMediaUrl().replace("\\", "/"));
            }
            if (song.getFullAudioUrl() != null) {
                song.setFullAudioUrl(song.getFullAudioUrl().replace("\\", "/"));
            }
            if (song.getVocalAudioUrl() != null) {
                song.setVocalAudioUrl(song.getVocalAudioUrl().replace("\\", "/"));
            }
            if (song.getInstAudioUrl() != null) {
                song.setInstAudioUrl(song.getInstAudioUrl().replace("\\", "/"));
            }
        }
        return song;
    }
}
