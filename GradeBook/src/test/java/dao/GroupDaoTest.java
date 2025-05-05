package dao;

import model.Group;
import model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GroupDaoTest {
    //我现在要测试我dao层，我应该模拟一个数据库连接，然后测试dao层的方法

    // 使用 @Mock 注解模拟 Connection 对象
    @Mock
    private Connection mockConnection;

    // 使用 @Mock 注解模拟 PreparedStatement 对象
    @Mock
    private PreparedStatement mockPreparedStatement;

    // 使用 @Mock 注解模拟 ResultSet 对象
    @Mock
    private ResultSet mockResultSet;

    // 使用 @InjectMocks 注解将模拟对象注入到 GroupDao 实例中
    @InjectMocks
    private GroupDao groupDao;

    // 在每个测试方法之前执行，初始化 Mockito 模拟对象
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        groupDao = new GroupDao();
        groupDao.conn = mockConnection; // 将模拟的连接注入到 groupDao 中
    }


    @Test
    void getAllGroups() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // 当调用 execute() 时返回 true
        when(mockPreparedStatement.execute()).thenReturn(true);
        // 当调用 getResultSet() 时返回你模拟的 ResultSet
        when(mockPreparedStatement.getResultSet()).thenReturn(mockResultSet);
//        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("name")).thenReturn("Group1", "Group2");
        when(mockResultSet.getString("description")).thenReturn("Description1", "Description2");

        List<Group> groups = groupDao.getAllGroupsByUser("EN", 1);

        assertEquals(2, groups.size());
        assertEquals("Group1", groups.get(0).getName());
        assertEquals("Group2", groups.get(1).getName());
    }

    @Test
    void getGroupById() throws Exception {
       when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
//        // 当调用 execute() 时返回 true
        when(mockPreparedStatement.execute()).thenReturn(true);
//        // 当调用 getResultSet() 时返回你模拟的 ResultSet
        when(mockPreparedStatement.getResultSet()).thenReturn(mockResultSet);
//        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true,false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Group1");
        when(mockResultSet.getString("description")).thenReturn("Description1");

        Group group = groupDao.getGroupById(1, "EN");

        assertNotNull(group);
        assertEquals("Group1", group.getName());
    }

    @Test
    void getStudentsNotInGroup() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.execute()).thenReturn(true);
//        // 当调用 getResultSet() 时返回你模拟的 ResultSet
        when(mockPreparedStatement.getResultSet()).thenReturn(mockResultSet);
//        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("name")).thenReturn("Student1", "Student2");

        List<Student> students = groupDao.getStudentsNotInGroup(1);

        assertEquals(2, students.size());
        assertEquals("Student1", students.get(0).getName());
        assertEquals("Student2", students.get(1).getName());
    }

    @Test
    void getStudentsInGroup() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.execute()).thenReturn(true);
//        // 当调用 getResultSet() 时返回你模拟的 ResultSet
        when(mockPreparedStatement.getResultSet()).thenReturn(mockResultSet);
//        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("name")).thenReturn("Student1", "Student2");

        List<Student> students = groupDao.getStudentsInGroup(1);

        assertEquals(2, students.size());
        assertEquals("Student1", students.get(0).getName());
        assertEquals("Student2", students.get(1).getName());
    }

    @Test
    void updateGroup() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        groupDao.updateGroup(1, "Updated Group", "Updated Description", "EN");

        verify(mockPreparedStatement, times(1)).setObject(1, "Updated Group");
        verify(mockPreparedStatement, times(1)).setObject(2, "Updated Description");
        verify(mockPreparedStatement, times(1)).setObject(3, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void deleteGroupStudents() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        groupDao.deleteGroupStudents(1);

        verify(mockPreparedStatement, times(1)).setObject(1, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void addStudentToGroup() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        groupDao.addStudentToGroup(1, 1);

        verify(mockPreparedStatement, times(1)).setObject(1, 1);
        verify(mockPreparedStatement, times(1)).setObject(2, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void executeUpdateQuery() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        groupDao.executeUpdateQuery("Test Query", "Test Param");

        verify(mockPreparedStatement, times(1)).setObject(1, "Test Param");
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetStudentsByGroupQuerySingleRow() throws Exception {
        int groupId = 1;
        String query = "SELECT id, name FROM students WHERE id IN (SELECT student_id FROM group_students WHERE group_id = ?)";

        // 当调用 prepareStatement(query) 时返回模拟的 PreparedStatement 对象
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        // 模拟调用 execute() 返回 true
        when(mockPreparedStatement.execute()).thenReturn(true);
        // 模拟调用 getResultSet() 返回模拟的 ResultSet 对象
        when(mockPreparedStatement.getResultSet()).thenReturn(mockResultSet);
        // 模拟 ResultSet：第一次调用 next() 返回 true（有一行数据），第二次返回 false
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Student1");

        // 真正调用你写的方法
        List<Student> students = groupDao.getStudentsByGroupQuery(groupId, query);

        // 断言返回的 List 不为空，且包含一条数据，并验证 Student 对象各属性是否正确
        assertNotNull(students);
        assertEquals(1, students.size());
        Student student = students.get(0);
        assertEquals(1, student.getId());
        assertEquals("Student1", student.getName());

        // 验证在方法内部调用了 setInt(1, groupId)
        verify(mockPreparedStatement).setInt(1, groupId);
    }

    // 测试返回多行数据的情况
    @Test
    void testGetStudentsByGroupQueryMultipleRows() throws Exception {
        int groupId = 2;
        String query = "SELECT id, name FROM students WHERE id IN (SELECT student_id FROM group_students WHERE group_id = ?)";

        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.execute()).thenReturn(true);
        when(mockPreparedStatement.getResultSet()).thenReturn(mockResultSet);
        // 模拟 ResultSet 返回两行数据：依次调用 next() 返回 true, true, false
        when(mockResultSet.next()).thenReturn(true, true, false);
        // 模拟依次返回两行数据中各字段的值
        when(mockResultSet.getInt("id")).thenReturn(2, 3);
        when(mockResultSet.getString("name")).thenReturn("Student2", "Student3");

        List<Student> students = groupDao.getStudentsByGroupQuery(groupId, query);

        assertNotNull(students);
        assertEquals(2, students.size());

        Student student1 = students.get(0);
        Student student2 = students.get(1);
        assertEquals(2, student1.getId());
        assertEquals("Student2", student1.getName());
        assertEquals(3, student2.getId());
        assertEquals("Student3", student2.getName());

        // 验证调用了 setInt(1, groupId)
        verify(mockPreparedStatement).setInt(1, groupId);
    }

}