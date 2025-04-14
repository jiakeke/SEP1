package dao;

import datasource.MariaDbConnection;
import model.Group;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupDao {
    Connection conn = MariaDbConnection.getConnection();
    private static final Logger logger = LoggerFactory.getLogger(GroupDao.class);

    // This method adds a new group to the database
    public int createGroup(int createdBy) {
        String insertGroupQuery = "INSERT INTO groups (created_by) VALUES (?)";
        int groupId = -1;

        try (PreparedStatement stmt = conn.prepareStatement(insertGroupQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, createdBy);
            stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                groupId = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("Error creating group", e);
        }

        return groupId;
    }

    public void addGroupLocalized(int groupId, String lang, String name, String description) {
        String insertLocalizedQuery = "INSERT INTO group_localized (group_id, lang, name, description) VALUES (?, ?, ?, ?)";
        executeUpdateQuery(insertLocalizedQuery, groupId, lang, name, description);
    }

    // This method deletes a group from the database
    public boolean removeGroup(int id) {
        String deleteGroupGradesQuery = "DELETE FROM grades WHERE group_id = ?";
        String deleteGroupGradeQuery = "DELETE FROM grade_types WHERE group_id = ?";
        String deleteGroupStudentQuery = "DELETE FROM group_students WHERE group_id = ?";
        String deleteGroupLocalizedQuery = "DELETE FROM group_localized WHERE group_id = ?";
        String deleteQuery = "DELETE FROM groups WHERE id = ?";
        try {
            conn.setAutoCommit(false);
            executeUpdateQuery(deleteGroupGradesQuery, id);
            executeUpdateQuery(deleteGroupGradeQuery, id);
            executeUpdateQuery(deleteGroupStudentQuery, id);
            executeUpdateQuery(deleteGroupLocalizedQuery, id);
            executeUpdateQuery(deleteQuery, id);
            conn.commit();
            return true;
        } catch (Exception e) {
            logger.error("Error removing group", e);
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                logger.error("Error rolling back transaction", rollbackEx);
            }
            return false;
        }
    }

    public List<Group> getAllGroupsByUser(String lang, int userId) {
        String query = "SELECT g.id, gl.name, gl.description " +
                "FROM groups g JOIN group_localized gl ON g.id = gl.group_id " +
                "WHERE gl.lang = ? and g.created_by = ?";
        return getGroupsByQuery(query, lang, userId);
    }

    public Group getGroupById(int id, String lang) {
        String query = "SELECT g.id, gl.name, gl.description " +
                "FROM groups g JOIN group_localized gl ON g.id = gl.group_id " +
                "WHERE g.id = ? AND gl.lang = ?";
        List<Group> groups = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, lang);
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                groups.add(new Group(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (Exception e) {
            logger.error("Error getting group by id", e);
        }
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

    public void updateGroup(int groupId, String name, String description, String lang) {
        String updateQuery = "UPDATE group_localized SET name = ?, description = ? WHERE group_id = ? AND lang = ?";
        executeUpdateQuery(updateQuery, name, description, groupId, lang);
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
            logger.error("Error executing update query", e);
        }
    }

    // This method returns a list of groups
    public List<Group> getGroupsByQuery(String query, String lang,int userId) {
        List<Group> groups = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, lang);
            stmt.setInt(2, userId);
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                groups.add(new Group(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (Exception e) {
            logger.error("Error getting groups by query", e);
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
            logger.error("Error getting students by group query", e);
        }
        return students;
    }


}
