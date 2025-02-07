package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;


import controller.UserController;

public class GradeBookView extends Application {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button registerButton;
    private Button loginButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Grade Book");
        Label loginLabel = new Label("Login or Register!");
        loginLabel.setStyle("-fx-font-weight: bold;");

        // Create the form layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(loginLabel, 0, 1, 2, 1);

        // Add form fields
        Label username = new Label("Username:");
        username.setStyle("-fx-font-weight: bold;");
        grid.add(username, 0, 2);

        usernameField = new TextField();
        //usernameField.setPromptText("Username");
        grid.add(usernameField, 1, 2);

        Label password = new Label("Password:");
        password.setStyle("-fx-font-weight: bold;");
        grid.add(password, 0, 3);

        passwordField = new PasswordField();
        //passwordField.setPromptText("Password");
        grid.add(passwordField, 1, 3);

        // Buttons
        loginButton = new Button("Login");
        registerButton = new Button("Register");

        // Create HBox for buttons
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().addAll(loginButton, registerButton);

        // Add buttons to the grid
        grid.add(hbBtn, 1, 4);

        // Create a border pane to add a border around the form
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(grid);
        borderPane.setPadding(new Insets(10)); // Add padding to move the border away from the window edges
        borderPane.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        // Create a StackPane to add padding around the BorderPane
        StackPane root = new StackPane();
        root.setPadding(new Insets(40)); // Padding outside the border
        root.getChildren().add(borderPane);

        // Create a scene with a larger size
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add("styles.css");

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
        welcomeLabel.getStyleClass().add("page-title");
        VBox vbox = new VBox(10, welcomeLabel);

        Button studentsButton = new Button("Students");
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
