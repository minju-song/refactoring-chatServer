package com.tgtg.chat.subject;

import com.tgtg.chat.subject.domain.Subject;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubjectRepository extends MongoRepository<Subject, Integer> {
    @Aggregation(pipeline = {"{ $sample: { size: 1 } }"})
    Optional<Subject> findRandomSubject();

    Subject findById(int id);
}
