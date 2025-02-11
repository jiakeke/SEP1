package controller;

import application.GradeBookView;
import dao.StudentDAO;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentControllerExceptionTest extends ApplicationTest {

    private GradeBookView gradeBookView;
    private StudentController studentController;
    private Connection h2Connection;

    @Override
    public void start(Stage stage) {
        gradeBookView = new GradeBookView();
        studentController = new StudentController(gradeBookView);
        gradeBookView.start(stage);
    }

    @BeforeEach
    void setup() throws SQLException {
        // **连接 H2 内存数据库**
        h2Connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        StudentDAO.setConnection(h2Connection); // 让 DAO 使用 H2 而不是 MariaDB

        // **登录系统**
        clickOn("#usernameField").write("admin");
        clickOn("#passwordField").write("password123");
        clickOn("#loginButton");

        // **确认进入主页面**
        verifyThat("#studentsButton", hasText("Students"));
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (h2Connection != null) {
            h2Connection.close();
        }
    }

    @Test
    void testLoadStudentsWithDBError() {
        try {
            h2Connection.close();

            clickOn("#studentsButton");
            clickOn("#refresh-button");

            assertTrue(lookup(".alert").tryQuery().isPresent(), "No alert is shown.");

            verifyThat(lookup(".alert .content").queryLabeled(), hasText("Failed to load students from database."));

        } catch (SQLException e) {
            Assertions.fail("Failed to close H2 connection.");
        }
    }
}
