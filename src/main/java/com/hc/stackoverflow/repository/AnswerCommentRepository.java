package com.hc.stackoverflow.repository;

import com.hc.stackoverflow.entity.AnswerCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerCommentRepository extends JpaRepository<AnswerCommentEntity, Long> {
    List<AnswerCommentEntity> findByAnswerId(Long answerId);
}
