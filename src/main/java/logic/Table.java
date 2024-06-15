package logic;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private static int maxID = 0;
    private int id;
    private List<Player> players;
    private int maxPlayers;
    private int playersAmount;
    private int nWaiting;
    private boolean isFilled;

    public Table(int maxPlayers) {
        this.id = ++maxID;
        this.playersAmount = 0;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.nWaiting = 0;
        this.isFilled = false;
    }
    public Table(int id, int maxPlayers,int playersAmount) {
        this.id = id;
        maxID = Math.max(maxID, id);
        this.playersAmount = playersAmount;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.nWaiting = 0;
        this.isFilled = maxPlayers == playersAmount;
    }

    public List<Player> getPlayers() {
        return players;
    }


    public int getPlayersAmount() {

        return players.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayersAmount(int playersAmount) {
        this.playersAmount = playersAmount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Player joinAsPlayer(String username, UserSession session){
        Player res = new Player(username, session);
        this.players.add(res);
        System.out.println(String.format("player %s joined to %d !!", username, id));
        playersAmount++;
        return res;

    }

    public int isAvailable(){
        if (maxPlayers == playersAmount) return -1;
        return 1;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void incWaiting(){
        nWaiting++;
    }
    public void incPlayerAmount(){
        playersAmount++;
    }
    public void decPlayerAmount(){
        playersAmount--;
    }
    public void decWaiting(){
        nWaiting--;
    }

    public int getnWaiting() {
        return nWaiting;
    }

    public void addPlayerNoIncrement(String username, UserSession session){
        this.players.add(new Player(username,session));

    }

    public boolean isFilled() {
        return maxPlayers <= playersAmount;
    }
}
