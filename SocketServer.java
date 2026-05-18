import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.FileWriter;

public class SocketServer {
    ServerSocket server;
    Socket sk;
    InetAddress addr;
    ChatBot chatBot = new ChatBot();
    ArrayList<ServerThread> list = new ArrayList<ServerThread>();

    public SocketServer() {
        try {
        	addr = InetAddress.getByName("127.0.0.1");
        	//addr = InetAddress.getByName("192.168.43.1");
            
        	server = new ServerSocket(1234,50,addr);
            System.out.println("\n Waiting for Client connection");
            SocketClient.main(null);
            while(true) {
                sk = server.accept();
                System.out.println(sk.getInetAddress() + " connect");
                logMessage(sk.getInetAddress() + " connected");
                //Thread connected clients to ArrayList
                ServerThread st = new ServerThread(this);
                addThread(st);
                st.start();
            }
        } catch(IOException e) {
            System.out.println(e + "-> ServerSocket failed");
        }
    }

    public void addThread(ServerThread st) {
        list.add(st);
    }

    public void removeThread(ServerThread st){
        list.remove(st); //remove
    }

    public void broadCast(String message){
        for(ServerThread st : list){
            st.pw.println(message);
        }
    }

    public void logMessage(String message) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("chatlog.txt", true));
            writer.println(message);
            writer.close();
        } catch (IOException e) {
            System.out.println("Logging failed.");
        }
    }

    public String getUserList() {
        String users = "Connected Users:\n";
        for (ServerThread st : list) {
            if (st.name != null) {
                users += st.name + "\n";
            }
        }
        return users;
    }

    public static void main(String[] args) {
        new SocketServer();
    }
}

class ServerThread extends Thread {
    SocketServer server;
    PrintWriter pw;
    String name;

    public ServerThread(SocketServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // read
            BufferedReader br = new BufferedReader(new InputStreamReader(server.sk.getInputStream()));

            // writing
            pw = new PrintWriter(server.sk.getOutputStream(), true);
            name = br.readLine();
            server.broadCast("**["+name+"] Entered**");
            server.logMessage("[" + name + "] Entered");

            String data;
            while((data = br.readLine()) != null ){
                server.logMessage("[" + name + "] " + data);
                if (data.equals("/list")) {
                    pw.println(server.getUserList());
                } else if (data.toLowerCase().startsWith("/bot")) {
                    pw.println(server.chatBot.handleCommand(data));
                } else {
                    server.broadCast("[" + name + "] " + data);
                }
            }
        } catch (Exception e) {
            //Remove the current thread from the ArrayList.
            server.removeThread(this);
            server.broadCast("**["+name+"] Left**");
            server.logMessage("[" + name + "] Left");
            System.out.println(server.sk.getInetAddress()+" - ["+name+"] Exit");
            System.out.println(e + "---->");
        }
    }
}