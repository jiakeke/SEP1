package application;

import controller.GroupManageController;
import controller.StudentController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import controller.UserController;

import java.io.IOException;

public class GradeBookView extends Application {
    private Text errorLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button registerButton;
    private Button loginButton;
    private Scene scene;
    private BorderPane root;

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
        root = new BorderPane();
        Button enLang = new Button("EN");
        Button zhLang = new Button("ZH");
        Button jpLang = new Button("JP");
        HBox langBox = new HBox(10);
        langBox.setAlignment(Pos.TOP_RIGHT);
        langBox.getChildren().addAll(enLang, zhLang, jpLang);
        root.setTop(langBox);

        StackPane loginPane = new StackPane();
        loginPane.setPadding(new Insets(200)); // Padding outside the border
        loginPane.getChildren().add(borderPane);
        root.setCenter(loginPane);

        // Create a scene with a larger size
        scene = new Scene(root, 800, 600);
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

    public BorderPane getRootPane() {
        return root;
    }

    public void showSystemInterface() {
        Label swLabel = new Label("Grade Book!");
        swLabel.getStyleClass().add("page-title");
        VBox vbox = new VBox(10, swLabel);
        Label welcomeLabel = new Label("Welcome!");
        welcomeLabel.getStyleClass().add("page-title");

        Button studentsButton = new Button("Students");
        studentsButton.setId("studentsButton");
        Button groupsButton = new Button("Groups");

        studentsButton.getStyleClass().add("big-button");
        groupsButton.getStyleClass().add("big-button");

        vbox.getChildren().addAll(studentsButton, groupsButton);

        studentsButton.setOnAction(e -> openStudents());
        groupsButton.setOnAction(e -> openGroups());
        root.setLeft(vbox);
        root.setCenter(welcomeLabel);

    }

    private void openStudents() {
        StudentController studentController = new StudentController(this);
        studentController.handleOpenStudents(new ActionEvent());
    }


    public void openGroups() {
        //GroupManageView groupManageView = new GroupManageView();
        //Stage stage = new Stage();
        //groupManageView.start(root);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group.fxml"));
            loader.setController(new GroupManageController(this));
            root.setCenter(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
