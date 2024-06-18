package com.example.demo1;

import javafx.beans.binding.BooleanExpression;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

public class SettingsView {
    public void display() {
        try {
            getPFP();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Stage stage = new Stage();
        stage.setTitle("Settings");

        ImageView pfpImageView = new ImageView();
        try {
            pfpImageView.setImage(new Image(new FileInputStream("media/client_side/pfps/"+ SceneController.activeUsername+".png")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        pfpImageView.setFitWidth(100);
        pfpImageView.setFitHeight(100);



        Button toMainMenu = new Button("Log out");
        toMainMenu.setOnAction(event -> {
            SceneController.openMenuView();
            stage.close();
        });

        Button toLounge = new Button("Lounge");
        toLounge.setOnAction(event -> {
            SceneController.openLoungeView();
            stage.close();
        });

        HBox buttons = new HBox(toMainMenu,toLounge);
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(pfpImageView);
        root.setBottom(buttons);
        Scene scene = new Scene(root, 720, 730);
        stage.setScene(scene);
        stage.show();



    }


    private void updatePFP(){

    }
    private void getPFP() throws IOException {
        HelloApplication.client.writeToServer("GET_PFP " + SceneController.activeUsername);
        InputStream in = HelloApplication.client.socket.getInputStream();
        BufferedImage img=ImageIO.read(ImageIO.createImageInputStream(in));
        File myObj = new File("media/client_side/pfps/"+SceneController.activeUsername+".png");
        myObj.createNewFile();
        ImageIO.write(img, "png", myObj);

    }
}
