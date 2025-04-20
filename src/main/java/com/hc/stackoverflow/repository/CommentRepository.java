package com.hc.stackoverflow.repository;

import com.hc.stackoverflow.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByQuestionId(Long questionId);

    List<CommentEntity> findByAnswerId(Long answerId);

    List<CommentEntity> findByUserId(Long userId);

    @Query("SELECT c FROM CommentEntity c WHERE c.question.id = :questionId ORDER BY c.createdAt DESC")
    Page<CommentEntity> findCommentsByQuestionId(@Param("questionId") Long questionId, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.answer.id = :answerId ORDER BY c.createdAt DESC")
    Page<CommentEntity> findCommentsByAnswerId(@Param("answerId") Long answerId, Pageable pageable);
}
