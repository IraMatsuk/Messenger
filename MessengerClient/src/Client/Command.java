package Client;

import java.util.ArrayList;

public class Command {
    enum Cmds {
        UNKNOWN,
        ERROR,
        REGISTRY,
        LOGIN,
        FIND_USER,
        LIST_USERS,
        SEND,
        RECEIVE,
    }
    enum States{
        UNKNOWN,
        OK,
        ERROR
    }
    private final String registryCmdStr = "<registry>";
    private final String loginCmdStr = "<login>";
    private final String findUserCmdStr = "<findUser>";
    private final String listUsersCmdStr = "<usersList>";
    private final String errorCmdStr = "<error>";
    private final String sendCmdStr = "<sendMsg>";
    private final String receiveCmdStr = "<receiveMsg>";
    private String cmdStr = "";
    private Cmds type = Cmds.UNKNOWN;
    private States state = States.UNKNOWN;
    private ArrayList<String> items = new ArrayList<>();

    public boolean create(Cmds cmd, String... args) {
        switch (cmd) {
            case REGISTRY:
                type = cmd;
                cmdStr += registryCmdStr;
                break;
            case LOGIN:
                type = cmd;
                cmdStr += loginCmdStr;
                break;
            case FIND_USER:
                type = cmd;
                cmdStr += findUserCmdStr;
                break;
            case LIST_USERS:
                type = cmd;
                cmdStr += listUsersCmdStr;
                break;
            case SEND:
                type = cmd;
                cmdStr += sendCmdStr;
                break;
            case RECEIVE:
                type = cmd;
                cmdStr += receiveCmdStr;
                break;
            case ERROR:
                type = cmd;
                cmdStr += errorCmdStr;
                break;

        }
        if(!cmdStr.isEmpty()) {
            for (String item : args) {
                cmdStr += "_" + item;
            }
            return true;
        } else{
            // unknown cmd
            return false;
        }
    }

    public String toString() {
        return cmdStr;
    }

    public boolean parse(String cmd){
        String [] cmdItems = cmd.split("_");
        String cmdTypeStr = cmdItems[0];

        switch (cmdTypeStr) {
            case registryCmdStr:
                type = Cmds.REGISTRY;
                break;
            case loginCmdStr:
                type = Cmds.LOGIN;
                break;
            case findUserCmdStr:
                type = Cmds.FIND_USER;
                break;
            case listUsersCmdStr:
                type = Cmds.LIST_USERS;
                break;
            case sendCmdStr:
                type = Cmds.SEND;
                break;
            case receiveCmdStr:
                type = Cmds.RECEIVE;
                break;
            case errorCmdStr:
                type = Cmds.ERROR;
                break;
            default:
                return false;
        }

        String cmdSuccessed = cmdTypeStr + "_ok";
        String cmdFailed = cmdTypeStr + "_error_";

        if(cmd.startsWith(cmdSuccessed)) {
            cmdStr = cmd;
            state = States.OK;
            for (int i = 2; i < cmdItems.length; ++i) {
                items.add(cmdItems[i]);
            }
            return true;
        } else if(cmd.startsWith(cmdFailed)){
            cmdStr = cmd;
            state = States.ERROR;
            return true;
        } else {
            return false;
        }
    }

    public Cmds getType() {
        return type;
    }

    public States getState() {
        return state;
    }

    public ArrayList<String> getItems(){
        return items;
    }
}
