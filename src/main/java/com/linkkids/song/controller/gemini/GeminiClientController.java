package com.linkkids.song.controller.gemini;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linkkids.song.dto.song.UserSongRequestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiClientController {
    
    private final GeminiClientService geminiClientService;
    
    // 가사 생성
    @PostMapping("/lyrics/generate")
    public ResponseEntity<String> generateLyrics(@RequestBody UserSongRequestDTO request) {
        try {
            // 가사 생성
            String lyrics = geminiClientService.generateLyrics(request);

            String response = lyrics;
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorResponse = "생성 중 오류가 발생했습니다.";
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
