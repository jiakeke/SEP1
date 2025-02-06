package controller;

import application.GradeBookView;
import dao.StudentDAO;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentController {
    private GradeBookView view;

    public StudentController(GradeBookView view) {
        this.view = view;
    }

    // 处理“学生”按钮点击事件
    public void handleOpenStudents(ActionEvent event) {
        Stage stage = new Stage();
        stage.setTitle("Students");

        // 搜索框
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name");
        Button searchButton = new Button("Search");
        Button refreshButton = new Button("Refresh");
        Button addStudentButton = new Button("Add Student");

        // 学生表格
        TableView<Student> studentTable = new TableView<>();
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");

        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        phoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        studentTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol);
        loadStudents(studentTable); // 载入数据

        searchButton.setOnAction(e -> handleSearch(searchField.getText(), studentTable));
        refreshButton.setOnAction(e -> loadStudents(studentTable));
        addStudentButton.setOnAction(e -> handleAddStudent(studentTable));

        VBox layout = new VBox(10, searchField, searchButton, studentTable, refreshButton, addStudentButton);
        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    // 载入所有学生
    private void loadStudents(TableView<Student> studentTable) {
        try {
            List<Student> students = StudentDAO.showAllStudents();
            studentTable.getItems().setAll(students);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 搜索学生
    private void handleSearch(String name, TableView<Student> studentTable) {
        if (!name.isEmpty()) {
            try {
                List<Student> students = StudentDAO.searchStudentByName(name);
                studentTable.getItems().setAll(students);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 添加学生
    private void handleAddStudent(TableView<Student> studentTable) {
        Stage addStage = new Stage();
        addStage.setTitle("Add New Student");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();

                Student newStudent = new Student(0, name, email, phone); // 无 ID
                StudentDAO.registerStudent(newStudent); // 由数据库生成 ID
                loadStudents(studentTable); // 刷新数据
                addStage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to add student.");
            }
        });

        VBox layout = new VBox(10, nameField, emailField, phoneField, submitButton);
        Scene scene = new Scene(layout, 400, 300);
        addStage.setScene(scene);
        addStage.show();
    }

    // 显示错误提示框
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
