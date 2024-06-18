package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.swap;

public class SeansGry implements Runnable{
    public Player[] players;

    private Stack<Card> cardStack;
    private Table table;
    private Card[] cardsOnTable;
    private Suit atut;
    private int lewa;
    private int[] bonusPoints = {0,0};
    private boolean declaration;

    public SeansGry(Table table) {
        this.cardStack = new Stack<>();
        this.cardsOnTable = new Card[3];
        this.table = table;
        this.players = new Player[2];
    }

    public SeansGry(int maxPlayers, String username) {
        this.cardStack = new Stack<>();
        this.cardsOnTable = new Card[3];
        this.table = new Table(maxPlayers, username);
        this.players = new Player[2];
    }

    public SeansGry(int id, int maxPlayers, int nPlayers, String username) {
        this.cardStack = new Stack<>();
        this.cardsOnTable = new Card[3];
        this.table = new Table(id, maxPlayers, nPlayers, username);
        this.players = new Player[2];
    }



    public void leaveTable(String username){
        for (Player player : table.getPlayers()){
            if (player.getUsername().equals(username)){
                writeToPlayer(player,"LEAVE_CONFIRM");
            } else {
                writeToPlayer(player, "PLAYER_LEAVE");
            }
        }
        table.playerLeave(username);
    }

    private void putAtut(){
        for (Player p : players){
            writeToPlayer(p, "SET_ATUT " + cardsOnTable[2].getSpriteFilename());
        }
    }

