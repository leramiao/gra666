package logic;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private static int maxID = 0;
    private String filledRatio;
    private int id;
    private List<Player> players;
    private int maxPlayers;
    public int playersAmount;
    private int nWaiting;
    private String owner;

    public Table(int maxPlayers, String owner) {
        this.id = ++maxID;
        this.playersAmount = 0;
        this.maxPlayers = maxPlayers;
        this.filledRatio = "0/"+maxPlayers;
        this.players = new ArrayList<>();
        this.nWaiting = 0;
        this.owner = owner;
    }
    public Table(int id, int maxPlayers,int playersAmount, String owner) {
        this.id = id;
        maxID = Math.max(maxID, id);
        this.filledRatio = playersAmount+"/"+maxPlayers;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.nWaiting = 0;
        this.owner = owner;
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
    public void playerLeave(String username){
        for (Player p : players){
            if (p.getUsername().equals(username)){
                playersAmount--;
                players.remove(p);
                return;
            }
        }
    }

    public boolean isAvailable(){
        return playersAmount < maxPlayers;
    }


    public void incWaiting(){
        nWaiting++;
    }
    public void decWaiting(){
        nWaiting--;
    }

    public int getnWaiting() {
        return nWaiting;
    }

    public boolean isFilled() {
        return maxPlayers == playersAmount;
    }

    public String getOwner() {
        return owner;
    }

    public String getFilledRatio() {
        return filledRatio;
    }
}
