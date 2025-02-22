package controller;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
import model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AddNewGroupControllerTest {

    private GroupDao mockGroupDao;
    private GroupManageController mockGroupManageController;
    private AddNewGroupController controller;

    // 模拟 FXML 控件
    private Button creatBtn;
    private TextField groupName;
    private TextField groupDes;
    private TableView<Student> unSelectedStudentsList;
    private TableView<Student> selectedStudentsList;
    private TableColumn<Student, Integer> unSelectedId;
    private TableColumn<Student, String> unSelectedName;
    private TableColumn<Student, Integer> selectedId;
    private TableColumn<Student, String> selectedName;

    @BeforeAll
    public static void initJFX() throws Exception {
        // 初始化 JavaFX 平台
        new JFXPanel();
        Thread.sleep(500);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AddNewGroupController();

        // 模拟依赖注入
        mockGroupDao = mock(GroupDao.class);
        controller.setGroupDao(mockGroupDao);
        mockGroupManageController = mock(GroupManageController.class);
        controller.setGroupManageController(mockGroupManageController);

        // 创建 FXML 控件
        creatBtn = new Button();
        groupName = new TextField();
        groupDes = new TextField();
        unSelectedStudentsList = new TableView<>();
        selectedStudentsList = new TableView<>();
        // 创建 TableColumn 控件（避免 initialize() 时为 null）
        unSelectedId = new TableColumn<>();
        unSelectedName = new TableColumn<>();
        selectedId = new TableColumn<>();
        selectedName = new TableColumn<>();

        // 注入控件到 controller
        controller.setCreatBtn(creatBtn);
        controller.setGroupName(groupName);
        controller.setGroupDes(groupDes);
        controller.setUnSelectedStudentsList(unSelectedStudentsList);
        controller.setSelectedStudentsList(selectedStudentsList);
        controller.setUnSelectedId(unSelectedId);
        controller.setUnSelectedName(unSelectedName);
        controller.setSelectedId(selectedId);
        controller.setSelectedName(selectedName);

        // 初始化内部列表
        controller.setUnselectedStudents(FXCollections.observableArrayList());
        controller.setSelectedStudents(FXCollections.observableArrayList());

        // 将 TableView 的 items 绑定
        unSelectedStudentsList.setItems(controller.getUnselectedStudents());
        selectedStudentsList.setItems(controller.getSelectedStudents());
    }

    // 测试 initialize() 方法：验证从 DAO 获取数据填充未选中学生列表
    @Test
    public void testInitialize() {
        Student s1 = new Student(1, "Alice");
        Student s2 = new Student(2, "Bob");
        List<Student> students = Arrays.asList(s1, s2);
        when(mockGroupDao.getStudentsNotInGroup(0)).thenReturn(students);

        controller.initialize();

        assertEquals(2, controller.getUnselectedStudents().size());
        assertEquals(controller.getUnselectedStudents(), unSelectedStudentsList.getItems());
    }

    // 测试 createNewGroup()：当 groupName 或 groupDes 为空时，不调用 DAO 操作
    @Test
    public void testCreateNewGroup_EmptyFields() {
        groupName.setText("");
        groupDes.setText("");
        controller.createNewGroup(null);
        verify(mockGroupDao, never()).addGroup(anyString(), anyString());
    }

    // 测试 createNewGroup() 成功分支：
    // 模拟输入非空，selectedStudents 非空，DAO.getAllGroups() 返回包含目标组，
    // 并验证调用 creatBtn.getScene().getWindow().hide()、DAO 方法、groupManageController.initialize()
    @Test
    public void testCreateNewGroup_Success() throws Exception {
        groupName.setText("Group1");
        groupDes.setText("Description1");
        Student student = new Student(1, "Alice");
        controller.getSelectedStudents().add(student);

        // 模拟 DAO 返回的组列表中包含目标组 Group1，id 为 100
        model.Group group = new model.Group(100, "Group1", "Description1");
        when(mockGroupDao.getAllGroups()).thenReturn(Arrays.asList(group));

        // 使用 Platform.runLater() 在 FX 线程中执行
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // 为 creatBtn 构造一个 dummyStage，并将 creatBtn 包含在其 Scene 中
                VBox root = new VBox(creatBtn);
                Scene dummyScene = new Scene(root);
                Stage dummyStage = spy(new Stage());
                dummyStage.setScene(dummyScene);
                dummyStage.show(); // 显示 stage 以确保 getWindow() 返回 dummyStage

                // 调用 createNewGroup()，此时 creatBtn.getScene().getWindow() 应返回 dummyStage
                controller.createNewGroup(null);

                // 验证窗口 hide() 被调用
                verify(dummyStage, times(1)).hide();
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "FX task did not complete in time");

        verify(mockGroupDao).addGroup("Group1", "Description1");
        verify(mockGroupDao).addStudentToGroup(100, student.getId());
        verify(mockGroupManageController).initialize();
    }

    // 测试 moveStudentToSelectedList()：将学生从未选中列表移到选中列表
    @Test
    public void testMoveStudentToSelectedList() {
        Student student = new Student(1, "Alice");
        controller.getUnselectedStudents().add(student);
        unSelectedStudentsList.getSelectionModel().select(student);

        controller.moveStudentToSelectedList(null);

        assertTrue(controller.getSelectedStudents().contains(student));
        assertFalse(controller.getUnselectedStudents().contains(student));
    }

    // 测试 moveStudentToUnselectedList()：将学生从选中列表移到未选中列表
    @Test
    public void testMoveStudentToUnselectedList() {
        Student student = new Student(1, "Alice");
        controller.getSelectedStudents().add(student);
        selectedStudentsList.getSelectionModel().select(student);

        controller.moveStudentToUnselectedList(null);

        assertTrue(controller.getUnselectedStudents().contains(student));
        assertFalse(controller.getSelectedStudents().contains(student));
    }
}
