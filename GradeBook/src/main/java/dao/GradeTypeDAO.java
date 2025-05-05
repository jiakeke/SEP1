package dao;

import datasource.MariaDbConnection;
import model.GradeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeTypeDAO {
    private static Connection conn = MariaDbConnection.getConnection();

    private GradeTypeDAO() {
        // Private constructor to prevent instantiation
    }

    public static void setConnection(Connection conn) {
        GradeTypeDAO.conn = conn;
    }
    private static final Logger logger = LoggerFactory.getLogger(GradeTypeDAO.class);

    // Insert data
    public static void registerGradeType(GradeType gradeType,Map<String, String> localizedNames) throws SQLException {
        double currentWeight = getTotalWeightByGroup(gradeType.getGroupId());
        if (currentWeight + gradeType.getWeight() > 100) {
            throw new SQLException("Total weight cannot exceed 100.");
        }

        //先将数据插入到grade_types表中
        String insterGradeTypeQuery="Insert into grade_types (weight,group_id) values (?,?)";

        int gradeTypeId = -1;

        try (PreparedStatement stmt = conn.prepareStatement(insterGradeTypeQuery, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, gradeType.getWeight());
            stmt.setInt(2, gradeType.getGroupId());
            stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                gradeTypeId = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("Error creating group", e);
        }

        //再将数据插入到grade_type_localized表中

        String insertLocalizedQuery = "INSERT INTO grade_type_localized (grade_type_id, lang, name) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertLocalizedQuery)) {
            stmt.setInt(1, gradeTypeId);
            for (var entry: localizedNames.entrySet()) {
                String lang = entry.getKey();
                String name = entry.getValue();
                stmt.setString(2, lang);
                stmt.setString(3, name);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("Error creating group", e);
        }

    }

    // Update data
    public static void updateGradeType(GradeType gradeType, Map<String, String> localizedNames ) throws SQLException {
        double currentWeight = getTotalWeightByGroup(gradeType.getGroupId());
        double previousWeight = getWeightById(gradeType.getId());
        double newTotalWeight = currentWeight - previousWeight + gradeType.getWeight();

        if (newTotalWeight > 100) {
            throw new SQLException("Total weight cannot exceed 100.");
        }
        //需要将新的数据更新到数据库中，名称和权重，分别更新两个表
        //先更新权重值
        String query = "UPDATE grade_types SET  weight = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, gradeType.getWeight());
            stmt.setInt(2, gradeType.getId());
            stmt.executeUpdate();
        }
        //再更新名称
        String updateLocalizedQuery = "UPDATE grade_type_localized SET name = ? WHERE grade_type_id = ? AND lang = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateLocalizedQuery)) {
            for (var entry: localizedNames.entrySet()) {
                String lang = entry.getKey();
                String name = entry.getValue();
                stmt.setString(1, name);
                stmt.setInt(2, gradeType.getId());
                stmt.setString(3, lang);
                stmt.executeUpdate();
            }

        }

    }

    // Delete data
    public static void deleteGradeType(int id) throws SQLException {
        //delete from grade_type_localized  first
        String deleteLocalizedQuery = "DELETE FROM grade_type_localized WHERE grade_type_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteLocalizedQuery)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

        String query = "DELETE FROM grade_types WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // check all GradeType
    public static List<GradeType> showAllGradeTypes() throws SQLException {


        //query from localizd db
        String query="select a.id,b.name,a.weight,a.group_id  from grade_types a left join grade_type_localized b on a.id=b.grade_type_id where b.lang=?";

        List<GradeType> gradeTypes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "EN");
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

    // check GradeType by groupId

    public static List<GradeType> showGradeTypesByGroupId(int groupId,String lang) throws SQLException {
        //query from localizd db
        String query="select a.id,b.name,a.weight,a.group_id  from grade_types a left join grade_type_localized b on a.id=b.grade_type_id where a.group_id=? and b.lang=?";


        List<GradeType> gradeTypes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setString(2, lang);
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

    public static Map<String, String> getLocalizedNamesByGradeTypeId(int gradeTypeId) {
        String query = "SELECT lang, name FROM grade_type_localized WHERE grade_type_id = ?";
        Map<String, String> localizedNames = new HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gradeTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String lang = rs.getString("lang");
                    String name = rs.getString("name");
                    localizedNames.put(lang, name);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting localized names", e);
        }
        return localizedNames;
    }
}
