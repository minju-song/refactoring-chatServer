package com.tgtg.chat.subject.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SubjectDTO {

    String _id;
    private String subjectTitle;
    private String subjectAnswerA;
    private String subjectAnswerB;

}
