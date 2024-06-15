package com.example.demo1;


import javafx.scene.control.Alert;

public interface BasicForm {
    void display();


    default void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}