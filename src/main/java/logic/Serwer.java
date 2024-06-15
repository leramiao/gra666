package logic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class Serwer {
    private static final String DATABASE_NAME = "cardgame";
    static String driver = "com.mysql.jdbc.Driver";
    static String url = "jdbc:mysql://127.0.0.1:3306/" + DATABASE_NAME;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private ServerSocket serverSocket;

    private int sessionID = 0;
    private ExecutorService service;
    private static Connection connection;
    private static Statement statement;
    private static SeansManager seansManager;

    public static List<SeansGry> getSeanse(){
        return seansManager.getSeanse();
    }
    /*public static List<Table> getTables(){
        return tableManager.getTables();
    }

     */


    public void start(int port) throws IOException {
        prepDatabase();
        seansManager = new SeansManager();
        seansManager.createSeans(new Table(2));
        serverSocket = new ServerSocket(port);
        //service = new ThreadPoolExecutor(5,100,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        while (true){
            socket = serverSocket.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //in = new Scanner(socket.getInputStream());
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            if (readFromKlient().equals("CONNECT")){
                writeToKlient("CONNECTED");
                System.out.println("connection established");
                //service.submit(new UserSession(sessionID, socket, in, out, statement));
                new Thread(new UserSession(sessionID, socket, in, out, statement)).start();
                sessionID++;

            }

        }
    }
    public void close() throws IOException {
        disconnectFromDB(connection, statement);
        socket.close();
        serverSocket.close();
    }

    public String readFromKlient() throws IOException {
        System.out.println("Waiting for client response.. ");
        String line = in.readLine();
        System.out.println(line);
        return line;
    }

    public void writeToKlient(String line) throws IOException {
        System.out.println(">"+line);
        out.write(line+"\n");
        out.flush();
    }

    public static void main(String[] args) throws IOException {
        Serwer serwer = new Serwer();
        serwer.start(6666);
        serwer.close();
    }


    public static int createSeans(int nPlayers){
        Table table = new Table(nPlayers);
        seansManager.createSeans(table);
        return table.getId();
    }

    public static int addPlayerToWaiting(int tableID){
        System.out.println("SERVER ADD PLAYER TO WAITING");
        for (SeansGry seansGry : seansManager.getSeanse()){
            if (seansGry.getTableID() == tableID){
                Table table = seansGry.getTable();
                int required = table.getMaxPlayers();
                if (table.getnWaiting() >= required){
                    return -1;
                }
                table.incWaiting();
                if (table.getnWaiting() == required){
                    return 2;
                }
                return 1;
            }
        }
        return 1;
    }
    public static void startGameOnTable(int tableID){
        for (SeansGry seans : seansManager.getSeanse()) {
            if (seans.getTableID() == tableID){
                seans.startGame();
                return ;
            }
        }
    }


    public static void joinTable(String username, int tableID){
        for (SeansGry seans : seansManager.getSeanse()){
            if (seans.getTableID() == tableID){
                seans.addPlayer(username);
                return;
            }
        }
    }
    public static int addUserToDB(String username, String password){

        String sql = String.format("INSERT INTO users (username, password) VALUES(\"%s\",\"%s\")", username, password);
        return executeUpdate(statement, sql);
    }

    public static boolean authenticate(String username, String password) {
        String correct_password = null;
        String sql = String.format("SELECT password FROM users WHERE username = %s", username);
        ResultSet rs = executeQuery(statement, sql);
        try {
            if (rs.next()){
                correct_password = rs.getString("password");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return password.equals(correct_password);
    }

    public static void initDatabase(Statement statement){
        initAccountData(statement);
    }
    public static void initAccountData(Statement statement){
        String sql = "CREATE TABLE users " +
                "(username varchar(16) not NULL, " +
                " password varchar(16) not NULL, " +
                " PRIMARY KEY ( username ))";
        executeUpdate(statement, sql);
    }

    public static void prepDatabase() {
        connection = connectToDB(url, "miao", "miao");
        statement = createStatement(connection);
        if (executeUpdate(statement, "USE " + DATABASE_NAME) == 0){
            System.out.println("ok, baza wybrana");
        } else {
            System.out.println("brak takiej bazy, tworze");
            executeUpdate(statement,"CREATE database " + DATABASE_NAME + ";");
            executeUpdate(statement, "USE " + DATABASE_NAME);
        }
        initDatabase(statement);
    }

    public static Connection connectToDB(String url, String username, String password)   {
        Connection connection = null;
        Properties con_props = new Properties();
        con_props.put("username", username);
        con_props.put("password", password);
        try {
            connection = DriverManager.getConnection(url, con_props);

            System.out.println("connected to db");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to connect to the database fsr" + e.getMessage());
            System.exit(1);
        }
        return connection;
    }

    public static void disconnectFromDB(Connection connection, Statement statement)   {

        try {
            connection.close();
            statement.close();
            System.out.println("disconnected from db");
        } catch (SQLException e) {
            System.out.println("failed to disconnect fsr" + e.getErrorCode() + ":" + e.getMessage());
            System.exit(4);
        }
    }

    public static Statement createStatement(Connection connection){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            System.out.println("statement created ok");
        } catch (SQLException e) {
            System.out.println("failed to create statement fsr: " + e.getErrorCode() + ":" + e.getMessage());
            System.exit(3);
        }
        return statement;
    }

    public static ResultSet executeQuery(Statement s, String sql){
        ResultSet res;
        try {
            res = s.executeQuery(sql);
            System.out.println("query executed ok");
        } catch (SQLException e) {
            System.out.println("failed to execute query fsr: " + e.getErrorCode() + ":" + e.getMessage());
            throw new RuntimeException(e);
        }
        return res;
    }

    public static int executeUpdate(Statement s, String sql){
        int res = 1;
        try {
            res = s.executeUpdate(sql);
            System.out.println("query executed ok");
        } catch (SQLException e) {
            res = -1;
            System.out.println("failed to execute query fsr: " + e.getErrorCode() + ":" + e.getMessage());

        }
        return res;
    }

}