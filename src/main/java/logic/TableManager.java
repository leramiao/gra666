package logic;

import java.util.ArrayList;
import java.util.List;

public class TableManager {
    private List<Table> tables;

    public TableManager() {
        this.tables = new ArrayList<>();
    }

    public void addTable(Table table){
        System.out.println("Table added!");
        tables.add(table);
    }

    public List<Table> getTables() {
        System.out.println("amount of tables = " + tables.size());
        if (!tables.isEmpty())
            System.out.println("id of table 0 = " + tables.get(0).getId());
        return tables;
    }
}
