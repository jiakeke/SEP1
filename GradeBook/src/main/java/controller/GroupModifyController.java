package controller;

import datasource.MariaDbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GroupModifyController {

    @FXML
    private ListView<String> allStudentList;

    @FXML
    private TextField groupDes;

    @FXML
    private TextField groupName;

    @FXML
    private Button saveBtn;

    @FXML
    private ListView<String> selectedStudentList;

    private int groupId;

    private Connection conn= MariaDbConnection.getConnection();
    private ObservableList<String> unselectedStudents= FXCollections.observableArrayList();;
    private ObservableList<String> selectedStudents=FXCollections.observableArrayList();;

    private GroupManageController groupManageController=new GroupManageController();
    public void setGroupManageController(GroupManageController groupManageController ,int groupId) {
        this.groupManageController = groupManageController;
        this.groupId=groupId;
        loadGroupDetails();
    }


    public void loadGroupDetails() {

        String gruopInfoQuery = "SELECT * FROM groups WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(gruopInfoQuery)) {
            stmt.setInt(1,groupId);
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
            stmt.setInt(1,groupId);
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                unselectedStudents.add(rs.getInt("id") + " " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String selectedQuery = "SELECT id,name FROM students WHERE id IN (SELECT student_id FROM group_students WHERE group_id=?)";
        try (PreparedStatement stmt = conn.prepareStatement(selectedQuery)) {
            stmt.setInt(1,groupId);
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                selectedStudents.add(rs.getInt("id") + " " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        allStudentList.setItems( unselectedStudents);
        selectedStudentList.setItems(selectedStudents);
    }

    void refresh(){
        allStudentList.setItems( unselectedStudents);
        selectedStudentList.setItems(selectedStudents);
    }

    @FXML
    void moveStudentToSelectedList(MouseEvent event) {
        String selectedStudent = allStudentList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            selectedStudents.add(selectedStudent);
            unselectedStudents.remove(selectedStudent);
        }
//        selectedStudentsList.setItems(selectedStudents);
        refresh();
    }

    @FXML
    void moveStudentToUnselectedList(MouseEvent event) {
        String selectedStudent = selectedStudentList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            unselectedStudents.add(selectedStudent);
            selectedStudents.remove(selectedStudent);
        }
//        selectedStudentsList.setItems(selectedStudents);
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
            stmt.setInt(3, groupId);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();

        }

        String deleteGroupStudents = "DELETE FROM group_students WHERE group_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteGroupStudents)) {
            stmt.setInt(1, groupId);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String student : selectedStudents) {
            String[] studentInfo = student.split(" ");
            String insertGroupStudents = "INSERT INTO group_students (group_id,student_id) VALUES (?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertGroupStudents)) {
                stmt.setInt(1, groupId);
                stmt.setInt(2, Integer.parseInt(studentInfo[0]));
                stmt.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        groupManageController.initialize();
        saveBtn.getScene().getWindow().hide();



    }
}
