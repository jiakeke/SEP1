
DROP DATABASE IF EXISTS gradebook;
CREATE DATABASE gradebook;
USE gradebook;


DROP USER IF EXISTS 'grade_admin'@'localhost';
CREATE USER 'grade_admin'@'localhost' IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE ON exchanger.* TO 'grade_admin'@'localhost';

FLUSH PRIVILEGES;

