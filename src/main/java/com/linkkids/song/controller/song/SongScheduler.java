package com.linkkids.song.controller.song;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.linkkids.song.common.model.song.SongModel;
import com.linkkids.song.controller.media.MediaDownloader;

@Component
public class SongScheduler {

    @Value("${song.api}")
    private String songApi;

    @Autowired
    private SongService songService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MediaDownloader mediaDownloader;

    // 1분마다 모든 READY 상태의 노래를 체크
    @Scheduled(fixedDelay = 60000)
    public void checkAllSongStatus() {
        try {
            List<SongModel> readySongs = songService.getReadySongs();
            if (readySongs.isEmpty()) {
                return;
            }
            for (SongModel song : readySongs) {
                checkSongStatus(song);
            }
        } catch (Exception e) {
            System.err.println("[스케줄러] 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 노래 마다 상태 확인
    private void checkSongStatus(SongModel song) {
        try {
            String jobId = song.getJobId();
            String url = songApi + "/jobs/" + jobId;

            HashMap<String, String> response = new HashMap<>();
            response = restTemplate.getForObject(url, HashMap.class);

            if (response == null) {
                return;
            }

            String status = response.get("status");

            if ("succeeded".equalsIgnoreCase(status)) {
                // 파일 다운로드 URL
                String fullAudioUrl = songApi + "/jobs/" + jobId + "/files/output";
                String vocalAudioUrl = songApi + "/jobs/" + jobId + "/files/vocal";
                String instAudioUrl = songApi + "/jobs/" + jobId + "/files/mr";

                String baseDir = "uploads/media/media_songs/";

                // 파일 다운로드
                fullAudioUrl = mediaDownloader
                        .downloadTo(fullAudioUrl, baseDir, song.getJobId(), song.getJobId() + "_full_audio.mp3")
                        .toString();
                vocalAudioUrl = mediaDownloader
                        .downloadTo(vocalAudioUrl, baseDir, song.getJobId(), song.getJobId() + "_vocal_audio.mp3")
                        .toString();
                instAudioUrl = mediaDownloader
                        .downloadTo(instAudioUrl, baseDir, song.getJobId(), song.getJobId() + "_inst_audio.mp3")
                        .toString();

                songService.completeSong(song.getSongId(), song.getUserSeq(), fullAudioUrl, vocalAudioUrl,
                        instAudioUrl);
            } else if ("failed".equalsIgnoreCase(status)) {
                songService.failSong(song.getSongId());
            }
        } catch (Exception e) {
            System.err.println("노래 상태 확인 오류: " + e.getMessage());
        }
    }
}
