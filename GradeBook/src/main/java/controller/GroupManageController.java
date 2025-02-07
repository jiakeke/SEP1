package controller;

import application.AddNewGroupView;
import application.ModifyGroupView;
import dao.GroupDao;
import datasource.MariaDbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;

public class GroupManageController {

    @FXML
    private Button DelBtn;

    @FXML
    private Button addNewGroupButton;

    @FXML
    private TextField groupViewLabel;

    @FXML
    private Button modifyBtn;

    @FXML
    private ListView<String> groupsInfo;

    private GroupDao groupDao = new GroupDao();


    private Connection conn= MariaDbConnection.getConnection();
    private ObservableList<String> groupInfoList= FXCollections.observableArrayList();;

    @FXML
    public void initialize() {
        groupInfoList.clear(); // Clear the list to avoid duplicates
        String query = "SELECT * FROM groups";
        try (var stmt = conn.prepareStatement(query)) {
            stmt.execute();
            var rs = stmt.getResultSet();
            while (rs.next()) {
                groupInfoList.add("id:"+rs.getInt("id") + " name:" + rs.getString("name") + " Des:" + rs.getString("description"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        groupsInfo.setItems(groupInfoList);
    }
    @FXML
    void addNewGroup(MouseEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddNewGroup.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Add New Group");
            stage.setScene(scene);

            AddNewGroupController controller = fxmlLoader.getController();
            controller.setGroupManageController(this);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void deleteGroupInfo(MouseEvent event) {
        if (groupsInfo.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        String selectedGroup = groupsInfo.getSelectionModel().getSelectedItem();
        String groupId = selectedGroup.split(" ")[0].split(":")[1];
//        groupDao.removeGroup(Integer.parseInt(groupId));
//        groupsInfo.getItems().remove(groupsInfo.getSelectionModel().getSelectedItem());
        if (groupDao.removeGroup(Integer.parseInt(groupId))) {
            groupsInfo.getItems().remove(groupsInfo.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    void handleAddNewGroup(ActionEvent event) {

    }

    @FXML
    void modifyGroupInfo(MouseEvent event) {
        if (groupsInfo.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        try {FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ModifyGroupInfo.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            GroupModifyController controller = fxmlLoader.getController();
            int groupId = Integer.parseInt(groupsInfo.getSelectionModel().getSelectedItem().split(" ")[0].split(":")[1]);
            controller.setGroupManageController(this,groupId);


            Stage stage = new Stage();
            stage.setTitle("Modify GroupInfo");
            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
