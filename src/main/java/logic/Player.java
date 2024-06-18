package logic;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private Card[] cards;
    private String username;
    public UserSession session;
    private int points;
    private int globalPoints;
    private int nCards;
    private int bonus;

    public Player(String username, UserSession session) {
        this.username = username;
        this.cards = new Card[6];
        this.nCards = 0;
        this.points = 0;
        this.globalPoints = 0;
        this.bonus = 0;
        this.session = session;
    }

    private boolean addCardToArray(Card card){
        for (int i = 0; i < 6; i++){
            if (cards[i] == null){
                cards[i] = card;
                nCards++;
                return true;
            }
        }
        return false;
    }

    public int getCardsAmount(){
        return nCards;
    }

    public int getGlobalPoints() {
        return globalPoints;
    }
    public void addGlobalPoints(int pts) {
        this.globalPoints += pts;
    }

    public void setGlobalPoints(int globalPoints) {
        this.globalPoints = globalPoints;
    }

    public void loseCard(int i){
        cards[i] = null;
        nCards--;
    }
    public void takeCard(Card card){
        addCardToArray(card);
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
        this.points += bonus;
        bonus = 0;
    }
    public void resetRoundData(){
        points = 0;
        bonus = 0;
        for (int i = 0; i < 6; i++){
            cards[i] = null;
        }

    }
    public void addBonus(int points) {
        this.bonus += points;
    }

    public Card[] getCards() {
        return cards;
    }

    public String getUsername() {
        return username;
    }
}
