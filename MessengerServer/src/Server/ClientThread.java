package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientThread extends Thread{
    private Socket client;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    File users;
    FileOutputStream out;

    ClientThread (Socket client) throws IOException {
        this.client = client;
        users = new File("users_list.data");
        out = new FileOutputStream(users, true);
    }

    private void send(String data) throws IOException {
        outputStream.writeUTF(data);
        //outputStream.writeUTF(data + '\n');

    }

    private boolean registry(Command cmd) throws FileNotFoundException {
        ArrayList<String> items = cmd.getItems();
        if(items.isEmpty() || items.size() != 8)
            return false;

        String userName = items.get(1) + " " + items.get(2);
        try(Scanner reader = new Scanner(users)) {
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if (data.startsWith(userName)) {
                    return false;
                }
            }
            for(int i = 1; i < items.size(); ++i){
                String item = items.get(i) + " ";
                out.write(item.getBytes(), 0, item.getBytes().length);
            }
            out.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean login(Command cmd) throws FileNotFoundException {
        ArrayList<String> items = cmd.getItems();
        if(items.isEmpty() || items.size() != 4)
            return false;

        String userNameAndPsw = items.get(1) + " " + items.get(2) + " " + items.get(3);
        try(Scanner reader = new Scanner(users)) {
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if (data.startsWith(userNameAndPsw)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        try {
            outputStream = new DataOutputStream(client.getOutputStream()); //write to the socket
            inputStream = new DataInputStream(client.getInputStream()); //read from the socket

            while (!client.isClosed()) {
                if(inputStream.available() != 0) {
                    String command = inputStream.readUTF();
                    if (command.equalsIgnoreCase("<quit>")) {
                        break;
                    }

                    Command requestedCmd = new Command();
                    Command responseCmd = new Command();
                    if(!requestedCmd.parse(command)) {
                        responseCmd.create(Command.Cmds.ERROR, Command.States.ERROR,"Invalid command");
                        send(responseCmd.toString());
                        continue;
                    }

                    switch (requestedCmd.getType()) {
                        case REGISTRY:
                            if(registry(requestedCmd))
                                responseCmd.create(requestedCmd.getType(), Command.States.OK);
                            else
                                responseCmd.create(requestedCmd.getType(), Command.States.ERROR,"Invalid command");
                            send(responseCmd.toString());
                            continue;
                        case LOGIN:
                            if(login(requestedCmd))
                                responseCmd.create(requestedCmd.getType(), Command.States.OK);
                            else
                                responseCmd.create(requestedCmd.getType(), Command.States.ERROR,"Invalid login");
                            send(responseCmd.toString());
                            continue;
                        default:
                            responseCmd.create(Command.Cmds.ERROR, Command.States.ERROR,"Invalid command");
                            send(responseCmd.toString());
                            continue;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
