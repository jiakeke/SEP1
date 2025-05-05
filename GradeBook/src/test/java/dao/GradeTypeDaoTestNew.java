package dao;

import model.GradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GradeTypeDaoTestNew {
    private Connection mockConn;
    private PreparedStatement mockStmt;
    private ResultSet mockRs;

    @BeforeEach
    public void setup() throws Exception {
        mockConn = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);

        GradeTypeDAO.setConnection(mockConn);
    }

    @Test
    void testRegisterGradeType_success() throws Exception {
        GradeType gradeType = new GradeType(0, "", 30.0, 1);
        Map<String, String> localized = Map.of("EN", "Test Name");

        // mock weight check
        when(mockConn.prepareStatement(contains("SUM"))).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getDouble("total_weight")).thenReturn(60.0);

        // mock insert into grade_types
        PreparedStatement insertStmt = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(mockConn.prepareStatement(startsWith("Insert into grade_types"), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(insertStmt);

        when(insertStmt.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(123); // mock gradeTypeId

        // mock insert into localized
        PreparedStatement localizedStmt = mock(PreparedStatement.class);
        when(mockConn.prepareStatement(startsWith("INSERT INTO grade_type_localized"))).thenReturn(localizedStmt);

        GradeTypeDAO.registerGradeType(gradeType, localized);

        verify(insertStmt).setDouble(1, 30.0);
        verify(insertStmt).setInt(2, 1);
        verify(insertStmt).executeUpdate();
        verify(localizedStmt).setInt(1, 123);
        verify(localizedStmt).setString(2, "EN");
        verify(localizedStmt).setString(3, "Test Name");
        verify(localizedStmt).executeUpdate();
    }

    @Test
    void testUpdateGradeType_success() throws Exception {
        GradeType gradeType = new GradeType(5, "", 40.0, 1);
        Map<String, String> localized = Map.of("EN", "Updated Name");

        // mock get total weight
        PreparedStatement stmtTotal = mock(PreparedStatement.class);
        ResultSet rsTotal = mock(ResultSet.class);
        when(mockConn.prepareStatement(contains("SUM"))).thenReturn(stmtTotal);
        when(stmtTotal.executeQuery()).thenReturn(rsTotal);
        when(rsTotal.next()).thenReturn(true);
        when(rsTotal.getDouble("total_weight")).thenReturn(70.0);

        // mock get previous weight
        PreparedStatement stmtPrev = mock(PreparedStatement.class);
        ResultSet rsPrev = mock(ResultSet.class);
        when(mockConn.prepareStatement(contains("SELECT weight"))).thenReturn(stmtPrev);
        when(stmtPrev.executeQuery()).thenReturn(rsPrev);
        when(rsPrev.next()).thenReturn(true);
        when(rsPrev.getDouble(1)).thenReturn(30.0); // previous weight

        // mock update grade_types
        PreparedStatement stmtUpdate = mock(PreparedStatement.class);
        when(mockConn.prepareStatement(startsWith("UPDATE grade_types"))).thenReturn(stmtUpdate);

        // mock update localized
        PreparedStatement stmtLocalized = mock(PreparedStatement.class);
        when(mockConn.prepareStatement(startsWith("UPDATE grade_type_localized"))).thenReturn(stmtLocalized);

        GradeTypeDAO.updateGradeType(gradeType, localized);

        verify(stmtUpdate).setDouble(1, 40.0);
        verify(stmtUpdate).setInt(2, 5);
        verify(stmtUpdate).executeUpdate();

        verify(stmtLocalized).setString(1, "Updated Name");
        verify(stmtLocalized).setInt(2, 5);
        verify(stmtLocalized).setString(3, "EN");
        verify(stmtLocalized).executeUpdate();
    }

    @Test
    void testDeleteGradeType_success() throws Exception {
        PreparedStatement stmt1 = mock(PreparedStatement.class);
        PreparedStatement stmt2 = mock(PreparedStatement.class);
        when(mockConn.prepareStatement(contains("grade_type_localized"))).thenReturn(stmt1);
        when(mockConn.prepareStatement(contains("grade_types"))).thenReturn(stmt2);

        GradeTypeDAO.deleteGradeType(5);

        verify(stmt1).setInt(1, 5);
        verify(stmt1).executeUpdate();
        verify(stmt2).setInt(1, 5);
        verify(stmt2).executeUpdate();
    }

    @Test
    void testGetLocalizedNamesByGradeTypeId_success() throws Exception {
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(mockConn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("lang")).thenReturn("EN");
        when(rs.getString("name")).thenReturn("English Name");

        Map<String, String> result = GradeTypeDAO.getLocalizedNamesByGradeTypeId(1);

        assertEquals(1, result.size());
        assertEquals("English Name", result.get("EN"));
    }

    @Test
    void testGetTotalWeightByGroup_zeroOnNull() throws Exception {
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(mockConn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getDouble("total_weight")).thenReturn(0.0);
        when(rs.wasNull()).thenReturn(true);

        double total = GradeTypeDAO.getTotalWeightByGroup(1);
        assertEquals(0.0, total);
    }
}
