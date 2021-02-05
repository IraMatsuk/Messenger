package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static Session session;
    private static Map<Socket, String> clientList = new HashMap<>(); //Map, хранящая в ключе - сокет, у сокета есть имя (client)

    static Map<Socket, String> getClientList(){
        return clientList;
    }

    public static void main(String[] args) throws UnknownHostException {
        int port = 50000;
        int backlog = 8;
        InetAddress ip = InetAddress.getByName("127.0.0.1");

        ArrayList<ClientThread> clientThreads = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(port, backlog, ip);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            while(!serverSocket.isClosed()) {
                if(bufferedReader.ready()) {
                    String cmd = bufferedReader.readLine();
                    if (cmd.equalsIgnoreCase("quit")) {
                        serverSocket.close();
                        break;
                    }
                }

                Socket client = serverSocket.accept();
                clientList.put(client, "");
                ClientThread clientThread = new ClientThread(client);
                clientThreads.add(clientThread);
                clientThread.start();
            }

        } catch (IOException e) {
            System.out.println("Cannot connect to socket " + ip.toString() + ":" + port);
        } finally {
            try {
                for (ClientThread thread :clientThreads) {
                    if (thread.isAlive()) {
                        thread.join();
                    }
                }
                for(Map.Entry<Socket, String> item : clientList.entrySet()){
                    item.getKey().close();
                }
            } catch (InterruptedException | IOException e) {
                System.out.println(e);
            }
        }
    }
}
