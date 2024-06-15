package com.example.demo1;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import logic.User;
import logic.UserManager;

public class RegisterForm implements BasicForm {
    // Pola na dane użytkownika
    private SceneController controller;

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField firstNameField;
    private TextField lastNameField;

    public RegisterForm() {




        // Inicjalizacja pól
        usernameField = new TextField();
        usernameField.setPromptText("Login");

        passwordField = new PasswordField();
        passwordField.setPromptText("Haslo");

        firstNameField = new TextField();
        firstNameField.setPromptText("Imie");

        lastNameField = new TextField();
        lastNameField.setPromptText("Nazwisko");
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

        // Dodanie pól do gridu
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


    // Funkcja obsługująca proces rejestracji
    private void registerUser() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();



            // Walidacja danych wejściowych
            if (username.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("Wszystkie niezbedne pola muszą być wypełnione");
            }
            // Sprawdzenie, czy login jest dostępny
            if (!isUsernameAvailable(username)) {
                showAlert("Błąd rejestracji", "Login '" + username + "' jest już zajęty.");
                return;
            }
            //Koncowy etap - zapisujemy dane
            User user = new User(username, password);
            saveUserData(user);
            UserManager.addUser(user);
            showAlert("Rejestracja zakończona", "Konto zostało pomyślnie utworzone.");

        } catch (IllegalArgumentException ex) {
            showAlert("Błąd rejestracji", ex.getMessage());
        } catch (Exception ex) {
            showAlert("Błąd rejestracji", "Wystąpił nieoczekiwany błąd: " + ex.getMessage());
        }
    }

    //Zapisywanie do pliku
    private void saveUserData(User user) throws IOException {
        HelloApplication.client.writeToServer("ADD_USER " + user.getLogin() + " " + user.getPassword());
        String reply = HelloApplication.client.readFromServer();
        if (!reply.equals("SUCCESS")){
            showAlert("Błąd rejestracji", reply);
        }
    }

    private boolean isUsernameAvailable(String username) {
        File file = new File("credentials.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length > 1 && parts[1].equals(username)) {
                        return false; // Login już istnieje
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true; // Login jest dostępny
    }


}
