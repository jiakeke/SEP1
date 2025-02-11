//package controller;
//
//import dao.StudentDAO;
//import javafx.application.Platform;
//import javafx.scene.Scene;
//import javafx.scene.control.TableView;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import model.Student;
//import org.junit.jupiter.api.*;
//import org.testfx.framework.junit5.ApplicationTest;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class StudentControllerTest extends ApplicationTest {
//    private StudentController studentController;
//    private TableView<Student> studentTable;
//    private static Connection conn;
//
//    @Override
//    public void start(Stage stage) {
//        studentTable = new TableView<>();
//        studentController = new StudentController(null);
//
//        VBox root = new VBox(studentTable);
//        Scene scene = new Scene(root, 600, 400);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    @BeforeAll
//    static void setupDatabase() throws SQLException, ClassNotFoundException {
//        Class.forName("org.h2.Driver");
//        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
//
//        // Use H2 in-memory database to create a table
//        StudentDAO.setConnection(conn);
//
//        try (Statement stmt = conn.createStatement()) {
//            String createTable = "CREATE TABLE IF NOT EXISTS students (" +
//                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
//                    "name VARCHAR(255), " +
//                    "email VARCHAR(255), " +
//                    "phone VARCHAR(20)" +
//                    ")";
//            stmt.execute(createTable);
//        }
//    }
//
//    @BeforeEach
//    void setup() {
//        studentTable.getItems().clear();
//    }
//
//    @Test
//    void testHandleSearch() throws SQLException {
//        StudentDAO.registerStudent(new Student(1, "Alice", "alice@example.com", "123456"));
//        studentController.handleSearch("Alice", studentTable);
//
//        List<Student> students = studentTable.getItems();
//        assertEquals(1, students.size());
//        assertEquals("Alice", students.get(0).getName());
//    }
//
//    @Test
//    void testHandleAddStudent() throws SQLException {
//        Platform.runLater(() -> {
//            try {
//                int initialSize = StudentDAO.showAllStudents().size();
//
//                // Add a new student
//                Student newStudent = new Student(0, "Charlie", "charlie@example.com", "123456");
//                StudentDAO.registerStudent(newStudent);
//
//                int newSize = StudentDAO.showAllStudents().size();
//
//                // Make sure the student data added is correct
//                assertEquals(initialSize + 1, newSize);
//
//                // Make sure the student is in the database
//                assertTrue(StudentDAO.showAllStudents().stream()
//                        .anyMatch(s -> s.getName().equals("Charlie") && s.getEmail().equals("charlie@example.com"))
//                );
//
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
//
//
//    @Test
//    void testHandleDeleteStudent() throws SQLException {
//        Platform.runLater(() -> {
//            try {
//                // Get the initial number of students in the database
//                int initialSize = StudentDAO.showAllStudents().size();
//
//                // If there are no students, skip the test
//                if (initialSize == 0) {
//                    System.out.println("No students to delete. Skipping test.");
//                    return;
//                }
//
//                // Get the first student in the database
//                Student student = StudentDAO.showAllStudents().get(0);
//                assertNotNull(student, "Student should not be null before deletion.");
//
//                // Delete the student
//                StudentDAO.deleteStudent(student.getId());
//
//                int newSize = StudentDAO.showAllStudents().size();
//
//                assertEquals(initialSize - 1, newSize);
//
//                assertFalse(StudentDAO.showAllStudents().stream()
//                        .anyMatch(s -> s.getId() == student.getId()));
//
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
//
//}

package controller;

import application.GradeBookView;
import dao.StudentDAO;
import javafx.scene.control.Button;
import model.Student;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.TableViewMatchers;

import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentControllerTest extends ApplicationTest {

    private GradeBookView gradeBookView;
    private StudentController studentController;
    private static Connection conn;

    @Override
    public void start(Stage stage) {
        gradeBookView = new GradeBookView();
        studentController = new StudentController(gradeBookView);
        gradeBookView.start(stage);
    }

    @BeforeAll
    static void setupDatabase() throws Exception {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        StudentDAO.setConnection(conn);
        System.out.println("H2 数据库已初始化：" + conn.getMetaData().getURL());
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS students ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255), "
                    + "email VARCHAR(255), "
                    + "phone VARCHAR(20))");

            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(255) NOT NULL, "
                    + "password VARCHAR(255) NOT NULL)");
        }

        StudentDAO.registerStudent(new Student(0, "Alice", "alice@example.com", "123456"));
        StudentDAO.registerStudent(new Student(0, "Bob", "bob@example.com", "654321"));
        StudentDAO.registerStudent(new Student(0, "Charlie", "charlie@example.com", "111222"));
    }

    @BeforeEach
    void setup() throws Exception {

        clickOn("#usernameField").write("admin");
        clickOn("#passwordField").write("password123");
        clickOn("#loginButton");

        verifyThat("#studentsButton", hasText("Students"));
    }

    @Test
    void testHandleAddStudent() {
        clickOn("#studentsButton");

        clickOn("#add-button");

        clickOn("#name-field").write("Lucy");
        clickOn("#email-field").write("lucy@example.com");
        clickOn("#phone-field").write("111222");

        clickOn("#submit-button");

        verifyThat("#studentTable", TableViewMatchers.containsRow("Lucy", "lucy@example.com", "111222"));
    }

    @Test
    void testHandleSearch() {
        clickOn("#studentsButton");

        clickOn("#search-field").write("Alice");

        clickOn("#search-button");

        verifyThat("#studentTable", TableViewMatchers.hasNumRows(1));
        verifyThat("#studentTable", TableViewMatchers.containsRow("Alice", "alice@example.com", "123456"));
    }

    @Test
    void testHandleModifyStudent() {
        clickOn("#studentsButton");

        interact(() -> {
            TableView<Student> studentTable = lookup("#studentTable").query();
            studentTable.getSelectionModel().selectFirst();
        });
        rightClickOn();
        clickOn("#edit-item");

        doubleClickOn("#name-field").eraseText(7).write("Charlie Modified");
        doubleClickOn("#email-field").eraseText(19).write("modified@example.com");
        doubleClickOn("#phone-field").eraseText(6).write("987654");

        clickOn("#save-button");

        verifyThat("#studentTable", TableViewMatchers.containsRow("Charlie Modified", "modified@example.com", "987654"));
    }

    @Test
    void testHandleDeleteStudent() {
        clickOn("#studentsButton");

        interact(() -> {
            TableView<Student> studentTable = lookup("#studentTable").query();
            studentTable.getSelectionModel().selectFirst();
        });

        rightClickOn();

        clickOn("#delete-item");

        Button okButton = lookup(".button").match(hasText("确定")).queryButton();
        clickOn(okButton);

        verifyThat("#studentTable", TableViewMatchers.hasNumRows(3));
    }
}
