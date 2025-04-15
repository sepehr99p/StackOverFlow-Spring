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

    @Query("SELECT q FROM QuestionEntity q WHERE " +
            "LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(q.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<QuestionEntity> searchQuestions(@Param("keyword") String keyword, Pageable pageable);
}