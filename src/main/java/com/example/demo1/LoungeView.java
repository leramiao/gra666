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
import logic.Table;
import logic.TableManager;
import logic.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoungeView implements  BasicForm {

    public TableView<Table> tablesList;
    public TableColumn<Table, String> currentPlayersColumn;
    public Button joinTableButton;
    @FXML
    private TableColumn<Table, Integer> tableIDs;

    //private TableColumn<Table, Integer> nPlayers;


    @FXML
    private Button goToMenuButton;
    @FXML
    private Button createTableButton;
    private Stage stage;
    private SceneController controller;
    //private static TableManager tableManager;



    public LoungeView() {
        try {

            HelloApplication.client.writeToServer("LIST_TABLES");
            int nTables = Integer.parseInt(HelloApplication.client.readFromServer());
            String[] tableInfo;
            SceneController.clearTables();
            for (int i = 0 ; i < nTables; i++){
                tableInfo = HelloApplication.client.readFromServer().split(" ");
                SceneController.tableManager.addTable(new Table(Integer.parseInt(tableInfo[0]),Integer.parseInt(tableInfo[1]),Integer.parseInt(tableInfo[2])));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void display() {
        Button joinTableButton = new Button("Join table");
        joinTableButton.setOnAction( e -> {

            Table table = tablesList.getSelectionModel().getSelectedItem();
            try {
                HelloApplication.client.writeToServer("JOIN_TABLE " + table.getId() + " " + SceneController.activeUsername);
                String response = HelloApplication.client.readFromServer();
                System.out.println("LOUNGE VIEW, RECIEVED RESPONSE " + response);
                if (!response.equals("FAIL")){
                    stage.close();
                    SceneController.openTableView(table);
                }
                else {
                    showAlert("CANT JOIN", "SORRY");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        Button createTableButton = new Button("Create table");
        createTableButton.setOnAction( e -> {
            SceneController.openCreateTableView();
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

        tablesList = new TableView<>();
        tablesList.setFixedCellSize(50);
        tablesList.getColumns().add(tableIDs);
        tablesList.getColumns().add(currentPlayersColumn);

        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(joinTableButton);
        buttonBox.getChildren().add(createTableButton);
        buttonBox.getChildren().add(toMainMenu);
        buttonBox.getChildren().add(refreshButton);

        Scene scene = null;
        stage = new Stage();
        try {
            BorderPane root = new BorderPane();
            BackgroundImage bg = new BackgroundImage(new Image(new FileInputStream("media/bg/chic.png")), BackgroundRepeat.REPEAT,BackgroundRepeat.SPACE,null,null);
            root.backgroundProperty().set(new Background(bg));

            tablesList = createTableView();
            root.setCenter(tablesList);
            root.setBottom(buttonBox);


            scene = new Scene(root, 600, 500);
            stage.setTitle("Lounge");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("oooooops");
            throw new RuntimeException(e);
        }

        stage.show();
    }
    private TableView<Table> createTableView() {
        tableIDs.setCellValueFactory( t -> new SimpleIntegerProperty(t.getValue().getId()).asObject());

        currentPlayersColumn.setCellValueFactory( t -> new SimpleStringProperty(t.getValue().getPlayersAmount()+"/"+t.getValue().getMaxPlayers()));


        tablesList.getItems().setAll(SceneController.tableManager.getTables());


        return tablesList;
    }

    public void refreshTable() {
        ObservableList<Table> tables = FXCollections.observableArrayList(SceneController.tableManager.getTables());
        System.out.println("Observable = " + tables.size());
        tablesList.setItems(tables);
    }

    public void onGoToMenuButton(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
        SceneController.openMenuView();
    }
    public void onCreateTableButton(ActionEvent event) {
        stage.close();
        SceneController.openCreateTableView();
    }


}