    public void addPlayer(String username){
        if (!table.isAvailable()){
            try {
                UserSession.sessions.get(username).writeToKlient("FAIL");
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
    public void startRound(){
        for (Player player : table.getPlayers()){
            writeToPlayer(player, "START");
        }
        lewa = 0;
        declaration = false;
        generateCards();
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
        Player attacker = players[0];
        Player defender = players[1];
        writeToPlayer(attacker, "ATTACK");
        writeToPlayer(defender, "DEFEND");
        boolean skip = startAttack(attacker, defender);
        if (skip) return;
        boolean defended = startDefense(attacker,defender);
        lewa = cardsOnTable[0].getValue() + cardsOnTable[1].getValue();
        if (defended){
            defender.addPoints(lewa);
            swapPlayerRoles();
        }
        else {
            attacker.addPoints(lewa);
        }
        takeCardFromStack(defender,attacker);
        takeCardFromStack(attacker,defender);


        clearTableCards();

    }

    private void takeCardFromStack(Player taker, Player observer){
        if (!cardStack.empty()){
            Card tmp =  cardStack.pop();
            writeToPlayer(taker, "COLLECT_CARD " + tmp.getSpriteFilename());
            writeToPlayer(observer, "OPP_COLLECT_CARD");
            taker.takeCard(tmp);
            if (cardStack.empty()){
                writeToPlayer(observer, "EMPTY_STACK");
                writeToPlayer(taker, "EMPTY_STACK");
            }
        }
    }

    private boolean startDefense(Player attacker, Player defender){
        writeToPlayer(defender, "REQUEST_CARD DEFEND");
        String line = readFromPlayer(defender);
        String cardinfo = line.split(" ")[1];
        int cardID = Integer.parseInt(cardinfo);
        cardsOnTable[1] =  defender.getCards()[cardID];
        if (!cardStack.empty() || !isGreaterCardAvailable(defender,cardsOnTable[0])){
            endDefense(attacker,defender,cardID);
        }
        else {
            while (!isCardGreater(cardsOnTable[1],cardsOnTable[0])){
                System.out.println("bad card");
                line = readFromPlayer(defender);
                cardinfo = line.split(" ")[1];
                cardID = Integer.parseInt(cardinfo);
                cardsOnTable[1] =  defender.getCards()[cardID];
            }
            endDefense(attacker,defender,cardID);

        }
        return isCardGreater(cardsOnTable[1],cardsOnTable[0]);
    }

    private void endDefense(Player attacker, Player defender, int cardID){
        writeToPlayer(attacker, "ACCEPT_CARD " + defender.getCards()[cardID].getSpriteFilename());
        writeToPlayer(defender, "CARD_ACCEPTED " + cardID + " " + defender.getCards()[cardID].getSpriteFilename());
        defender.loseCard(cardID);
        clearTableCards();
    }

    private int checkForMeldunek(Player player, int cardID){
        if (player.getCards()[cardID].getValue() == 4){
            for (int i = 0; i < 6; i++){
                if (i == cardID) continue;
                if (player.getCards()[i] != null && player.getCards()[i].getValue() == 3){
                    Color color = player.getCards()[i].getColor();
                    if (color.equals(player.getCards()[cardID].getColor())){
                        System.out.println("meldunek");
                        return i;

                    }
                }
            }
        } else if (player.getCards()[cardID].getValue() == 3){
            for (int i = 0; i < 6; i++){
                if (i == cardID) continue;
                if (player.getCards()[i] != null && player.getCards()[i].getValue() == 4){
                    Color color = player.getCards()[i].getColor();
                    if (color.equals(player.getCards()[cardID].getColor())){
                        System.out.println("meldunek");
                        return i;

                    }
                }
            }
        }
        System.out.println("no meldunek");
        return -1;
    }
    private int getMeldunekBonus(Player player, int cardID){
        if (player.getCards()[cardID].getColor().equals(atut.getColor())){
            return 40;
        }
        return 20;
    }


    private boolean startAttack(Player attacker, Player defender){
        writeToPlayer(attacker, "REQUEST_CARD ATTACK");
        String[] line = readFromPlayer(attacker).split(" ");
        if (line[0].equals("CLOSE_STACK")){
            cardStack.clear();
            writeToPlayer(attacker, "STACK_CLOSED");
            writeToPlayer(defender, "STACK_CLOSED");
            line = readFromPlayer(attacker).split(" ");
        }
        if (line[0].equals("MELDUNEK")){
            line = readFromPlayer(attacker).split(" ");

            while (!line[0].equals("PUT")) {
                System.out.println("put a card pleasseee");
                line = readFromPlayer(attacker).split(" ");
            }
            String cardinfo = line[1];
            int cardID = Integer.parseInt(cardinfo);
            int cardIDb = checkForMeldunek(attacker,cardID);
            if (cardIDb == -1){ //brak meldunku, zwykly atak
                cardsOnTable[0] =  attacker.getCards()[cardID];
                writeToPlayer(defender, "ACCEPT_CARD " + attacker.getCards()[cardID].getSpriteFilename());
                writeToPlayer(attacker, "CARD_ACCEPTED " + cardID + " " +attacker.getCards()[cardID].getSpriteFilename());
                attacker.loseCard(cardID);
                return false;
            }
            else {
                attacker.addBonus(getMeldunekBonus(attacker, cardID));
                attacker.loseCard(cardID);
                attacker.loseCard(cardIDb);
                writeToPlayer(attacker, "MELDUNEK " + cardID + " " + cardIDb);
                writeToPlayer(defender, "MELDUNEK_ACK");
                takeCardFromStack(attacker,defender);
                takeCardFromStack(attacker,defender);
                return true; //meldunek, nowa runda
            }
        }
        if (line[0].equals("DECLARE66")){
            assignGlobalPoints();
            declaration = true;
            return true;
        }
        while (!line[0].equals("PUT")) {
            System.out.println("put a card pleasseee");
            line = readFromPlayer(attacker).split(" ");
        }
        String cardinfo = line[1];
        int cardID = Integer.parseInt(cardinfo);
        cardsOnTable[0] =  attacker.getCards()[cardID];
        writeToPlayer(defender, "ACCEPT_CARD " + attacker.getCards()[cardID].getSpriteFilename());
        writeToPlayer(attacker, "CARD_ACCEPTED " + cardID + " " +attacker.getCards()[cardID].getSpriteFilename());
        attacker.loseCard(cardID);
        return false;
    }

    private void assignGlobalPoints() {
        if (players[0].getPoints() >= 66) {
            if (players[1].getPoints() >= 33) {
                players[0].addGlobalPoints(1);
            } else if (players[1].getPoints() > 0) {
                players[0].addGlobalPoints(2);
            } else {
                players[0].addGlobalPoints(3);
            }
            writeToPlayer(players[0], "SET_POINTS " + players[0].getGlobalPoints());
        } else {
            if (players[0].getPoints() >= 33) {
                players[1].addGlobalPoints(1);
            } else if (players[0].getPoints() > 0) {
                players[1].addGlobalPoints(2);
            } else {
                players[1].addGlobalPoints(3);
            }
            writeToPlayer(players[1], "SET_POINTS " + players[1].getGlobalPoints());
        }
    }
    private boolean finishGame() {
        if (players[0].getGlobalPoints() >=7){
            writeToPlayer(players[0], "VICTORY");
            writeToPlayer(players[1], "LOSS");
            return true;
        }
        if (players[1].getGlobalPoints() >=7){
            writeToPlayer(players[1], "VICTORY");
            writeToPlayer(players[0], "LOSS");
            return true;
        }
        return false;

    }

    private boolean isGreaterCardAvailable(Player player, Card card){
        for (Card playerCard : player.getCards()){
            if (playerCard == null) continue;
            if (isCardGreater(playerCard, card)){
                return true;
            }
        }
        return false;
    }

    synchronized private void clearTableCards(){
        try {
            TimeUnit.MILLISECONDS.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (Player p : players){
            writeToPlayer(p, "CLEAR");

        }
        lewa = 0;
    }

    public boolean isOver(){
        if (declaration) return true;

        System.out.println(players[0].getCardsAmount() + " " + players[0].getUsername());
        System.out.println(players[1].getCardsAmount() + " " + players[1].getUsername());

        return (players[0].getCardsAmount() == 0) || (players[1].getCardsAmount() == 0);
    }

    private void gameLoop(){
        while (!finishGame()){
            startRound();
            roundLoop();
        }
    }

    public void roundLoop(){
        while (true){
            if (isOver()){
                break;
            }
            round();
        }
    }
    public void generateCards(){
        cardStack.clear();
        players[0].resetRoundData();
        players[1].resetRoundData();
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
        putAtut();
    }
    private void swapPlayerRoles(){
        Player tmp = players[0];
        players[0] = players[1];
        players[1] = tmp;
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
        System.out.println(player.getUsername() + ":"+s);
        return s;
    }

    public boolean isCardGreater(Card a, Card b){
        if (a.getSuit().equals(atut)){
            if (b.getSuit().equals(atut)){
                return a.getValue() > b.getValue();
            }
            else {
                return true;
            }
        }
        else if (b.getSuit().equals(atut)){
            return false;
        }
        else if (a.getColor().equals(b.getColor())){
                return a.getValue() > b.getValue();
            }
        return false;

    }

    @Override
    public void run() {
        gameLoop();
        Serwer.deleteTable(table.getId());
    }
}
