package controller;

import application.GradeBookView;
import dao.GradeTypeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.GradeType;
import util.LangContext;

import java.sql.SQLException;
import java.util.List;

import java.util.ResourceBundle;

public class GradeTypeController {

    private static TableView<GradeType> gradeTypeTable;
    private static TableColumn<GradeType, String> nameColumn;
    private static TableColumn<GradeType, Double> weightColumn;
    private static TextField nameField;
    private static TextField weightField;
    private static Button saveButton;
    private static Button deleteButton;

    private static int currentGroupId;
    private static Stage stage;
    private static GradeBookView rootview;
    private static ResourceBundle bundle;

    public static void showGradeTypeEditor(GradeBookView view, int groupId) {
        rootview = view;
        currentGroupId = groupId;
        initializeUI(LangContext.getBundle());
        loadGradeTypes();

        LangContext.currentLang.addListener((obs, oldlang, newLang)->{
            bundle=LangContext.getBundle();
            updateTexts();
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
                nameField.setText(newSelection.getName());
                weightField.setText(String.valueOf(newSelection.getWeight()));
            }
        });

        // TextFields
        nameField = new TextField();
        nameField.setPromptText(bundle.getString("grade_type_name"));

        weightField = new TextField();
        weightField.setPromptText(bundle.getString("weight"));

        // Buttons
        saveButton = new Button(bundle.getString("save"));
        saveButton.getStyleClass().add("save-button");
        deleteButton = new Button(bundle.getString("delete"));
        deleteButton.getStyleClass().add("delete-button");

        saveButton.setOnAction(e -> handleSave());
        deleteButton.setOnAction(e -> handleDelete());
        HBox buttonSet = new HBox(15, saveButton, deleteButton);
        buttonSet.setAlignment(Pos.CENTER);

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(gradeTypeTable, nameField, weightField, buttonSet);

        rootview.getRootPane().setCenter(layout);
    }

    // Load GradeType
    private static void loadGradeTypes() {
        try {
            List<GradeType> gradeTypes = GradeTypeDAO.showGradeTypesByGroupId(currentGroupId);
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
        if (selectedGradeType == null) {
            GradeType newGradeType = new GradeType(0, name, weight, currentGroupId);
            try {
                GradeTypeDAO.registerGradeType(newGradeType);
                loadGradeTypes();
            } catch (SQLException e) {
                showError("Database error", "Unable to add grade type:" + e.getMessage());
            }
        } else {
            selectedGradeType.setName(name);
            selectedGradeType.setWeight(weight);
            try {
                GradeTypeDAO.updateGradeType(selectedGradeType);
                loadGradeTypes();
            } catch (SQLException e) {
                showError("Database error", "Unable to update grade type" + e.getMessage());
            }
        }
        nameField.clear();
        weightField.clear();
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
        } catch (SQLException e) {
            showError("Database error", "Unable to delete grade type" + e.getMessage());
        }
    }

    private static void updateTexts() {
        nameColumn.setText(bundle.getString("name"));
        weightColumn.setText(bundle.getString("weight"));
        nameField.setPromptText(bundle.getString("grade_type_name"));
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
