package controller;

import datasource.MariaDbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.Group;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;

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

    private Connection conn= MariaDbConnection.getConnection();
    private ObservableList<Student> unselectedStudents= FXCollections.observableArrayList();;
    private ObservableList<Student> selectedStudents=FXCollections.observableArrayList();;

    private GroupManageController groupManageController=new GroupManageController();
    public void setGroupManageController(GroupManageController groupManageController , Group group) {
        this.groupManageController = groupManageController;
        this.group=group;
        loadGroupDetails();
    }


    public void loadGroupDetails() {

        selectedId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        selectedName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        unselectedId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        unselectedName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));

        String gruopInfoQuery = "SELECT * FROM groups WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(gruopInfoQuery)) {
            stmt.setInt(1,group.getId());
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                groupName.setText(rs.getString("name"));
                groupDes.setText(rs.getString("description"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String query = "SELECT id,name FROM students WHERE id NOT IN (SELECT student_id FROM group_students WHERE group_id=?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1,group.getId());
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                unselectedStudents.add(new Student(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String selectedQuery = "SELECT id,name FROM students WHERE id IN (SELECT student_id FROM group_students WHERE group_id=?)";
        try (PreparedStatement stmt = conn.prepareStatement(selectedQuery)) {
            stmt.setInt(1,group.getId());
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                selectedStudents.add(new Student(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        unSelectedStudentsList.setItems( unselectedStudents);
        selectedStudentList.setItems(selectedStudents);
    }

    void refresh(){
        unSelectedStudentsList.setItems( unselectedStudents);
        selectedStudentList.setItems(selectedStudents);
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
        Student selectedStudent = selectedStudentList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            unselectedStudents.add(selectedStudent);
            selectedStudents.remove(selectedStudent);
        }
        refresh();

    }

    @FXML
    void saveModify(MouseEvent event) {

        if (groupName.getText().isEmpty() || groupDes.getText().isEmpty()) {
            return;
        }
        String updateGroupInfo = "UPDATE groups SET name=?,description=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(updateGroupInfo)) {
            stmt.setString(1, groupName.getText());
            stmt.setString(2, groupDes.getText());
            stmt.setInt(3, group.getId());
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();

        }

        String deleteGroupStudents = "DELETE FROM group_students WHERE group_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteGroupStudents)) {
            stmt.setInt(1, group.getId());
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Student student : selectedStudents) {
            String insertGroupStudents = "INSERT INTO group_students (group_id,student_id) VALUES (?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertGroupStudents)) {
                stmt.setInt(1, group.getId());
                stmt.setInt(2, student.getId());
                stmt.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        groupManageController.initialize();
        saveBtn.getScene().getWindow().hide();



    }
}
