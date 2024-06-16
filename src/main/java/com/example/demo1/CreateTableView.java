package com.example.demo1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import logic.SeansGry;
import logic.SeansManager;
import logic.Table;
import logic.TableManager;

import java.io.IOException;

public class CreateTableView {
    private final SeansManager seansManager;
    private final Stage stage;

    public CreateTableView(SeansManager seansManager) {
        this.seansManager = seansManager;
        this.stage = new Stage();
        this.stage.setTitle("Create a New Table");
    }

    public void display() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        TextField nPlayersField = new TextField();
        nPlayersField.setPromptText("Number of Players");
        grid.add(nPlayersField, 0, 0, 2, 1);

        Button createButton = new Button("Create Table");
        createButton.setOnAction(e -> {
            try {
                int nPlayers = Integer.parseInt(nPlayersField.getText());
                SeansGry table = new SeansGry(nPlayers, SceneController.activeUsername);
                seansManager.addSeans(table);
                HelloApplication.client.writeToServer("CREATE_TABLE " + nPlayers);
                stage.close();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number for players.");
            } catch (IllegalArgumentException ex) {
                showAlert("Error", ex.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        grid.add(createButton, 1, 1);

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}