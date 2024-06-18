package com.example.demo1;


import java.io.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RegisterForm implements BasicForm {

    private TextField usernameField;
    private PasswordField passwordField;

    public RegisterForm() {

        usernameField = new TextField();
        usernameField.setPromptText("Login");

        passwordField = new PasswordField();
        passwordField.setPromptText("Haslo");
    }

    @Override
    public void display() {
        Stage stage = new Stage();

        stage.setTitle("Rejestracja konta");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(usernameField, 0, 0);
        grid.add(passwordField, 0, 1);

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            registerUser();
        });

        grid.add(registerButton, 0, 6);

        Button loginButton = new Button("Log In");
        loginButton.setOnAction(e -> {
            SceneController.openLoginView();
            stage.close();
        });

        grid.add(loginButton, 1, 6);

        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
        stage.show();
    }


    private void registerUser() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();


            if (username.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("Wszystkie pola muszą być wypełnione");
            }
            saveUserData(username, password);

        } catch (IllegalArgumentException ex) {
            showAlert("Błąd rejestracji", ex.getMessage());
        } catch (Exception ex) {
            showAlert("Błąd rejestracji", "Wystąpił nieoczekiwany błąd: " + ex.getMessage());
        }
    }

    private void saveUserData(String username, String password) throws IOException {
        Application.client.writeToServer("ADD_USER " + username + " " + password);
        String reply = Application.client.readFromServer();
        if (!reply.equals("SUCCESS")){
            showAlert("Błąd rejestracji", reply);
        }
        else {
            showAlert("Rejestracja zakończona", "Konto zostało pomyślnie utworzone.");
        }
    }

}
