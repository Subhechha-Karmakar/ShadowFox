-- ============================================================
--  Library Management System — Database Schema
--  Run this ONCE against your MySQL instance before starting
--  the application for the first time.
--
--  MySQL 8.0+  |  Charset: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS library_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_db;

-- ─────────────────────────────────────────────────────────────
--  AUTHORS  (normalized — not stored in books table)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS authors (
    author_id  INT          AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(200) NOT NULL,
    UNIQUE KEY uq_author_name (name)
) ENGINE=InnoDB;

-- ─────────────────────────────────────────────────────────────
--  BOOKS
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS books (
    book_id       INT          AUTO_INCREMENT PRIMARY KEY,
    isbn          VARCHAR(20)  UNIQUE,
    title         VARCHAR(300) NOT NULL,
    author_id     INT          NOT NULL,
    genre         VARCHAR(100),
    total_copies  INT          NOT NULL DEFAULT 1,
    avail_copies  INT          NOT NULL DEFAULT 1,
    CONSTRAINT fk_book_author FOREIGN KEY (author_id)
        REFERENCES authors (author_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ─────────────────────────────────────────────────────────────
--  USERS
--  password_hash : SHA-256 hex digest of the plain-text password
--  role          : ADMIN can manage books/users; MEMBER can borrow
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id       INT          AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(64)  NOT NULL,
    full_name     VARCHAR(200),
    email         VARCHAR(200),
    role          ENUM('ADMIN','MEMBER') NOT NULL DEFAULT 'MEMBER',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_username (username)
) ENGINE=InnoDB;

-- ─────────────────────────────────────────────────────────────
--  BORROW RECORDS
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS borrow_records (
    record_id   INT  AUTO_INCREMENT PRIMARY KEY,
    user_id     INT  NOT NULL,
    book_id     INT  NOT NULL,
    borrow_date DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE DEFAULT NULL,          -- NULL means still on loan
    CONSTRAINT fk_br_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_br_book FOREIGN KEY (book_id)
        REFERENCES books (book_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ─────────────────────────────────────────────────────────────
--  FINES
--  Computed and stored when a book is returned overdue.
--  Rate: 5.00 per day (configurable in BorrowService.java)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS fines (
    fine_id   INT            AUTO_INCREMENT PRIMARY KEY,
    record_id INT            NOT NULL,
    amount    DECIMAL(10, 2) NOT NULL,
    paid      BOOLEAN        NOT NULL DEFAULT FALSE,
    UNIQUE KEY uq_fine_record (record_id),
    CONSTRAINT fk_fine_record FOREIGN KEY (record_id)
        REFERENCES borrow_records (record_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ─────────────────────────────────────────────────────────────
--  SEED DATA — default admin account
--  username : admin
--  password : admin123   (SHA-256 hex stored below)
-- ─────────────────────────────────────────────────────────────
INSERT IGNORE INTO users (username, password_hash, full_name, email, role)
VALUES (
    'admin',
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',  -- SHA-256("admin123")
    'System Administrator',
    'admin@library.local',
    'ADMIN'
);

-- ─────────────────────────────────────────────────────────────
--  SEED DATA — sample authors & books
-- ─────────────────────────────────────────────────────────────
INSERT IGNORE INTO authors (name) VALUES
    ('J.K. Rowling'),
    ('George Orwell'),
    ('Yuval Noah Harari'),
    ('Robert C. Martin'),
    ('Agatha Christie');

INSERT IGNORE INTO books (isbn, title, author_id, genre, total_copies, avail_copies) VALUES
    ('9780439708180', 'Harry Potter and the Sorcerer''s Stone', 1, 'Fantasy',   3, 3),
    ('9780451524935', '1984',                                   2, 'Dystopian', 2, 2),
    ('9780062316110', 'Sapiens',                                3, 'History',   2, 2),
    ('9780132350884', 'Clean Code',                             4, 'Technology',2, 2),
    ('9780062073495', 'And Then There Were None',               5, 'Mystery',   2, 2);
