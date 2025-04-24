package com.hc.stackoverflow.service;

import com.hc.stackoverflow.entity.CommentEntity;
import com.hc.stackoverflow.entity.QuestionCommentEntity;
import com.hc.stackoverflow.entity.AnswerCommentEntity;
import com.hc.stackoverflow.entity.QuestionEntity;
import com.hc.stackoverflow.entity.AnswerEntity;
import com.hc.stackoverflow.entity.UserEntity;
import com.hc.stackoverflow.exception.ResourceNotFoundException;
import com.hc.stackoverflow.repository.CommentRepository;
import com.hc.stackoverflow.repository.QuestionCommentRepository;
import com.hc.stackoverflow.repository.AnswerCommentRepository;
import com.hc.stackoverflow.repository.QuestionRepository;
import com.hc.stackoverflow.repository.AnswerRepository;
import com.hc.stackoverflow.repository.UserRepository;
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
public class CommentService {
    private final CommentRepository commentRepository;
    private final QuestionCommentRepository questionCommentRepository;
    private final AnswerCommentRepository answerCommentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuestionCommentEntity createQuestionComment(Long questionId, String content, Long userId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        QuestionCommentEntity comment = new QuestionCommentEntity();
        comment.setContent(content);
        comment.setQuestion(question);
        comment.setUserId(userId);

        return questionCommentRepository.save(comment);
    }

    @Transactional
    public AnswerCommentEntity createAnswerComment(Long answerId, String content, Long userId) {
        AnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AnswerCommentEntity comment = new AnswerCommentEntity();
        comment.setContent(content);
        comment.setAnswer(answer);
        comment.setUserId(userId);

        return answerCommentRepository.save(comment);
    }

    @Cacheable(value = "comments", key = "#id")
    public Optional<CommentEntity> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @Cacheable(value = "comments", key = "'question:' + #questionId")
    public List<CommentEntity> getCommentsByQuestionId(Long questionId) {
        return commentRepository.findByQuestionId(questionId);
    }

    @Cacheable(value = "comments", key = "'answer:' + #answerId")
    public List<CommentEntity> getCommentsByAnswerId(Long answerId) {
        return commentRepository.findByAnswerId(answerId);
    }

    @Cacheable(value = "comments", key = "'user:' + #userId")
    public List<CommentEntity> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    @Cacheable(value = "comments", key = "'question:' + #questionId + ':page:' + #page + ':size:' + #size")
    public Page<CommentEntity> getCommentsByQuestionId(Long questionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepository.findCommentsByQuestionId(questionId, pageable);
    }

    @Cacheable(value = "comments", key = "'answer:' + #answerId + ':page:' + #page + ':size:' + #size")
    public Page<CommentEntity> getCommentsByAnswerId(Long answerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepository.findCommentsByAnswerId(answerId, pageable);
    }

    @Transactional
    @CacheEvict(value = {"comments", "questions", "answers"}, allEntries = true)
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public List<QuestionCommentEntity> getQuestionComments(Long questionId) {
        return questionCommentRepository.findByQuestionId(questionId);
    }

    public List<AnswerCommentEntity> getAnswerComments(Long answerId) {
        return answerCommentRepository.findByAnswerId(answerId);
    }
}
