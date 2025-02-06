package dao;

import datasource.MariaDbConnection;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class studentDAO {
    static Connection conn = MariaDbConnection.getConnection();

    public static void registerStudent(Student student) throws SQLException {
        String query = "INSERT INTO students (id, name, email, phone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhone());
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
        String query = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static void showAllStudents() throws SQLException {
        String query = "SELECT * FROM students";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeQuery();
        }
    }
}
