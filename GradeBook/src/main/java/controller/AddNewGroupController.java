package controller;

import application.GradeBookView;
import dao.GroupDao;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.Student;
import util.GoogleTranslateUtil;
import util.LangContext;

import java.io.IOException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class AddNewGroupController {
    private GradeBookView view;
    private  ResourceBundle bundle;
    private static final Logger logger = LoggerFactory.getLogger(AddNewGroupController.class);

    public AddNewGroupController(GradeBookView view, ResourceBundle bundle) {
        this.view = view;
        this.bundle = bundle;
    }

    @FXML
    private Button creatBtn;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField groupDes;

    @FXML
    private TextField groupName;
    @FXML
    private TableColumn<Student, Integer> selectedId;

    @FXML
    private TableColumn<Student, String> selectedName;

    @FXML
    private TableColumn<Student, Integer> unSelectedId;

    @FXML
    private TableColumn<Student, String> unSelectedName;

    @FXML
    private TableView<Student> selectedStudentsList;

    @FXML
    private TableView<Student> unSelectedStudentsList;

    @FXML
    private Label allStudentslabel;

    @FXML
    private Label selectedStudentslabel;
    @FXML
    private TextField groupNameCn;

    @FXML
    private TextField groupDesCn;

    @FXML
    private TextField groupNameJa;

    @FXML
    private TextField groupDesJa;

    private GroupDao groupDao = new GroupDao();
    private ObservableList<Student> unselectedStudents = FXCollections.observableArrayList();
    private ObservableList<Student> selectedStudents = FXCollections.observableArrayList();

    //initialize the table view with the students
    @FXML
    public void initialize() {
        LangContext.currentLang.addListener((obs, oldlang, newLang)->{
            this.bundle=LangContext.getBundle();
            updateTexts();
        });

        // 名称字段监听器
        PauseTransition delayNameEn = new PauseTransition(Duration.millis(800));
        PauseTransition delayNameZh = new PauseTransition(Duration.millis(800));
        PauseTransition delayNameJa = new PauseTransition(Duration.millis(800));

        groupName.setOnKeyReleased(e -> {
            delayNameEn.stop();
            delayNameEn.setOnFinished(ev -> autoTranslate(groupName.getText(), groupNameCn, "zh", groupNameJa, "ja"));
            delayNameEn.playFromStart();
        });

        groupNameCn.setOnKeyReleased(e -> {
            delayNameZh.stop();
            delayNameZh.setOnFinished(ev -> autoTranslate(groupNameCn.getText(), groupName, "en", groupNameJa, "ja"));
            delayNameZh.playFromStart();
        });

        groupNameJa.setOnKeyReleased(e -> {
            delayNameJa.stop();
            delayNameJa.setOnFinished(ev -> autoTranslate(groupNameJa.getText(), groupName, "en", groupNameCn, "zh"));
            delayNameJa.playFromStart();
        });

        // 描述字段监听器
        PauseTransition delayDesEn = new PauseTransition(Duration.millis(800));
        PauseTransition delayDesZh = new PauseTransition(Duration.millis(800));
        PauseTransition delayDesJa = new PauseTransition(Duration.millis(800));

        groupDes.setOnKeyReleased(e -> {
            delayDesEn.stop();
            delayDesEn.setOnFinished(ev -> autoTranslate(groupDes.getText(), groupDesCn, "zh", groupDesJa, "ja"));
            delayDesEn.playFromStart();
        });

        groupDesCn.setOnKeyReleased(e -> {
            delayDesZh.stop();
            delayDesZh.setOnFinished(ev -> autoTranslate(groupDesCn.getText(), groupDes, "en", groupDesJa, "ja"));
            delayDesZh.playFromStart();
        });

        groupDesJa.setOnKeyReleased(e -> {
            delayDesJa.stop();
            delayDesJa.setOnFinished(ev -> autoTranslate(groupDesJa.getText(), groupDes, "en", groupDesCn, "zh"));
            delayDesJa.playFromStart();
        });

        unSelectedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        unSelectedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        selectedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        selectedName.setCellValueFactory(new PropertyValueFactory<>("name"));

        unselectedStudents.addAll(groupDao.getStudentsNotInGroup(0));
        unSelectedStudentsList.setItems(unselectedStudents);
        selectedStudentsList.setItems(selectedStudents);
    }

    private void autoTranslate(String sourceText,
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
                logger.error("Translation failed", e);
            }
        }).start();
    }


    private void updateTexts() {
        creatBtn.setText(bundle.getString("create"));
        cancelButton.setText(bundle.getString("cancel"));
        groupName.setPromptText(bundle.getString("groupName"));
        groupDes.setPromptText(bundle.getString("groupDescription"));
        selectedName.setText(bundle.getString("name"));
        unSelectedName.setText(bundle.getString("name"));
        allStudentslabel.setText(bundle.getString("allStudents"));
        selectedStudentslabel.setText(bundle.getString("selectedStudents"));
    }

    //refresh the table view
    void refresh() {
        unSelectedStudentsList.setItems(unselectedStudents);
        selectedStudentsList.setItems(selectedStudents);
    }

    //create a new group
    @FXML
    void createNewGroup(MouseEvent event) {
        if (groupName.getText().isEmpty() || groupDes.getText().isEmpty()) {
            logger.error(bundle.getString("groupNameEmpty"));
            return;
        }
        
        int groupId = groupDao.createGroup(view.getCurrentUserId());

        if (groupId != -1) {
            groupDao.addGroupLocalized(groupId, "en", groupName.getText(), groupDes.getText());
            groupDao.addGroupLocalized(groupId, "zh", groupNameCn.getText(), groupDesCn.getText());
            groupDao.addGroupLocalized(groupId, "ja", groupNameJa.getText(), groupDesJa.getText());
        }

        if (!selectedStudents.isEmpty()) {


            for (Student student : selectedStudents) {
                groupDao.addStudentToGroup(groupId, student.getId());
            }
        }

        this.view.openGroups();
    }

    @FXML
    void cancelNewGroup(MouseEvent event) {
        this.view.openGroups();
    }

    //move a student to the selected list
    @FXML
    void moveStudentToSelectedList(MouseEvent event) {
        Student selectedStudent = unSelectedStudentsList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            selectedStudents.add(selectedStudent);
            unselectedStudents.remove(selectedStudent);
        }
        refresh();
    }

    //move a student to the unselected list
    @FXML
    void moveStudentToUnselectedList(MouseEvent event) {
        Student selectedStudent = selectedStudentsList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            unselectedStudents.add(selectedStudent);
            selectedStudents.remove(selectedStudent);
        }
        refresh();
    }

    public Button getCreatBtn() {
        return creatBtn;
    }

    public void setCreatBtn(Button creatBtn) {
        this.creatBtn = creatBtn;
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

    public TableColumn<Student, Integer> getUnSelectedId() {
        return unSelectedId;
    }

    public void setUnSelectedId(TableColumn<Student, Integer> unSelectedId) {
        this.unSelectedId = unSelectedId;
    }

    public TableColumn<Student, String> getUnSelectedName() {
        return unSelectedName;
    }

    public void setUnSelectedName(TableColumn<Student, String> unSelectedName) {
        this.unSelectedName = unSelectedName;
    }

    public TableView<Student> getSelectedStudentsList() {
        return selectedStudentsList;
    }

    public void setSelectedStudentsList(TableView<Student> selectedStudentsList) {
        this.selectedStudentsList = selectedStudentsList;
    }

    public TableView<Student> getUnSelectedStudentsList() {
        return unSelectedStudentsList;
    }

    public void setUnSelectedStudentsList(TableView<Student> unSelectedStudentsList) {
        this.unSelectedStudentsList = unSelectedStudentsList;
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

    public void setGroupNameCn(TextField groupNameCn) {
        this.groupNameCn = groupNameCn;
    }

    public void setGroupDesCn(TextField groupDesCn) {
        this.groupDesCn = groupDesCn;
    }

    public void setGroupNameJa(TextField groupNameJa) {
        this.groupNameJa = groupNameJa;
    }

    public void setGroupDesJa(TextField groupDesJa) {
        this.groupDesJa = groupDesJa;
    }
}
