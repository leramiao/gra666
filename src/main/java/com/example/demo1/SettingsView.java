package com.example.demo1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsView {
    public void display() {
        Stage stage = new Stage();
        stage.setTitle("Settings");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Login");
        grid.add(usernameTextField, 0, 0, 2, 1);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");
        grid.add(passwordField, 0, 1, 2, 1);

        Button loginButton = new Button("Zaloguj");
        loginButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            try {
                HelloApplication.client.writeToServer("LOGIN " + username + " " + password);
                if (HelloApplication.client.readFromServer().equals("SUCCESS")) {
                    stage.close();
                    SceneController.activeUsername = username;
                    SceneController.openLoungeView();
                } else {
                    System.out.println("Błędne dane logowania");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        grid.add(loginButton, 1, 2);

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            SceneController.openRegisterForm();
            stage.close();
        });

        grid.add(registerButton, 1, 3);

        Scene scene = new Scene(grid, 300, 200);
        stage.setScene(scene);
        stage.show();
    }
}
