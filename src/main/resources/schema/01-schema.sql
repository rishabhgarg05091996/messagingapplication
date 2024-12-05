-- Users table to store user information
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Messages table to store message details
CREATE TABLE messages (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          sender_id INT NOT NULL,
                          title VARCHAR(255) NOT NULL,
                          body TEXT NOT NULL,
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Recipients table to establish many-to-many relationship
CREATE TABLE message_recipients (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    message_id INT NOT NULL,
                                    recipient_id INT NOT NULL,
                                    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
                                    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
                                    UNIQUE (message_id, recipient_id)
);

-- Indexes for performance
CREATE INDEX idx_recipient_id ON message_recipients (recipient_id);
CREATE INDEX idx_sent_at ON messages (sent_at);
