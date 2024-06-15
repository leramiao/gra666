package logic;

import java.util.ArrayList;
import java.util.List;

public class PlayerQueue {
    private List<Player> playerList;

    public PlayerQueue() {
        this.playerList = new ArrayList<>();
    }

    public void addPlayer(Player player){
        if (playerList.size()>4) return;
        playerList.add(player);
    }
    public Player getPlayer(int id){
        return playerList.get(id);
    }
    public Player circle(){
        Player res = playerList.get(0);
        playerList.remove(0);
        playerList.add(res);
        return res;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public int getSize(){
        return playerList.size();
    }
}
