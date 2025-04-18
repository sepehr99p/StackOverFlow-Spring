package com.hc.stackoverflow.repository;


import com.hc.stackoverflow.entity.AnswerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    boolean existsByQuestionIdAndUserId(Long questionId, Long userId);
    List<AnswerEntity> findByQuestionId(Long questionId);

    List<AnswerEntity> findByUserId(Long userId);

    @Query("SELECT a FROM AnswerEntity a WHERE a.question.id = :questionId ORDER BY a.votes DESC")
    Page<AnswerEntity> findTopAnswersByQuestionId(@Param("questionId") Long questionId, Pageable pageable);

    @Query("SELECT a FROM AnswerEntity a WHERE a.question.id = :questionId AND a.isAccepted = true")
    Optional<AnswerEntity> findAcceptedAnswerByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT a FROM AnswerEntity a WHERE a.question.id = :questionId AND a.userId = :userId")
    Optional<AnswerEntity> findByQuestionIdAndUserId(@Param("questionId") Long questionId, @Param("userId") Long userId);
}
