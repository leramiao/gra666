package logic;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class UserSession extends Thread{

    private final int sessionID;
    private BufferedReader in;
    private PrintWriter out;
    String username;

    private int correctAnswersGiven;
    private Socket socket;
    private Statement statement;

    public static HashMap<String, UserSession> sessions;

    static {
        sessions = new HashMap<>();
    }

    public UserSession(int sessionID, Socket socket, BufferedReader in, PrintWriter out, Statement statement)  {
        this.sessionID = sessionID;
        this.in = in;
        this.out = out;
        this.correctAnswersGiven = 0;
        this.socket = socket;
        this.statement = statement;
    }


    @Override
    public void run() {
        String[] client_input;
        while (true){
            client_input = readFromKlient().split(" ");
            switch(client_input[0]){
                case "ADD_USER":
                    try {
                        addUser(client_input[1], client_input[2]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "LOGIN":
                    try {
                        authenticate(client_input[1], client_input[2]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "CLOSE_STACK":
                    break;
                case "MELDUNEK":
                    break;
                case "CREATE_TABLE":
                    createTable(2);
                    break;
                case "LIST_TABLES":
                    try {
                        listTables();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "JOIN_TABLE":
                    try {
                        joinTable(client_input[2], Integer.parseInt(client_input[1]));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "LEAVE":
                    leaveTable(Integer.parseInt(client_input[1]),client_input[2]);
                    break;
                case "DELETE_TABLE":
                    deleteTable(Integer.parseInt(client_input[1]));
                    break;
                case "PUT":
                    int cardID = Integer.parseInt(client_input[1]);
                    break;
                case "ACCEPT_CARD":
                    break;
                case "READY":
                    try {
                        setReady(Integer.parseInt(client_input[1]));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "CANCEL_READY":
                    break;
                default:
                    System.out.println("UNKNOWN COMMANd!");
                    break;
            }
        }
    }

    private void getPFP(String username) throws IOException {
        OutputStream out = socket.getOutputStream();
        File file = new File("media/pfps/"+username+".png");
        if (!file.exists()){
            file = new File("media/pfps/default.png");
        }
        BufferedImage image = ImageIO.read(file);
        ImageIO.write(image,"PNG",out);
        out.flush();
        out.close();
    }

    public String readFromKlient() {

        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(line + " in session");

        return line;
    }

    public void writeToKlient(String line) throws IOException {
        System.out.println(line+"->"+username);
        out.write(line+"\n");
        out.flush();
    }

    public void deleteTable(int tableID){
        Serwer.deleteTable(tableID);
    }
    public void leaveTable(int tableID, String username){
        Serwer.leaveTable(tableID, username);
    }



    public void addUser(String username, String password) throws IOException {

        if (Serwer.addUserToDB(username, password) == -1){
            writeToKlient("FAILURE");
        }
        else {
            writeToKlient("SUCCESS");
        }
    }
    public void authenticate(String username, String password) throws IOException {
        if (Serwer.authenticate(username, password)){
            sessions.put(username, this);
            writeToKlient("SUCCESS");
            this.username = username;
            //SceneController.openLoungeView();
            return;
        }
        writeToKlient("FAILURE");
    }
    public void createTable(int nPlayers){
        Serwer.createSeans(nPlayers, username);
    }

    public void setReady(int tableID) throws IOException {

        int res = Serwer.addPlayerToWaiting(tableID);
        if (res == 2){
            Serwer.startGameOnTable(tableID);
        }
    }


    public void joinTable(String username, int tableID) throws IOException {
        System.out.println(username + " is joining table " + tableID);
        Serwer.joinTable(username,tableID);
        //writeToKlient("OK");
        //sendTableInfo(tableID);

    }
    public void listTables() throws IOException {
        List<SeansGry> seanseList = Serwer.getSeanse();
        writeToKlient(String.valueOf(seanseList.size()));
        System.out.println("Listing basic info for all tables");
        for (SeansGry seans : seanseList){
            writeToKlient(seans.getTableID() + " " + seans.getTable().getMaxPlayers() + " " + seans.getTable().getPlayersAmount()+ " " + seans.getTable().getOwner() );

        }

    }

    /*
    public void sendTableInfo(int tableID) throws IOException {
        List<Table> tableList = Serwer.getTables();
        //writeToKlient(String.valueOf(tableList.size()));
        for (Table table : tableList){
            if (table.getId() == tableID){
                //writeToKlient(table.getId() + " " + table.getMaxPlayers() + " " + table.getPlayersAmount());
                System.out.println("N players:");
                writeToKlient(String.valueOf(table.getPlayersAmount()));
                System.out.println("Listing players already at the table");
                for (Player player : table.getPlayers()){
                    writeToKlient(player.getUsername());
                }

            }
        }

    }

     */

    public Socket getSocket() {
        return socket;
    }
}
