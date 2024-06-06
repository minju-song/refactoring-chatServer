package com.tgtg.chat.subject.service;

import com.tgtg.chat.subject.SubjectRepository;
import com.tgtg.chat.subject.domain.Subject;
import com.tgtg.chat.subject.dto.SubjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class SubjectServiceImpl implements SubjectService{

    @Autowired
    SubjectRepository subjectRepository;

    @Cacheable("subject")
    @Override
    public Subject getSubject(int num) {
        Subject subject = subjectRepository.findById(num);
        return subject;
    }
}
