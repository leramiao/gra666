package com.example.demo1;

import javafx.stage.Stage;
import logic.Client;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Application extends javafx.application.Application {
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

        SceneController controller = new SceneController(stage);
        MainMenuView firstView = new MainMenuView();
        //LoungeView firstView = new LoungeView();
        firstView.display();
    }

    public static void main(String[] args) {
        launch();
    }
}