# Teacher’s Gradebook and Report Card System

## Overview
This project is a desktop application that allows teachers to manage student grades, calculate averages, and generate report cards. It uses **JavaFX** for the user interface and **MariaDB** for database management, with JDBC for database interaction.

---

## Project Structure

### 1. **Entities**

#### Student
- `id` (Primary Key)
- `name` (String)
- `group_id` (Foreign Key)
- `email` (String)
- `phone` (String)

#### Group
- `id` (Primary Key)
- `name` (String)
- `description` (String)

#### Grade
- `id` (Primary Key)
- `student_id` (Foreign Key)
- `type` (Enum: ASSIGNMENT, EXAM, PROJECT)
- `grade` (Double)
- `weight` (Double)  

#### User (For authentication)
- `id` (Primary Key)
- `username` (String, Unique)
- `password` (String, Hashed)

---

### 2. **Database Schema**
```sql
CREATE TABLE User (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE Group (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE Student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    group_id INT,
    email VARCHAR(100),
    phone VARCHAR(20),
    FOREIGN KEY (group_id) REFERENCES Group(id)
);

CREATE TABLE Grade (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    type ENUM('ASSIGNMENT', 'EXAM', 'PROJECT') NOT NULL,
    grade DOUBLE NOT NULL,
    weight DOUBLE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(id)
);
```

---

### 3. **Features and Functionalities**

#### Authentication
- **Registration**: New users can create an account.
- **Login**: Existing users can log in.

#### CRUD Operations
- **Group**: Create, Read, Update, Delete groups of students.
- **Student**: Manage individual student records.

#### Grade Management
- Add, view, update, and delete grades for each student.
- Record grades with categories: Assignment, Exam, Project.
- Assign weights to each category for calculating averages.
- Calculate overall average and weighted average.

#### Report Card
- Generate a performance report for each student.
- Include breakdown by grade category (Assignment, Exam, Project).
- Export report cards as PDF files.

---

## JavaFX User Interface Design

### Screens
1. **Login Screen**
   - Fields: Username, Password
   - Buttons: Login, Register

2. **Dashboard**
   - Menu Options: Manage Groups, Manage Students, View Grades, Generate Reports

3. **Group Management**
   - TableView: List all groups
   - Buttons: Add Group, Edit Group, Delete Group

4. **Student Management**
   - TableView: List all students with search and filter by group
   - Buttons: Add Student, Edit Student, Delete Student

5. **Grade Management**
   - TableView: List grades by student
   - Fields: Assignment, Exam, Project, Grade, Weight
   - Buttons: Add Grade, Edit Grade, Delete Grade
   - Summary: Display average grade and weighted average

6. **Report Card Generation**
   - Dropdown: Select Student
   - Display: Grades (categorized by type), averages, and remarks
   - Buttons: Export to PDF, Print

---

## Java Class Design

### Entities
```java
public class User {
    private int id;
    private String username;
    private String password;

    // Getters and Setters
}

public class Group {
    private int id;
    private String name;
    private String description;

    // Getters and Setters
}

public class Student {
    private int id;
    private String name;
    private int groupId;
    private String email;
    private String phone;

    // Getters and Setters
}

public class Grade {
    private int id;
    private int studentId;
    private GradeType type; // ASSIGNMENT, EXAM, PROJECT
    private double grade;
    private double weight;

    // Getters and Setters
}

public enum GradeType {
    ASSIGNMENT, EXAM, PROJECT
}
```

---

### Database Utility Class
```java
public class DatabaseUtil {
    private static final String URL = "jdbc:mariadb://localhost:3306/gradebook";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

---

### DAO Classes

#### UserDAO
```java
public class UserDAO {
    public boolean authenticate(String username, String password) {
        // Logic to verify username and hashed password from the database
    }

    public void register(User user) {
        // Logic to insert a new user into the database
    }
}
```

#### StudentDAO, GroupDAO, GradeDAO
- Perform CRUD operations for respective entities.

---

### Report Card Generator
```java
public class ReportCardGenerator {
    public void generateReport(int studentId) {
        // Fetch student data, grades, and calculate averages
        // Calculate weighted averages based on type and weights
        // Generate a PDF report using a library like iText
    }
}
```

---

### Application Entry Point
```java
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Load Login Screen
        primaryStage.setTitle("Teacher's Gradebook");
        primaryStage.setScene(new Scene(new LoginScreen()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

---

## Additional Notes
- **Password Security**: Hash passwords using a secure hashing algorithm (e.g., bcrypt).
- **PDF Generation**: Use a Java library like iText for exporting report cards.
- **Error Handling**: Ensure proper error handling for database operations and input validations.
- **Styling**: Use JavaFX CSS for a polished UI design.

## Deployment
1. Set up MariaDB and create the required schema.
2. Package the application as a JAR file.
3. Provide installation instructions for the database setup and application.

---
