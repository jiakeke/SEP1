package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import controller.UserController;

public class GradeBookView extends Application {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button registerButton;
    private Button loginButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Grade Book");

        // Input fields
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Buttons
        registerButton = new Button("Register");
        loginButton = new Button("Login");

        // Layout
        VBox vbox = new VBox(10, usernameField, passwordField, registerButton, loginButton);
        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Create controller and pass view components
        UserController controller = new UserController(this);
        registerButton.setOnAction(controller::handleRegister);
        loginButton.setOnAction(controller::handleLogin);
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getRegisterButton() {
        return registerButton;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void showSystemInterface(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome to Grade Book!");
        VBox vbox = new VBox(10, welcomeLabel);

        Button studentsButton = new Button("Students");
        Button groupsButton = new Button("Groups");

        vbox.getChildren().addAll(studentsButton, groupsButton);

        studentsButton.setOnAction(e -> openStudents());
        groupsButton.setOnAction(e -> openGroups());

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
    }

    private void openStudents() {
        Stage stage = new Stage();
        stage.setTitle("Students");
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void openGroups() {
        Stage stage = new Stage();
        stage.setTitle("Groups");
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

}
