package com.hc.stackoverflow.repository;


import com.hc.stackoverflow.entity.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByUserId(Long userId);

    @Query("SELECT q FROM QuestionEntity q WHERE q.description LIKE %:keyword% OR q.title LIKE %:keyword%")
    Page<QuestionEntity> searchQuestions(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT q FROM QuestionEntity q WHERE q.userId = :userId AND (q.description LIKE %:keyword% OR q.title LIKE %:keyword%)")
    List<QuestionEntity> searchUserQuestions(@Param("userId") Long userId, @Param("keyword") String keyword);
}
