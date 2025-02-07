package dao;

import datasource.MariaDbConnection;

import java.sql.Connection;

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

}