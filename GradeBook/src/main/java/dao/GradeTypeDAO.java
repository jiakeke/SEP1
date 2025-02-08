package dao;

import datasource.MariaDbConnection;
import model.GradeType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeTypeDAO {

    // Insert data
    public static void registerGradeType(GradeType gradeType) throws SQLException {
        String query = "INSERT INTO grade_types (name, weight, group_id) VALUES (?, ?, ?)";
        try (Connection conn = MariaDbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, gradeType.getName());
            stmt.setDouble(2, gradeType.getWeight());
            stmt.setInt(3, gradeType.getGroupId());
            stmt.executeUpdate();
        }
    }

    // Update data
    public static void updateGradeType(GradeType gradeType) throws SQLException {
        String query = "UPDATE grade_types SET name = ?, weight = ?, group_id = ? WHERE id = ?";
        try (Connection conn = MariaDbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, gradeType.getName());
            stmt.setDouble(2, gradeType.getWeight());
            stmt.setInt(3, gradeType.getGroupId());
            stmt.setInt(4, gradeType.getId());
            stmt.executeUpdate();
        }
    }

    // Delete data
    public static void deleteGradeType(int id) throws SQLException {
        String query = "DELETE FROM grade_types WHERE id = ?";
        try (Connection conn = MariaDbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // check all GradeType
    public static List<GradeType> showAllGradeTypes() throws SQLException {
        String query = "SELECT * FROM grade_types";
        List<GradeType> gradeTypes = new ArrayList<>();

        try (Connection conn = MariaDbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                GradeType gradeType = new GradeType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("weight"),
                        rs.getInt("group_id")
                );
                gradeTypes.add(gradeType);
            }
        }
        return gradeTypes;
    }

    // check GradeType by groupId
    public static List<GradeType> showGradeTypesByGroupId(int groupId) throws SQLException {
        String query = "SELECT * FROM grade_types WHERE group_id = ?";
        List<GradeType> gradeTypes = new ArrayList<>();

        try (Connection conn = MariaDbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GradeType gradeType = new GradeType(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("weight"),
                            rs.getInt("group_id")
                    );
                    gradeTypes.add(gradeType);
                }
            }
        }
        return gradeTypes;
    }
}
