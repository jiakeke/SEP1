package controller;

import application.GradeBookView;
import dao.GradeTypeDAO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.GradeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.GoogleTranslateUtil;
import util.LangContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.ResourceBundle;

public class GradeTypeController {

    private static TableView<GradeType> gradeTypeTable;
    private static TableColumn<GradeType, String> nameColumn;
    private static TableColumn<GradeType, Double> weightColumn;
    private static TextField nameField;
    private static TextField weightField;
    private static Button saveButton;
    private static Button deleteButton;
    private static TextField nameFieldCN;
    private static TextField nameFieldJP;

    private static int currentGroupId;
    private static Stage stage;
    private static GradeBookView rootview;
    private static ResourceBundle bundle;
    private static Map<String, String> localizedNames=new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(GradeTypeController.class);
    public static void showGradeTypeEditor(GradeBookView view, int groupId) {
        rootview = view;
        currentGroupId = groupId;
        initializeUI(LangContext.getBundle());
        loadGradeTypes();

        LangContext.currentLang.addListener((obs, oldlang, newLang)->{
            bundle=LangContext.getBundle();
            updateTexts();
            loadGradeTypes();

        });
    }

    private static void initializeUI(ResourceBundle bundle) {
        // Build TableView
        gradeTypeTable = new TableView<>();
        nameColumn = new TableColumn<>(bundle.getString("name"));
        weightColumn = new TableColumn<>(bundle.getString("weight"));

        nameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        weightColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("weight"));

        gradeTypeTable.getColumns().addAll(nameColumn, weightColumn);

        gradeTypeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                //要把三种语言的内容都填进去，需要先通过gradeId去查。

                int gradeTypeId = newSelection.getId();
                Map<String, String> localizedNames = GradeTypeDAO.getLocalizedNamesByGradeTypeId(gradeTypeId);
                nameField.setText(localizedNames.get("en"));
                nameFieldCN.setText(localizedNames.get("zh"));
                nameFieldJP.setText(localizedNames.get("ja"));
                weightField.setText(String.valueOf(newSelection.getWeight()));

            }
        });

        // TextFields
        nameField = new TextField();
        nameField.setPromptText("Grade Type Name");

        weightField = new TextField();
        weightField.setPromptText(bundle.getString("weight"));

        nameFieldCN= new TextField();
        nameFieldCN.setPromptText("成绩类型");

        nameFieldJP= new TextField();
        nameFieldJP.setPromptText("成績の種類");

        // Buttons
        saveButton = new Button(bundle.getString("save"));
        saveButton.getStyleClass().add("save-button");
        deleteButton = new Button(bundle.getString("delete"));
        deleteButton.getStyleClass().add("delete-button");

        saveButton.setOnAction(e -> handleSave());
        deleteButton.setOnAction(e -> handleDelete());
        HBox buttonSet = new HBox(15, saveButton, deleteButton);
        buttonSet.setAlignment(Pos.CENTER);

        //增加监听器
        PauseTransition delayNameEn = new PauseTransition(Duration.millis(800));
        PauseTransition delayNameZh = new PauseTransition(Duration.millis(800));
        PauseTransition delayNameJa = new PauseTransition(Duration.millis(800));

        nameField.setOnKeyReleased(e -> {
            delayNameEn.stop();
            delayNameEn.setOnFinished(ev -> autoTranslate(nameField.getText(), nameFieldCN, "zh", nameFieldJP, "ja"));
            delayNameEn.playFromStart();
        });

        nameFieldCN.setOnKeyReleased(e -> {
            delayNameZh.stop();
            delayNameZh.setOnFinished(ev -> autoTranslate(nameFieldCN.getText(), nameField, "en", nameFieldJP, "ja"));
            delayNameZh.playFromStart();
        });

        nameFieldJP.setOnKeyReleased(e -> {
            delayNameJa.stop();
            delayNameJa.setOnFinished(ev -> autoTranslate(nameFieldJP.getText(), nameField, "en", nameFieldCN, "zh"));
            delayNameJa.playFromStart();
        });


        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(gradeTypeTable, nameField,nameFieldCN,nameFieldJP, weightField, buttonSet);

        rootview.getRootPane().setCenter(layout);
    }

    // Auto translate
    private static void autoTranslate(String sourceText,
                               TextField target1, String target1Lang,
                               TextField target2, String target2Lang) {
        if (sourceText == null || sourceText.isBlank()) return;

        new Thread(() -> {
            try {
                String translated1 = GoogleTranslateUtil.translate(sourceText, target1Lang);
                String translated2 = GoogleTranslateUtil.translate(sourceText, target2Lang);

                Platform.runLater(() -> {
                    target1.setText(translated1);
                    target2.setText(translated2);
                });
            } catch (IOException e) {
                logger.error("Translation failed", e);
            }
        }).start();
    }

    // Load GradeType
    private static void loadGradeTypes() {
        try {
            List<GradeType> gradeTypes = GradeTypeDAO.showGradeTypesByGroupId(currentGroupId, LangContext.currentLang.get());
            ObservableList<GradeType> observableList = FXCollections.observableArrayList(gradeTypes);
            gradeTypeTable.setItems(observableList);
        } catch (SQLException e) {
            showError("Load fail", "Unable to load: " + e.getMessage());
        }
    }

    // Save GradeType
    private static void handleSave() {
        String name = nameField.getText();
        String weightText = weightField.getText();

        if (name.isEmpty() || weightText.isEmpty()) {
            showError("Input error", "Please fill in all fields");
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightText);
        } catch (NumberFormatException e) {
            showError("Input error", "Weight must be a number");
            return;
        }

        GradeType selectedGradeType = gradeTypeTable.getSelectionModel().getSelectedItem();
        localizedNames.put("en",nameField.getText());
        localizedNames.put("zh",nameFieldCN.getText());
        localizedNames.put("ja",nameFieldJP.getText());
        if (selectedGradeType == null) {
            GradeType newGradeType = new GradeType(0, name, weight, currentGroupId);
            try {
                GradeTypeDAO.registerGradeType(newGradeType,localizedNames);
                loadGradeTypes();
            } catch (SQLException e) {
                showError("Database error", "Unable to add grade type:" + e.getMessage());
            }
        } else {
            selectedGradeType.setName(name);
            selectedGradeType.setWeight(weight);
            try {
                GradeTypeDAO.updateGradeType(selectedGradeType,localizedNames);
                loadGradeTypes();
            } catch (SQLException e) {
                showError("Database error", "Unable to update grade type" + e.getMessage());
            }
        }
        nameField.clear();
        weightField.clear();
        nameFieldCN.clear();
        nameFieldJP.clear();
    }

    // Delete GradeType
    private static void handleDelete() {
        GradeType selectedGradeType = gradeTypeTable.getSelectionModel().getSelectedItem();
        if (selectedGradeType == null) {
            showError("Select error", "Please select a grade type to delete");
            return;
        }

        try {
            GradeTypeDAO.deleteGradeType(selectedGradeType.getId());
            loadGradeTypes();
            nameField.clear();
            weightField.clear();
            nameFieldCN.clear();
            nameFieldJP.clear();
        } catch (SQLException e) {
            showError("Database error", "Unable to delete grade type" + e.getMessage());
        }
    }

    private static void updateTexts() {
        nameColumn.setText(bundle.getString("name"));
        weightColumn.setText(bundle.getString("weight"));
        weightField.setPromptText(bundle.getString("weight"));
        saveButton.setText(bundle.getString("save"));
        deleteButton.setText(bundle.getString("delete"));
    }

    // Error message
    static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
