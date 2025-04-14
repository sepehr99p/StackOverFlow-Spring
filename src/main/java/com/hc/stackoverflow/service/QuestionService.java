package com.hc.stackoverflow.service;


import com.hc.stackoverflow.entity.Question;
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
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Cacheable(value = "questions", key = "#id")
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    @Cacheable(value = "questions", key = "'all'")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Cacheable(value = "questions", key = "#userId")
    public List<Question> getQuestionsByUserId(Long userId) {
        return questionRepository.findByUserId(userId);
    }

    @Cacheable(value = "questions", key = "#keyword + ':' + #page + ':' + #size")
    public Page<Question> searchQuestions(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("votes").descending());
        return questionRepository.searchQuestions(keyword, pageable);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Transactional
    public Question updateQuestionVotes(Long id, int voteChange) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setVotes(question.getVotes() + voteChange);
        return questionRepository.save(question);
    }
}