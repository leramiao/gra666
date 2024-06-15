package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.Stack;

import static java.util.Collections.swap;

public class SeansGry  {
    public Player[] players;

    private Stack<Card> cardStack;
    private Table table;
    private Card[] cardsOnTable;
    private Suit atut;
    private boolean phase2;
    private int lewa;

    public SeansGry(Table table) {
        this.cardStack = new Stack<>();
        this.cardsOnTable = new Card[3];
        this.table = table;
        this.players = new Player[2];
        //this.players[0] = table.getPlayers().get(0);
        //this.players[1] = table.getPlayers().get(1);
    }


    public boolean isTableFilled() {
        return table.isFilled();
    }

    public void addPlayer(String username){
        Player newPlayer = table.joinAsPlayer(username, UserSession.sessions.get(username));
        this.players[table.getPlayersAmount()-1] = newPlayer;
        if (newPlayer != null){
            writeToPlayer(newPlayer, "OK");
        }
        for (Player player : table.getPlayers()){
            if (player.getUsername().equals(username)) continue;
            writeToPlayer(player, "ACCEPT_PLAYER " + username);
        }
        for (Player player : table.getPlayers()){
            if (player.getUsername().equals(username)) continue;
            writeToPlayer(newPlayer, "ACCEPT_PLAYER " + player.getUsername());
        }
        if (table.isFilled()){
            waitTillReady();
        }
    }
    public void startGame(){
        for (Player player : table.getPlayers()){
            writeToPlayer(player, "START");
        }
        generateCards();
        gameLoop();
    }

    public void waitTillReady(){
        for (Player player : table.getPlayers()){
            writeToPlayer(player, "FULL");
        }

    }

    public void endGame(boolean firstWon){
        writeToPlayer(players[0], "VICTORY");
        writeToPlayer(players[1], "LOSS");
    }

    public int getTableID(){
        return table.getId();
    }

    public Table getTable() {
        return table;
    }

    public void round(){
        Player attacker = table.getPlayers().get(1);
        Player defender = table.getPlayers().get(0);
        writeToPlayer(attacker, "ATTACK");
        writeToPlayer(defender, "DEFEND");
        writeToPlayer(attacker, "REQUEST_CARD");
        String line = readFromPlayer(attacker);
        System.out.println(line+"<-");
        //putCard(attacker.getUsername(), Integer.parseInt(line.split(" ")[1]), 0);
        String cardinfo = line.split(" ")[1];
        int cardID = Integer.parseInt(cardinfo);
        writeToPlayer(defender, "ACCEPT_CARD " + attacker.getCards()[cardID].getSpriteFilename());
        //writeToPlayer(attacker, "CARD_ACCEPTED " + attacker.getCards().get(Integer.parseInt(cardinfo)).getSpriteFilename());
        writeToPlayer(attacker, "CARD_ACCEPTED " + cardID + " " +attacker.getCards()[cardID].getSpriteFilename());
        attacker.loseCard(cardID);

        writeToPlayer(defender, "REQUEST_CARD");
        line = readFromPlayer(defender);
        cardinfo = line.split(" ")[1];
        cardID = Integer.parseInt(cardinfo);
        writeToPlayer(attacker, "ACCEPT_CARD " + defender.getCards()[cardID].getSpriteFilename());
        writeToPlayer(defender, "CARD_ACCEPTED " + cardID + " " + defender.getCards()[cardID].getSpriteFilename());
        defender.loseCard(cardID);

        writeToPlayer(defender, "CLEAR");
        writeToPlayer(attacker, "CLEAR");

    }

    public boolean isOver(){

        System.out.println(players[0].getCardsAmount() + " " + players[0].getUsername());
        System.out.println(players[1].getCardsAmount() + " " + players[1].getUsername());

        return (players[0].getCardsAmount() == 0) || (players[1].getCardsAmount() == 0);

    }

    public void gameLoop(){
        while (true){
            if (isOver()){
                System.out.println("done");
                break;
            }
            round();
        }
        System.out.println("doneee");
    }
    public void generateCards(){
        for (Suit suit : Suit.values()){
            Color color = (suit == Suit.HEART || suit == Suit.DIAMOND) ? Color.RED : Color.BLACK;
            cardStack.push(new Card(suit, color,0));
            cardStack.push(new Card(suit, color,2));
            cardStack.push(new Card(suit, color,3));
            cardStack.push(new Card(suit, color,4));
            cardStack.push(new Card(suit, color,10));
            cardStack.push(new Card(suit, color,11));
        }
        Collections.shuffle(cardStack);
        Card card;
        writeToPlayer(players[0],"COLLECT_CARDS");
        writeToPlayer(players[1],"COLLECT_CARDS");
        for (Player player : players){
            for (int i = 0; i < 6; i++){
                card = cardStack.pop();
                writeToPlayer(player, card.getSpriteFilename());
                player.takeCard(card);
            }
        }
        cardsOnTable[2] = cardStack.pop();
        atut = cardsOnTable[2].getSuit();
    }

    public void writeToPlayer(Player player, String line){
        try {
            player.session.writeToKlient(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String readFromPlayer(Player player){
        String s = player.session.readFromKlient();
        System.out.println(s);
        return s;
    }


}
