package controller;


import application.GradeBookView;
import dao.GroupDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import model.Group;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 为了捕获错误提示，我们定义一个测试子类，重写 showError 方法
class TestableGroupManageController extends GroupManageController {
    public String capturedTitle;
    public String capturedMessage;

    public TestableGroupManageController(GradeBookView view, ResourceBundle bundle) {
        super(view, bundle);
    }

    public TestableGroupManageController() {
        super(null, null);
    }

    @Override
    protected void showError(String title, String message) {
        this.capturedTitle = title;
        this.capturedMessage = message;
    }
}

class TestableGroupManageControllerWithStage extends TestableGroupManageController {
    public Stage testStage;

    public TestableGroupManageControllerWithStage(GradeBookView view, ResourceBundle bundle) {
        super(view, bundle);
    }

    public TestableGroupManageControllerWithStage() {
        super(null, null);
    }

    @Override
    protected Stage getStage() {
        // 创建一个 spy 的 Stage 对象
        testStage = spy(new Stage());
        return testStage;
    }
}

// 测试子类：重写 getConfirmation() 使其始终返回 ButtonType.OK
class TestableGroupManageControllerWithConfirmation extends TestableGroupManageController {
    public TestableGroupManageControllerWithConfirmation(GradeBookView view, ResourceBundle bundle) {
        super(view, bundle);
    }

    public TestableGroupManageControllerWithConfirmation() {
        super(null, null);
    }

    @Override
    protected ButtonType getConfirmation() {
        return ButtonType.OK;
    }
}

public class GroupManageControllerTest {

    @BeforeAll
    public static void initJFX() throws InterruptedException {
        // 初始化 JavaFX 工具包，解决 "Toolkit not initialized" 错误
        new JFXPanel();

        Thread.sleep(500);
    }

    private TestableGroupManageController controller;
    private TableView<Group> groupsInfo;
    private TableColumn<Group, String> groupsName;
    private TableColumn<Group, String> groupsDes;
    private GroupDao mockDao;

    @BeforeEach
    public void setUp() {
        // 使用测试子类构造 controller
        controller = new TestableGroupManageController();

        // 模拟 FXML 注入，手动创建 TableView 和 TableColumn 实例
        groupsInfo = new TableView<>();
        groupsName = new TableColumn<>();
        groupsDes = new TableColumn<>();
        controller.setGroupsInfo(groupsInfo);
        controller.setGroupsNameclu(groupsName);
        controller.setGroupsDesClu(groupsDes);

        // 注入一个 mock 的 GroupDao，模拟 getAllGroups 返回两条数据
        mockDao = mock(GroupDao.class);
        when(mockDao.getAllGroupsByUser("EN", 1)).thenReturn(Arrays.asList(
                new Group(1, "Group1", "Desc1"),
                new Group(2, "Group2", "Desc2")
        ));
        controller.setGroupDao(mockDao);

        // 确保内部 ObservableList 为空，方便 initialize() 时填充数据
        controller.setGroupInfoList(FXCollections.observableArrayList());
    }

    @Test
    public void testDeleteGroupInfo_NoSelection() {
        // 未选中任何组时，删除操作应提示错误
        groupsInfo.getSelectionModel().clearSelection();
        controller.deleteGroupInfo(null);
        // 验证捕获的错误提示
        assertEquals("Selection error", controller.capturedTitle);
        assertEquals("Please select a group to delete.", controller.capturedMessage);
    }

    @Test
    public void testModifyGroupInfo_NoSelection() {
        // 当没有选中组时，modifyGroupInfo 应该不做任何操作，也不抛异常
        groupsInfo.getSelectionModel().clearSelection();
        assertDoesNotThrow(() -> controller.modifyGroupInfo(null));
    }

    @Test
    public void testAddGradeType_NoSelection() {
        // 当没有选中组时，调用 addGradeType 应提示错误
        groupsInfo.getSelectionModel().clearSelection();
        controller.addGradeType(null);
        assertEquals("Select error", controller.capturedTitle);
        assertEquals("Please select a group to add grade type", controller.capturedMessage);
    }

    @Test
    public void testViewGrade_NoSelection() {
        // 当没有选中组时，调用 viewGrade 应提示错误
        groupsInfo.getSelectionModel().clearSelection();
        controller.viewGrade(null);
        assertEquals("Selection error", controller.capturedTitle);
        assertEquals("Please select a Group first.", controller.capturedMessage);
    }

