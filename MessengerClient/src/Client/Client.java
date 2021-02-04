package Client;
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

    /*public static commands getCmdFromPage1() {
        while (true) {
            System.out.println("Menu:");
            System.out.println("1.Registry user");
            System.out.println("2.Login");
            System.out.println("For exit press \\q");
            Scanner scanner = new Scanner(System.in);
            String cmd = scanner.nextLine();
            switch (cmd) {
                case "1":
                    return commands.registry;
                case "2":
                    return commands.login;
                case "\\q":
                    return commands.quit;
                default:
                    break;
            }
        }
    }*/

    /*public static void showRegistryPage() {
        System.out.println("*** Registry user ***");
        System.out.println("Enter first name: ");
        Scanner scanner = new Scanner(System.in);
        String firstName = scanner.nextLine();
        System.out.println("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.println("Enter date of birth (DD.MM.YYYY): ");
        String dateOfBirth = scanner.nextLine();
        System.out.println("Enter country: ");
        String country = scanner.nextLine();
        System.out.println("Enter sex (M/F): ");
        String sex = scanner.nextLine();
        System.out.println("Enter password: ");
        String psw = scanner.nextLine();
        System.out.println("Repeat password: ");
        String repeatPsw = scanner.nextLine();

        if(registry(firstName, lastName, dateOfBirth, country, sex, psw, repeatPsw)) {
            System.out.println("***" + firstName + " " + lastName + " registered successfully *** \n");
        } else {
            System.out.println("***" + firstName + " " + lastName + " was not registered *** \n");
        }
    }*/


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

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 50000;
        session = new Session(ip, port);
        States state = States.MAIN_PAGE;
        Scanner scanner = new Scanner(System.in);

        while(session.connect()) {
            while (state == States.MAIN_PAGE) {
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