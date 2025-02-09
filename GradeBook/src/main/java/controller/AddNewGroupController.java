package controller;

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


public class AddNewGroupController {

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

    private GroupManageController groupManageController = new GroupManageController();

    //
    public void setGroupManageController(GroupManageController groupManageController) {
        this.groupManageController = groupManageController;
    }

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
            System.out.println("Please fill in all fields");
            return;
        }

        groupDao.addGroup(groupName.getText(), groupDes.getText());

        if (!selectedStudents.isEmpty()) {
            int groupId = groupDao.getAllGroups().stream()
                    .filter(group -> group.getName().equals(groupName.getText()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Group not found"))
                    .getId();

            for (Student student : selectedStudents) {
                groupDao.addStudentToGroup(groupId, student.getId());
            }
        }

        creatBtn.getScene().getWindow().hide();
        if (groupManageController != null) {
            groupManageController.initialize();
        }
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
}