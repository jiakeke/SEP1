package dao;

import datasource.MariaDbConnection;
import model.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    private static Connection conn = MariaDbConnection.getConnection();

    public static void setConnection(Connection conn) {
        GradeDAO.conn = conn;
    }

    // Insert data
    public static void registerGrade(Grade grade) throws SQLException {
        if (!gradeTypeExists(grade.getGradeTypeId())) {
            throw new SQLException("Error: GradeType ID " + grade.getGradeTypeId() + " does not exist.");
        }

        String query = "INSERT INTO grades (grade, student_id, group_id, grade_type_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, grade.getGrade());
            stmt.setInt(2, grade.getStudentId());
            stmt.setInt(3, grade.getGroupId());
            stmt.setInt(4, grade.getGradeTypeId());
            stmt.executeUpdate();
        }
    }

    // Update data
    public static void updateGrade(Grade grade) throws SQLException {
        if (!gradeTypeExists(grade.getGradeTypeId())) {
            throw new SQLException("Error: GradeType ID " + grade.getGradeTypeId() + " does not exist.");
        }

        String query = "UPDATE grades SET grade = ?, student_id = ?, group_id = ?, grade_type_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, grade.getGrade());
            stmt.setInt(2, grade.getStudentId());
            stmt.setInt(3, grade.getGroupId());
            stmt.setInt(4, grade.getGradeTypeId());
            stmt.setInt(5, grade.getId());
            stmt.executeUpdate();
        }
    }

    // Update data by type
    public static void updateGradeByType(int studentId, int groupId, int gradeTypeId, double grade) throws SQLException {
        String query = "UPDATE grades SET grade = ? WHERE student_id = ? AND group_id = ? AND grade_type_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, grade);
            stmt.setInt(2, studentId);
            stmt.setInt(3, groupId);
            stmt.setInt(4, gradeTypeId);
            stmt.executeUpdate();
        }
    }

    // Delete data
    public static void deleteGrade(int id) throws SQLException {
        String query = "DELETE FROM grades WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Delete data by student and type
    public static void deleteGradeByStudentAndType(int studentId, int groupId, int gradeTypeId) throws SQLException {
        String query = "DELETE FROM grades WHERE student_id = ? AND group_id = ? AND grade_type_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, groupId);
            stmt.setInt(3, gradeTypeId);
            stmt.executeUpdate();
        }
    }


    // check all Grade
    public static List<Grade> showAllGrades() throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String query = "SELECT * FROM grades";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                grades.add(new Grade(
                        rs.getInt("id"),
                        rs.getDouble("grade"),
                        rs.getInt("student_id"),
                        rs.getInt("group_id"),
                        rs.getInt("grade_type_id")
                ));
            }
        }
        return grades;
    }

    // check all Grade by student id
    public static List<Grade> showGradesByStudentId(int studentId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String query = "SELECT * FROM grades WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                            rs.getInt("id"),
                            rs.getDouble("grade"),
                            rs.getInt("student_id"),
                            rs.getInt("group_id"),
                            rs.getInt("grade_type_id")
                    ));
                }
            }
        }
        return grades;
    }

    // check all Grade by group id
    public static List<Grade> showGradesByGroupId(int groupId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String query = "SELECT * FROM grades WHERE group_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                            rs.getInt("id"),
                            rs.getDouble("grade"),
                            rs.getInt("student_id"),
                            rs.getInt("group_id"),
                            rs.getInt("grade_type_id")
                    ));
                }
            }
        }
        return grades;
    }

    // check grade type exists
    private static boolean gradeTypeExists(int gradeTypeId) throws SQLException {
        String query = "SELECT COUNT(*) FROM grade_types WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gradeTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // check grade exists
    public static boolean gradeExists(int studentId, int groupId, int gradeTypeId) throws SQLException {
        String query = "SELECT COUNT(*) FROM grades WHERE student_id = ? AND group_id = ? AND grade_type_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, groupId);
            stmt.setInt(3, gradeTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // insert grade
    public static void insertGrade(int studentId, int groupId, int gradeTypeId, double grade) throws SQLException {
        String query = "INSERT INTO grades (grade, student_id, group_id, grade_type_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, grade);
            stmt.setInt(2, studentId);
            stmt.setInt(3, groupId);
            stmt.setInt(4, gradeTypeId);
            stmt.executeUpdate();
        }
    }
}
