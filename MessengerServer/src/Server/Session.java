package Server;

import java.io.IOException;
import java.net.*;

public class Session implements ISession{
    private String ip;
    private int port;
    Socket clientSocket;

    Session(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean connect() {
        try{
            clientSocket = new Socket(ip, port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void disconnect() {

    }
}
