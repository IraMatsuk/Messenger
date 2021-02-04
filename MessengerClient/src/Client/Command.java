package Client;

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
            case ERROR:
                type = cmd;
                cmdStr += errorCmdStr;
                break;
        }
        if(!cmdStr.equalsIgnoreCase("")) {
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
            case errorCmdStr:
                type = Cmds.ERROR;
                break;
            default:
                return false;
        }

        String cmdSuccessed = cmdTypeStr + "_ok";
        String cmdFailed = cmdTypeStr + "_error_";

        if(cmd.equalsIgnoreCase(cmdSuccessed)) {
            cmdStr = cmd;
            state = States.OK;
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
}
