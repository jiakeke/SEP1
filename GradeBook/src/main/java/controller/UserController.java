package controller;

import application.GradeBookView;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.User;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class UserController {
    private GradeBookView view;

    public UserController(GradeBookView view) {
        this.view = view;
    }

    public void handleRegister(ActionEvent event) {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();
        User user = new User(0, username, password);
        try {
            UserDAO.registerUser(user);
            System.out.println("User registered successfully!");
            if (UserDAO.loginUser(username, password)) {
                System.out.println("Login successful!");
                view.showSystemInterface((Stage) view.getRegisterButton().getScene().getWindow());
            }
        } catch (SQLException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    public void handleLogin(ActionEvent event) {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();
        try {
            if (UserDAO.loginUser(username, password)) {
                System.out.println("Login successful!");
                view.showSystemInterface((Stage) view.getLoginButton().getScene().getWindow());
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }
}
