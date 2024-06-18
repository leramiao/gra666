package com.example.demo1;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import logic.*;

import java.io.IOException;

public class LoungeView implements  BasicForm {

    public TableView<SeansGry> tablesList;
    public TableColumn<SeansGry, String> currentPlayersColumn;
    private TableColumn<SeansGry, Integer> tableIDs;

    private TableColumn<SeansGry, String> tableOwners;
    private Stage stage;


    private void readTables() throws IOException {
        Application.client.writeToServer("LIST_TABLES");
        int nTables = Integer.parseInt(Application.client.readFromServer());
        String[] tableInfo;
        SceneController.clearTables();
        for (int i = 0 ; i < nTables; i++){
            tableInfo = Application.client.readFromServer().split(" ");
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
                Application.client.writeToServer("JOIN_TABLE " + table.getTableID() + " " + SceneController.activeUsername);
                String response = Application.client.readFromServer();
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
                Application.client.writeToServer("CREATE_TABLE");
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
                    Application.client.writeToServer("DELETE_TABLE " + table.getTableID());
                    SceneController.seansManager.removeByTableID(table.getTableID());
                    refreshTable();
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
        tablesList.setItems(tables);
    }


}
