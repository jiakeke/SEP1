package controller;

import dao.StudentDAO;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Student;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentControllerTest extends ApplicationTest {
    private StudentController studentController;
    private TableView<Student> studentTable;
    private static Connection conn;

    @Override
    public void start(Stage stage) {
        studentTable = new TableView<>();
        studentController = new StudentController(null);

        VBox root = new VBox(studentTable);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    static void setupDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

        // Use H2 in-memory database to create a table
        StudentDAO.setConnection(conn);

        try (Statement stmt = conn.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS students (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "phone VARCHAR(20)" +
                    ")";
            stmt.execute(createTable);
        }
    }

    @BeforeEach
    void setup() {
        studentTable.getItems().clear();
    }

    @Test
    void testHandleSearch() throws SQLException {
        StudentDAO.registerStudent(new Student(1, "Alice", "alice@example.com", "123456"));
        studentController.handleSearch("Alice", studentTable);

        List<Student> students = studentTable.getItems();
        assertEquals(1, students.size());
        assertEquals("Alice", students.get(0).getName());
    }

    @Test
    void testHandleAddStudent() throws SQLException {
        Platform.runLater(() -> {
            try {
                int initialSize = StudentDAO.showAllStudents().size();

                // Add a new student
                Student newStudent = new Student(0, "Charlie", "charlie@example.com", "123456");
                StudentDAO.registerStudent(newStudent);

                int newSize = StudentDAO.showAllStudents().size();

                // Make sure the student data added is correct
                assertEquals(initialSize + 1, newSize);

                // Make sure the student is in the database
                assertTrue(StudentDAO.showAllStudents().stream()
                        .anyMatch(s -> s.getName().equals("Charlie") && s.getEmail().equals("charlie@example.com"))
                );

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    void testHandleDeleteStudent() throws SQLException {
        Platform.runLater(() -> {
            try {
                // Get the initial number of students in the database
                int initialSize = StudentDAO.showAllStudents().size();

                // If there are no students, skip the test
                if (initialSize == 0) {
                    System.out.println("No students to delete. Skipping test.");
                    return;
                }

                // Get the first student in the database
                Student student = StudentDAO.showAllStudents().get(0);
                assertNotNull(student, "Student should not be null before deletion.");

                // Delete the student
                StudentDAO.deleteStudent(student.getId());

                int newSize = StudentDAO.showAllStudents().size();

                assertEquals(initialSize - 1, newSize);

                assertFalse(StudentDAO.showAllStudents().stream()
                        .anyMatch(s -> s.getId() == student.getId()));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
