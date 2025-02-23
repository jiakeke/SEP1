package dao;

import model.GradeType;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GradeTypeDAOTest {
    private static Connection conn;

    @BeforeAll
    static void setupDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

        // Use H2 in-memory database to create a table
        GradeTypeDAO.setConnection(conn);

        try (Statement stmt = conn.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS grade_types (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "weight DOUBLE, " +
                    "group_id INT" +
                    ")";
            stmt.execute(createTable);
        }
    }

    @BeforeEach
    void resetDatabase() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM grade_types");
            stmt.execute("ALTER TABLE grade_types ALTER COLUMN id RESTART WITH 1");
            stmt.execute("INSERT INTO grade_types (name, weight, group_id) VALUES ('Homework', 30, 1)");
            stmt.execute("INSERT INTO grade_types (name, weight, group_id) VALUES ('Exam', 50, 1)");
        }
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        conn.close();
    }

    @Test
    void testRegisterGradeType() throws SQLException {
        GradeType newGradeType = new GradeType(0, "Project", 20, 1);
        GradeTypeDAO.registerGradeType(newGradeType);

        List<GradeType> gradeTypes = GradeTypeDAO.showAllGradeTypes();
        assertEquals(3, gradeTypes.size());
        assertEquals("Project", gradeTypes.get(2).getName());
    }

    @Test
    void testRegisterGradeTypeExceedingWeight() {
        GradeType newGradeType = new GradeType(0, "Bonus", 60, 1);
        Exception exception = assertThrows(SQLException.class, () -> GradeTypeDAO.registerGradeType(newGradeType));

        assertEquals("Total weight cannot exceed 100.", exception.getMessage());
    }

    @Test
    void testUpdateGradeType() throws SQLException {
        GradeType updatedGradeType = new GradeType(1, "Homework Updated", 25, 1);
        GradeTypeDAO.updateGradeType(updatedGradeType);

        List<GradeType> gradeTypes = GradeTypeDAO.showAllGradeTypes();
        assertEquals("Homework Updated", gradeTypes.get(0).getName());
        assertEquals(25, gradeTypes.get(0).getWeight());
    }

    @Test
    void testUpdateGradeTypeExceedingWeight() {
        GradeType updatedGradeType = new GradeType(1, "Homework", 80, 1);
        Exception exception = assertThrows(SQLException.class, () -> GradeTypeDAO.updateGradeType(updatedGradeType));

        assertEquals("Total weight cannot exceed 100.", exception.getMessage());
    }

    @Test
    void testDeleteGradeType() throws SQLException {
        GradeTypeDAO.deleteGradeType(1);
        List<GradeType> gradeTypes = GradeTypeDAO.showAllGradeTypes();
        assertEquals(1, gradeTypes.size());
        assertEquals("Exam", gradeTypes.get(0).getName());
    }

    @Test
    void testShowAllGradeTypes() throws SQLException {
        List<GradeType> gradeTypes = GradeTypeDAO.showAllGradeTypes();
        assertEquals(2, gradeTypes.size());
    }

    @Test
    void testShowGradeTypesByGroupId() throws SQLException {
        List<GradeType> gradeTypes = GradeTypeDAO.showGradeTypesByGroupId(1);
        assertEquals(2, gradeTypes.size());
    }

    @Test
    void testGetTotalWeightByGroup() throws SQLException {
        double totalWeight = GradeTypeDAO.getTotalWeightByGroup(1);
        assertEquals(80, totalWeight);
    }

    @Test
    void testGetWeightById() throws SQLException {
        double weight = GradeTypeDAO.getWeightById(1);
        assertEquals(30, weight);
    }

    @Test
    void testGradeTypeDAOConstructor() {
        new GradeTypeDAO();
    }

    @Test
    void testGetTotalWeightByGroup_EmptyResult() throws SQLException {
        double totalWeight = GradeTypeDAO.getTotalWeightByGroup(-1);
        assertEquals(0, totalWeight);
    }

    @Test
    void testGetWeightById_EmptyResult() throws SQLException {
        double weight = GradeTypeDAO.getWeightById(-1);
        assertEquals(0, weight);
    }
}
