package controller;

import dao.GroupDao;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Group;
import datasource.MariaDbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;

import static controller.GradeTypeController.showError;

public class GroupManageController {

    @FXML
    private Button DelBtn;

    @FXML
    private Button addNewGroupButton;

    @FXML
    private TextField groupViewLabel;

    @FXML
    private Button modifyBtn;

    @FXML
    private Button typeBtn;

    @FXML
    private Button viewGradebtn;

    @FXML
    private TableView<Group> GroupsInfo;
    @FXML
    private TableColumn<Group, String> groupsDesClu;

    @FXML
    private TableColumn<Group, Integer> groupsIdClu;

    @FXML
    private TableColumn<Group, String> groupsNameclu;

    private GroupDao groupDao = new GroupDao();


    private Connection conn = MariaDbConnection.getConnection();
    private ObservableList<Group> groupInfoList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        groupInfoList.clear();
        groupsNameclu.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupsDesClu.setCellValueFactory(new PropertyValueFactory<>("description"));
        groupDao.getAllGroups().forEach(group -> {
            groupInfoList.add(group);
        });
        GroupsInfo.setItems(groupInfoList);
    }

    @FXML
    void addNewGroup(MouseEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddNewGroup.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Add New Group");
            stage.setScene(scene);

            AddNewGroupController controller = fxmlLoader.getController();
            controller.setGroupManageController(this);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void deleteGroupInfo(MouseEvent event) {
        Group selectedGroup = GroupsInfo.getSelectionModel().getSelectedItem();
        if (groupDao.removeGroup(selectedGroup.getId())) {
            groupInfoList.remove(selectedGroup);
        }
    }

    @FXML
    void handleAddNewGroup(ActionEvent event) {

    }

    @FXML
    void modifyGroupInfo(MouseEvent event) {
        if (GroupsInfo.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Group selectedGroup = GroupsInfo.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ModifyGroupInfo.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Modify Group");
            stage.setScene(scene);

            GroupModifyController controller = fxmlLoader.getController();
            controller.setGroupManageController(this, selectedGroup);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @FXML
    void addGradeType(MouseEvent event) {
        Group selectedGroup = GroupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup.getId() == 0) {
            showError("Select error", "Please select a group to add grade type");
            return;
        }

        GradeTypeController.showGradeTypeEditor(selectedGroup.getId());
    }

    @FXML
    void viewGrade(MouseEvent event) {
        Group selectedGroup = GroupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup.getId() == 0) {
            showError("Selection error", "Please select a Group first.");
            return;
        }

        // 调用 GradeController，打开成绩管理界面
        GradeController.showGradeEditor(selectedGroup.getId());
    }
}
