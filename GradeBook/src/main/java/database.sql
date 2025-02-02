
DROP DATABASE IF EXISTS gradebook;
CREATE DATABASE gradebook;
USE gradebook;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE group_students (
    group_id INT,
    student_id INT,
    PRIMARY KEY (group_id, student_id),
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE grade_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    weight DOUBLE NOT NULL,
    group_id INT,
    FOREIGN KEY (group_id) REFERENCES groups(id)
);

CREATE TABLE grades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    group_id INT,
    grade_type_id INT,
    grade DOUBLE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (grade_type_id) REFERENCES grade_types(id)
);

DROP USER IF EXISTS 'grade_admin'@'localhost';
CREATE USER 'grade_admin'@'localhost' IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE ON gradebook.* TO 'grade_admin'@'localhost';

FLUSH PRIVILEGES;

