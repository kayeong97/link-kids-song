package com.linkkids.song.dto.video;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CheckFaceRequestDTO {
    @JsonProperty("user_id")
    String userId;

    @JsonProperty("work_id")
    String workId;

    @JsonProperty("image_url")
    String imageUrl;

    @JsonProperty("character_type")
    String characterType;
}
