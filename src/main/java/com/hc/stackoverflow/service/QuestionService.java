package com.hc.stackoverflow.service;


import com.hc.stackoverflow.entity.QuestionEntity;
import com.hc.stackoverflow.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        return questionRepository.save(questionEntity);
    }

    @Cacheable(value = "questions", key = "#id")
    public Optional<QuestionEntity> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    @Cacheable(value = "questions", key = "'all'")
    public List<QuestionEntity> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Cacheable(value = "questions", key = "#userId")
    public List<QuestionEntity> getQuestionsByUserId(Long userId) {
        return questionRepository.findByUserId(userId);
    }

    @Cacheable(value = "questions", key = "#keyword + ':' + #page + ':' + #size")
    public Page<QuestionEntity> searchQuestions(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("votes").descending());
        return questionRepository.searchQuestions(keyword, pageable);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Transactional
    public QuestionEntity updateQuestionVotes(Long id, int voteChange) {
        QuestionEntity questionEntity = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        questionEntity.setVotes(questionEntity.getVotes() + voteChange);
        return questionRepository.save(questionEntity);
    }
}