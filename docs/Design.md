# Teacher’s Gradebook and Report Card System

## Overview
This project is a desktop application that allows teachers to manage student grades, calculate averages, and generate report cards. It uses **JavaFX** for the user interface and **MariaDB** for database management, with JDBC for database interaction.

---

## Project Structure

### 1. **Entities**

#### Student
- `id` (Primary Key)
- `name` (String)
- `email` (String)
- `phone` (String)

#### Group
- `id` (Primary Key)
- `name` (String)
- `description` (String)

#### GroupStudents (Many-to-Many Relationship)
- `group_id` (Foreign Key)
- `student_id` (Foreign Key)

#### GradeType
- `id` (Primary Key)
- `name` (String)
- `weight` (Double)
- `group_id` (Foreign Key)

#### Grade
- `id` (Primary Key)
- `student_id` (Foreign Key)
- `group_id` (Foreign Key)
- `grade_type_id` (Foreign Key)
- `grade` (Double)

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
    email VARCHAR(100),
    phone VARCHAR(20),
);

CREATE TABLE GroupStudents (
    group_id INT,
    student_id INT,
    PRIMARY KEY (group_id, student_id),
    FOREIGN KEY (group_id) REFERENCES Group(id),
    FOREIGN KEY (student_id) REFERENCES Student(id)
);

CREATE TABLE GradeType (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    weight DOUBLE NOT NULL,
    group_id INT,
    FOREIGN KEY (group_id) REFERENCES Group(id)
);

CREATE TABLE Grade (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    group_id INT,
    grade_type_id INT,
    grade DOUBLE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(id),
    FOREIGN KEY (group_id) REFERENCES Group(id),
    FOREIGN KEY (grade_type_id) REFERENCES GradeType(id)
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

#### Group-Student Relationship
- Assign students to groups and view students by group.

#### Grade Categories and Weights Management
- Define grade categories (e.g., Assignment, Exam, Project) with weights.

#### Grade Management
- Add, view, update, and delete grades for each student.
- Record grades with categories: (e.g., Assignment, Exam, Project).
- Calculate overall average and weighted average.

#### Report Card
- Generate a performance report for each student.
- Include breakdown by grade category (Assignment, Exam, Project).
- Export report cards as PDF files.
- Print report cards.

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
   - Buttons: Add Group, Edit Group, Delete Group, Manage Students

4. **Student Management**
   - TableView: List all students with search and filter by group
   - Buttons: Add Student, Edit Student, Delete Student

5. **Grade Management**
   - TableView: List grades by student
   - Fields: Assignment, Exam, Project, Grade
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
    private String email;
    private String phone;

    // Getters and Setters
}

public class GroupStudents {
    private int groupId;
    private int studentId;

    // Getters and Setters
}

public class GradeType {
    private int id;
    private String name;
    private double weight;
    private int groupId;

    // Getters and Setters
}

public class Grade {
    private int id;
    private int studentId;
    private int groupId;
    private int gradeTypeId;
    private double grade;

    // Getters and Setters
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
