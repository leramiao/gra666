package logic;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class Client {
    public Socket socket;
    private BufferedReader in;
    private PrintWriter out;



    public void start(String host, int port) throws IOException, InterruptedException, ExecutionException {
        try {
            this.socket = new Socket(host, port);
            System.out.println("success so far");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeToServer("CONNECT");
        if (readFromServer().equals("CONNECTED")){
            System.out.println("connection established");
        }

    }

    public String readFromServer() throws IOException {
        String line = in.readLine();
        System.out.println(line);
        return line;
    }

    public void writeToServer(String line) throws IOException {
        System.out.println("you: "+line);
        out.write(line+"\n");
        out.flush();
    }


    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);
        Client klient = new Client();
        klient.start("127.0.0.1", 6666);
        String input;
        while (true){
            input = scanner.nextLine();
            if (input.equals("quit")){
                klient.close();
                break;
            }
        }
    }
}