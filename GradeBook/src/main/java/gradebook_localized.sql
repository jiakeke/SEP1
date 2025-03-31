DROP DATABASE IF EXISTS `gradebook_localized`;
CREATE DATABASE `gradebook_localized` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `gradebook_localized`;

CREATE TABLE `students` (
`id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;

CREATE TABLE `users` (
`id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB;

CREATE TABLE `groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users`(`id`)
) ENGINE = InnoDB;

CREATE TABLE `grade_types` (
`id` int(11) NOT NULL AUTO_INCREMENT,
  `weight` double NOT NULL,
  `group_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  CONSTRAINT `grade_types_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE `grades` (
`id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` int(11) NULL DEFAULT NULL,
  `group_id` int(11) NULL DEFAULT NULL,
  `grade_type_id` int(11) NULL DEFAULT NULL,
  `grade` double NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `grade_type_id`(`grade_type_id`) USING BTREE,
  CONSTRAINT `grades_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `grades_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `grades_ibfk_3` FOREIGN KEY (`grade_type_id`) REFERENCES `grade_types` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE `group_students` (
`group_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  PRIMARY KEY (`group_id`, `student_id`) USING BTREE,
  INDEX `student_id`(`student_id`) USING BTREE,
  CONSTRAINT `group_students_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `group_students_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;




CREATE TABLE `group_localized` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `group_id` INT NOT NULL,
  `lang` CHAR(2) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  FOREIGN KEY (`group_id`) REFERENCES `groups`(`id`),
  UNIQUE (`group_id`, `lang`)
) ENGINE = InnoDB;
        


CREATE TABLE `grade_type_localized` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `grade_type_id` INT NOT NULL,
  `lang` CHAR(2) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  FOREIGN KEY (`grade_type_id`) REFERENCES `grade_types`(`id`),
  UNIQUE (`grade_type_id`, `lang`)
) ENGINE = InnoDB;
        