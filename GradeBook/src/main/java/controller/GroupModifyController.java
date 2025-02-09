package controller;

import dao.GroupDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.Group;
import model.Student;


public class GroupModifyController {

    @FXML
    private TextField groupDes;

    @FXML
    private TextField groupName;

    @FXML
    private Button saveBtn;

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

    private Group group;

    private GroupDao groupDao = new GroupDao();
    private ObservableList<Student> unselectedStudents = FXCollections.observableArrayList();
    private ObservableList<Student> selectedStudents = FXCollections.observableArrayList();

    private GroupManageController groupManageController = new GroupManageController();

    // This method sets the groupManageController and group
    public void setGroupManageController(GroupManageController groupManageController, Group group) {
        this.groupManageController = groupManageController;
        this.group = group;
        loadGroupDetails();
    }

    // This method loads the group details
    public void loadGroupDetails() {
        selectedId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        selectedName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        unselectedId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        unselectedName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));

        Group groupDetails = groupDao.getGroupById(group.getId());
        if (groupDetails != null) {
            groupName.setText(groupDetails.getName());
            groupDes.setText(groupDetails.getDescription());
        }

        unselectedStudents.addAll(groupDao.getStudentsNotInGroup(group.getId()));
        selectedStudents.addAll(groupDao.getStudentsInGroup(group.getId()));

        unSelectedStudentsList.setItems(unselectedStudents);
        selectedStudentList.setItems(selectedStudents);
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

        groupDao.updateGroup(group.getId(), groupName.getText(), groupDes.getText());
        groupDao.deleteGroupStudents(group.getId());

        for (Student student : selectedStudents) {
            groupDao.addStudentToGroup(group.getId(), student.getId());
        }

        groupManageController.initialize();
        saveBtn.getScene().getWindow().hide();
    }
}