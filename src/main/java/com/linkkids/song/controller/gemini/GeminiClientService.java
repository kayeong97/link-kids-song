package com.linkkids.song.controller.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.ThinkingConfig;
import com.google.genai.types.ThinkingLevel;
import com.linkkids.song.dto.song.UserSongRequestDTO;

@Service
public class GeminiClientService {

  @Value("${gemini.api-key}")
  private String apiKey;

  // 가사 생성
  public String generateLyrics(UserSongRequestDTO request) {
    try (Client client = Client.builder().apiKey(apiKey).build()) {

      // 곡 정보를 포함한 프롬프트 생성
      String prompt = buildPrompt(request);

      // Gemini API 호출 설정
      GenerateContentConfig config = GenerateContentConfig.builder()
          .thinkingConfig(ThinkingConfig.builder()
              .thinkingLevel(new ThinkingLevel("low"))
              .build())
          .build();

      // 가사 생성 요청
      GenerateContentResponse response = client.models.generateContent(
          "gemini-3-pro-preview",
          prompt,
          config);

      return response.text();

    } catch (Exception e) {
      System.err.println("가사 생성 중 에러 발생:");
      e.printStackTrace();
      throw new RuntimeException("가사 생성 실패: " + e.getMessage(), e);
    }
  }

  // 프롬프트 작성
  private String buildPrompt(UserSongRequestDTO request) {
    StringBuilder promptBuilder = new StringBuilder();

    promptBuilder
        .append("당신은 아이들의 순수한 마음을 노래하는 '전문 동요 작사가'입니다. 주어진 변수를 바탕으로 YuE 모델이 자곡하기 가장 좋은 형태의 한국어 동요 가사를 작성해야 합니다.\n\n");

    int audioLength = Integer.parseInt(request.getRun_n_segments()) * 15;
    // 곡 정보 추가
    promptBuilder.append("Title: " + request.getTitle() + "\n");
    promptBuilder.append("Subject: " + request.getSubject() + "\n");
    promptBuilder.append("Mood: " + request.getMood() + "\n");
    promptBuilder.append("Category: " + request.getCategory() + "\n");
    promptBuilder.append("Voice Gender: " + request.getGender() + "\n");
    promptBuilder.append("Total Length: " + Integer.toString(audioLength) + "s \n");
    promptBuilder.append("Number of Verses: " + request.getVerseNo() + "\n\n");

    promptBuilder.append("# Instructions\n");

    promptBuilder.append("2. **Structure**: `").append(request.getVerseNo())
        .append("절`에 맞춰 `[Verse]`와 `[Chorus]`를 배치하세요.\n");
    promptBuilder.append("   - 태그 2개당 30초 가량 소유되니 시간을 고려하여 주세요.\n");
    promptBuilder.append("   - 앞뒤로 간주가 들어갈 여유 시간을 확보하세요.\n");
    promptBuilder.append("   - `").append(audioLength).append("`초가 길다면 `[Bridge]`를 추가하여 길이를 확보하세요.\n");
    promptBuilder.append("   - 제목을 임의로 변경하지 마세요. test라도 제목은 반드시 지켜야 합니다.\n\n");

    promptBuilder.append("3. **Lyrics Style (한국어 동요 특징)**:\n");
    promptBuilder.append("   - **의성어/의태어 필수**: (예: 깡충깡충, 살랑살랑, 데굴데굴)을 적극적으로 사용하여 리듬감을 살리세요.\n");
    promptBuilder.append("   - **어휘**: 아이들이 이해하기 쉬운 순수하고 예쁜 단어를 사용하세요.\n");
    promptBuilder.append("   - **운율**: 4/4박자나 3/4박자에 맞춰 3-4-3-4조 또는 7-5조의 운율을 맞추세요.\n");
    promptBuilder.append("   - **성별 반영**: `").append(request.getGender()).append("`가 'Male'이면 씩씩하고 활기찬 어조, ")
        .append("'Female'이면 맑고 고운 어조를 가미하되 고정관념에 얽매이지 않게 하세요.\n\n");

    promptBuilder.append("# Output Format\n");
    promptBuilder.append("(반드시 아래 형식을 지켜주세요)\n");
    promptBuilder.append("[verse], [chorus]는 반드시 한번 이상 포함되어야 합니다.\n");
    promptBuilder.append("가사 이외의 것들은 포함하지 마세요.\n\n");

    promptBuilder.append("[Verse]\n");
    promptBuilder.append("(주제와 관련된 1절 가사)\n\n");

    promptBuilder.append("[Chorus]\n");
    promptBuilder.append("(가장 신나고 반복되는 핵심 가사, 의성어/의태어 포함)\n\n");

    promptBuilder.append("... (").append(request.getVerseNo()).append("에 따라 Verse 반복. 숫자는 적지 않도록 하세요.) ...\n\n");

    return promptBuilder.toString();
  }
}
