CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    reference_id BIGINT NOT NULL,
    reference_type VARCHAR(10) NOT NULL CHECK (reference_type IN ('QUESTION', 'ANSWER')),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    -- Add indexes for better query performance
    INDEX idx_reference (reference_id, reference_type),
    INDEX idx_user (user_id)
); 