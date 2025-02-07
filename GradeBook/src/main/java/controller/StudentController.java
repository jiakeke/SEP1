package controller;

import application.GradeBookView;
import dao.StudentDAO;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
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
    public void handleOpenStudents(ActionEvent open) {
        Stage stage = new Stage();
        stage.setTitle("Students");

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name");
        Button searchButton = new Button("Search");
        Button refreshButton = new Button("Refresh");
        Button addStudentButton = new Button("Add Student");

        // Student table
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
        loadStudents(studentTable);

        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            // Modify option
            MenuItem editItem = new MenuItem("🖊 Modify");
            editItem.setOnAction(event -> handleModifyStudent(studentTable.getSelectionModel().getSelectedItem(), studentTable));

            // Delete option
            MenuItem deleteItem = new MenuItem(" 🗑 Delete");
            deleteItem.setOnAction(event -> handleDeleteStudent(studentTable.getSelectionModel().getSelectedItem(), studentTable));

            contextMenu.getItems().addAll(editItem, deleteItem);

            // Show context menu on right-click
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            return row;
        });

        searchButton.setOnAction(e -> handleSearch(searchField.getText(), studentTable));
        refreshButton.setOnAction(e -> loadStudents(studentTable));
        addStudentButton.setOnAction(e -> handleAddStudent(studentTable));

        VBox layout = new VBox(10, searchField, searchButton, studentTable, refreshButton, addStudentButton);
        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    // Load students from database
    private void loadStudents(TableView<Student> studentTable) {
        try {
            List<Student> students = StudentDAO.showAllStudents();
            studentTable.getItems().setAll(students);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search students by name
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

    // Add a new student
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

                Student newStudent = new Student(0, name, email, phone);
                StudentDAO.registerStudent(newStudent);
                loadStudents(studentTable);
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

    // Modify student
    private void handleModifyStudent(Student student, TableView<Student> studentTable) {
        if (student == null) {
            showAlert("Error", "Please select a student to modify.");
            return;
        }

        Stage editStage = new Stage();
        editStage.setTitle("Modify Student");

        TextField nameField = new TextField(student.getName());
        TextField emailField = new TextField(student.getEmail());
        TextField phoneField = new TextField(student.getPhone());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            try {
                student.setName(nameField.getText());
                student.setEmail(emailField.getText());
                student.setPhone(phoneField.getText());

                StudentDAO.updateStudent(student);
                loadStudents(studentTable);
                editStage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to update student.");
            }
        });

        VBox layout = new VBox(10, nameField, emailField, phoneField, saveButton);
        Scene scene = new Scene(layout, 400, 300);
        editStage.setScene(scene);
        editStage.show();
    }

    // Delete student
    private void handleDeleteStudent(Student student, TableView<Student> studentTable) {
        if (student == null) {
            showAlert("Error", "Please select a student to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + student.getName() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    StudentDAO.deleteStudent(student.getId());
                    loadStudents(studentTable);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Database Error", "Failed to delete student.");
                }
            }
        });
    }

    // Show alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
