package controller;

import application.GradeBookView;
import dao.StudentDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
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

    // Handle open students
    public void handleOpenStudents(ActionEvent open) {
        Stage stage = new Stage();
        stage.setTitle("Students");

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name");
        searchField.setId("search-field");
        Button searchButton = new Button("Search");
        searchButton.setId("search-button");
        Button refreshButton = new Button("Refresh");
        refreshButton.setId("refresh-button");
        Button addStudentButton = new Button("Add Student");
        addStudentButton.setId("add-button");

        // Student table
        TableView<Student> studentTable = new TableView<>();
        studentTable.setId("studentTable");
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setId("name-col");
        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setId("email-col");
        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setId("phone-col");

        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        phoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        studentTable.getColumns().addAll(nameCol, emailCol, phoneCol);
        loadStudents(studentTable);

        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setId("student-row");
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setId("context-menu");

            // Modify option
            MenuItem editItem = new MenuItem("🖊 Modify");
            editItem.setId("edit-item");
            editItem.setOnAction(event -> handleModifyStudent(studentTable.getSelectionModel().getSelectedItem(), studentTable));

            // Delete option
            MenuItem deleteItem = new MenuItem(" 🗑 Delete");
            deleteItem.setId("delete-item");
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

        searchButton.getStyleClass().add("search-button");
        refreshButton.getStyleClass().add("refresh-button");
        addStudentButton.getStyleClass().add("add-button");

        HBox searchButtonContainer = new HBox(searchButton);
        searchButtonContainer.setAlignment(Pos.CENTER);
        HBox buttons = new HBox(15, refreshButton, addStudentButton);
        buttons.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10, searchField, searchButtonContainer, studentTable, buttons);
        layout.setPadding(new Insets(20, 20, 20, 20));
        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    // Load students from database
    void loadStudents(TableView<Student> studentTable) {
        try {
            List<Student> students = StudentDAO.showAllStudents();
            studentTable.getItems().setAll(students);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load students from database.");
        }
    }

    // Search students by name
    void handleSearch(String name, TableView<Student> studentTable) {
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
    void handleAddStudent(TableView<Student> studentTable) {
        Stage addStage = new Stage();
        addStage.setTitle("Add New Student");

        TextField nameField = new TextField();
        nameField.setId("name-field");
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setId("email-field");
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setId("phone-field");
        phoneField.setPromptText("Phone");

        Button submitButton = new Button("Submit");
        submitButton.setId("submit-button");
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

        submitButton.getStyleClass().add("submit-button");
        HBox submitButtonContainer = new HBox(submitButton);
        submitButtonContainer.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10, nameField, emailField, phoneField, submitButtonContainer);
        layout.setPadding(new Insets(20, 20, 20, 20));
        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        addStage.setScene(scene);
        addStage.show();
    }

    // Modify student
    private void handleModifyStudent(Student student, TableView<Student> studentTable) {
//        if (student == null) {
//            showAlert("Error", "Please select a student to modify.");
//            return;
//        }

        Stage editStage = new Stage();
        editStage.setTitle("Modify Student");

        TextField nameField = new TextField(student.getName());
        nameField.setId("name-field");
        nameField.setPromptText("Name");
        TextField emailField = new TextField(student.getEmail());
        emailField.setId("email-field");
        emailField.setPromptText("Email");
        TextField phoneField = new TextField(student.getPhone());
        phoneField.setId("phone-field");
        phoneField.setPromptText("Phone");

        Button saveButton = new Button("Save");
        saveButton.setId("save-button");
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

        saveButton.getStyleClass().add("save-button");
        HBox saveButtonContainer = new HBox(saveButton);
        saveButtonContainer.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10, nameField, emailField, phoneField, saveButtonContainer);
        layout.setPadding(new Insets(20, 20, 20, 20));
        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        editStage.setScene(scene);
        editStage.show();
    }

    // Delete student
    void handleDeleteStudent(Student student, TableView<Student> studentTable) {
//        if (student == null) {
//            showAlert("Error", "Please select a student to delete.");
//            return;
//        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + student.getName() + "?");
        confirmation.getDialogPane().setId("delete-confirmation");

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
