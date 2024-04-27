package com.tgtg.chat.subject.service;

import com.tgtg.chat.subject.domain.Subject;
import com.tgtg.chat.subject.dto.SubjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService{

    private final MongoTemplate mongoTemplate;

    @Autowired
    public SubjectServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public SubjectDTO getSubject() {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.sample(1));

        AggregationResults<Subject> results = mongoTemplate.aggregate(aggregation, "subject", Subject.class);

        List<Subject> randomSubject = results.getMappedResults();

        SubjectDTO resultSubject = randomSubject.get(0).toResponseDto();
        System.out.println("주제 "+resultSubject.getSubjectTitle());
        if(!randomSubject.isEmpty()) {
            return resultSubject;
        }
        else return  null;
    }
}
