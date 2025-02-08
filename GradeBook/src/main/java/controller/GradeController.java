package controller;

import dao.GradeDAO;
import dao.StudentDAO;
import dao.GradeTypeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Grade;
import model.Student;
import model.GradeType;

import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class GradeController {

    private static TableView<Map<String, Object>> gradeTable;
    private static Button deleteButton, exportButton;

    private static int currentGroupId;
    private static Stage stage;

    public static void showGradeEditor(int groupId) {
        currentGroupId = groupId;
        stage = new Stage();
        initializeUI();
        loadGradeData();
        stage.show();
    }

    private static void initializeUI() {
        gradeTable = new TableView<>();

        deleteButton = new Button("Delete");
        exportButton = new Button("Export to PDF");

        deleteButton.setOnAction(e -> handleDelete());
        exportButton.setOnAction(e -> exportToPDF());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(deleteButton, exportButton, gradeTable);

        Scene scene = new Scene(layout, 800, 600);
        stage.setTitle("Grade Management");
        stage.setScene(scene);
    }

    private static void loadGradeData() {
        try {
            List<GradeType> gradeTypes = GradeTypeDAO.showGradeTypesByGroupId(currentGroupId);
            List<Student> students = StudentDAO.getStudentsByGroupId(currentGroupId);

            for (Student student : students) {
                for (GradeType gt : gradeTypes) {
                    if (!GradeDAO.gradeExists(student.getId(), currentGroupId, gt.getId())) {
                        GradeDAO.insertGrade(student.getId(), currentGroupId, gt.getId(), 0.0);
                    }
                }
            }

            List<Grade> grades = GradeDAO.showGradesByGroupId(currentGroupId);
            gradeTable.getColumns().clear();

            TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("name"))
            );
            gradeTable.getColumns().add(nameColumn);

            Map<Integer, TableColumn<Map<String, Object>, Double>> gradeColumns = new HashMap<>();

            for (GradeType gt : gradeTypes) {
                TableColumn<Map<String, Object>, Double> gradeColumn = new TableColumn<>(gt.getName());

                gradeColumn.setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleDoubleProperty((Double) cellData.getValue().getOrDefault(gt.getName(), 0.0)).asObject()
                );

                // allow editing
                gradeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));

                // listen for edit commit
                gradeColumn.setOnEditCommit(event -> {
                    Map<String, Object> selectedRow = event.getRowValue();
                    double newGrade = event.getNewValue();

                    // validate input
                    if (newGrade > 100) {
                        showError("Invalid Input", "Grade cannot be greater than 100.");
                        gradeTable.refresh();
                        return;
                    }

                    int studentId = (int) selectedRow.get("studentId");
                    int gradeTypeId = gt.getId();

                    selectedRow.put(gt.getName(), newGrade);
                    updateGrade(studentId, gradeTypeId, newGrade);
                });

                gradeTable.getColumns().add(gradeColumn);
                gradeColumns.put(gt.getId(), gradeColumn);
            }

            TableColumn<Map<String, Object>, Double> totalColumn = new TableColumn<>("Total");
            totalColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleDoubleProperty((Double) cellData.getValue().getOrDefault("total", 0.0)).asObject()
            );
            gradeTable.getColumns().add(totalColumn);

            ObservableList<Map<String, Object>> tableData = FXCollections.observableArrayList();

            for (Student student : students) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentId", student.getId());
                row.put("name", student.getName());

                double total = 0;
                for (GradeType gt : gradeTypes) {
                    Optional<Grade> gradeOpt = grades.stream()
                            .filter(g -> g.getStudentId() == student.getId() && g.getGradeTypeId() == gt.getId())
                            .findFirst();

                    double gradeValue = gradeOpt.map(Grade::getGrade).orElse(0.0);
                    row.put(gt.getName(), gradeValue);
                    total += gradeValue * (gt.getWeight() / 100.0);
                }
                row.put("total", total);
                tableData.add(row);
            }

            gradeTable.setItems(tableData);
            gradeTable.setEditable(true);

        } catch (SQLException e) {
            showError("Load fail", "Unable to load grades: " + e.getMessage());
        }
    }

    private static void updateGrade(int studentId, int gradeTypeId, double newGrade) {
        try {
            GradeDAO.updateGradeByType(studentId, currentGroupId, gradeTypeId, newGrade);
            loadGradeData();
        } catch (SQLException e) {
            showError("Database error", "Unable to update grade: " + e.getMessage());
        }
    }

    private static void handleDelete() {
        Map<String, Object> selectedRow = gradeTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showError("Selection error", "Please select a grade to delete.");
            return;
        }

        int studentId = (int) selectedRow.get("studentId");
        int gradeTypeId = 1;
        double gradeValue = (double) selectedRow.getOrDefault("total", 0.0);

        try {
            GradeDAO.deleteGradeByStudentAndType(studentId, currentGroupId, gradeTypeId);
            loadGradeData();
        } catch (SQLException e) {
            showError("Database error", "Unable to delete grade: " + e.getMessage());
        }
    }

    private static void exportToPDF() {
        try {
            List<Grade> grades = GradeDAO.showGradesByGroupId(currentGroupId);
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Group_" + currentGroupId + "_Report.pdf"));
            document.open();

            document.add(new Paragraph("Grade Report for Group " + currentGroupId, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));

            PdfPTable table = new PdfPTable(3);
            table.addCell("Grade ID");
            table.addCell("Student ID");
            table.addCell("Grade");

            for (Grade g : grades) {
                table.addCell(String.valueOf(g.getId()));
                table.addCell(String.valueOf(g.getStudentId()));
                table.addCell(String.valueOf(g.getGrade()));
            }

            document.add(table);
            document.close();

            showInfo("Export Success", "PDF report generated successfully!");
        } catch (Exception e) {
            showError("Export Failed", "Failed to generate PDF: " + e.getMessage());
        }
    }

    private static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
