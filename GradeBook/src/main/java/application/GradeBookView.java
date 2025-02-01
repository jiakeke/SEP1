package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class GradeBookView extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("GradeBook.fxml"));
            // Create a controller instance
            //GradeBookController controller = new GradeBookController();
            // Set the controller
            //loader.setController(controller);
            // Load the FXML file
            //Parent root = loader.load();
            // Create a scene
            GridPane root = new GridPane();
            Scene scene = new Scene(root);
            // Set the scene
            primaryStage.setScene(scene);
            // Set the title
            primaryStage.setTitle("Grade Book");
            // Show the stage
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
