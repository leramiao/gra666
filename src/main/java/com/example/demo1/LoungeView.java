package com.example.demo1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import logic.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoungeView implements  BasicForm {

    public TableView<SeansGry> tablesList;
    public TableColumn<SeansGry, String> currentPlayersColumn;
    private TableColumn<SeansGry, Integer> tableIDs;

    private TableColumn<SeansGry, String> tableOwners;
    private Stage stage;


    private void readTables() throws IOException {
        HelloApplication.client.writeToServer("LIST_TABLES");
        int nTables = Integer.parseInt(HelloApplication.client.readFromServer());
        String[] tableInfo;
        SceneController.clearTables();
        for (int i = 0 ; i < nTables; i++){
            tableInfo = HelloApplication.client.readFromServer().split(" ");
            SceneController.seansManager.addSeans(new SeansGry(Integer.parseInt(tableInfo[0]),Integer.parseInt(tableInfo[1]),Integer.parseInt(tableInfo[2]),tableInfo[3]));
        }
    }
    public LoungeView() {
        try {

            readTables();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void display() {
        Button joinTableButton = new Button("Join table");
        joinTableButton.setOnAction( e -> {

            SeansGry table = tablesList.getSelectionModel().getSelectedItem();
            try {
                if (table == null) return;
                HelloApplication.client.writeToServer("JOIN_TABLE " + table.getTableID() + " " + SceneController.activeUsername);
                String response = HelloApplication.client.readFromServer();
                System.out.println("LOUNGE VIEW, RECIEVED RESPONSE " + response);
                if (!response.equals("FAIL")){
                    stage.close();
                    SceneController.openTableView(table.getTable());
                }
                else {
                    showAlert("CANT JOIN", "TABLE FULL");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        Button createTableButton = new Button("Create table");
        createTableButton.setOnAction( e -> {

            try {
                HelloApplication.client.writeToServer("CREATE_TABLE");
                refreshTable();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button deleteTableButton = new Button("Delete table");
        deleteTableButton.setOnAction( e -> {
            SeansGry table = tablesList.getSelectionModel().getSelectedItem();
            if (table == null) return;
            if (table.getTable().getOwner().equals(SceneController.activeUsername)){
                try {
                    HelloApplication.client.writeToServer("DELETE_TABLE " + table.getTableID());
                    SceneController.seansManager.removeByTableID(table.getTableID());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else {
                showAlert("ERROR", "NOT YOUR TABLE");
            }
        });

        Button refreshButton = new Button("Refresh tables");
        refreshButton.setOnAction( e -> {
           refreshTable();
        });

        Button toMainMenu = new Button("To menu");
        toMainMenu.setOnAction(event -> {
            SceneController.openMenuView();
            stage.close();
        });

        tableIDs = new TableColumn<>("TABLE ID");
        currentPlayersColumn = new TableColumn<>("PLAYERS");
        tableOwners = new TableColumn<>("OWNER");

        tablesList = new TableView<>();
        tablesList.setFixedCellSize(50);
        tablesList.getColumns().add(tableIDs);
        tablesList.getColumns().add(currentPlayersColumn);
        tablesList.getColumns().add(tableOwners);

        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(joinTableButton);
        buttonBox.getChildren().add(createTableButton);
        buttonBox.getChildren().add(toMainMenu);
        buttonBox.getChildren().add(refreshButton);
        buttonBox.getChildren().add(deleteTableButton);

        Scene scene = null;
        stage = new Stage();
        BorderPane root = new BorderPane();

        tablesList = createTableView();
        root.setCenter(tablesList);
        root.setBottom(buttonBox);


        scene = new Scene(root, 600, 500);
        stage.setTitle("Lounge");
        stage.setScene(scene);

        stage.show();
    }
    private TableView<SeansGry> createTableView() {
        tableIDs.setCellValueFactory( t -> new SimpleIntegerProperty(t.getValue().getTableID()).asObject());
        tableOwners.setCellValueFactory(t -> new SimpleStringProperty(t.getValue().getTable().getOwner()));
        currentPlayersColumn.setCellValueFactory( t -> new SimpleStringProperty(t.getValue().getTable().getFilledRatio()));

        //tablesList.getItems().setAll(SceneController.tableManager.getTables());
        tablesList.getItems().setAll(SceneController.seansManager.getSeanse());


        return tablesList;
    }

    public void refreshTable() {
        try {
            readTables();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObservableList<SeansGry> tables = FXCollections.observableArrayList(SceneController.seansManager.getSeanse());
        System.out.println("Observable = " + tables.size());
        tablesList.setItems(tables);
    }


}
