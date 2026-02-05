package com.linkkids.song.controller.media;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class MediaDownloader {
    @Autowired
    private RestTemplate restTemplate;

    // 미디어 파일 다운로드
    public Path downloadTo(String fileUrl, String baseDir, String jobId, String filename) throws Exception {

        // 저장 경로 설정 - 지정 폴더 / jobID
        Path targetPath = Path.of(baseDir, jobId, filename);
        Files.createDirectories(targetPath.getParent());

        // 파일 다운로드
        restTemplate.execute(fileUrl, HttpMethod.GET, null, response -> {
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("파일 다운로드 실패: " + response.getStatusCode());
            }
            try (InputStream inputstream = response.getBody()) {
                Files.copy(inputstream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return null;
        });
        return targetPath;
    }
}
