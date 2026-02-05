package com.linkkids.song.controller.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.linkkids.song.common.model.media.MediaModel;
import com.linkkids.song.common.model.user.UserModel;

@Controller
public class MediaController {
    @Autowired
    private MediaService mediaService;

    @PostMapping("/media/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "purpose", required = false, defaultValue = "song") String purpose,
            @RequestParam Map<String, Object> params) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "파일이 비어있습니다."));
            }

            UserModel user = UserModel.getCurrentUser(true);

            // 파일 타입 결정
            MediaModel.MediaType mediaType = null;
            String contentType = file.getContentType();
            if (contentType.startsWith("image/")) {
                mediaType = MediaModel.MediaType.IMAGE;
            } else if (contentType.startsWith("audio/")) {
                mediaType = MediaModel.MediaType.AUDIO;
            } else if (contentType.startsWith("video/")) {
                mediaType = MediaModel.MediaType.VIDEO;
            }

            if (mediaType == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "지원하지 않는 파일 형식입니다."));
            }

            // 파일명 - 중복이 존재할 수 있으니 UUID 추가
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String uploadPath;
            Path uploadDir;

            // 저장 경로
            if (contentType.startsWith("image/") && !"video".equals(purpose)) {
                uploadPath = "uploads/media/media_thumbnails/" + user.getUsername() + "/";
                uploadDir = Paths.get(uploadPath);
            } else {
                uploadPath = "uploads/media/media_video_thumbnails/" + user.getUsername() + "/";
                uploadDir = Paths.get(uploadPath);
            }

            // 디렉토리가 없으면 생성
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 파일 저장
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // DB에 저장할 모델 생성
            MediaModel mediaModel = new MediaModel();
            mediaModel.setUserSeq(user.getUserSeq());
            mediaModel.setType(mediaType);
            mediaModel.setMimeType(contentType);
            mediaModel.setSizeBytes(file.getSize());
            mediaModel.setUrl(uploadPath + fileName);

            // DB에 저장
            Long mediaId = mediaService.saveMedia(mediaModel);

            Map<String, Object> response = Map.of(
                    "mediaId", mediaId,
                    "url", "/" + uploadPath + fileName,
                    "type", mediaModel.getType().toString());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "파일 저장 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @DeleteMapping("/media/delete/{mediaId}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long mediaId) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.ok().build();
    }
}
