package com.tgtg.chat.anonymous.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AnonymousDTO {
    private String id;
    private int anonymousId;
    private String anonymousImage;
    private String anonymousNickname;
    private String anonymousImageName;
    private int roomId;

    //역할
    // answerA, answerB, judge
    private String role;

    private String key;
}
