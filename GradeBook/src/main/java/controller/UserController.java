package controller;

import application.GradeBookView;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController {
    private GradeBookView view;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(GradeBookView view) {
        this.view = view;
    }

    public String registerValidation(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return "Username and password cannot be empty.";
        }

        if (username.length() < 3 || username.length() > 20) {
            return "Username must be between 3 and 20 characters.";
        }

        if (password.length() < 6 || password.length() > 20) {
            return "Password must be between 6 and 20 characters.";
        }

        if (!username.matches("^[a-zA-Z0-9]*$")) {
            return "Username can only contain letters and numbers.";
        }

        if (!password.matches("^[a-zA-Z0-9/!@#$%&]*$")) {
            return "Password can only contain letters, numbers, \nand special characters.";
        }
        return null;
    }

    public void handleRegister(ActionEvent event) {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();
        String errorMessages = registerValidation(username, password);
        if (errorMessages != null) {
            view.setErrorLabel(errorMessages);
            return;
        }
        User user = new User(0, username, password);
        try {
            UserDAO.registerUser(user);
            logger.info("User registered successfully");
            Integer userId = UserDAO.loginUser(username, password);
            if (userId != null) {
                logger.info("User logged in successfully");
                view.showSystemInterface();
            }
        } catch (SQLException | NoSuchAlgorithmException ex) {
            logger.error("Error during user registration", ex);
        }
    }

    public void handleLogin(ActionEvent event) {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();
        try {
            Integer userId = UserDAO.loginUser(username, password);
            if (userId != null) {
                logger.info("Login successful! User ID: " + userId);
                view.setCurrentUserId(userId);
                view.showSystemInterface();
            } else {
                view.setErrorLabel("Invalid username or password.");
            }
        } catch (SQLException | NoSuchAlgorithmException ex) {
            logger.error("Error during user login", ex);
        }
    }
}
