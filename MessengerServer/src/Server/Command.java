package Server;

import java.util.ArrayList;

public class Command {
    enum Cmds {
        UNKNOWN,
        ERROR,
        REGISTRY,
        LOGIN
    }
    enum States{
        UNKNOWN,
        OK,
        ERROR
    }
    private final String registryCmdStr = "<registry>";
    private final String loginCmdStr = "<login>";
    private final String errorCmdStr = "<error>";
    private String cmdStr = "";
    private Cmds type = Cmds.UNKNOWN;
    private States state = States.UNKNOWN;
    private ArrayList<String> items = new ArrayList<>();

    public boolean create(Cmds cmd, States state, String... args) {
        switch (cmd) {
            case REGISTRY:
                type = cmd;
                cmdStr += registryCmdStr;
                break;
            case LOGIN:
                type = cmd;
                cmdStr += loginCmdStr;
                break;
            case ERROR:
                type = cmd;
                cmdStr += errorCmdStr;
                break;
            default:
                return false;
        }

        switch (state) {
            case OK:
                this.state = state;
                cmdStr += "_ok";
                break;
            case ERROR:
                type = cmd;
                cmdStr += "_error";
                break;
            default:
                return false;
        }

        if(!cmdStr.isEmpty()) {
            return true;
        } else{
            // unknown cmd
            return false;
        }
    }

    public boolean parse(String cmd){
        String [] itemsStr = cmd.split("_");
        String cmdTypeStr = itemsStr[0];

        switch (cmdTypeStr) {
            case registryCmdStr:
                type = Cmds.REGISTRY;
                break;
            case loginCmdStr:
                type = Cmds.LOGIN;
                break;
            case errorCmdStr:
                type = Cmds.ERROR;
                break;
            default:
                return false;
        }

        cmdStr = cmd;
        for(String item : itemsStr)
            items.add(item);
        return true;
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

    public String toString(){
        return cmdStr;
    }
}
