package controller;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.concurrent.CompletableFuture;

public class ConfirmDialog {

    private final String message;
    private final Stage dialogStage;
    private boolean confirmed = false;

    public ConfirmDialog(Window owner, String message) {
        this.message = message;
        this.dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Confirmation");
    }

    public CompletableFuture<Boolean> showAndWait() {
        VBox layout = new VBox(10);
        HBox buttonLayout = new HBox(10);
        Label messageLabel = new Label(message);

        Button okButton = new Button("OK");
        okButton.setId("confirmOkButton");
        Button cancelButton = new Button("Cancel");
        cancelButton.setId("confirmCancelButton");

        okButton.setOnAction(e -> {
            confirmed = true;
            dialogStage.close();
        });

        cancelButton.setOnAction(e -> {
            confirmed = false;
            dialogStage.close();
        });

        buttonLayout.setAlignment(Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(okButton, cancelButton);
        layout.getChildren().addAll(messageLabel, buttonLayout);

        Scene scene = new Scene(layout, 300, 150);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogStage.setScene(scene);

        CompletableFuture<Boolean> result = new CompletableFuture<>();
        dialogStage.setOnHidden(e -> result.complete(confirmed));

        dialogStage.show();

        return result;
    }
}

