package com.tgtg.chat.subject.service;

import com.tgtg.chat.subject.domain.Subject;
import com.tgtg.chat.subject.dto.SubjectDTO;

import java.util.Optional;

public interface SubjectService {

    //    랜덤 주제 객체 받기
    public Subject getSubject(int num);
}
