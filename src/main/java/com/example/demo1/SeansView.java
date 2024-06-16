package com.example.demo1;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import logic.Player;
import logic.Theme;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.FutureTask;

public class SeansView {
    private Button amReady;
    private Label opponentName;
    private Label playerName;
    private VBox playerSection;
    private VBox opponentSection;
    private BorderPane root;
    private ImageView[] playerCards;
    private ImageView[] opponentCards;
    private ImageView[] tableCards;
    private Stage primaryStage;
    private Image cardBack;
    private Theme theme;
    int tableID;

    public SeansView(Stage primaryStage, int table) {
        this.theme = Theme.SPACE;
        this.tableID = table;
        this.primaryStage = primaryStage;
        this.playerCards = new ImageView[6];
        this.opponentCards = new ImageView[6];
        this.tableCards = new ImageView[2];
        this.playerSection = new VBox();
        this.opponentSection = new VBox();
    }

    public SeansView(Stage primaryStage, int table, Theme theme) {
        this.theme = theme;
        this.tableID = table;
        this.primaryStage = primaryStage;
        this.playerCards = new ImageView[6];
        this.opponentCards = new ImageView[6];
        this.tableCards = new ImageView[2];
        this.playerSection = new VBox();
        this.opponentSection = new VBox();
    }
    private void initialize() {
        VBox buttons = new VBox();
        BackgroundImage bg = null;
        try {
            bg = new BackgroundImage(new Image(new FileInputStream(String.format("media/bg/%s.png", theme.name()))), BackgroundRepeat.REPEAT,null,null,null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        amReady = new Button("Ready!");
        amReady.setOnAction(e -> {
            try {
                HelloApplication.client.writeToServer("READY " + tableID);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        amReady.setDisable(true);



        Image opponentProfileImage = null;
        Image playerProfileImage = null;
        try {
            opponentProfileImage = new Image(new FileInputStream("media/pfps/default.jpg"));
            playerProfileImage = new Image(new FileInputStream("media/pfps/default.jpg"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Opponent's cards (replace with actual card images as needed)
        ImageView[] opponentCards = new ImageView[6];
        for (int i = 0; i < opponentCards.length; i++) {
            opponentCards[i] = new ImageView();
        }

        // Player's cards (replace with actual card images as needed)
        ImageView[] playerCards = new ImageView[6];
        for (int i = 0; i < playerCards.length; i++) {
            playerCards[i] = new ImageView();
        }

        // Labels for opponent and player names

        playerName = new Label();
        playerName.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        opponentName = new Label(null);
        opponentName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        opponentSection = new VBox(10);
        opponentSection.setVisible(false);



        // Profile picture ImageViews
        ImageView opponentProfileImageView = new ImageView(opponentProfileImage);
        opponentProfileImageView.setFitWidth(100);
        opponentProfileImageView.setFitHeight(100);

        ImageView playerProfileImageView = new ImageView(playerProfileImage);
        playerProfileImageView.setFitWidth(100);
        playerProfileImageView.setFitHeight(100);

        // Layout for opponent's section
        opponentSection.setAlignment(Pos.CENTER);
        opponentSection.setPadding(new Insets(20));
        opponentSection.getChildren().addAll(opponentName, opponentProfileImageView);
        HBox opponentCardsBox = new HBox(10);
        opponentCardsBox.setAlignment(Pos.CENTER);
        opponentCardsBox.setPadding(new Insets(10));
        opponentSection.getChildren().add(opponentCardsBox);
        for (ImageView card : opponentCards) {
            opponentCardsBox.getChildren().add(card);
        }

        // Layout for player's section
        playerSection = new VBox(10);
        playerSection.setAlignment(Pos.CENTER);
        playerSection.setPadding(new Insets(20));
        playerSection.getChildren().addAll(playerName, playerProfileImageView);
        HBox playerCardsBox = new HBox(10);
        playerCardsBox.setAlignment(Pos.CENTER);
        playerCardsBox.setPadding(new Insets(10));
        playerSection.getChildren().add(playerCardsBox);
        for (ImageView card : playerCards) {
            playerCardsBox.getChildren().add(card);
        }

        buttons.getChildren().addAll(amReady);
        buttons.setAlignment(Pos.CENTER);

        // Main layout
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(opponentSection);
        root.setBottom(playerSection);
        root.setCenter(buttons);
        root.backgroundProperty().set(new Background(bg));

        // Create scene and set on stage
        Scene scene = new Scene(root, 720, 730);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Card Game Layout Example");
        primaryStage.show();
        initPlayer();
        gameLoop();
    }

    public void display() {
        initialize();
    }

    public void acceptPlayer(String username){
        System.out.println("accepting " + username);
        opponentName.setText(username);
        opponentSection.setVisible(true);
    }
    public void initPlayer(){
        String myUsername = SceneController.activeUsername;
        playerName.setText(myUsername);
    }
    public void putCard(int pos, String filename) throws FileNotFoundException {
        switch (pos){
            case 0, 1, 2:
                tableCards[pos].setVisible(true);
                tableCards[pos].setImage(new Image(new FileInputStream(filename)));
                break;
            case 3,4,5,6,7,8:
                playerCards[pos-3].setImage(new Image(new FileInputStream(filename)));
                playerCards[pos-3].setVisible(true);
                break;
            case 9,10,11,12,13,14:
                opponentCards[pos-9].setVisible(true);
                opponentCards[pos-9].setImage(new Image(new FileInputStream(filename)));
                break;
        }
    }


    public void waitTillReady(){

        amReady.setDisable(false);
    }
    public void moveCard(int posA, int posB){
        ImageView slotA = getSlotByPosition(posA);
        Image sprite = slotA.getImage();
        slotA.setVisible(false);
        slotA.setImage(null); //rethink later
        getSlotByPosition(posB).setImage(sprite);
    }

    public ImageView getSlotByPosition(int pos) {
        return switch (pos) {
            case 0, 1, 2 -> tableCards[pos];
            case 3, 4, 5, 6, 7, 8 -> playerCards[pos - 3];
            case 9, 10, 11, 12, 13, 14 -> opponentCards[pos - 9];
            default -> null;
        };
    }

    public void gameLoop() {
        System.out.println("in game loop");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    System.out.println("Waitnig for command GTV");
                    try {
                        String[] command = HelloApplication.client.readFromServer().split(" ");
                        System.out.println("received command " + command[0]);
                        switch (command[0]) {
                            case "ACCEPT_PLAYER":
                                System.out.println("accepting playr");
                                System.out.println("accepting " + command[1]);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        acceptPlayer(command[1]);
                                    }
                                });
                                break;
                            case "FULL":
                                System.out.println("full");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        waitTillReady();
                                    }
                                });
                                break;
                            case "START":
                                System.out.println("starting");
                                initGame();
                                break;
                            case "COLLECT_CARDS":
                                String[] cards = new String[6];
                                for (int i = 0; i < 6; i++){
                                    cards[i] = HelloApplication.client.readFromServer();
                                    System.out.println(cards[i] + " is recorded");
                                }
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        initCards(cards);
                                    }
                                });
                                break;
                            case "CLEAR":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeCard(0);
                                        removeCard(1);

                                    }
                                });
                                break;
                            case "ATTACK":
                                playerName.setTextFill(Paint.valueOf("green"));
                                opponentName.setTextFill(Paint.valueOf("white"));
                                break;
                            case "DEFEND":
                                opponentName.setTextFill(Paint.valueOf("green"));
                                playerName.setTextFill(Paint.valueOf("white"));
                                break;
                            case "ACCEPT_CARD":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            putCard(0, command[1]);
                                            undrawCardForOpponent();
                                        } catch (FileNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                                break;
                            case "REQUEST_CARD":
                                System.out.println("  card  requested");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                       // waitForCardClick();
                                        waitForCardClickv0();
                                    }
                                });
                                break;
                            case "PUT_CARD":
                                putCard(Integer.parseInt(command[1]), command[2]);
                                break;
                            case "CARD_ACCEPTED":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        int cardIndex = Integer.parseInt(command[1]);
                                        String cardInfo = command[2];
                                        try {
                                            putCard(1,cardInfo);
                                        } catch (FileNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                        removeCard(cardIndex + 3);

                                    }
                                });
                                break;

                            default:
                                System.out.println("UNKNOWN COMMANd! gametableview");
                                break;
                        }

                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Processed command GTV");
                }
            }
        };
        new Thread(task).start();
    }
    public void undrawCardForOpponent(){
        for (int i = 0; i < 6; i++){
            if (opponentCards[i].isVisible()){
                removeCard(9+i);
                return;
            }
        }
    }
    /*
    Empty the slot
     */
    public void removeCard(int pos){
        ImageView slot = getSlotByPosition(pos);
        slot.setVisible(false);
        slot.setImage(null);

    }
    private void waitForCardClickv0() {
        for (int i = 0; i < 6; i++) {
            int cardIndex = i;
            playerCards[cardIndex].setOnMouseClicked(event -> {
                System.out.println("You clicked the card " + cardIndex);
                try {
                    HelloApplication.client.writeToServer("PUT " + cardIndex);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    private void initCards(String[] cards) {
        VBox roundCards = new VBox();
        HBox tableMyCards = new HBox();
        HBox tableOppCards = new HBox();

        for (int i = 0; i < 6; i++) {


            playerCards[i] = new ImageView();
            try {
                putCard(3 + i, cards[i]);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            tableMyCards.getChildren().add(playerCards[i]);
        }

        for (int i = 0; i < 6; i++) {

            try {
                opponentCards[i] = new ImageView(new Image(new FileInputStream("media/cards/MYSTERY.png")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            tableOppCards.getChildren().add(opponentCards[i]);
        }

        for (int i = 0; i < 2; i++) {
            try {
                tableCards[i] = new ImageView(new Image(new FileInputStream("media/cards/BLANK.png")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            tableCards[i].setVisible(false);
            roundCards.getChildren().add(tableCards[i]);
        }


        tableMyCards.setAlignment(Pos.CENTER);
        tableOppCards.setAlignment(Pos.CENTER);
        roundCards.setAlignment(Pos.CENTER);
        roundCards.setVisible(true);
        root.setCenter(roundCards);
        playerSection.getChildren().add(0, tableMyCards);
        opponentSection.getChildren().add(tableOppCards);
    }
    private Task<Void> waitForCardClick() {
        Task<Void> task = new Task<>(){
            @Override
            protected Void call() throws Exception {

                for (int i = 0; i < 6; i++) {
                    int cardIndex = i;
                    playerCards[cardIndex].setOnMouseClicked(event -> {
                        System.out.println("You clicked the card " + cardIndex);
                        try {
                            HelloApplication.client.writeToServer("PUT " + String.valueOf(cardIndex));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                return null;
            }
        };

        new Thread(task).start();
        return task;
    }
    public void initGame() {
        //acceptPlayersThread.interrupt();
        System.out.println("init game !");

        try {
            this.cardBack = new Image(new FileInputStream("media/cards/MYSTERY.png"));
            System.out.println("assign sprite !");
        } catch (FileNotFoundException e) {
            System.out.println("no card");
            throw new RuntimeException(e);
        }
        System.out.println("remove button !");
        amReady.setVisible(false);
    }
}
