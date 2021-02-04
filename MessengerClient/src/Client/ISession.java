package Client;

public interface ISession {
    boolean connect();
    void disconnect();
    boolean send(String text);
    String receive();
}
