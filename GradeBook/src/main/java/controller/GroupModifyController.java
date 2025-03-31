package controller;

import application.GradeBookView;
import dao.GroupDao;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.Group;
import model.Student;
import util.GoogleTranslateUtil;
import util.LangContext;

import java.io.IOException;
import java.util.ResourceBundle;


public class GroupModifyController {
    private GradeBookView view;
    private Group group;
    private ResourceBundle bundle;


    public GroupModifyController(GradeBookView view, Group group, ResourceBundle bundle) {
        this.bundle = bundle;
        this.view = view;
        this.group = group;
        this.groupManageController = new GroupManageController(view, bundle);
    }

    @FXML
    private TextField groupDes;

    @FXML
    private TextField groupName;
    @FXML
    private TextField groupName_cn;

    @FXML
    private TextField groupDes_cn;

    @FXML
    private TextField groupName_ja;

    @FXML
    private TextField groupDes_ja;

    @FXML
    private Button saveBtn;

    @FXML
    private Label allStudentsLabel;

    @FXML
    private Label selectedStudentsLabel;

    @FXML
    private Label topLabel;

    @FXML
    private TableColumn<Student, Integer> selectedId;

    @FXML
    private TableColumn<Student, String> selectedName;

    @FXML
    private TableView<Student> selectedStudentList;

    @FXML
    private TableView<Student> unSelectedStudentsList;

    @FXML
    private TableColumn<Student, Integer> unselectedId;

    @FXML
    private TableColumn<Student, String> unselectedName;

    private GroupDao groupDao = new GroupDao();
    private ObservableList<Student> unselectedStudents = FXCollections.observableArrayList();
    private ObservableList<Student> selectedStudents = FXCollections.observableArrayList();

    private GroupManageController groupManageController;

