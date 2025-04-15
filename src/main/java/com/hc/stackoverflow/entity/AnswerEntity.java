package com.hc.stackoverflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @Column(nullable = false)
    private boolean isAccepted = false;

    @Column(nullable = false)
    private int votes = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CommentEntity> comments = new HashSet<>();

    // Helper methods for managing relationships
    public void addComment(CommentEntity comment) {
        comments.add(comment);
        comment.setAnswer(this);
    }

    public void removeComment(CommentEntity comment) {
        comments.remove(comment);
        comment.setAnswer(null);
    }

    // Explicitly define getter and setter for question
    public QuestionEntity getQuestion() {
        return this.question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
}