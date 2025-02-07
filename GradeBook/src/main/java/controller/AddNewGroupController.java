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
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class AddNewGroupController {

    @FXML
    private Button creatBtn;

    @FXML
    private TextField groupDes;

    @FXML
    private TextField groupName;

    @FXML
    private ListView<String> selectedStudentsList;

    @FXML
    private ListView<String> unSelectStudentsList;

    private Connection conn= MariaDbConnection.getConnection();
    private ObservableList<String> unselectedStudents=FXCollections.observableArrayList();;
    private ObservableList<String> selectedStudents=FXCollections.observableArrayList();;

    private GroupManageController groupManageController=new GroupManageController();

    public void setGroupManageController(GroupManageController groupManageController) {
        this.groupManageController = groupManageController;
    }

    @FXML
    public void initialize() {
        String query = "SELECT id,name FROM students";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                unselectedStudents.add(rs.getInt("id") + " " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        unSelectStudentsList.setItems( unselectedStudents);
        selectedStudentsList.setItems(selectedStudents);
    }

    void refresh(){
        unSelectStudentsList.setItems( unselectedStudents);
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
            for (String student : selectedStudents) {
                String[] studentInfo = student.split(" ");
                query = "INSERT INTO group_students (group_id,student_id) VALUES (?,?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, groupId);
                    stmt.setInt(2, Integer.parseInt(studentInfo[0]));
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
        String selectedStudent = unSelectStudentsList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            selectedStudents.add(selectedStudent);
            unselectedStudents.remove(selectedStudent);
        }
//        selectedStudentsList.setItems(selectedStudents);
        refresh();
    }

    @FXML
    void moveStudentToUnselectedList(MouseEvent event) {
        String selectedStudent = selectedStudentsList.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            unselectedStudents.add(selectedStudent);
            selectedStudents.remove(selectedStudent);
        }
//        selectedStudentsList.setItems(selectedStudents);
        refresh();

    }

}
