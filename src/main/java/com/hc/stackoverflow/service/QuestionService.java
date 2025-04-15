package com.hc.stackoverflow.service;


import com.hc.stackoverflow.entity.QuestionEntity;
import com.hc.stackoverflow.exception.ResourceNotFoundException;
import com.hc.stackoverflow.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        return questionRepository.save(questionEntity);
    }

    @Cacheable(value = "questions", key = "#id")
    public QuestionEntity getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
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
    public void deleteQuestion(Long id, Long userId) {
        QuestionEntity question = getQuestionById(id);
        if (!question.getUserId().equals(userId)) {
            throw new AccessDeniedException("You can only delete your own questions");
        }
        questionRepository.deleteById(id);
    }

    @Transactional
    public QuestionEntity updateQuestionVotes(Long id, int voteChange) {
        QuestionEntity questionEntity = getQuestionById(id);
        questionEntity.setVotes(questionEntity.getVotes() + voteChange);
        return questionRepository.save(questionEntity);
    }
}