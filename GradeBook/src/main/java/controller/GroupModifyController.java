package controller;

import application.GradeBookView;
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
    private GradeBookView view;
    private Group group;


    public GroupModifyController(GradeBookView view, Group group) {
        this.view = view;
        this.group = group;
    }

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

    private GroupDao groupDao = new GroupDao();
    private ObservableList<Student> unselectedStudents = FXCollections.observableArrayList();
    private ObservableList<Student> selectedStudents = FXCollections.observableArrayList();

    private GroupManageController groupManageController = new GroupManageController(this.view);

    // This method loads the group details
    public void initialize() {
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