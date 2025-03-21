package application;

import controller.StudentController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;

import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import controller.UserController;

public class GradeBookView extends Application {
    private Text errorLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button registerButton;
    private Button loginButton;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Grade Book");
        errorLabel = new Text();
        errorLabel.getStyleClass().add("error-message");

        Label loginLabel = new Label("Login or Register!");
        loginLabel.getStyleClass().add("label");

        // Create the form layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(errorLabel, 0, 0, 2, 1);

        grid.add(loginLabel, 0, 2, 2, 1);

        // Add form fields
        Label username = new Label("Username:");
        username.getStyleClass().add("label");
        grid.add(username, 0, 3);

        usernameField = new TextField();
        usernameField.setId("usernameField");
        //usernameField.setPromptText("Username");
        grid.add(usernameField, 1, 3);

        Label password = new Label("Password:");
        password.getStyleClass().add("label");
        grid.add(password, 0, 4);

        passwordField = new PasswordField();
        passwordField.setId("passwordField");
        //passwordField.setPromptText("Password");
        grid.add(passwordField, 1, 4);

        // Buttons
        loginButton = new Button("Login");
        loginButton.setId("loginButton");
        registerButton = new Button("Register");
        registerButton.setId("registerButton");

        // Create HBox for buttons
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().addAll(loginButton, registerButton);

        // Add buttons to the grid
        grid.add(hbBtn, 1, 5);

        // Create a border pane to add a border around the form
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(grid);
        borderPane.setPadding(new Insets(10)); // Add padding to move the border away from the window edges
        borderPane.getStyleClass().add("border-pane");

        // Create a StackPane to add padding around the BorderPane
        StackPane root = new StackPane();
        root.setPadding(new Insets(40)); // Padding outside the border
        root.getChildren().add(borderPane);

        // Create a scene with a larger size
        scene = new Scene(root, 400, 300);
        scene.getStylesheets().add("styles.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        // Create controller and pass view components
        UserController controller = new UserController(this);
        registerButton.setOnAction(controller::handleRegister);
        loginButton.setOnAction(controller::handleLogin);
    }

    public void setErrorLabel(String message) {
        errorLabel.setText(message);
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
        welcomeLabel.getStyleClass().add("page-title");
        VBox vbox = new VBox(10, welcomeLabel);

        Button studentsButton = new Button("Students");
        studentsButton.setId("studentsButton");
        Button groupsButton = new Button("Groups");

        studentsButton.getStyleClass().add("big-button");
        groupsButton.getStyleClass().add("big-button");

        HBox hbox = new HBox(20); // 20px spacing between buttons
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(studentsButton, groupsButton);

        vbox.getChildren().add(hbox);

        studentsButton.setOnAction(e -> openStudents());
        groupsButton.setOnAction(e -> openGroups());

        Scene scene = new Scene(vbox, 800, 600);
        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
    }

//    private void openStudents() {
//        Stage stage = new Stage();
//        stage.setTitle("Students");
//        VBox layout = new VBox(10);
//        Scene scene = new Scene(layout, 800, 600);
//        stage.setScene(scene);
//        stage.show();
//    }
    private void openStudents() {
        StudentController studentController = new StudentController(this);
        studentController.handleOpenStudents(new ActionEvent());
    }


    private void openGroups() {
//        Stage stage = new Stage();
//        stage.setTitle("Groups");
//        VBox layout = new VBox(10);
//        Scene scene = new Scene(layout, 800, 600);
//        stage.setScene(scene);
//        stage.show();
        GroupManageView groupManageView = new GroupManageView();
        Stage stage = new Stage();
        groupManageView.start(stage);
    }

}
