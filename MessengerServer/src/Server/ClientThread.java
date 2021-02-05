package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

//Create threads for clients
public class ClientThread extends Thread{
    private Socket client;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    File users;
    FileOutputStream out;

    ClientThread (Socket client) throws IOException {
        this.client = client;
        users = new File("users_list.data"); //указывааем путь к файлу
        out = new FileOutputStream(users, true);
    }

    private void send(String data) throws IOException {
        outputStream.writeUTF(data); //пишем в поток
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
                    Map<Socket, String> clientList = Server.getClientList();
                    clientList.replace(client, items.get(1) + " " + items.get(2));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findUser(Command cmd) throws FileNotFoundException {
        ArrayList<String> items = cmd.getItems();
        if(items.isEmpty()) {
            return false;
        }

        String userName = items.get(1);
        try(Scanner reader = new Scanner(users)) {
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                //boolean dataIgnoreCase = data.equalsIgnoreCase(userName);
                if(data.startsWith(userName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getUserList() throws FileNotFoundException {
        String userList = "";
        try(Scanner reader = new Scanner(users)) {
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] userData = data.split(" ");
                userList += userData[0] + " " + userData[1] + "_";
            }
        }
        return userList;
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
                        case FIND_USER:
                            if(findUser(requestedCmd))
                                responseCmd.create(requestedCmd.getType(), Command.States.OK);
                            else
                                responseCmd.create(requestedCmd.getType(), Command.States.ERROR,"Invalid find user");
                            send(responseCmd.toString());
                            continue;
                        case LIST_USERS:
                            String users = getUserList();
                            if (!users.isEmpty()) {
                                responseCmd.create(requestedCmd.getType(), Command.States.OK, users);
                            } else {
                                responseCmd.create(requestedCmd.getType(), Command.States.ERROR, "Invalid");
                            }
                            send(responseCmd.toString());
                            continue;
                        case SEND:
                            if (send(requestedCmd)) {
                                responseCmd.create(requestedCmd.getType(), Command.States.OK);
                            } else {
                                responseCmd.create(requestedCmd.getType(), Command.States.ERROR, "Invalid");
                            }
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

    private boolean send(Command cmd) throws IOException {
        ArrayList<String> items = cmd.getItems();
        if(items.isEmpty() || items.size() != 3)
            return false;

        String destinationUserName = items.get(1);
        String msg = items.get(2);
        Map<Socket, String> clientList = Server.getClientList();
        for(Map.Entry<Socket, String> item : clientList.entrySet()){
            if (item.getValue().equals(destinationUserName)) {
                Socket destSocket = item.getKey();
                try(DataOutputStream dos = new DataOutputStream(destSocket.getOutputStream())){
                    Command responseCmd = new Command();
                    responseCmd.create(Command.Cmds.RECEIVE, Command.States.OK, msg);
                    dos.writeUTF(responseCmd.toString());
                    dos.close();
                }
            }
        }
        return true;
    }
}
