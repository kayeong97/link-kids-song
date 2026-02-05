package com.linkkids.song.common.util;

import java.util.HashMap;
import java.util.List;

// category 한글 영어 매핑을 위한 클래스
// 모델이 한국어를 잘 인식을 못해서 영어로 번역하는 작업이 별도로 필요
public class CategoryUtil {
    private static final HashMap<String, String> categoryMap = new HashMap<>();
    static {
        categoryMap.put("모험", "Adventure");
        categoryMap.put("여행", "Travel");
        categoryMap.put("영화", "Movie");
        categoryMap.put("자장가", "Chillout");
        categoryMap.put("오케스트라", "Orchestra");
        categoryMap.put("휴식", "Easy listening");
        categoryMap.put("클래식", "Classical");
        categoryMap.put("댄스", "Dance");
        categoryMap.put("크리스마스", "Christmas");
        categoryMap.put("봄", "Spring");
        categoryMap.put("여름", "Summer");
        categoryMap.put("가을", "Autumn");
        categoryMap.put("겨울", "Winter");
        categoryMap.put("자신감", "Confident");
    }
    
    // 모든 무드 리스트 한글 반환
    static public List<String> getCategoryList() {
        List<String> categoryKeys = categoryMap.keySet().stream().toList();
        return categoryKeys;
    }

    // 한글 단어에 해당하는 영어 단어 반환
    static public String getCategoryEng(String categoryKor) {
        return categoryMap.getOrDefault(categoryKor, "");
    }
}
