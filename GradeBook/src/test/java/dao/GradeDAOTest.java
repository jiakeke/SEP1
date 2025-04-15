package dao;

import model.Grade;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GradeDAOTest {
    private Connection conn;

    @BeforeAll
    void setUpDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        GradeDAO.setConnection(conn);

        try (Statement stmt = conn.createStatement()) {
            // 创建 grade_types 表（如果不存在）
            stmt.execute("CREATE TABLE IF NOT EXISTS grade_types (id INT PRIMARY KEY AUTO_INCREMENT)");
            stmt.execute("INSERT INTO grade_types (id) VALUES (1)"); // 添加一个默认 gradeType

            // 创建 grades 表
            stmt.execute("CREATE TABLE IF NOT EXISTS grades (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "grade DOUBLE, " +
                    "student_id INT, " +
                    "group_id INT, " +
                    "grade_type_id INT, " +
                    "FOREIGN KEY (grade_type_id) REFERENCES grade_types(id))");
        }
    }

    @BeforeEach
    void clearDatabase() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM grades");
        }
    }

    @Test
    void testRegisterGrade() throws SQLException {
        Grade grade = new Grade(0, 95.0, 1, 1, 1);
        GradeDAO.registerGrade(grade);
        List<Grade> grades = GradeDAO.showAllGrades();
        assertEquals(1, grades.size());
        assertEquals(95.0, grades.get(0).getMark());
    }

    @Test
    void testUpdateGrade() throws SQLException {
        Grade grade = new Grade(0, 88.0, 1, 1, 1);
        GradeDAO.registerGrade(grade);

        List<Grade> grades = GradeDAO.showAllGrades();
        int gradeId = grades.get(0).getId();

        Grade updatedGrade = new Grade(gradeId, 92.5, 1, 1, 1);
        GradeDAO.updateGrade(updatedGrade);

        List<Grade> updatedGrades = GradeDAO.showAllGrades();
        assertEquals(92.5, updatedGrades.get(0).getMark());
    }

    @Test
    void testDeleteGrade() throws SQLException {
        Grade grade = new Grade(0, 75.0, 1, 1, 1);
        GradeDAO.registerGrade(grade);

        List<Grade> gradesBeforeDelete = GradeDAO.showAllGrades();
        assertEquals(1, gradesBeforeDelete.size());

        int gradeId = gradesBeforeDelete.get(0).getId();
        GradeDAO.deleteGrade(gradeId);

        List<Grade> gradesAfterDelete = GradeDAO.showAllGrades();
        assertEquals(0, gradesAfterDelete.size());
    }

    @Test
    void testShowGradesByStudentId() throws SQLException {
        GradeDAO.registerGrade(new Grade(0, 90.0, 1, 1, 1));
        GradeDAO.registerGrade(new Grade(0, 85.0, 2, 1, 1));
        List<Grade> student1Grades = GradeDAO.showGradesByStudentId(1);
        assertEquals(1, student1Grades.size());
        assertEquals(90.0, student1Grades.get(0).getMark());
    }

    @AfterAll
    void tearDownDatabase() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE grades");
            stmt.execute("DROP TABLE grade_types");
        }
    }
}
