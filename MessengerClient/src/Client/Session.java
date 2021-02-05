package Client;
import java.io.*;
import java.net.*;

public class Session implements ISession{
    private String ip;
    private int port;
    Socket clientSocket;
    DataOutputStream outputStream;
    DataInputStream inputStream;

    Session(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }



    @Override
    public boolean connect() {
        try{
            clientSocket = new Socket(ip, port);
            outputStream = new DataOutputStream(clientSocket.getOutputStream()); // выходной поток, который будет писать из клиента на сервер
            inputStream = new DataInputStream(clientSocket.getInputStream());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean send(String text) {
        try {
            outputStream.writeUTF(text);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String receive() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            return "";
        }
    }

    public String nonBlockReceive() {
        try {
            if(inputStream.available() != 0)
                return inputStream.readUTF();
            else
                return "";
        } catch (IOException e) {
            return "";
        }
    }
}
