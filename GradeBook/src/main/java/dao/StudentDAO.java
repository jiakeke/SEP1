package dao;

import datasource.MariaDbConnection;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private static Connection conn = MariaDbConnection.getConnection();

    // Private constructor to prevent instantiation
    private StudentDAO() {
    }

    public static void setConnection(Connection conn) {
        StudentDAO.conn = conn;
    }

    public static void registerStudent(Student student) throws SQLException {
        String query = "INSERT INTO students (name, email, phone) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhone());
            stmt.executeUpdate();
        }
    }

    public static void updateStudent(Student student) throws SQLException {
        String query = "UPDATE students SET name = ?, email = ?, phone = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhone());
            stmt.setInt(4, student.getId());
            stmt.executeUpdate();
        }
    }

    public static void deleteStudent(int id) throws SQLException {
        try (PreparedStatement deleteGradesStmt = conn.prepareStatement("DELETE FROM grades WHERE student_id = ?");
             PreparedStatement deleteGroupStudentsStmt = conn.prepareStatement("DELETE FROM group_students WHERE student_id = ?");
             PreparedStatement deleteStudentStmt = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {

            conn.setAutoCommit(false); // Open transaction mode

            // Delete records in the grades table for the student
            deleteGradesStmt.setInt(1, id);
            deleteGradesStmt.executeUpdate();

            // Delete records in the group_students table for the student
            deleteGroupStudentsStmt.setInt(1, id);
            deleteGroupStudentsStmt.executeUpdate();

            // Delete the student record
            deleteStudentStmt.setInt(1, id);
            deleteStudentStmt.executeUpdate();

            conn.commit(); // Commit the transaction

        } catch (SQLException e) {
            conn.rollback(); // Roll back the transaction if an exception occurs
            throw e;
        } finally {
            conn.setAutoCommit(true); // Close transaction mode
        }
    }


    public static List<Student> showAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT id, name, email, phone FROM students";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        }
        return students;
    }

    public static List<Student> searchStudentByName(String name) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT id, name, email, phone FROM students WHERE name LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        }
        return students;
    }

    public static List<Student> getStudentsByGroupId(int groupId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.id, s.name FROM students s " +
                "JOIN group_students gs ON s.id = gs.student_id " +
                "WHERE gs.group_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                            rs.getInt("id"),
                            rs.getString("name")
                    ));
                }
            }
        }
        return students;
    }

}
