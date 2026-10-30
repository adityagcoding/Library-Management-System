CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS Books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    quantity INT DEFAULT 1,
    available INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS Members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(15),
    email VARCHAR(100),
    membership_date DATE
);

CREATE TABLE IF NOT EXISTS Issues (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT,
    member_id INT,
    issue_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'Issued',
    FOREIGN KEY (book_id) REFERENCES Books(book_id),
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);

CREATE TABLE IF NOT EXISTS Admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
);

-- Insert default admin
INSERT INTO Admin (username, password) VALUES ('admin', 'admin123') 
ON DUPLICATE KEY UPDATE username=username;

-- Insert sample books
INSERT INTO Books (title, author, category, quantity, available) VALUES 
('The Great Gatsby', 'F. Scott Fitzgerald', 'Fiction', 3, 3),
('To Kill a Mockingbird', 'Harper Lee', 'Fiction', 2, 2),
('1984', 'George Orwell', 'Science Fiction', 5, 5),
('Pride and Prejudice', 'Jane Austen', 'Romance', 4, 4),
('The Catcher in the Rye', 'J.D. Salinger', 'Fiction', 3, 3);

-- Insert sample members
INSERT INTO Members (name, contact, email, membership_date) VALUES 
('John Doe', '1234567890', 'john@example.com', CURDATE()),
('Jane Smith', '0987654321', 'jane@example.com', CURDATE()),
('Alice Johnson', '5551234567', 'alice@example.com', CURDATE());
