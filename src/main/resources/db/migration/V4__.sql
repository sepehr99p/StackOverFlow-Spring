CREATE TABLE answers
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    `description` TEXT   NOT NULL,
    user_id       BIGINT NOT NULL,
    question_id   BIGINT NOT NULL,
    is_accepted   BIT(1) NOT NULL,
    votes         INT    NOT NULL,
    created_at    datetime NULL,
    updated_at    datetime NULL,
    CONSTRAINT pk_answers PRIMARY KEY (id)
);

ALTER TABLE answers
    ADD CONSTRAINT FK_ANSWERS_ON_QUESTION FOREIGN KEY (question_id) REFERENCES questions (id);