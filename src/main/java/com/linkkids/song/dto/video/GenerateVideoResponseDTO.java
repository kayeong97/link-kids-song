package com.linkkids.song.dto.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class GenerateVideoResponseDTO {
    private Result result;
    private Info info;
    private Data data;

    @Getter
    public class Result {
        private String code;
        private String message;
    }

    @Getter
    public class Info {
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("work_id")
        private String workId;
    }

    @Getter
    public class Data {
        @JsonProperty("video_url")
        private String videoUrl;
    }
}
