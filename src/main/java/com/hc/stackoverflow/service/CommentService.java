package com.hc.stackoverflow.service;

import com.hc.stackoverflow.entity.CommentEntity;
import com.hc.stackoverflow.repository.CommentRepository;
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

    @Transactional
    @CacheEvict(value = {"comments", "questions", "answers"}, allEntries = true)
    public CommentEntity createComment(CommentEntity comment) {
        // Validate that either question or answer is present, but not both
        if ((comment.getQuestion() == null && comment.getAnswer() == null) ||
                (comment.getQuestion() != null && comment.getAnswer() != null)) {
            throw new RuntimeException("Comment must be associated with either a question or an answer, but not both");
        }

        return commentRepository.save(comment);
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
}
