package dao;

import model.Student;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentDAOTest {
    private static Connection conn;

    @BeforeAll
    static void setupDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

        StudentDAO.setConnection(conn);
        GradeDAO.setConnection(conn);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "phone VARCHAR(20))");

            stmt.execute("CREATE TABLE IF NOT EXISTS grades (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT, " +
                    "group_id INT, " +
                    "grade_type_id INT, " +
                    "grade DOUBLE NOT NULL, " +
                    "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS group_students (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT, " +
                    "group_id INT, " +
                    "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE)");

        }
    }

    @BeforeEach
    void resetDatabase() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM students");
            stmt.execute("ALTER TABLE students ALTER COLUMN id RESTART WITH 1");
            stmt.execute("INSERT INTO students (name, email, phone) VALUES ('Alice', 'alice@example.com', '123456')");
            stmt.execute("INSERT INTO students (name, email, phone) VALUES ('Bob', 'bob@example.com', '654321')");
        }
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        conn.close();
    }


    @Test
    void testRegisterStudent() throws SQLException {
        Student newStudent = new Student(0, "Charlie", "charlie@example.com", "987654");
        StudentDAO.registerStudent(newStudent);

        List<Student> students = StudentDAO.showAllStudents();
        assertEquals(3, students.size());
        assertEquals("Charlie", students.get(2).getName());
    }

    @Test
    void testUpdateStudent() throws SQLException {
        Student updatedStudent = new Student(1, "Alice Updated", "alice.updated@example.com", "111111");
        StudentDAO.updateStudent(updatedStudent);

        List<Student> students = StudentDAO.showAllStudents();
        assertEquals("Alice Updated", students.get(0).getName());
        assertEquals("alice.updated@example.com", students.get(0).getEmail());
    }

    @Test
    void testDeleteStudent() throws SQLException {
        StudentDAO.deleteStudent(1);
        List<Student> students = StudentDAO.showAllStudents();
        assertEquals(1, students.size());
        assertEquals("Bob", students.get(0).getName());
    }

    @Test
    void testShowAllStudents() throws SQLException {
        List<Student> students = StudentDAO.showAllStudents();
        assertEquals(2, students.size());
    }

    @Test
    void testSearchStudentByName() throws SQLException {
        List<Student> students = StudentDAO.searchStudentByName("Alice");
        assertEquals(1, students.size());
        assertEquals("Alice", students.get(0).getName());
    }

    @Test
    void testGetStudentsByGroupId() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS group_students (group_id INT, student_id INT)");
            stmt.execute("INSERT INTO group_students (group_id, student_id) VALUES (1, 1)");
            stmt.execute("INSERT INTO group_students (group_id, student_id) VALUES (1, 2)");
        }

        List<Student> students = StudentDAO.getStudentsByGroupId(1);

        assertEquals(2, students.size());
        assertEquals("Alice", students.get(0).getName());
        assertEquals("Bob", students.get(1).getName());
    }
}
