package logic;

import java.util.ArrayList;
import java.util.List;

public class SeansManager {
    private List<SeansGry> seanse;

    public SeansManager() {
        this.seanse = new ArrayList<>();
    }

    public void addSeans(SeansGry seans){
        seanse.add(seans);
    }
    public void createSeans(Table table){
        seanse.add(new SeansGry(table));
    }


    public List<SeansGry> getSeanse() {
        return seanse;
    }
}
