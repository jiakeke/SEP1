package dao;

import model.GradeType;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

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
        try (Statement stmt = conn.createStatement()) {
            String createLocalizedTable = "CREATE TABLE IF NOT EXISTS grade_type_localized (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "grade_type_id INT, " +
                    "lang VARCHAR(10), " +
                    "name VARCHAR(255), " +
                    "FOREIGN KEY (grade_type_id) REFERENCES grade_types(id) ON DELETE CASCADE" +
                    ")";
            stmt.execute(createLocalizedTable);
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
    void testRegisterGradeTypeExceedingWeight() {
        GradeType newGradeType = new GradeType(0, "Bonus", 60, 1);
        Map<String, String> localizedNames = Map.of("en", "Bonus", "fr", "Bonus");
        Exception exception = assertThrows(SQLException.class, () -> GradeTypeDAO.registerGradeType(newGradeType, localizedNames));

        assertEquals("Total weight cannot exceed 100.", exception.getMessage());
    }

    @Test
    void testUpdateGradeTypeExceedingWeight() {
        GradeType updatedGradeType = new GradeType(1, "Homework", 80, 1);
        Map<String, String> localizedNames = Map.of("en", "Homework", "fr", "Devoirs");
        Exception exception = assertThrows(SQLException.class, () -> GradeTypeDAO.updateGradeType(updatedGradeType, localizedNames));

        assertEquals("Total weight cannot exceed 100.", exception.getMessage());
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
