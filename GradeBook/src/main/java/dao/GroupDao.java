package dao;

import datasource.MariaDbConnection;
import model.Group;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class GroupDao {
    Connection conn = MariaDbConnection.getConnection();

    // This method adds a new group to the database
    public void addGroup(String name, String description) {
        String insertQuery = "INSERT INTO groups (name, description) VALUES (?, ?)";
        executeUpdateQuery(insertQuery, name, description);
    }

    // This method deletes a group from the database
    public boolean removeGroup(int id) {
        String deleteGroupGradesQuery = "DELETE FROM grades WHERE group_id = ?";
        String deleteGroupGradeQuery = "DELETE FROM grade_types WHERE group_id = ?";
        String deleteGroupStudentQuery = "DELETE FROM group_students WHERE group_id = ?";
        String deleteQuery = "DELETE FROM groups WHERE id = ?";
        try {
            conn.setAutoCommit(false);
            executeUpdateQuery(deleteGroupGradesQuery, id);
            executeUpdateQuery(deleteGroupGradeQuery, id);
            executeUpdateQuery(deleteGroupStudentQuery, id);
            executeUpdateQuery(deleteQuery, id);
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    }

    // This method returns all groups from the database
    public List<Group> getAllGroups() {
        return getGroupsByQuery("SELECT * FROM groups", null);
    }

    // This method returns a group by its id
    public Group getGroupById(int id) {
        List<Group> groups = getGroupsByQuery("SELECT * FROM groups WHERE id = ?", id);
        return groups.isEmpty() ? null : groups.get(0);
    }

    // This method returns all students that are not in a group
    public List<Student> getStudentsNotInGroup(int groupId) {
        return getStudentsByGroupQuery(groupId, "SELECT id, name FROM students WHERE id NOT IN (SELECT student_id FROM group_students WHERE group_id = ?)");
    }

    // This method returns all students that are in a group
    public List<Student> getStudentsInGroup(int groupId) {
        return getStudentsByGroupQuery(groupId, "SELECT id, name FROM students WHERE id IN (SELECT student_id FROM group_students WHERE group_id = ?)");
    }

    // This method updates a group
    public void updateGroup(int id, String name, String description) {
        String updateQuery = "UPDATE groups SET name = ?, description = ? WHERE id = ?";
        executeUpdateQuery(updateQuery, name, description, id);
    }

    // This method deletes all students from a group
    public void deleteGroupStudents(int groupId) {
        String deleteQuery = "DELETE FROM group_students WHERE group_id = ?";
        executeUpdateQuery(deleteQuery, groupId);
    }

    // This method adds a student to a group
    public void addStudentToGroup(int groupId, int studentId) {
        String insertQuery = "INSERT INTO group_students (group_id, student_id) VALUES (?, ?)";
        executeUpdateQuery(insertQuery, groupId, studentId);
    }

    // This method removes a student from a group
    public void executeUpdateQuery(String query, Object... params) {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method returns a list of groups
    public List<Group> getGroupsByQuery(String query, Integer id) {
        List<Group> groups = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (id != null) {
                stmt.setInt(1, id);
            }
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                groups.add(new Group(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    // This method returns a list of students in a group
    public List<Student> getStudentsByGroupQuery(int groupId, String query) {
        List<Student> students = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                students.add(new Student(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }


}