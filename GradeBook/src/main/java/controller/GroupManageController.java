package controller;

import dao.GroupDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Group;

public class GroupManageController {

    @FXML
    private TableView<Group> GroupsInfo;
    @FXML
    private TableColumn<Group, String> groupsDesClu;

//    @FXML
//    private TableColumn<Group, Integer> groupsIdClu;

    @FXML
    private TableColumn<Group, String> groupsNameclu;

    private GroupDao groupDao = new GroupDao();
    private ObservableList<Group> groupInfoList = FXCollections.observableArrayList();

    // This method is called when the FXML file is loaded, it initializes the table view with the groups
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

    // This method is called when the user clicks the "Add New Group" button, it opens the AddNewGroup.fxml file
    @FXML
    void addNewGroup(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddNewGroup.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = getStage();
            stage.setTitle("Add New Group");
            stage.setScene(scene);

            AddNewGroupController controller = fxmlLoader.getController();
            controller.setGroupManageController(this);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected  Stage getStage() {
        return new Stage();
    }

    // This method is called when the user clicks the "Delete Group" button, it deletes the selected group
    @FXML
    void deleteGroupInfo(MouseEvent event) {
        Group selectedGroup = GroupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showError("Selection error", "Please select a group to delete.");
            return;
        }
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmation Dialog");
//        alert.setHeaderText("Delete Group");
//        alert.setContentText("Are you sure you want to delete this group?");

        if (getConfirmation() == ButtonType.OK) {
            if (groupDao.removeGroup(selectedGroup.getId())) {
                groupInfoList.remove(selectedGroup);
            }
        }
    }

    protected ButtonType getConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Group");
        alert.setContentText("Are you sure you want to delete this group?");
        return alert.showAndWait().orElse(ButtonType.CANCEL);
    }

    // This method is called when the user clicks the "Modify Group" button, it opens the ModifyGroupInfo.fxml file
    @FXML
    void modifyGroupInfo(MouseEvent event) {
        Group selectedGroup = GroupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ModifyGroupInfo.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = getStage();
            stage.setTitle("Modify Group");
            stage.setScene(scene);

            GroupModifyController controller = fxmlLoader.getController();
            controller.setGroupManageController(this, selectedGroup);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is called when the user clicks the "Add Grade Type" button
    @FXML
    void addGradeType(MouseEvent event) {
        Group selectedGroup = GroupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showError("Select error", "Please select a group to add grade type");
            return;
        }
        GradeTypeController.showGradeTypeEditor(selectedGroup.getId());
    }

    // This method is called when the user clicks the "View Grade" button
    @FXML
    void viewGrade(MouseEvent event) {
        Group selectedGroup = GroupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showError("Selection error", "Please select a Group first.");
            return;
        }
        GradeController.showGradeEditor(selectedGroup.getId(), selectedGroup.getName());
    }

    // This method is shown when an error occurs
    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public TableView<Group> getGroupsInfo() {
        return GroupsInfo;
    }

    public void setGroupsInfo(TableView<Group> groupsInfo) {
        GroupsInfo = groupsInfo;
    }

    public TableColumn<Group, String> getGroupsDesClu() {
        return groupsDesClu;
    }

    public void setGroupsDesClu(TableColumn<Group, String> groupsDesClu) {
        this.groupsDesClu = groupsDesClu;
    }

    public TableColumn<Group, String> getGroupsNameclu() {
        return groupsNameclu;
    }

    public void setGroupsNameclu(TableColumn<Group, String> groupsNameclu) {
        this.groupsNameclu = groupsNameclu;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public ObservableList<Group> getGroupInfoList() {
        return groupInfoList;
    }

    public void setGroupInfoList(ObservableList<Group> groupInfoList) {
        this.groupInfoList = groupInfoList;
    }
}