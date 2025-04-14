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
import util.LangContext;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradeBookView extends Application {
    private Text errorLabel;
    private Label loginLabel;
    private Label username;
    private Label swLabel;
    private Label password;
    private Label welcomeLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button registerButton;
    private Button loginButton;
    private Button studentsButton;
    private Button groupsButton;
    private Scene scene;
    private BorderPane root;
    private String currentLang = "en";
    private ResourceBundle bundle;
    private int currentUserId;
    private static final Logger logger = LoggerFactory.getLogger(GradeBookView.class);


    @Override
    public void start(Stage primaryStage) {
        setLang(currentLang);
        primaryStage.setTitle(bundle.getString("sw"));
        LangContext.currentLang.addListener((obs,oldlang,newLang)->{
            this.bundle=LangContext.getBundle();
            updateTexts(primaryStage);
        });
        errorLabel = new Text();
        errorLabel.getStyleClass().add("error-message");

        loginLabel = new Label(bundle.getString("loginOrRegister"));
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
        username = new Label(bundle.getString("userName"));
        username.getStyleClass().add("label");
        grid.add(username, 0, 3);

        usernameField = new TextField();
        usernameField.setId("usernameField");
        //usernameField.setPromptText("Username");
        grid.add(usernameField, 1, 3);

        password = new Label(bundle.getString("password"));
        password.getStyleClass().add("label");
        grid.add(password, 0, 4);

        passwordField = new PasswordField();
        passwordField.setId("passwordField");
        //passwordField.setPromptText("Password");
        grid.add(passwordField, 1, 4);

        // Buttons
        loginButton = new Button(bundle.getString("login"));
        loginButton.setId("loginButton");
        registerButton = new Button(bundle.getString("register"));
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
        Button enLang = new Button("English");
        Button zhLang = new Button("中文");
        Button jpLang = new Button("日本語");
        enLang.setOnAction(e -> LangContext.setLang("en"));
        zhLang.setOnAction(e -> LangContext.setLang("zh"));
        jpLang.setOnAction(e -> LangContext.setLang("ja"));
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

    private void updateTexts(Stage primaryStage) {
        if (bundle == null) return;
        if (primaryStage != null) primaryStage.setTitle(bundle.getString("sw"));
        if (loginLabel != null) loginLabel.setText(bundle.getString("loginOrRegister"));
        if (username != null) username.setText(bundle.getString("userName"));
        if (password != null) password.setText(bundle.getString("password"));
        if (registerButton != null) registerButton.setText(bundle.getString("register"));
        if (loginButton != null) loginButton.setText(bundle.getString("login"));
        if (swLabel != null) swLabel.setText(bundle.getString("sw") + "!");
        if (welcomeLabel != null) welcomeLabel.setText(bundle.getString("welcome"));
        if (studentsButton != null) studentsButton.setText(bundle.getString("students_button"));
        if (groupsButton != null) groupsButton.setText(bundle.getString("groups_button"));
    }

    public void setErrorLabel(String message) {
        errorLabel.setText(message);
    }
    public void setLang(String lang) {
        this.currentLang = lang;
        this.bundle= ResourceBundle.getBundle("messages", new Locale(lang));
        System.out.println("Language set to: " + lang.toUpperCase());

        //todo refresh the UI with the new language
    }
    public ResourceBundle getBundle() {
        return bundle;
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
    public String getCurrentLang(){
        return currentLang;
    }

    public void showSystemInterface() {
        swLabel = new Label(bundle.getString("sw") + "!");
        swLabel.getStyleClass().add("page-title");
        VBox vbox = new VBox(10, swLabel);
        welcomeLabel = new Label(bundle.getString("welcome"));
        welcomeLabel.getStyleClass().add("page-title");

        studentsButton = new Button(bundle.getString("students_button"));
        studentsButton.setId("studentsButton");
        groupsButton = new Button(bundle.getString("groups_button"));

        studentsButton.getStyleClass().add("big-button");
        groupsButton.getStyleClass().add("big-button");

        vbox.getChildren().addAll(studentsButton, groupsButton);

        studentsButton.setOnAction(e -> openStudents());
        groupsButton.setOnAction(e -> openGroups());
        root.setLeft(vbox);
        root.setCenter(welcomeLabel);

    }

    private void openStudents() {
        StudentController studentController = new StudentController(this, getBundle());
        studentController.handleOpenStudents(new ActionEvent());
    }


    public void openGroups() {
        //GroupManageView groupManageView = new GroupManageView();
        //Stage stage = new Stage();
        //groupManageView.start(root);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group.fxml"),getBundle());
            loader.setController(new GroupManageController(this,getBundle()));
            root.setCenter(loader.load());
        } catch (Exception e) {
            logger.error("Failed to load group.fxml", e);
        }
    }
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

}
