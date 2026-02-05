package com.linkkids.song.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.google.genai.Client;

@Configuration
public class GeminiClientConfig {
  @Bean(destroyMethod = "close")
  public Client geminiClinet(@Value("${GEMINI_API_KEY}") String apiKey){
    if (apiKey == null || apiKey.isEmpty()) {
      throw new IllegalArgumentException("GEMINI_API_KEY 환경 변수가 설정되지 않았습니다.");
    }
    return Client.builder().apiKey(apiKey).build();
  }
}
