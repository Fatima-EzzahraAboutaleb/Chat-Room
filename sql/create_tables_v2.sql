-- User authentication table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Chat messages table
CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    chat_id INT NOT NULL DEFAULT 1,
    message_type VARCHAR(20) DEFAULT 'DIRECT',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_id) REFERENCES chats(id)
);

-- New groups table for group chat support
CREATE TABLE IF NOT EXISTS chats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    chat_name VARCHAR(255) NOT NULL,
    chat_type VARCHAR(20) DEFAULT 'GROUP',
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- New table to map users to groups/chats
CREATE TABLE IF NOT EXISTS chat_members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    chat_id INT NOT NULL,
    username VARCHAR(100) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_id) REFERENCES chats(id),
    UNIQUE KEY unique_member (chat_id, username)
);


