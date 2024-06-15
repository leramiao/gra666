package logic;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private Card[] cards;
    private String username;
    public UserSession session;
    private int points;
    private int nCards;

    public Player(String username, UserSession session) {
        this.username = username;
        this.cards = new Card[6];
        this.nCards = 0;
        this.points = 0;
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

    public boolean isOutOfCards(){
        return nCards == 0;
    }

    public Card peekCard(int i){
        return cards[i];
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

    public void setPoints(int points) {
        this.points = points;
    }
    public void addPoints(int points) {
        this.points += points;
    }

    public Card[] getCards() {
        return cards;
    }

    public String getUsername() {
        return username;
    }
}
