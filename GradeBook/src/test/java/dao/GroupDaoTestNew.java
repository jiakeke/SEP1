package dao;

import dao.GroupDao;
import model.Group;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupDaoTestNew {

    private GroupDao groupDao;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setup() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // 使用匿名子类注入 mockConnection
        groupDao = new GroupDao() {
            {
                this.conn = mockConnection;
            }
        };
    }

    @Test
    void testCreateGroup_success() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStatement);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(42);

        int groupId = groupDao.createGroup(1);

        verify(mockStatement).setInt(1, 1);
        verify(mockStatement).executeUpdate();
        assertEquals(42, groupId);
    }

    @Test
    void testAddGroupLocalized() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        groupDao.addGroupLocalized(1, "EN", "Test Group", "Test Description");

        verify(mockStatement, times(4)).setObject(anyInt(), any());
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testRemoveGroup() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        boolean result = groupDao.removeGroup(1);

        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).commit();
        verify(mockStatement, atLeastOnce()).setObject(1, 1);
        verify(mockStatement, atLeastOnce()).executeUpdate();

        assertTrue(result);
    }

    @Test
    void testGetGroupById_found() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.getResultSet()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test Group");
        when(mockResultSet.getString("description")).thenReturn("Description");

        Group group = groupDao.getGroupById(1, "EN");

        assertNotNull(group);
        assertEquals(1, group.getId());
        assertEquals("Test Group", group.getName());
    }

    @Test
    void testGetStudentsInGroup_empty() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.getResultSet()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Student> students = groupDao.getStudentsInGroup(1);
        assertTrue(students.isEmpty());
    }
}
