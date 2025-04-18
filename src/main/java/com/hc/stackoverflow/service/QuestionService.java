package com.hc.stackoverflow.service;


import com.hc.stackoverflow.entity.QuestionEntity;
import com.hc.stackoverflow.entity.UserEntity;
import com.hc.stackoverflow.exception.ResourceNotFoundException;
import com.hc.stackoverflow.repository.QuestionRepository;
import com.hc.stackoverflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "questions", allEntries = true)
    public QuestionEntity createQuestion(QuestionEntity question) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        question.setUserId(user.getId());
        return questionRepository.save(question);
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
    @CacheEvict(value = "questions", allEntries = true)
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = "questions", allEntries = true)
    public QuestionEntity updateQuestion(Long id, QuestionEntity updatedQuestion) {
        QuestionEntity existingQuestion = getQuestionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        existingQuestion.setTitle(updatedQuestion.getTitle());
        existingQuestion.setDescription(updatedQuestion.getDescription());
        existingQuestion.setTags(updatedQuestion.getTags());

        return questionRepository.save(existingQuestion);
    }

    @Transactional
    @CacheEvict(value = "questions", allEntries = true)
    public QuestionEntity updateQuestionVotes(Long id, int voteChange) {
        QuestionEntity question = getQuestionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        question.setVotes(question.getVotes() + voteChange);
        return questionRepository.save(question);
    }

    public void checkQuestionOwnership(Long questionId, Long userId) {
        QuestionEntity question = getQuestionById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if (!question.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to modify this question");
        }
    }
}