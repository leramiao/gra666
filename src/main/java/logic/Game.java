package logic;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private static List<Table> tables;

    public static void main(String[] args) {
        tables = new ArrayList<>();
        tables.add(new Table(2));

    }

    public static List<Table> getTables(){
        tables = new ArrayList<>();
        tables.add(new Table(2));
        return tables;
    }

}
