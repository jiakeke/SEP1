package controller;

import application.GradeBookView;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import dao.GradeDAO;
import dao.GradeTypeDAO;
import dao.StudentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.Grade;
import model.GradeType;
import model.Student;
import util.LangContext;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class GradeController {

    private static TableView<Map<String, Object>> gradeTable;
    private static Button exportButton;

    private static int currentGroupId;
    private static Stage stage;
    private static String currentGroupName;
    private static GradeBookView rootview;
    private static TableColumn<Map<String, Object>, String> nameColumn;
    private static TableColumn<Map<String, Object>, Double> totalColumn;
    private static Map<String, Object> avgRow;
    private static ResourceBundle bundle = LangContext.getBundle();

    public static void showGradeEditor(GradeBookView view, int groupId, String groupName) {
        rootview = view;
        currentGroupId = groupId;
        currentGroupName = groupName;
        initializeUI(LangContext.getBundle());
        loadGradeData();

        LangContext.currentLang.addListener((obs, oldlang, newLang)->{
            bundle=LangContext.getBundle();
            updateTexts();
        });
    }

    private static void initializeUI(ResourceBundle bundle) {
        gradeTable = new TableView<>();

        exportButton = new Button(bundle.getString("export"));
        exportButton.getStyleClass().add("export-button");
        HBox buttonContainer = new HBox(exportButton);
        buttonContainer.setAlignment(Pos.CENTER);

//        exportButton.setOnAction(e -> exportToPDF());
        exportButton.setOnAction(e -> exportToPDFFromTable());
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(gradeTable, buttonContainer);

        rootview.getRootPane().setCenter(layout);
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

            nameColumn = new TableColumn<>(bundle.getString("name"));
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

            totalColumn = new TableColumn<>(bundle.getString("total"));
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
                row.put("total", Double.parseDouble(String.format("%.2f", total)));
                tableData.add(row);
            }

            // Calculate average grades
            if (!students.isEmpty()) {
                avgRow = new HashMap<>();
                avgRow.put("name", bundle.getString("average"));

                for (GradeType gt : gradeTypes) {
                    double sum = 0;
                    int count = 0;

                    for (Map<String, Object> row : tableData) {
                        Object gradeObj = row.get(gt.getName());
                        if (gradeObj instanceof Double) {
                            sum += (Double) gradeObj;
                            count++;
                        }
                    }

                    double avg = count > 0 ? sum / count : 0.0;
                    avgRow.put(gt.getName(), Double.parseDouble(String.format("%.2f", avg)));
                }

                double totalSum = 0;
                for (Map<String, Object> row : tableData) {
                    Object totalObj = row.get("total");
                    if (totalObj instanceof Double) {
                        totalSum += (Double) totalObj;
                    }
                }
                avgRow.put("total", Double.parseDouble(String.format("%.2f", students.size() > 0 ? totalSum / students.size() : 0.0)));

                tableData.add(avgRow);
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

//    private static void handleDelete() {
//        Map<String, Object> selectedRow = gradeTable.getSelectionModel().getSelectedItem();
//        if (selectedRow == null) {
//            showError("Selection error", "Please select a grade to delete.");
//            return;
//        }
//
//        int studentId = (int) selectedRow.get("studentId");
//        int gradeTypeId = 1;
//        double gradeValue = (double) selectedRow.getOrDefault("total", 0.0);
//
//        try {
//            GradeDAO.deleteGradeByStudentAndType(studentId, currentGroupId, gradeTypeId);
//            loadGradeData();
//        } catch (SQLException e) {
//            showError("Database error", "Unable to delete grade: " + e.getMessage());
//        }
//    }

    private static void updateTexts() {
        exportButton.setText(bundle.getString("export"));
        nameColumn.setText(bundle.getString("name"));
        totalColumn.setText(bundle.getString("total"));
        avgRow = new HashMap<>();
        avgRow.put("name", bundle.getString("average"));
        loadGradeData();
    }

    public static void exportToPDFFromTable() {
        if (gradeTable == null || gradeTable.getColumns().isEmpty()) {
            showError("Export Failed", "No data to export.");
            return;
        }

        // set default file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String defaultFileName = "Group_" + currentGroupName + "_Table_Report_" + timestamp + ".pdf";

        // user selects folder
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder to Save PDF");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory == null) {
            showError("Export Failed", "No folder selected.");
            return;
        }

        String filePath = new File(selectedDirectory, defaultFileName).getAbsolutePath();

        try {
            Document document = new Document(PageSize.A4, 30, 30, 30, 30);  // 设置页边距
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

            writer.setPageEvent(createPageEvent());
            document.open();

            addDocumentHeader(document, currentGroupName);

            PdfPTable table = createPdfTableFromTableView(gradeTable);
            document.add(table);

            document.close();

            Desktop.getDesktop().open(new File(filePath));

            showInfo("Export Success", "PDF report generated successfully:\n" + filePath);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Export Failed", "Failed to generate PDF: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }


    // this method is used to create a footer for the pdf
    public static PdfPageEventHelper createPageEvent() {
        return new PdfPageEventHelper() {
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte canvas = writer.getDirectContent();

                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                        new Phrase("metropolia.fi", footerFont),
                        document.left(), document.top() + 20, 0);

                ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                        new Phrase(String.format("Page %d", writer.getPageNumber()), footerFont),
                        (document.right() + document.left()) / 2, document.bottom() - 20, 0);
            }
        };
    }

    // this method is used to add a header to the pdf
    public static void addDocumentHeader(Document document, String groupName) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        document.add(new Paragraph("Grade Report: " + groupName, titleFont));
        document.add(Chunk.NEWLINE);

        String exportTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Font timeFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        document.add(new Paragraph("Exported on: " + exportTime, timeFont));
        document.add(Chunk.NEWLINE);
    }

    // this method is used to create a pdf table from a JavaFX TableView
    public static PdfPTable createPdfTableFromTableView(TableView<Map<String, Object>> tableView) throws DocumentException {
        int columnCount = tableView.getColumns().size();
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);

        float[] columnWidths = new float[columnCount];
        Arrays.fill(columnWidths, 1f);
        table.setWidths(columnWidths);

        // 表头（灰底+加粗+居中）
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        for (TableColumn<Map<String, Object>, ?> column : tableView.getColumns()) {
            PdfPCell headerCell = new PdfPCell(new Phrase(column.getText(), headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(8);
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(headerCell);
        }

        // 数据行
        for (Map<String, Object> row : tableView.getItems()) {
            for (TableColumn<Map<String, Object>, ?> column : tableView.getColumns()) {
                String columnName = column.getText();
                Object cellValue = getCellValueIgnoreCase(row, columnName);
                String cellText = formatCellValue(cellValue);
                PdfPCell cell = new PdfPCell(new Phrase(cellText));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }
        }
        return table;
    }

    // this method is used to get a cell value from a row map, ignoring case
    public static Object getCellValueIgnoreCase(Map<String, Object> row, String columnName) {
        Object cellValue = row.get(columnName);
        if (cellValue != null) {
            return cellValue;
        }
        for (String key : row.keySet()) {
            if (key.equalsIgnoreCase(columnName)) {
                return row.get(key);
            }
        }
        return null;
    }

    // this method is used to format a cell value as a string
    public static String formatCellValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Number) {
            return String.format("%.2f", ((Number) value).doubleValue());
        }
        return value.toString();
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
