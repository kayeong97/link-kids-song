package com.linkkids.song.dto.video;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GenerateVideoRequestDTO {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("work_id")
    private String workId;
    @JsonProperty("character_type")
    private String characterType;
    private String voice; // mix 음원 파일
    private String bgm; // inst 음원 파일
    @JsonProperty("crop_info")
    private String cropInfo;
    @JsonProperty("source_path")
    private String sourcePath;
}
