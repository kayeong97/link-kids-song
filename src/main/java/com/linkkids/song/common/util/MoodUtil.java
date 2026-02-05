package com.linkkids.song.common.util;

import java.util.HashMap;
import java.util.List;

// mood 한글 영어 매핑을 위한 클래스
// 모델이 한국어를 잘 인식을 못해서 영어로 번역하는 작업이 별도로 필요
public class MoodUtil {
    private static final HashMap<String, String> moodMap = new HashMap<>();
    static {
        moodMap.put("장난기 넘치는", "Playful");
        moodMap.put("행복한", "Happy");
        moodMap.put("쾌활한", "Cheerful");
        moodMap.put("기쁜", "Joyful");
        moodMap.put("재미있는", "Funny");
        moodMap.put("활기찬", "Energetic");
        moodMap.put("생기 있는", "Lively");
        moodMap.put("경쾌한", "Upbeat");
        moodMap.put("흥미진진한", "Exciting");
        moodMap.put("긍정적인", "Positive");
        moodMap.put("희망찬", "Uplifting");
        moodMap.put("다정한", "Affectionate");
        moodMap.put("온화한", "Gentle");
        moodMap.put("차분한", "Calm");
        moodMap.put("편안한", "Relaxed");
        moodMap.put("마음을 달래주는", "Soothing");
        moodMap.put("동화같은", "Dreamy");
        moodMap.put("희망적인", "Hopeful");
        moodMap.put("낙관적인", "Optimistic");
        moodMap.put("진심 어린", "Heartfelt");
    }
    
    // 모든 무드 리스트 한글 반환
    static public List<String> getMoodList() {
        List<String> moodsKeys = moodMap.keySet().stream().toList();
        return moodsKeys;
    }

    // 한글 단어에 해당하는 영어 단어 반환
    static public String getMoodEng(String moodKor) {
        return moodMap.getOrDefault(moodKor, "");
    }
}
