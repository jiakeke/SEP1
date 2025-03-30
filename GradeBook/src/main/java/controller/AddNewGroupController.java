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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Student;

import java.util.ResourceBundle;


public class AddNewGroupController {
    private GradeBookView view;
    private final ResourceBundle bundle;

    public AddNewGroupController(GradeBookView view, ResourceBundle bundle) {
        this.view = view;
        this.bundle = bundle;
    }

    @FXML
    private Button creatBtn;

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

    private GroupDao groupDao = new GroupDao();
    private ObservableList<Student> unselectedStudents = FXCollections.observableArrayList();
    private ObservableList<Student> selectedStudents = FXCollections.observableArrayList();

    //initialize the table view with the students
    @FXML
    public void initialize() {
        unSelectedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        unSelectedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        selectedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        selectedName.setCellValueFactory(new PropertyValueFactory<>("name"));

        unselectedStudents.addAll(groupDao.getStudentsNotInGroup(0));
        unSelectedStudentsList.setItems(unselectedStudents);
        selectedStudentsList.setItems(selectedStudents);
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
            System.out.println(bundle.getString("groupNameEmpty"));
            return;
        }

        groupDao.addGroup(groupName.getText(), groupDes.getText());

        if (!selectedStudents.isEmpty()) {
            int groupId = groupDao.getAllGroups().stream()
                    .filter(group -> group.getName().equals(groupName.getText()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(bundle.getString("groupNotFound")))
                    .getId();

            for (Student student : selectedStudents) {
                groupDao.addStudentToGroup(groupId, student.getId());
            }
        }

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

}