package controller;

import application.GradeBookView;
import dao.StudentDAO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Student;
import util.LangContext;

import java.util.ResourceBundle;

import java.sql.SQLException;
import java.util.List;

public class StudentController {
    private GradeBookView view;
    Stage studentsStage = new Stage();
    private ResourceBundle bundle;

    private Label titleLabel;
    private Label subtitleLabel;
    private Label modifytitleLabel;
    private TextField searchField;
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private Button searchButton;
    private Button refreshButton;
    private Button addStudentButton;
    private Button modifyStudentButton;
    private Button deleteStudentButton;
    private Button submitButton;
    private Button saveButton;
    private TableColumn<Student, String> nameCol;
    private TableColumn<Student, String> emailCol;
    private TableColumn<Student, String> phoneCol;

    public StudentController(GradeBookView view ,ResourceBundle bundle) {
        this.view = view;
        this.bundle = bundle;
        this.studentsStage.setTitle("Students");

        LangContext.currentLang.addListener((obs, oldlang, newLang)->{
            this.bundle=LangContext.getBundle();
            updateTexts();
        });
    }

    // Handle open students
    public void handleOpenStudents() {
        titleLabel = new Label(bundle.getString("students_button"));
        titleLabel.getStyleClass().add("page-title");
        // Search bar
        searchField = new TextField();
        searchField.setPromptText(bundle.getString("search"));
        searchField.setId("search-field");
        searchButton = new Button(bundle.getString("search_button"));
        searchButton.setId("search-button");
        refreshButton = new Button(bundle.getString("refresh"));
        refreshButton.setId("refresh-button");
        addStudentButton = new Button(bundle.getString("add_student"));
        addStudentButton.setId("add-button");
        modifyStudentButton = new Button(bundle.getString("modify_student"));
        modifyStudentButton.setId("modify-button");
        deleteStudentButton = new Button(bundle.getString("delete_student"));
        deleteStudentButton.setId("delete-button");

        // Student table
        TableView<Student> studentTable = new TableView<>();
        studentTable.setId("studentTable");
        nameCol = new TableColumn<>(bundle.getString("name"));
        nameCol.setId("name-col");
        emailCol = new TableColumn<>(bundle.getString("email"));
        emailCol.setId("email-col");
        phoneCol = new TableColumn<>(bundle.getString("phone"));
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
            return row;
        });

        searchButton.setOnAction(e -> handleSearch(searchField.getText(), studentTable));
        refreshButton.setOnAction(e -> loadStudents(studentTable));
        addStudentButton.setOnAction(e -> handleAddStudent(studentTable));
        modifyStudentButton.setOnAction(e -> handleModifyStudent(studentTable.getSelectionModel().getSelectedItem(), studentTable));
        deleteStudentButton.setOnAction(e -> handleDeleteStudent(studentTable.getSelectionModel().getSelectedItem(), studentTable));

        searchButton.getStyleClass().add("search-button");
        refreshButton.getStyleClass().add("refresh-button");
        addStudentButton.getStyleClass().add("add-button");
        modifyStudentButton.getStyleClass().add("modify-button");
        deleteStudentButton.getStyleClass().add("delete-button");

        HBox searchButtonContainer = new HBox(searchButton);
        searchButtonContainer.setAlignment(Pos.CENTER);
        HBox buttons = new HBox(15, refreshButton, addStudentButton, modifyStudentButton, deleteStudentButton);
        buttons.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10, titleLabel, searchField, searchButtonContainer, studentTable, buttons);
        layout.setPadding(new Insets(20, 20, 20, 20));
        view.getRootPane().setCenter(layout);
    }

    private void setTextIfNotNull(Labeled node, String key) {
        if (node != null) node.setText(bundle.getString(key));
    }

    private void setPromptIfNotNull(TextInputControl node, String key) {
        if (node != null) node.setPromptText(bundle.getString(key));
    }

    private void updateTexts() {
        if (studentsStage != null) studentsStage.setTitle(bundle.getString("students"));

        setTextIfNotNull(titleLabel, "students_button");
        setTextIfNotNull(subtitleLabel, "add_new_student");
        setTextIfNotNull(modifytitleLabel, "modify_student_title");

        setPromptIfNotNull(searchField, "search");
        setPromptIfNotNull(nameField, "name");
        setPromptIfNotNull(emailField, "email");
        setPromptIfNotNull(phoneField, "phone");

        setTextIfNotNull(searchButton, "search_button");
        setTextIfNotNull(refreshButton, "refresh");
        setTextIfNotNull(addStudentButton, "add_student");
        setTextIfNotNull(modifyStudentButton, "modify_student");
        setTextIfNotNull(deleteStudentButton, "delete_student");
        setTextIfNotNull(submitButton, "submit");
        setTextIfNotNull(saveButton, "save");

        if (nameCol != null) nameCol.setText(bundle.getString("name"));
        if (emailCol != null) emailCol.setText(bundle.getString("email"));
        if (phoneCol != null) phoneCol.setText(bundle.getString("phone"));
    }


    // Load students from database
    void loadStudents(TableView<Student> studentTable) {
        try {
            List<Student> students = StudentDAO.showAllStudents();
            studentTable.getItems().setAll(students);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("database_error", "load_students_fail");
        }
    }

    // Search students by name
    void handleSearch(String name, TableView<Student> studentTable) {
        try {
            List<Student> students = StudentDAO.searchStudentByName(name);
            studentTable.getItems().setAll(students);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("database_error", "search_fail");
        }
    }

    // Add a new student
    void handleAddStudent(TableView<Student> studentTable) {
        subtitleLabel = new Label(bundle.getString("add_new_student"));
        subtitleLabel.getStyleClass().add("page-title");

        nameField = new TextField();
        nameField.setId("name-field");
        nameField.setPromptText(bundle.getString("name"));

        emailField = new TextField();
        emailField.setId("email-field");
        emailField.setPromptText(bundle.getString("email"));

        phoneField = new TextField();
        phoneField.setId("phone-field");
        phoneField.setPromptText(bundle.getString("phone"));

        submitButton = new Button(bundle.getString("submit"));
        submitButton.setId("submit-button");
        submitButton.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();

                if (!validateStudentInput(name, email, phone)) {
                    return;
                }

                Student newStudent = new Student(0, name, email, phone);
                StudentDAO.registerStudent(newStudent);
                loadStudents(studentTable);

                handleOpenStudents();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("database_error", "add_student_fail");
            }
        });

        submitButton.getStyleClass().add("submit-button");
        HBox submitButtonContainer = new HBox(submitButton);
        submitButtonContainer.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10, subtitleLabel, nameField, emailField, phoneField, submitButtonContainer);
        layout.setPadding(new Insets(20, 20, 20, 20));
        view.getRootPane().setCenter(layout);
    }

    // Modify student
    private void handleModifyStudent(Student student, TableView<Student> studentTable) {
        modifytitleLabel = new Label(bundle.getString("modify_student_title"));
        modifytitleLabel.getStyleClass().add("page-title");

        nameField = new TextField(student.getName());
        nameField.setId("name-field");
        nameField.setPromptText(bundle.getString("name"));
        emailField = new TextField(student.getEmail());
        emailField.setId("email-field");
        emailField.setPromptText(bundle.getString("email"));
        phoneField = new TextField(student.getPhone());
        phoneField.setId("phone-field");
        phoneField.setPromptText(bundle.getString("phone"));

        saveButton = new Button(bundle.getString("save"));
        saveButton.setId("save-button");
        saveButton.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                student.setName(name);
                student.setEmail(email);
                student.setPhone(phone);

                if (!validateStudentInput(name, email, phone)) {
                    return;
                }

                StudentDAO.updateStudent(student);
                loadStudents(studentTable);

                handleOpenStudents();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("database_error", "update_student_fail");
            }
        });

        saveButton.getStyleClass().add("save-button");
        HBox saveButtonContainer = new HBox(saveButton);
        saveButtonContainer.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10, modifytitleLabel, nameField, emailField, phoneField, saveButtonContainer);
        layout.setPadding(new Insets(20, 20, 20, 20));
        view.getRootPane().setCenter(layout);
    }

    // Delete student
    void handleDeleteStudent(Student student, TableView<Student> studentTable) {


        ConfirmDialog dialog = new ConfirmDialog(studentsStage, bundle.getString("delete_student_message"));

        dialog.showAndWait().thenAccept(confirmed -> {
            if (confirmed) {
                try {
                    StudentDAO.deleteStudent(student.getId());
                    loadStudents(studentTable);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("database_error", "delete_student_fail");
                }
            } else {
                System.out.println("Deletion cancelled");
            }
        });

    }

    private boolean validateStudentInput(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            showAlert("invalid_name", "name_empty");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert("invalid_email", "email_message");
            return false;
        }

        if (!isValidPhoneNumber(phone)) {
            showAlert("invalid_phone", "phone_message");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phone) {
        String phoneRegex = "^\\d{6,15}$";
        return phone.matches(phoneRegex);
    }

    // Show alert dialog
    public void showAlert(String titleKey, String messageKey) {
        Platform.runLater(() -> {
            ResourceBundle bundle = LangContext.getBundle();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString(titleKey));
            alert.setHeaderText(null);
            alert.setContentText(bundle.getString(messageKey));
            alert.showAndWait();
        });
    }
}
