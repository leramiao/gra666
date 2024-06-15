package logic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Klasa przechowujaca i zarzadzajaca userami ogólna - na razie tylko klienci i pewnie tak zostanie
//Na razie wczytuje tylko dane

public class UserManager {
    private static List<User> users;


    static  {
        users = new ArrayList<>();
        loadCredentials(); // Wczytaj uzytkownikow z pliku przy tworzeniu obiektu
    }
    public static List<User> getUsers() {
        return users;
    }

    public static void loadCredentials() {
            users.clear();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("credentials.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String line : lines) {
                String[] parts = line.split(":");
                int id = Integer.parseInt(parts[0].trim());
                String login = parts[1].trim();
                String password = parts[2].trim();
                User user = new User(id, login, password);

            }
    }

    public static void saveCredentials() {
        File file = new File("credentials.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (User user : users){
                writer.write(user.toString()+"\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeUser(User user){
        users.remove(user);
        saveCredentials();
    }
    public static void addUser(User user){

        users.add(user);
        saveCredentials();
    }




}