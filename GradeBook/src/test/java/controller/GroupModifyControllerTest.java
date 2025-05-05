package controller;

import dao.GroupDao;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.Group;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import application.GradeBookView;

public class GroupModifyControllerTest {

    private GroupModifyController controller;
    private GroupDao mockDao;
    private Group mockGroup;
    private ResourceBundle mockBundle;
    private GradeBookView mockView;

    @BeforeEach
    public void setup() {
        // 初始化 JavaFX 环境
        new JFXPanel();

        mockDao = mock(GroupDao.class);
        mockGroup = new Group(1, "Test Group", "Description");
        mockBundle = ResourceBundle.getBundle("messages"); // 确保 lang_en 存在
        mockView = mock(GradeBookView.class);

        controller = new GroupModifyController(mockView, mockGroup, mockBundle);
        controller.setGroupDao(mockDao);

        // 初始化 JavaFX 控件
        controller.setGroupName(new TextField());
        controller.setGroupDes(new TextField());
        controller.setGroupNameCn(new TextField());
        controller.setGroupDesCn(new TextField());
        controller.setGroupNameJa(new TextField());
        controller.setGroupDesJa(new TextField());
        controller.setSaveBtn(new Button());
        controller.setCancelButton(new Button());
        controller.setAllStudentsLabel(new Label());
        controller.setSelectedStudentsLabel(new Label());
        controller.setTopLabel(new Label());
        controller.setUnselectedId(new TableColumn<>());
        controller.setUnselectedName(new TableColumn<>());
        controller.setSelectedId(new TableColumn<>());
        controller.setSelectedName(new TableColumn<>());
        controller.setUnSelectedStudentsList(new TableView<>());
        controller.setSelectedStudentList(new TableView<>());

        controller.setUnselectedStudents(FXCollections.observableArrayList());
        controller.setSelectedStudents(FXCollections.observableArrayList());
    }

    @Test
    public void testMoveStudentToSelectedList() {
        Student s = new Student(1, "John");
        controller.getUnselectedStudents().add(s);
        controller.getUnSelectedStudentsList().getItems().add(s);
        controller.getUnSelectedStudentsList().getSelectionModel().select(s);

        controller.moveStudentToSelectedList(mock(MouseEvent.class));

        assertEquals(0, controller.getUnselectedStudents().size());
        assertEquals(1, controller.getSelectedStudents().size());
    }

    @Test
    public void testMoveStudentToUnselectedList() {
        Student s = new Student(2, "Alice");
        controller.getSelectedStudents().add(s);
        controller.getSelectedStudentList().getItems().add(s);
        controller.getSelectedStudentList().getSelectionModel().select(s);

        controller.moveStudentToUnselectedList(mock(MouseEvent.class));

        assertEquals(1, controller.getUnselectedStudents().size());
        assertEquals(0, controller.getSelectedStudents().size());
    }

    @Test
    public void testSaveModify() {
        controller.getGroupName().setText("Updated Name");
        controller.getGroupDes().setText("Updated Description");

        Student s1 = new Student(10, "S1");
        Student s2 = new Student(11, "S2");
        controller.getSelectedStudents().addAll(s1, s2);

        controller.saveModify(mock(MouseEvent.class));

        verify(mockDao).updateGroup(mockGroup.getId(), "Updated Name", "Updated Description", mockView.getCurrentLang());
        verify(mockDao).deleteGroupStudents(mockGroup.getId());
        verify(mockDao).addStudentToGroup(mockGroup.getId(), s1.getId());
        verify(mockDao).addStudentToGroup(mockGroup.getId(), s2.getId());
        verify(mockView).openGroups();
    }

    @Test
    public void testSaveModify_missingFields_shouldNotSave() {
        controller.getGroupName().setText("");
        controller.getGroupDes().setText("");

        controller.saveModify(mock(MouseEvent.class));

        verify(mockDao, never()).updateGroup(anyInt(), anyString(), anyString(), anyString());
    }

    @Test
    public void testCancelModify() {
        controller.cancelModify(mock(MouseEvent.class));
        verify(mockView).openGroups();
    }

    @Test
    public void testInitialize_setsGroupFields() {
        Group groupEN = new Group(1, "GroupEN", "DescEN");
        Group groupCN = new Group(1, "组CN", "描述CN");
        Group groupJA = new Group(1, "グループJA", "説明JA");
        List<Student> inGroup = Arrays.asList(new Student(1, "Tom"));
        List<Student> notInGroup = Arrays.asList(new Student(2, "Jerry"));

        when(mockDao.getGroupById(1, "en")).thenReturn(groupEN);
        when(mockDao.getGroupById(1, "zh")).thenReturn(groupCN);
        when(mockDao.getGroupById(1, "ja")).thenReturn(groupJA);
        when(mockDao.getStudentsInGroup(1)).thenReturn(inGroup);
        when(mockDao.getStudentsNotInGroup(1)).thenReturn(notInGroup);

        controller.initialize();

        assertEquals("GroupEN", controller.getGroupName().getText());
        assertEquals("组CN", controller.getGroupNameCn().getText());
        assertEquals("グループJA", controller.getGroupNameJa().getText());

        assertEquals(1, controller.getSelectedStudents().size());
        assertEquals(1, controller.getUnselectedStudents().size());
    }
}
