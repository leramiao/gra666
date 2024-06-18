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
import logic.UserSession;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SeansView implements BasicForm{
    private static final String ICON_FILENAME = "client/pfps/default.jpg";
    private static final String CARD_SPRITES_DIR = "client/cards/borrowed/";
    private static final String BG_DIR = "client/bg/";
    private Button amReady;
    private Label opponentName;
    private Label playerName;
    private VBox playerSection;
    private VBox opponentSection;
    private HBox actionButtons;
    private Button closeStackButton;
    private BorderPane root;
    private ImageView[] playerCards;
    private ImageView[] opponentCards;
    private ImageView[] tableCards;
    private ImageView stack;
    private StackPane stackSlot;
    private Stage primaryStage;
    private Image cardBack;
    private Label pointsLabel;
    private int tableID;
    private boolean listen;
    private VBox sideCards;
    private Button meldunekButton;
    private Button leaveButton;
    private Button declare66Button;

    public SeansView(Stage primaryStage, int table) {
        this.tableID = table;
        this.primaryStage = primaryStage;
        this.playerCards = new ImageView[6];
        this.opponentCards = new ImageView[6];
        this.tableCards = new ImageView[3];
    }
    private void initialize() {
        listen = true;
        this.playerSection = new VBox();
        this.opponentSection = new VBox();
        VBox buttons = new VBox();
        BackgroundImage bg = null;
        try {
            bg = new BackgroundImage(new Image(new FileInputStream(String.format(BG_DIR+"%s.png", "HEAVEN"))), BackgroundRepeat.REPEAT,null,null,null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        amReady = new Button("Ready!");
        amReady.setOnAction(e -> {
            try {
                Application.client.writeToServer("READY " + tableID);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        amReady.setDisable(true);

        leaveButton = new Button("Leave");
        leaveButton.setOnAction(e -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Application.client.writeToServer("LEAVE " + tableID + " " + SceneController.activeUsername);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            });
        });



        Image iconImage = null;
        try {
            iconImage = new Image(new FileInputStream(ICON_FILENAME));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < opponentCards.length; i++) {
            opponentCards[i] = new ImageView();
        }

        for (int i = 0; i < playerCards.length; i++) {
            playerCards[i] = new ImageView();
        }

        for (int i = 0; i < tableCards.length; i++) {
            tableCards[i] = new ImageView();
        }

        playerName = new Label();
        playerName.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        opponentName = new Label(null);
        opponentName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        opponentSection = new VBox(10);
        opponentSection.setVisible(false);


        ImageView opponentProfileImageView = new ImageView(iconImage);
        opponentProfileImageView.setFitWidth(100);
        opponentProfileImageView.setFitHeight(100);

        ImageView playerProfileImageView = new ImageView(iconImage);
        playerProfileImageView.setFitWidth(100);
        playerProfileImageView.setFitHeight(100);

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

        buttons.getChildren().addAll(amReady, leaveButton);
        buttons.setAlignment(Pos.CENTER);


        pointsLabel = new Label("0");
        pointsLabel.setVisible(false);


        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(opponentSection);
        root.setBottom(playerSection);
        root.setLeft(pointsLabel);
        root.setCenter(buttons);
        initActionButtons();
        playerSection.getChildren().add(actionButtons);
        root.backgroundProperty().set(new Background(bg));

        Scene scene = new Scene(root, 720, 730);
        primaryStage.setScene(scene);
        primaryStage.setTitle("gra 666");
        primaryStage.show();
        initPlayer();
        gameLoop();
    }

    public void display() {
        initialize();
    }

    public void acceptPlayer(String username){
        opponentName.setText(username);
        opponentSection.setVisible(true);
    }
    public void initPlayer(){
        String myUsername = SceneController.activeUsername;
        playerName.setText(myUsername);
    }
    public void putCard(int pos, String sprite) throws FileNotFoundException {
        String filename = CARD_SPRITES_DIR+sprite;
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

    public ImageView getSlotByPosition(int pos) {
        return switch (pos) {
            case 0, 1, 2 -> tableCards[pos];
            case 3, 4, 5, 6, 7, 8 -> playerCards[pos - 3];
            case 9, 10, 11, 12, 13, 14 -> opponentCards[pos - 9];
            default -> null;
        };
    }

    public void gameLoop() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (listen) {
                    try {
                        String[] command = Application.client.readFromServer().split(" ");
                        System.out.println("received command " + command[0]);
                        switch (command[0]) {
                            case "ACCEPT_PLAYER":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        acceptPlayer(command[1]);
                                    }
                                });
                                break;
                            case "PLAYER_LEAVE":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        amReady.setDisable(true);
                                        clearOpponent();
                                    }
                                });
                                break;
                            case "LEAVE_CONFIRM":
                                listen = false;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        primaryStage.close();
                                        SceneController.openLoungeView();
                                    }
                                });
                                break;
                            case "FULL":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        waitTillReady();
                                    }
                                });
                                break;
                            case "CLOSE_STACK":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        closeStack();
                                    }
                                });
                                break;
                            case "STACK_CLOSED":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        closeStackButton.setVisible(false);
                                        meldunekButton.setVisible(false);
                                        drawClosedStack();
                                    }
                                });
                                break;
                            case "EMPTY_STACK":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        stack.setVisible(false);
                                    }
                                });
                                break;
                            case "START":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        initGame();
                                    }
                                });
                                break;
                            case "SET_POINTS":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setPoints(Integer.parseInt(command[1]));
                                        meldunekButton.setVisible(true);
                                        declare66Button.setVisible(true);
                                        closeStackButton.setVisible(true);
                                    }
                                });
                                break;
                            case "VICTORY":
                                listen = false;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAlert("WIN", "YOU WON");
                                        primaryStage.close();
                                        SceneController.openLoungeView();
                                    }
                                });
                                break;
                            case "LOSS":
                                listen = false;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAlert("LOSS", "YOU LOST");
                                        primaryStage.close();
                                        SceneController.openLoungeView();
                                    }
                                });
                                break;
                            case "SET_ATUT":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            putCard(2,command[1]);
                                        } catch (FileNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                                break;
                            case "COLLECT_CARDS":
                                String[] cards = new String[6];
                                for (int i = 0; i < 6; i++) {
                                    cards[i] = Application.client.readFromServer();
                                }
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        initCards(cards);
                                    }
                                });
                                break;
                            case "COLLECT_CARD":
                                String cardinfo = command[1];
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        takeCard(cardinfo);
                                    }
                                });
                                break;
                            case "OPP_COLLECT_CARD":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        drawCardForOpponent();
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

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (command[1].equals("ATTACK")){
                                            closeStackButton.setDisable(false);
                                            meldunekButton.setDisable(false);
                                            declare66Button.setDisable(false);
                                        } else {
                                            closeStackButton.setDisable(true);
                                            meldunekButton.setDisable(true);
                                            declare66Button.setDisable(true);
                                        }
                                        unlockCards();
                                    }
                                });
                                break;
                            case "MELDUNEK":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeCard(Integer.parseInt(command[1]) + 3);
                                        removeCard(Integer.parseInt(command[2]) + 3);

                                    }
                                });
                                break;
                            case "MELDUNEK_ACK":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        undrawCardForOpponent();
                                        undrawCardForOpponent();

                                    }
                                });
                                break;
                            case "MELDUNEK_TRY":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        meldunekButton.setDisable(true);

                                    }
                                });
                                break;
                            case "CARD_ACCEPTED":
                                lockCards();
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        closeStackButton.setDisable(true);
                                        meldunekButton.setDisable(true);
                                        declare66Button.setDisable(true);
                                        int cardIndex = Integer.parseInt(command[1]);
                                        String cardInfo = command[2];
                                        try {
                                            putCard(1, cardInfo);
                                        } catch (FileNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                        removeCard(cardIndex + 3);

                                    }
                                });
                                break;

                            default:
                                System.out.println("UNKNOWN COMMAND!");
                                break;
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void clearOpponent() {
        opponentSection.setVisible(false);
        opponentName.setText(null);
    }

    private void closeStack() {
        try {
            Application.client.writeToServer("CLOSE_STACK");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawClosedStack() {

        tableCards[2].setRotate(tableCards[2].getRotate() + 90);
        stackSlot.getChildren().add(tableCards[2]);
        sideCards.getChildren().remove(tableCards[2]);
    }

    private void setPoints(int i) {
        pointsLabel.setText(String.valueOf(i));
    }

    public void undrawCardForOpponent(){
        for (int i = 0; i < 6; i++){
            if (opponentCards[i].isVisible()){
                removeCard(9+i);
                return;
            }
        }
    }
    public void removeCard(int pos){
        ImageView slot = getSlotByPosition(pos);
        slot.setVisible(false);
        slot.setImage(null);

    }
    private void initCards(String[] cards) {
        try {
            this.cardBack = new Image(new FileInputStream(CARD_SPRITES_DIR+"BACK.png"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        VBox roundCards = new VBox();
        HBox tableMyCards = new HBox();
        HBox tableOppCards = new HBox();
        sideCards = new VBox();

        this.stack = new ImageView();
        stackSlot = new StackPane(stack);
        sideCards.getChildren().addAll(stackSlot,tableCards[2]);
        stack.setImage(cardBack);

        for (int i = 0; i < 6; i++) {


            try {
                putCard(3 + i, cards[i]);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            tableMyCards.getChildren().add(playerCards[i]);
        }

        activateCards();
        lockCards();

        for (int i = 0; i < 6; i++) {

            try {
                putCard(9+i,"BACK.png");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            tableOppCards.getChildren().add(opponentCards[i]);
        }

        for (int i = 0; i < 2; i++) {
            try {
                putCard(i,"BACK.png");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            tableCards[i].setVisible(false);
            roundCards.getChildren().add(tableCards[i]);
        }

        try {
            putCard(2, "BACK.png");
            root.setRight(sideCards);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        tableMyCards.setAlignment(Pos.CENTER);
        tableOppCards.setAlignment(Pos.CENTER);
        roundCards.setAlignment(Pos.CENTER);
        roundCards.setVisible(true);
        root.setCenter(roundCards);
        playerSection.getChildren().add(0, tableMyCards);
        opponentSection.getChildren().add(tableOppCards);
    }

    public void initGame() {

        pointsLabel.setVisible(true);
        amReady.setVisible(false);
        actionButtons.setVisible(true);

        closeStackButton.setDisable(true);
        meldunekButton.setDisable(true);
        declare66Button.setDisable(true);
    }

    private void takeCard(String cardinfo) {
        for (int i = 0; i < 6; i++) {
            if (!playerCards[i].isVisible()) {
                try {
                    putCard(i + 3, cardinfo);
                    return;
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void lockCards(){
        for (ImageView card : playerCards){
            card.setDisable(true);
        }
    }
    private void unlockCards(){
        for (ImageView card : playerCards){
            card.setDisable(false);
        }
    }

    private void activateCards() {
        for (int i = 0; i < 6; i++) {
            int cardIndex = i;
            playerCards[cardIndex].setOnMouseClicked(event -> {
                System.out.println("You clicked the card " + cardIndex);
                try {
                    Application.client.writeToServer("PUT " + cardIndex);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void initActionButtons() {
        actionButtons = new HBox();
        meldunekButton = new Button("Meldunek");
        meldunekButton.setOnAction(event -> {
            meldunek();
        });
        closeStackButton = new Button("Zamykam stos");
        closeStackButton.setOnAction(event -> {
            closeStack();
        });
        declare66Button = new Button("66");
        declare66Button.setOnAction(event -> {
            declare66();
        });
        actionButtons.getChildren().addAll(meldunekButton,closeStackButton,declare66Button);
        actionButtons.setVisible(false);

    }

    private void declare66() {
        try {
            Application.client.writeToServer("DECLARE66");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void meldunek() {
        try {
            Application.client.writeToServer("MELDUNEK");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void drawCardForOpponent(int n) {
        int ctr = 0;
        for (int i = 0; i < 6; i++) {
            if (!opponentCards[i].isVisible()) {
                opponentCards[i].setVisible(true);
                opponentCards[i].setImage(cardBack);
                ctr++;
            }
            if (ctr == n) return;
        }
    }

    public void drawCardForOpponent() {
        for (int i = 0; i < 6; i++) {
            if (!opponentCards[i].isVisible()) {
                opponentCards[i].setVisible(true);
                opponentCards[i].setImage(cardBack);
                return;
            }

        }
    }
}