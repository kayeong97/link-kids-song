package com.linkkids.song.controller.media;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linkkids.song.common.model.media.MediaModel;
import com.linkkids.song.common.model.user.UserModel;

@Service
public class MediaService {
    @Autowired
    private MediaMapper mediaMapper;
    @Autowired
    private MediaDownloader mediaDownloader;

    // 미디어 저장
    public Long saveMedia(MediaModel mediaModel) {
        mediaMapper.saveMedia(mediaModel);
        return mediaModel.getMediaId();
    }

    // 미디어 ID로 조회
    public MediaModel getMediaById(Long mediaId) {
        return mediaMapper.getMediaById(mediaId);
    }

    // 미디어 경로 조회
    public String getMediaPathById(Long mediaId) {
        return mediaMapper.getMediaPathById(mediaId);
    }

    // 미디어 삭제
    public void deleteMedia(Long mediaId) {
        mediaMapper.deleteMedia(mediaId);
    }

    // 동영상 미디어 저장
    public Long saveVideo(String videoUrl, String title) {

        // 동영상 다운로드
        String downloadedPath;
        try {
            downloadedPath = mediaDownloader.downloadTo(
                    videoUrl,
                    "uploads/media/media_video/",
                    UUID.randomUUID().toString(),
                    title + ".mp4").toString();
        } catch (Exception e) {
            throw new RuntimeException("동영상 다운로드 실패", e);
        }

        // 동영상 크기 조회
        long sizeBytes;
        try {
            java.io.File file = new java.io.File(downloadedPath);
            sizeBytes = file.length();
        } catch (Exception e) {
            throw new RuntimeException("파일 크기 조회 실패", e);
        }

        // 미디어 정보 저장 및 mediaId return
        MediaModel mediaModel = new MediaModel();

        mediaModel.setUserSeq(UserModel.getCurrentUser(true).getUserSeq());
        mediaModel.setType(MediaModel.MediaType.VIDEO);
        mediaModel.setMimeType("video/mp4");
        mediaModel.setUrl(downloadedPath);
        mediaModel.setSizeBytes(sizeBytes);
        mediaMapper.saveMedia(mediaModel);

        return mediaModel.getMediaId();
    }
}
