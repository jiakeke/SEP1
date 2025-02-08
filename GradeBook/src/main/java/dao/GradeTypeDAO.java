package dao;

import datasource.MariaDbConnection;
import model.GradeType;

import java.sql.*;

public class GradeTypeDAO {
    private static final Connection conn = MariaDbConnection.getConnection();

    public static void registerGradeType(GradeType gradeType) throws SQLException {
        String query = "INSERT INTO grade_types (name, weight, group_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, gradeType.getName());
            stmt.setDouble(2, gradeType.getWeight());
            stmt.setInt(3, gradeType.getGroupId());
            stmt.executeUpdate();
        }
    }

    public static void updateGradeType(GradeType gradeType) throws SQLException {
        String query = "UPDATE grade_types SET name = ?, weight = ?, group_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, gradeType.getName());
            stmt.setDouble(2, gradeType.getWeight());
            stmt.setInt(3, gradeType.getGroupId());
            stmt.setInt(4, gradeType.getId());
            stmt.executeUpdate();
        }
    }

    public static void deleteGradeType(int id) throws SQLException {
        String query = "DELETE FROM grade_types WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static ResultSet showAllGradeTypes() throws SQLException {
        String query = "SELECT * FROM grade_types";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            return stmt.executeQuery();
        }
    }

    public static ResultSet showGradeTypesByGroupId(int groupId) throws SQLException {
        String query = "SELECT * FROM grade_types WHERE group_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            return stmt.executeQuery();
        }
    }
}
