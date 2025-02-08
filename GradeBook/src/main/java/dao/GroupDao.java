package dao;

import datasource.MariaDbConnection;
import model.Group;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class GroupDao {
    Connection conn = MariaDbConnection.getConnection();

    public void addGroup(String name, String description) {
        // add group to database
        String insertQuery = "INSERT INTO groups (name, description) VALUES ('" + name + "', '" + description + "')";
        try {
            conn.createStatement().executeUpdate(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean removeGroup(int id) {
        String deleteGruopGradesQuery = "DELETE FROM grades WHERE group_id = " + id;
        try {
            conn.createStatement().executeUpdate(deleteGruopGradesQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String deleteGruopGradeQuery = "DELETE FROM grade_types WHERE group_id = " + id;
        try {
            conn.createStatement().executeUpdate(deleteGruopGradeQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String deleteGruopStudentQuery = "DELETE FROM group_students WHERE group_id = " + id;
        try {
            conn.createStatement().executeUpdate(deleteGruopStudentQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }





        // remove group from database
        String deleteQuery = "DELETE FROM groups WHERE id = " + id;
        try {
            conn.createStatement().executeUpdate(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT * FROM groups";
        try (var stmt = conn.prepareStatement(query)) {
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
}