package com.hc.stackoverflow.service;


import com.hc.stackoverflow.entity.AnswerEntity;
import com.hc.stackoverflow.entity.QuestionEntity;
import com.hc.stackoverflow.repository.AnswerRepository;
import com.hc.stackoverflow.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    @CacheEvict(value = "questions", key = "#answer.question.id")
    public AnswerEntity createAnswer(AnswerEntity answer) {
        QuestionEntity question = questionRepository.findById(answer.getQuestion().getId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Optional<AnswerEntity> existingAnswer = answerRepository.findByQuestionIdAndUserId(
                question.getId(), answer.getUserId());
        if (existingAnswer.isPresent()) {
            throw new RuntimeException("User has already answered this question");
        }

        return answerRepository.save(answer);
    }

    @Cacheable(value = "answers", key = "#id")
    public Optional<AnswerEntity> getAnswerById(Long id) {
        return answerRepository.findById(id);
    }

    @Cacheable(value = "answers", key = "'question:' + #questionId")
    public List<AnswerEntity> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    @Cacheable(value = "answers", key = "'user:' + #userId")
    public List<AnswerEntity> getAnswersByUserId(Long userId) {
        return answerRepository.findByUserId(userId);
    }

    @Cacheable(value = "answers", key = "'top:question:' + #questionId + ':page:' + #page + ':size:' + #size")
    public Page<AnswerEntity> getTopAnswersByQuestionId(Long questionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("votes").descending());
        return answerRepository.findTopAnswersByQuestionId(questionId, pageable);
    }

    @Transactional
    @CacheEvict(value = {"answers", "questions"}, allEntries = true)
    public AnswerEntity markAsAccepted(Long answerId, Long userId) {
        AnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        // Verify the user is the question owner
        QuestionEntity question = answer.getQuestion();
        if (!question.getUserId().equals(userId)) {
            throw new RuntimeException("Only question owner can accept answers");
        }

        // Unaccept any previously accepted answer
        Optional<AnswerEntity> previouslyAccepted = answerRepository.findAcceptedAnswerByQuestionId(question.getId());
        previouslyAccepted.ifPresent(prevAnswer -> {
            prevAnswer.setAccepted(false);
            answerRepository.save(prevAnswer);
        });

        answer.setAccepted(true);
        return answerRepository.save(answer);
    }

    @Transactional
    @CacheEvict(value = {"answers", "questions"}, allEntries = true)
    public AnswerEntity updateAnswerVotes(Long id, int voteChange) {
        AnswerEntity answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        answer.setVotes(answer.getVotes() + voteChange);
        return answerRepository.save(answer);
    }

    @Transactional
    @CacheEvict(value = {"answers", "questions"}, allEntries = true)
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }
}
