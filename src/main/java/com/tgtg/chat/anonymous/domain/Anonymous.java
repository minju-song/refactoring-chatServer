package com.tgtg.chat.anonymous.domain;

import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "anonymous")
public class Anonymous {

    @Id
    private String id;

    @Field("anonymous_id")
    private int anonymousId;

    @Field("anonymous_nickname")
    private String anonymousNickname;

    @Field("anonymous_image")
    private String anonymousImage;

    @Field("anonymous_image_name")
    private String anonymousImageName;

    public AnonymousDTO toResponseDto(){
        return AnonymousDTO.builder()
                .anonymousId(anonymousId)
                .anonymousNickname(anonymousNickname)
                .anonymousImage(anonymousImage)
                .anonymousImageName(anonymousImageName)
                .build();
    }
}