    // This method loads the group details
    public void initialize() {
        LangContext.currentLang.addListener((obs, oldlang, newLang) -> {
            this.bundle = LangContext.getBundle();
            updateTexts();
        });

        // 名称字段监听器
        PauseTransition delayNameEn = new PauseTransition(Duration.millis(800));
        PauseTransition delayNameZh = new PauseTransition(Duration.millis(800));
        PauseTransition delayNameJa = new PauseTransition(Duration.millis(800));

        groupName.setOnKeyReleased(e -> {
            delayNameEn.stop();
            delayNameEn.setOnFinished(ev -> autoTranslate(groupName.getText(), "en", groupName_cn, "zh", groupName_ja, "ja"));
            delayNameEn.playFromStart();
        });

        groupName_cn.setOnKeyReleased(e -> {
            delayNameZh.stop();
            delayNameZh.setOnFinished(ev -> autoTranslate(groupName_cn.getText(), "zh", groupName, "en", groupName_ja, "ja"));
            delayNameZh.playFromStart();
        });

        groupName_ja.setOnKeyReleased(e -> {
            delayNameJa.stop();
            delayNameJa.setOnFinished(ev -> autoTranslate(groupName_ja.getText(), "ja", groupName, "en", groupName_cn, "zh"));
            delayNameJa.playFromStart();
        });

        // 描述字段监听器
        PauseTransition delayDesEn = new PauseTransition(Duration.millis(800));
        PauseTransition delayDesZh = new PauseTransition(Duration.millis(800));
        PauseTransition delayDesJa = new PauseTransition(Duration.millis(800));

        groupDes.setOnKeyReleased(e -> {
            delayDesEn.stop();
            delayDesEn.setOnFinished(ev -> autoTranslate(groupDes.getText(), "en", groupDes_cn, "zh", groupDes_ja, "ja"));
            delayDesEn.playFromStart();
        });

        groupDes_cn.setOnKeyReleased(e -> {
            delayDesZh.stop();
            delayDesZh.setOnFinished(ev -> autoTranslate(groupDes_cn.getText(), "zh", groupDes, "en", groupDes_ja, "ja"));
            delayDesZh.playFromStart();
        });

        groupDes_ja.setOnKeyReleased(e -> {
            delayDesJa.stop();
            delayDesJa.setOnFinished(ev -> autoTranslate(groupDes_ja.getText(), "ja", groupDes, "en", groupDes_cn, "zh"));
            delayDesJa.playFromStart();
        });

        selectedId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        selectedName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        unselectedId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        unselectedName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));

        Group groupDetailsEN = groupDao.getGroupById(group.getId(), "en");
        if (groupDetailsEN != null) {
            groupName.setText(groupDetailsEN.getName());
            groupDes.setText(groupDetailsEN.getDescription());
        }
        Group groupDetailsCN = groupDao.getGroupById(group.getId(), "zh");
        if (groupDetailsCN != null) {
            groupName_cn.setText(groupDetailsCN.getName());
            groupDes_cn.setText(groupDetailsCN.getDescription());
        }
        Group groupDetailsJA = groupDao.getGroupById(group.getId(), "ja");
        if (groupDetailsJA != null) {
            groupName_ja.setText(groupDetailsJA.getName());
            groupDes_ja.setText(groupDetailsJA.getDescription());
        }

        unselectedStudents.addAll(groupDao.getStudentsNotInGroup(group.getId()));
        selectedStudents.addAll(groupDao.getStudentsInGroup(group.getId()));

        unSelectedStudentsList.setItems(unselectedStudents);
        selectedStudentList.setItems(selectedStudents);
    }

    private void autoTranslate(String sourceText, String sourceLang,
                               TextField target1, String target1Lang,
                               TextField target2, String target2Lang) {
        if (sourceText == null || sourceText.isBlank()) return;

        new Thread(() -> {
            try {
                String translated1 = GoogleTranslateUtil.translate(sourceText, target1Lang);
                String translated2 = GoogleTranslateUtil.translate(sourceText, target2Lang);

                Platform.runLater(() -> {
                    target1.setText(translated1);
                    target2.setText(translated2);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateTexts() {
        saveBtn.setText(bundle.getString("save"));
        allStudentsLabel.setText(bundle.getString("allStudents"));
        selectedStudentsLabel.setText(bundle.getString("selectedStudents"));
        topLabel.setText(bundle.getString("groupEdit"));
        unselectedName.setText(bundle.getString("name"));
        selectedName.setText(bundle.getString("name"));


    }

    // This method refreshes the table view
    void refresh() {
        unSelectedStudentsList.setItems(unselectedStudents);
        selectedStudentList.setItems(selectedStudents);
    }

    // This method moves a student to the selected list
    @FXML
    void moveStudentToSelectedList(MouseEvent event) {
        Student selectedStudent = unSelectedStudentsList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            selectedStudents.add(selectedStudent);
            unselectedStudents.remove(selectedStudent);
        }
        refresh();
    }

    // This method moves a student to the unselected list
    @FXML
    void moveStudentToUnselectedList(MouseEvent event) {
        Student selectedStudent = selectedStudentList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            unselectedStudents.add(selectedStudent);
            selectedStudents.remove(selectedStudent);
        }
        refresh();
    }

    // This method saves the modifications
    @FXML
    void saveModify(MouseEvent event) {
        if (groupName.getText().isEmpty() || groupDes.getText().isEmpty()) {
            return;
        }

        groupDao.updateGroup(group.getId(), groupName.getText(), groupDes.getText(), view.getCurrentLang());
        groupDao.deleteGroupStudents(group.getId());

        for (Student student : selectedStudents) {
            groupDao.addStudentToGroup(group.getId(), student.getId());
        }

        this.view.openGroups();
    }

    public TextField getGroupDes() {
        return groupDes;
    }

    public void setGroupDes(TextField groupDes) {
        this.groupDes = groupDes;
    }

    public TextField getGroupName() {
        return groupName;
    }

    public void setGroupName(TextField groupName) {
        this.groupName = groupName;
    }

    public Button getSaveBtn() {
        return saveBtn;
    }

    public void setSaveBtn(Button saveBtn) {
        this.saveBtn = saveBtn;
    }

    public TableColumn<Student, Integer> getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(TableColumn<Student, Integer> selectedId) {
        this.selectedId = selectedId;
    }

    public TableColumn<Student, String> getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(TableColumn<Student, String> selectedName) {
        this.selectedName = selectedName;
    }

    public TableView<Student> getSelectedStudentList() {
        return selectedStudentList;
    }

    public void setSelectedStudentList(TableView<Student> selectedStudentList) {
        this.selectedStudentList = selectedStudentList;
    }

    public TableView<Student> getUnSelectedStudentsList() {
        return unSelectedStudentsList;
    }

    public void setUnSelectedStudentsList(TableView<Student> unSelectedStudentsList) {
        this.unSelectedStudentsList = unSelectedStudentsList;
    }

    public TableColumn<Student, Integer> getUnselectedId() {
        return unselectedId;
    }

    public void setUnselectedId(TableColumn<Student, Integer> unselectedId) {
        this.unselectedId = unselectedId;
    }

    public TableColumn<Student, String> getUnselectedName() {
        return unselectedName;
    }

    public void setUnselectedName(TableColumn<Student, String> unselectedName) {
        this.unselectedName = unselectedName;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public ObservableList<Student> getUnselectedStudents() {
        return unselectedStudents;
    }

    public void setUnselectedStudents(ObservableList<Student> unselectedStudents) {
        this.unselectedStudents = unselectedStudents;
    }

    public ObservableList<Student> getSelectedStudents() {
        return selectedStudents;
    }

    public void setSelectedStudents(ObservableList<Student> selectedStudents) {
        this.selectedStudents = selectedStudents;
    }

    public GroupManageController getGroupManageController() {
        return groupManageController;
    }

}