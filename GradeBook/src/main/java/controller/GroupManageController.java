package controller;

import application.GradeBookView;
import dao.GroupDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Group;
import util.LangContext;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GroupManageController {
    private GradeBookView view;
    private  ResourceBundle bundle;
    private static final Logger logger = LoggerFactory.getLogger(GroupManageController.class);

    public GroupManageController(GradeBookView view, ResourceBundle bundle) {
        this.view = view;
        this.bundle = bundle;
    }
    @FXML
    private Button addNewGroupBtn;

    @FXML
    private Button deleteGroupBtn;

    @FXML
    private Button modifyGroupBtn;

    @FXML
    private Button typesBtn;

    @FXML
    private Button gradesBtn;

    @FXML
    private Label mainLabel;

    @FXML
    private TableView<Group> groupsInfo;
    @FXML
    private TableColumn<Group, String> groupsDesClu;


    @FXML
    private TableColumn<Group, String> groupsNameclu;

    private GroupDao groupDao = new GroupDao();
    private ObservableList<Group> groupInfoList = FXCollections.observableArrayList();

    // This method is called when the FXML file is loaded, it initializes the table view with the groups
    @FXML
    public void initialize() {
        LangContext.currentLang.addListener((obs,oldlang,newLang)->{
            this.bundle=LangContext.getBundle();
            updateTexts();
        });
        
        groupInfoList.clear();
        groupsNameclu.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupsDesClu.setCellValueFactory(new PropertyValueFactory<>("description"));
        groupDao.getAllGroupsByUser(LangContext.currentLang.get(),view.getCurrentUserId()).forEach(group ->
            groupInfoList.add(group)
        );
        groupsInfo.setItems(groupInfoList);
    }

    private void updateTexts() {
        addNewGroupBtn.setText(bundle.getString("new"));
        deleteGroupBtn.setText(bundle.getString("delete"));
        modifyGroupBtn.setText(bundle.getString("modify"));
        typesBtn.setText(bundle.getString("types"));
        gradesBtn.setText(bundle.getString("grades"));
        mainLabel.setText(bundle.getString("mainLabel"));
        groupsNameclu.setText(bundle.getString("groupName"));
        groupsDesClu.setText(bundle.getString("groupDescription"));

        groupInfoList.clear();
        groupsNameclu.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupsDesClu.setCellValueFactory(new PropertyValueFactory<>("description"));
        groupDao.getAllGroupsByUser(LangContext.currentLang.get(),view.getCurrentUserId()).forEach(group ->
            groupInfoList.add(group)
        );
        System.out.println("GroupManageController: "+view.getCurrentLang());
        groupsInfo.setItems(groupInfoList);

    }

    // This method is called when the user clicks the "Add New Group" button, it opens the AddNewGroup.fxml file
    @FXML
    void addNewGroup(MouseEvent event) {
        try {
            Locale locale=new Locale(view.getCurrentLang());
            bundle= ResourceBundle.getBundle("messages", locale);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/addNewGroup.fxml"),bundle);
            fxmlLoader.setController(new AddNewGroupController(this.view, bundle));
            this.view.getRootPane().setCenter(fxmlLoader.load());
        } catch (Exception e) {
            logger.error("Error loading AddNewGroup.fxml", e);
        }
    }

    protected  Stage getStage() {
        return new Stage();
    }

    // This method is called when the user clicks the "Delete Group" button, it deletes the selected group
    @FXML
    void deleteGroupInfo(MouseEvent event) {
        Group selectedGroup = groupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showError("Selection error", "Please select a group to delete.");
            return;
        }

        if (getConfirmation() == ButtonType.OK && groupDao.removeGroup(selectedGroup.getId())) {

                groupInfoList.remove(selectedGroup);
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
        Group selectedGroup = groupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/modifyGroupInfo.fxml"),bundle);
            fxmlLoader.setController(new GroupModifyController(this.view, selectedGroup, bundle));
            this.view.getRootPane().setCenter(fxmlLoader.load());
        } catch (Exception e) {
            logger.error("Error loading ModifyGroupInfo.fxml", e);
        }
    }

    // This method is called when the user clicks the "Add Grade Type" button
    @FXML
    void addGradeType(MouseEvent event) {
        Group selectedGroup = groupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showError("Select error", "Please select a group to add grade type");
            return;
        }
        GradeTypeController.showGradeTypeEditor(this.view, selectedGroup.getId());
    }

    // This method is called when the user clicks the "View Grade" button
    @FXML
    void viewGrade(MouseEvent event) {
        Group selectedGroup = groupsInfo.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showError("Selection error", "Please select a Group first.");
            return;
        }
        GradeController.showGradeEditor(this.view, selectedGroup.getId(), selectedGroup.getName());
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
        return groupsInfo;
    }

    public void setGroupsInfo(TableView<Group> groupsInfo) {
        this.groupsInfo = groupsInfo;
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
