package Client;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    enum States {
        MAIN_PAGE,
        REGISTRY,
        LOGIN,
        USER_PAGE,
        FIND_USER,
        LIST_USERS,
        CHATTING,
        EXIT
    }
    private static Session session;
    private static String currUser = "";

    public static boolean registry(String firstName, String lastName, String psw, String repeatPsw, String dateOfBirth, String country, String sex) {
        Command requestCmd = new Command();
        requestCmd.create(Command.Cmds.REGISTRY, firstName, lastName, psw, repeatPsw, dateOfBirth, country, sex);

        session.send(requestCmd.toString());
        String response = session.receive();
        if (response.isEmpty()) {
            return false;
        }

        Command responseCmd = new Command();
        if(!responseCmd.parse(response))
            return false;

        if(responseCmd.getType() != requestCmd.getType() || responseCmd.getState() == Command.States.ERROR || responseCmd.getState() == Command.States.UNKNOWN)
            return false;
        return true;
    }

    public static void showLoginPage() {

    }

    public static boolean login(String firstName, String lastName,String psw) {
        //return executeCmd(Command.Cmds.LOGIN, firstName, lastName, psw);
        Command requestCmd = new Command();
        requestCmd.create(Command.Cmds.LOGIN, firstName, lastName, psw);

        session.send(requestCmd.toString());
        String response = session.receive();
        if (response.isEmpty()) {
            return false;
        }

        Command responseCmd = new Command();
        if(!responseCmd.parse(response))
            return false;

        if(responseCmd.getType() != requestCmd.getType() || responseCmd.getState() == Command.States.ERROR || responseCmd.getState() == Command.States.UNKNOWN)
            return false;
        return true;
    }

    public static boolean findUser(String userName) {
        Command requestCmd = new Command();
        requestCmd.create(Command.Cmds.FIND_USER, userName);

        session.send(requestCmd.toString());
        String response = session.receive();
        if(response.isEmpty()) {
            return false;
        }

        Command responseCmd = new Command();
        if(!responseCmd.parse(response))
            return false;

        if(responseCmd.getType() != requestCmd.getType() || responseCmd.getState() == Command.States.ERROR || responseCmd.getState() == Command.States.UNKNOWN)
            return false;
        return true;
    }

    public static ArrayList<String> getUsersList() {
        Command requestCmd = new Command();
        requestCmd.create(Command.Cmds.LIST_USERS);

        session.send(requestCmd.toString());
        String response = session.receive();
        if(response.isEmpty()) {
            return null;
        }

        Command responseCmd = new Command();
        if(!responseCmd.parse(response))
            return null;

        if(responseCmd.getType() != requestCmd.getType() || responseCmd.getState() == Command.States.ERROR || responseCmd.getState() == Command.States.UNKNOWN)
            return null;
        return responseCmd.getItems();
    }

    public static boolean send(String userName, String msg) {
        Command requestCmd = new Command();
        requestCmd.create(Command.Cmds.SEND, userName, msg);

        session.send(requestCmd.toString());
        String response = session.receive();
        if(response.isEmpty()) {
            return true;
        }

        Command responseCmd = new Command();
        if(!responseCmd.parse(response))
            return false;

        if(responseCmd.getType() != requestCmd.getType() || responseCmd.getState() == Command.States.ERROR || responseCmd.getState() == Command.States.UNKNOWN)
            return false;
        ArrayList<String> items = responseCmd.getItems();
        String received_msg = "";
        for(String item : items){
            received_msg += item;
        }
        return true;
    }

    private static String receive() {
        String response = session.nonBlockReceive();
        if (response.isEmpty()) {
            return "";
        }

        Command responseCmd = new Command();
        if (!responseCmd.parse(response))
            return null;

        if (responseCmd.getType() == Command.Cmds.RECEIVE){
            String str = "";
            ArrayList<String> items = responseCmd.getItems();
            for(String item : items)
                str += item;
            return str;
        }

            /*if (responseCmd.getType() == Command.Cmds.SEND){
                return "";*/
        return "";

    }

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 50000;
        session = new Session(ip, port);
        States state = States.MAIN_PAGE;
        Scanner scanner = new Scanner(System.in);

        while(session.connect()) {
            while (state == States.MAIN_PAGE) {
                currUser = "";
                System.out.println("Menu:");
                System.out.println("1.Registry user");
                System.out.println("2.Login");
                System.out.println("3.Exit");
                String cmd = scanner.nextLine();
                switch (cmd) {
                    case "1":
                        state = States.REGISTRY;
                        break;
                    case "2":
                        state = States.LOGIN;
                        break;
                    case "3":
                        state = States.EXIT;
                        break;
                    default:
                        System.out.println("Wrong command! Please try again.");
                        break;
                }
            }

            if (state == States.REGISTRY) {
                System.out.println("*** Registry user ***");
                System.out.println("Enter first name: ");
                String firstName = scanner.nextLine();
                System.out.println("Enter last name: ");
                String lastName = scanner.nextLine();
                System.out.println("Enter password: ");
                String psw = scanner.nextLine();
                System.out.println("Repeat password: ");
                String repeatPsw = scanner.nextLine();
                System.out.println("Enter date of birth (DD.MM.YYYY): ");
                String dateOfBirth = scanner.nextLine();
                System.out.println("Enter country: ");
                String country = scanner.nextLine();
                System.out.println("Enter sex (M/F): ");
                String sex = scanner.nextLine();


                if(registry(firstName, lastName, psw, repeatPsw, dateOfBirth, country, sex)) {
                    System.out.println("***" + firstName + " " + lastName + " registered successfully *** \n");
                    currUser = firstName + " " + lastName;
                    state = States.USER_PAGE;
                } else {
                    System.out.println("***" + firstName + " " + lastName + " was not registered *** \n");
                    state = States.MAIN_PAGE;
                }
            }

            while (state == States.LOGIN) {
                System.out.println("Enter first name: ");
                String firstName = scanner.nextLine();
                System.out.println("Enter last name: ");
                String lastName = scanner.nextLine();
                System.out.println("Enter password: ");
                String psw = scanner.nextLine();

                if (login(firstName, lastName, psw)) {
                    System.out.println("Login successfully");
                    state = States.USER_PAGE;
                    currUser = firstName + " " + lastName;
                } else {
                    System.out.println("Wrong command! Please try again or to go back enter 'back'.");
                    if (scanner.nextLine().equalsIgnoreCase("back")) {
                        state = States.MAIN_PAGE;
                    }
                }
            }

            while (state == States.USER_PAGE) {
                System.out.println("Menu:");
                System.out.println("1. Find user");
                System.out.println("2. List of users");
                System.out.println("3. Chat with user");
                System.out.println("4. Back");
                System.out.println("5. Exit");
                System.out.println("Enter command number:");
                switch (scanner.nextLine()) {
                    case "1":
                        state = States.FIND_USER;
                        break;
                    case "2":
                        state = States.LIST_USERS;
                        break;
                    case "3":
                        state = States.CHATTING;
                        break;
                    case "4":
                        state = States.MAIN_PAGE;
                        break;
                    case "5":
                        state = States.EXIT;
                        break;
                    default:
                        System.out.println("Wrong command! Please try again.");
                        break;
                }
                if (state == States.FIND_USER) {
                    System.out.println("Enter the user's first and last name: ");
                    String userName = scanner.nextLine();

                    if (findUser(userName)) {
                        System.out.println("User was found!");
                        state = States.USER_PAGE;
                    } else {
                        System.out.println("User not found! To go back enter 'back'.");
                        if (scanner.nextLine().equalsIgnoreCase("back")) {
                            state = States.USER_PAGE;
                        }
                    }
                }

                if (state == States.LIST_USERS) {
                    ArrayList<String> usersList = getUsersList();
                    if(usersList.isEmpty()) {
                        System.out.println("Users not found!");
                    } else {
                        System.out.println("The next user was found: ");
                        for (String user : usersList) {
                            System.out.println(user);
                        }
                    }
                    state = States.USER_PAGE;
                }

                if (state == States.CHATTING) {
                    System.out.println("Choose the user for chatting");
                    System.out.println("Enter the user's first and last name: ");
                    String userName = scanner.nextLine();
                    String msg = "";
                    String msgFromConsole = "";

                    Chatting chattingThread = new Chatting(currUser, userName);
                    chattingThread.start();

                    while((msg = receive()) != null && chattingThread.isAlive()){
                        if(!msg.isEmpty())
                            System.out.println("\n" + userName + ": " + msg);
                    }
                    state = States.USER_PAGE;
                }
            }


            /*System.out.println("*** Find user ***");
            System.out.println("Enter username: Kate");
            System.out.println("*** Kate was found *** \n");
            System.out.println("Menu:");
            System.out.println("1. Find user");
            System.out.println("2. List of users");
            System.out.println("3. Chat with user");
            System.out.println("4. Log out");
            System.out.println("Enter command number: 2 \n");
            System.out.println("*** List of users ***");
            System.out.println("| User name      | Status |");
            System.out.println("| Kate           | Online |");
            System.out.println("| Ira            | Offline|");
            System.out.println("*** End list of users *** \n");
            System.out.println("Menu:");
            System.out.println("1. Find user");
            System.out.println("2. List of users");
            System.out.println("3. Chat with user");
            System.out.println("4. Log out");
            System.out.println("Enter command number: 3 \n");
            System.out.println("*** Chat with user ***");
            System.out.println("Enter username: Kate");
            System.out.println("For exit press \\q");
            System.out.println("*** Chatting with Kate is started *** \n");
            System.out.println("Dan: Hello         10:16:53 12.01.2021");
            System.out.println("Dan: This is Dan   10:16:59 12.01.2021");
            System.out.println("Kate: Hello Dan!   10:17:59 12.01.2021");
            System.out.println("Dan: Bye!          10:18:59 12.01.2021");
            System.out.println("Dan: \\q");
            System.out.println("");
            System.out.println("Menu:");
            System.out.println("1. Find user");
            System.out.println("2. List of users");
            System.out.println("3. Chat with user");
            System.out.println("4. Log out");
            System.out.println("Enter command number: 4");
            System.out.println("*** Logout successfully *** \n");
            System.out.println("Menu:");
            System.out.println("1.Registry user");
            System.out.println("2.Login");
            System.out.println("For exit press \\q");
            System.out.println("\\q");
            System.out.println("*** Bye ***");*/
        /*} else {
            System.out.println("Cannot connect to server " + ip + ":" + port);*/
        }
    }
}