package controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import dao.GroupDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Group;
import model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GroupModifyControllerTest {

    // 自定义 DummyStage，用于记录 hide() 是否被调用
    public static class DummyStage extends Stage {
        private boolean hidden = false;
        @Override
        public void hide() {
            hidden = true;
            super.hide();
        }
        public boolean isHidden() {
            return hidden;
        }
    }

    private GroupModifyController controller;
    private GroupDao mockGroupDao;
    private GroupManageController mockGroupManageController;

    // 模拟 FXML 控件
    private TextField groupName;
    private TextField groupDes;
    private Button saveBtn;
    private TableView<Student> selectedStudentList;
    private TableView<Student> unSelectedStudentsList;
    private TableColumn<Student, Integer> selectedId;
    private TableColumn<Student, String> selectedName;
    private TableColumn<Student, Integer> unselectedId;
    private TableColumn<Student, String> unselectedName;

    // 用于测试 saveModify() 时窗口隐藏调用的 dummyStage（DummyStage 类型）
    private DummyStage dummyStage;

    // 用于测试的 Group 对象
    private Group dummyGroup;

    @BeforeAll
    public static void initJFX() throws Exception {
        new JFXPanel();
        Thread.sleep(500);
    }

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new GroupModifyController();

        // 模拟 DAO 和 GroupManageController 依赖
        mockGroupDao = mock(GroupDao.class);
        controller.setGroupDao(mockGroupDao);
        mockGroupManageController = mock(GroupManageController.class);

        // 构造一个 dummy Group 用于测试 loadGroupDetails()
        dummyGroup = new Group(1, "TestGroup", "TestDesc");

        // 创建控件
        groupName = new TextField();
        groupDes = new TextField();
        saveBtn = new Button();
        selectedStudentList = new TableView<>();
        unSelectedStudentsList = new TableView<>();
        selectedId = new TableColumn<>();
        selectedName = new TableColumn<>();
        unselectedId = new TableColumn<>();
        unselectedName = new TableColumn<>();

        // 注入控件到 controller
        controller.setGroupName(groupName);
        controller.setGroupDes(groupDes);
        controller.setSaveBtn(saveBtn);
        controller.setSelectedStudentList(selectedStudentList);
        controller.setUnSelectedStudentsList(unSelectedStudentsList);
        controller.setSelectedId(selectedId);
        controller.setSelectedName(selectedName);
        controller.setUnselectedId(unselectedId);
        controller.setUnselectedName(unselectedName);

        // 初始化内部 ObservableList
        controller.setUnselectedStudents(FXCollections.observableArrayList());
        controller.setSelectedStudents(FXCollections.observableArrayList());
        unSelectedStudentsList.setItems(controller.getUnselectedStudents());
        selectedStudentList.setItems(controller.getSelectedStudents());

        // 设置 dummyStage（必须在 FX 线程中创建）
        setUpDummyStage();

        // 模拟 DAO 方法，供 loadGroupDetails() 调用
        when(mockGroupDao.getGroupById(dummyGroup.getId()))
                .thenReturn(new Group(dummyGroup.getId(), "TestGroup", "TestDesc"));
        when(mockGroupDao.getStudentsNotInGroup(dummyGroup.getId()))
                .thenReturn(Arrays.asList(new Student(2, "StuNotIn")));
        when(mockGroupDao.getStudentsInGroup(dummyGroup.getId()))
                .thenReturn(Arrays.asList(new Student(1, "StuIn")));

        // 调用 setGroupManageController() 触发 loadGroupDetails()
        controller.setGroupManageController(mockGroupManageController, dummyGroup);
    }

    // 在 FX 线程中创建 dummyStage，并将 saveBtn 包含在 VBox 中，确保 saveBtn.getScene().getWindow() 返回 dummyStage
    private void setUpDummyStage() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                VBox root = new VBox(saveBtn);
                Scene scene = new Scene(root);
                dummyStage = new DummyStage();
                dummyStage.setScene(scene);
                dummyStage.show(); // 显示窗口，确保 scene.getWindow() 返回 dummyStage
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "FX task did not complete in time");
    }

    @Test
    public void testLoadGroupDetails() {
        // loadGroupDetails() 已在 setGroupManageController() 调用时执行
        assertEquals("TestGroup", groupName.getText());
        assertEquals("TestDesc", groupDes.getText());
        // 根据模拟 DAO，未选中学生列表和选中学生列表各有1条数据
        assertEquals(1, controller.getUnselectedStudents().size());
        assertEquals(1, controller.getSelectedStudents().size());
        assertEquals(controller.getUnselectedStudents(), unSelectedStudentsList.getItems());
        assertEquals(controller.getSelectedStudents(), selectedStudentList.getItems());
    }

    @Test
    public void testMoveStudentToSelectedList() {
        Student student = new Student(3, "Stu3");
        controller.getUnselectedStudents().add(student);
        unSelectedStudentsList.getSelectionModel().select(student);

        controller.moveStudentToSelectedList(null);

        assertTrue(controller.getSelectedStudents().contains(student));
        assertFalse(controller.getUnselectedStudents().contains(student));
    }

    @Test
    public void testMoveStudentToUnselectedList() {
        Student student = new Student(3, "Stu3");
        controller.getSelectedStudents().add(student);
        selectedStudentList.getSelectionModel().select(student);

        controller.moveStudentToUnselectedList(null);

        assertTrue(controller.getUnselectedStudents().contains(student));
        assertFalse(controller.getSelectedStudents().contains(student));
    }

    @Test
    public void testSaveModify_EmptyFields() throws Exception {
        groupName.setText("");
        groupDes.setText("");
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.saveModify(null);
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        verify(mockGroupDao, never()).updateGroup(anyInt(), anyString(), anyString());
    }

    @Test
    public void testSaveModify_Success() throws Exception {
        groupName.setText("ModifiedGroup");
        groupDes.setText("ModifiedDesc");
        Student student = new Student(4, "Stu4");
        controller.getSelectedStudents().add(student);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.saveModify(null);
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));

        verify(mockGroupDao).updateGroup(dummyGroup.getId(), "ModifiedGroup", "ModifiedDesc");
        verify(mockGroupDao).deleteGroupStudents(dummyGroup.getId());
        verify(mockGroupDao).addStudentToGroup(dummyGroup.getId(), student.getId());
        verify(mockGroupManageController).initialize();

        // 验证窗口关闭调用：saveBtn.getScene().getWindow() 应返回 dummyStage (DummyStage 类型)
        Stage window = (Stage) saveBtn.getScene().getWindow();
        // 由于我们使用 DummyStage 重写了 hide()，直接判断其标志
        assertTrue(((DummyStage) window).isHidden(), "Window should be hidden");
    }
}
