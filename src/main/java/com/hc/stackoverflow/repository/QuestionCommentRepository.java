package com.hc.stackoverflow.repository;

import com.hc.stackoverflow.entity.QuestionCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCommentRepository extends JpaRepository<QuestionCommentEntity, Long> {
    List<QuestionCommentEntity> findByQuestionId(Long questionId);
}
