package com.linkkids.song.dto.video;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class CheckFaceResponseDTO {
    private Result result;
    private Info info;
    private Data data;

    @Getter
    public static class Result {
        private String code;
        private String message;
    }

    @Getter
    public static class Info {
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("work_id")
        private String workId;
    }

    @Getter
    public static class Data {
        @JsonProperty("has_face")
        private boolean hasFace;
        @JsonProperty("crop_info")
        private String cropInfo;
        @JsonProperty("source_path")
        private String sourcePath;
    }
}
