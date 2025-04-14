package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupManageView extends Application {
    private static final Logger logger = LoggerFactory.getLogger(GroupManageView.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Group Manage");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            logger.error("Failed to load FXML file", e);
        }
    }

}
