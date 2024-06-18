package com.example.demo1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainMenuView {

    public void display()   {
        Stage stage = new Stage();
        stage.setTitle("Main menu");

        GridPane grid = new GridPane();
        BackgroundImage bg = null;
        try {
            bg = new BackgroundImage(new Image(new FileInputStream("client/bg/field2.png")), BackgroundRepeat.NO_REPEAT,null, BackgroundPosition.CENTER,null);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        grid.backgroundProperty().set(new Background(bg));

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button playButton = new Button("Zacznij grać");
        playButton.setOnAction(e -> {
            stage.close();
            SceneController.openLoginView();
        });

        grid.add(playButton, 1, 2);

        Scene scene = new Scene(grid, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}
