package controller;

import datasource.MariaDbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;


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

    private Connection conn= MariaDbConnection.getConnection();
    private ObservableList<Student> unselectedStudents=FXCollections.observableArrayList();;
    private ObservableList<Student> selectedStudents=FXCollections.observableArrayList();;

    private GroupManageController groupManageController=new GroupManageController();

    public void setGroupManageController(GroupManageController groupManageController) {
        this.groupManageController = groupManageController;
    }

    @FXML
    public void initialize() {

        // Load students
        unSelectedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        unSelectedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        selectedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        selectedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        String query = "SELECT id,name FROM students";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                unselectedStudents.add(new Student(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        unSelectedStudentsList.setItems(unselectedStudents);
        selectedStudentsList.setItems(selectedStudents);
    }

    void refresh(){
        unSelectedStudentsList.setItems( unselectedStudents);
        selectedStudentsList.setItems(selectedStudents);
    }


    @FXML
    void createNewGroup(MouseEvent event) {
        if (groupName.getText().isEmpty() || groupDes.getText().isEmpty() ) {
            System.out.println("Please fill in all fields");
            return;
        }
        if (selectedStudents.isEmpty()) {
            String query = "INSERT INTO groups (name,description) VALUES (?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, groupName.getText());
                stmt.setString(2, groupDes.getText());
                stmt.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            String query = "INSERT INTO groups (name,description) VALUES (?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, groupName.getText());
                stmt.setString(2, groupDes.getText());
                stmt.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int groupId = 0;
            query = "SELECT id FROM groups WHERE name=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, groupName.getText());
                stmt.execute();
                var rs = stmt.getResultSet();
                while (rs.next()) {
                    groupId = rs.getInt("id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Student student : selectedStudents) {

                query = "INSERT INTO group_students (group_id,student_id) VALUES (?,?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, groupId);
                    stmt.setInt(2, student.getId());
                    stmt.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // close the window
        creatBtn.getScene().getWindow().hide();
        if (groupManageController != null) {
            groupManageController.initialize();
        }
    }

    @FXML
    void moveStudentToSelectedList(MouseEvent event) {
        Student selectedStudent = unSelectedStudentsList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            selectedStudents.add(selectedStudent);
            unselectedStudents.remove(selectedStudent);
        }
        refresh();
    }

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
