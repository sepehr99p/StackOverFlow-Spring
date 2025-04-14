package com.hc.stackoverflow.repository;


import com.hc.stackoverflow.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByUserId(Long userId);

    @Query("SELECT q FROM Question q WHERE q.description LIKE %:keyword% OR q.title LIKE %:keyword%")
    List<Question> searchQuestions(@Param("keyword") String keyword);

    @Query("SELECT q FROM Question q WHERE q.userId = :userId AND (q.description LIKE %:keyword% OR q.title LIKE %:keyword%)")
    List<Question> searchUserQuestions(@Param("userId") Long userId, @Param("keyword") String keyword);
}