    @Test
    public void testAddNewGroup_OpensWindow() throws Exception {
        TestableGroupManageControllerWithStage controllerWithStage = new TestableGroupManageControllerWithStage();
        // 注入控件和 DAO（此处不关注数据，只关注窗口创建）
        groupsInfo = new TableView<>();
        groupsName = new TableColumn<>();
        groupsDes = new TableColumn<>();
        controllerWithStage.setGroupsInfo(groupsInfo);
        controllerWithStage.setGroupsNameclu(groupsName);
        controllerWithStage.setGroupsDesClu(groupsDes);
        controllerWithStage.setGroupDao(mockDao);
        controllerWithStage.setGroupInfoList(FXCollections.observableArrayList());

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controllerWithStage.addNewGroup(null);
                // 断言新窗口创建成功，并调用 show()；这里的断言在 FX 线程内执行
                assertNotNull(controllerWithStage.testStage, "Stage should not be null");
                verify(controllerWithStage.testStage, atLeastOnce()).show();
            } finally {
                latch.countDown();
            }
        });
        // 等待 FX 线程执行完成
        assertTrue(latch.await(10, TimeUnit.SECONDS), "FX task did not complete in time");
    }

    // 验证 modifyGroupInfo 方法在 FX 线程中能够创建新窗口并调用 show()
    @Test
    public void testModifyGroupInfo_OpensWindow() throws Exception {
        TestableGroupManageControllerWithStage controllerWithStage = new TestableGroupManageControllerWithStage();
        groupsInfo = new TableView<>();
        groupsName = new TableColumn<>();
        groupsDes = new TableColumn<>();
        controllerWithStage.setGroupsInfo(groupsInfo);
        controllerWithStage.setGroupsNameclu(groupsName);
        controllerWithStage.setGroupsDesClu(groupsDes);
        controllerWithStage.setGroupDao(mockDao);
        controllerWithStage.setGroupInfoList(FXCollections.observableArrayList());
        // 添加一条 dummy 数据，并选中之
        Group dummyGroup = new Group(10, "Dummy", "DummyDesc");
        controllerWithStage.getGroupInfoList().add(dummyGroup);
        groupsInfo.getSelectionModel().select(dummyGroup);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controllerWithStage.modifyGroupInfo(null);
                assertNotNull(controllerWithStage.testStage, "Stage should not be null");
                verify(controllerWithStage.testStage, atLeastOnce()).show();
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "FX task did not complete in time");
    }

    // 以下是验证删除成功的测试
    @Test
    public void testDeleteGroupInfo_Success() {
        // 使用重写 getConfirmation() 返回 ButtonType.OK 的测试子类
        TestableGroupManageControllerWithConfirmation controllerWithConf = new TestableGroupManageControllerWithConfirmation();

        // 模拟 FXML 注入
        TableView<Group> table = new TableView<>();
        TableColumn<Group, String> colName = new TableColumn<>();
        TableColumn<Group, String> colDesc = new TableColumn<>();
        controllerWithConf.setGroupsInfo(table);
        controllerWithConf.setGroupsNameclu(colName);
        controllerWithConf.setGroupsDesClu(colDesc);

        // 创建内部列表并注入
        ObservableList<Group> list = FXCollections.observableArrayList();
        controllerWithConf.setGroupInfoList(list);

        // 创建一个 dummy 组，并添加到列表
        Group dummyGroup = new Group(100, "Dummy", "DummyDesc");
        list.add(dummyGroup);
        // 同时在 TableView 中选中该组
        table.getSelectionModel().select(dummyGroup);

        // 模拟 DAO 的删除操作返回 true
        GroupDao mockDaoForDeletion = mock(GroupDao.class);
        when(mockDaoForDeletion.removeGroup(dummyGroup.getId())).thenReturn(true);
        controllerWithConf.setGroupDao(mockDaoForDeletion);

        // 调用删除方法（传 null 的 MouseEvent）
        controllerWithConf.deleteGroupInfo(null);

        // 验证删除成功后，dummyGroup 不再存在于内部列表中
        assertFalse(list.contains(dummyGroup));
    }
}
