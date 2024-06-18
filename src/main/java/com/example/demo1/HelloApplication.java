package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import logic.Client;
import logic.Table;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class HelloApplication extends Application {
    public static Client client;

    @Override
    public void start(Stage stage) throws IOException {
        client = new Client();
        try {
            client.start("127.0.0.1", 6666);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        /*


        Parent root = FXMLLoader.load(getClass().getResource("entrance-view.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
         */

        SceneController controller = new SceneController(stage);
        MainMenuView firstView = new MainMenuView();
        //LoungeView firstView = new LoungeView();
        firstView.display();
    }

    public static void main(String[] args) {
        launch();
    }
}