package dao;

import datasource.MariaDbConnection;
import model.GradeType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeTypeDAO {
    private static Connection conn = MariaDbConnection.getConnection();

    public static void setConnection(Connection conn) {
        GradeTypeDAO.conn = conn;
    }

    // Insert data
    public static void registerGradeType(GradeType gradeType) throws SQLException {
        double currentWeight = getTotalWeightByGroup(gradeType.getGroupId());
        if (currentWeight + gradeType.getWeight() > 100) {
            throw new SQLException("Total weight cannot exceed 100.");
        }

        String query = "INSERT INTO grade_types (name, weight, group_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, gradeType.getName());
            stmt.setDouble(2, gradeType.getWeight());
            stmt.setInt(3, gradeType.getGroupId());
            stmt.executeUpdate();
        }
    }

    // Update data
    public static void updateGradeType(GradeType gradeType) throws SQLException {
        double currentWeight = getTotalWeightByGroup(gradeType.getGroupId());
        double previousWeight = getWeightById(gradeType.getId());
        double newTotalWeight = currentWeight - previousWeight + gradeType.getWeight();

        if (newTotalWeight > 100) {
            throw new SQLException("Total weight cannot exceed 100.");
        }

        String query = "UPDATE grade_types SET name = ?, weight = ?, group_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // check all GradeType
    public static List<GradeType> showAllGradeTypes() throws SQLException {
        String query = "SELECT * FROM grade_types";
        List<GradeType> gradeTypes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query);
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

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

    // get total weight by group
    public static double getTotalWeightByGroup(int groupId) throws SQLException {
        String query = "SELECT SUM(weight) AS total_weight FROM grade_types WHERE group_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total_weight");
                    if (rs.wasNull()) {
                        return 0;
                    }
                    return total;
                }
            }
        }
        return 0;
    }

    // get weight by id
    public static double getWeightById(int gradeTypeId) throws SQLException {
        String query = "SELECT weight FROM grade_types WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gradeTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }
}
