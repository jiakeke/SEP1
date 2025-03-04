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

import static javafx.scene.input.KeyCode.*;
import javafx.application.Platform;
import application.GradeBookView;
import dao.StudentDAO;
import dao.UserDAO;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import model.Student;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.TableViewMatchers;
import java.util.Locale;
import javafx.stage.Window;
import javafx.scene.input.MouseButton;

import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentControllerTest extends ApplicationTest {

    private GradeBookView gradeBookView;
    private StudentController studentController;
    private static Connection conn;


    @Override
    public void start(Stage stage) {
        Locale.setDefault(new Locale("en", "US"));
        gradeBookView = new GradeBookView();
        studentController = new StudentController(gradeBookView);
        gradeBookView.start(stage);
    }

    @BeforeAll
    static void setupDatabase() throws Exception {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        StudentDAO.setConnection(conn);
        UserDAO.setConnection(conn);
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

            stmt.execute("INSERT INTO users (username, password) VALUES ('admin', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f')");
        }

        StudentDAO.registerStudent(new Student(0, "BigAlice", "alice@example.com", "123456"));
        StudentDAO.registerStudent(new Student(0, "Bob", "bob@example.com", "654321"));
        StudentDAO.registerStudent(new Student(0, "Charlie", "charlie@example.com", "111222"));
    }

    void submit(String fieldId) {
        Button button = lookup(fieldId).query();
        interact(() -> button.fire());
    }

    void setText(String fieldId, String text) {
        TextField textField = lookup(fieldId).query();
        interact(() -> textField.setText(text));
    }

    @BeforeEach
    void setup() throws Exception {
        if (conn.isClosed()) {
            conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
            StudentDAO.setConnection(conn);
            UserDAO.setConnection(conn);
        }

        TextField usernameField = lookup("#usernameField").query();
        PasswordField passwordField = lookup("#passwordField").query();
        interact(() -> usernameField.setText("admin"));
        interact(() -> passwordField.setText("password123"));

        submit("#loginButton");

        verifyThat("#studentsButton", hasText("Students"));
    }

    @Test
    void testHandleAddStudent() {
        submit("#studentsButton");

        submit("#add-button");

        setText("#name-field", "Lucy");
        setText("#email-field", "lucy@example.com");
        setText("#phone-field", "111222");

        submit("#submit-button");

        verifyThat("#studentTable", TableViewMatchers.containsRow("Lucy", "lucy@example.com", "111222"));
    }

    @Test
    void testHandleSearch() {
        submit("#studentsButton");

        setText("#search-field", "BigAlice");

        submit("#search-button");

        verifyThat("#studentTable", TableViewMatchers.hasNumRows(1));
        verifyThat("#studentTable", TableViewMatchers.containsRow("BigAlice", "alice@example.com", "123456"));
    }

    @Test
    void testHandleModifyStudent() {
        submit("#studentsButton");

        interact(() -> {
            TableView<Student> studentTable = lookup("#studentTable").query();
            studentTable.getSelectionModel().selectFirst();
        });
        clickOn();
        submit("#modify-button");

        setText("#name-field", "Charlie Modified");
        setText("#email-field", "modified@example.com");
        setText("#phone-field", "987654");

        submit("#save-button");

        verifyThat("#studentTable", TableViewMatchers.containsRow("Charlie Modified", "modified@example.com", "987654"));
    }

    @Test
    void testHandleDeleteStudent() {
        submit("#studentsButton");

        int initialRowCount = lookup("#studentTable").queryTableView().getItems().size();


        interact(() -> {
            TableView<Student> studentTable = lookup("#studentTable").query();
            studentTable.getSelectionModel().selectFirst();
            studentController.handleDeleteStudent(studentTable.getSelectionModel().getSelectedItem(), studentTable);
        });

        submit("#confirmCancelButton");

        verifyThat("#studentTable", TableViewMatchers.hasNumRows(initialRowCount));

        interact(() -> {
            TableView<Student> studentTable = lookup("#studentTable").query();
            studentTable.getSelectionModel().selectFirst();
            studentController.handleDeleteStudent(studentTable.getSelectionModel().getSelectedItem(), studentTable);
            type(ENTER);
        });

        submit("#confirmOkButton");

        verifyThat("#studentTable", TableViewMatchers.hasNumRows(initialRowCount - 1));
    }

    @Test
    void testLoadStudentsWithDBError() {
        try {
            submit("#studentsButton");
            submit("#refresh-button");

            conn.close();
            submit("#refresh-button");

            assertTrue(lookup(".alert").tryQuery().isPresent(), "No alert is shown.");

            verifyThat(lookup(".alert .content").queryLabeled(), hasText("Failed to load students from database."));

        } catch (SQLException e) {
            Assertions.fail("Failed to close H2 connection.");
        }
    }

    @Test
    void testAddStudentWithDBError() {

        submit("#studentsButton");

        submit("#add-button");

        setText("#name-field", "Lucy");
        setText("#email-field", "lucy@example.com");
        setText("#phone-field", "111222");
        try {
            conn.close();
            submit("#submit-button");

            assertTrue(lookup(".alert").tryQuery().isPresent(), "No alert is shown.");

            verifyThat(lookup(".alert .content").queryLabeled(), hasText("Failed to add student."));
        } catch (SQLException e) {
            Assertions.fail("Failed to add student.");
        }
    }

    @Test
    void testHandleSearchWithDBError() {
        try {
            submit("#studentsButton");

            setText("#search-field", "Alice");

            conn.close();
            submit("#search-button");

            assertTrue(lookup(".alert").tryQuery().isPresent(), "No alert is shown.");

            verifyThat(lookup(".alert .content").queryLabeled(), hasText("Failed to search students."));
        } catch (SQLException e) {
            Assertions.fail("Failed to close H2 connection.");
        }
    }

    @Test
    void testHandleModifyStudentWithDBError() {
        try {
            submit("#studentsButton");

            interact(() -> {
                TableView<Student> studentTable = lookup("#studentTable").query();
                studentTable.getSelectionModel().selectFirst();
            });
            clickOn();
            submit("#modify-button");

            setText("#name-field", "Charlie Modified");
            setText("#email-field", "modified@example.com");
            setText("#phone-field", "987654");

            conn.close();
            submit("#save-button");

            assertTrue(lookup(".alert").tryQuery().isPresent(), "No alert is shown.");

            verifyThat(lookup(".alert .content").queryLabeled(), hasText("Failed to update student."));
        } catch (SQLException e) {
            Assertions.fail("Failed to close H2 connection.");
        }
    }

    @Test
    void testHandleDeleteStudentWithDBError() {
        try {
            submit("#studentsButton");

            interact(() -> {
                TableView<Student> studentTable = lookup("#studentTable").query();
                studentTable.getSelectionModel().selectFirst();
            });

            submit("#delete-button");
            conn.close();
            submit("#confirmOkButton");


            assertTrue(lookup(".alert").tryQuery().isPresent(), "No alert is shown.");

            verifyThat(lookup(".alert .content").queryLabeled(), hasText("Failed to delete student."));
        } catch (SQLException e) {
            Assertions.fail("Failed to close H2 connection.");
        }
    }
}
