-- Users table
CREATE TABLE users
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Questions table
CREATE TABLE questions
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    user_id     BIGINT       NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_user (user_id),
    INDEX idx_created (created_at)
);

-- Answers table
CREATE TABLE answers
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT   NOT NULL,
    question_id BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    is_accepted BOOLEAN   DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_question (question_id),
    INDEX idx_user (user_id)
);

-- Question Comments table
CREATE TABLE question_comments
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    content     TEXT   NOT NULL,
    question_id BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_question (question_id),
    INDEX idx_user (user_id)
);

-- Answer Comments table
CREATE TABLE answer_comments
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    content    TEXT   NOT NULL,
    answer_id  BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (answer_id) REFERENCES answers (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_answer (answer_id),
    INDEX idx_user (user_id)
);

-- Comments table (generic approach - optional)
CREATE TABLE comments
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    content        TEXT        NOT NULL,
    reference_id   BIGINT      NOT NULL,
    reference_type VARCHAR(10) NOT NULL CHECK (reference_type IN ('QUESTION', 'ANSWER')),
    user_id        BIGINT      NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_reference (reference_id, reference_type),
    INDEX idx_user (user_id)
);

-- Votes table
CREATE TABLE votes
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_id   BIGINT      NOT NULL,
    reference_type VARCHAR(10) NOT NULL CHECK (reference_type IN ('QUESTION', 'ANSWER')),
    user_id        BIGINT      NOT NULL,
    vote_type      VARCHAR(4)  NOT NULL CHECK (vote_type IN ('UP', 'DOWN')),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    UNIQUE KEY unique_vote (user_id, reference_id, reference_type),
    INDEX idx_reference (reference_id, reference_type),
    INDEX idx_user (user_id)
);

-- Tags table
CREATE TABLE tags
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);

-- Question Tags junction table
CREATE TABLE question_tags
(
    question_id BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (question_id, tag_id),
    FOREIGN KEY (question_id) REFERENCES questions (id),
    FOREIGN KEY (tag_id) REFERENCES tags (id),
    INDEX idx_question (question_id),
    INDEX idx_tag (tag_id)
);