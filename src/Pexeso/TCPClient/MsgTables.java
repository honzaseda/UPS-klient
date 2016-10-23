package Pexeso.TCPClient;

public class MsgTables {
    public static String getType(MsgTypes msgType) {
        switch (msgType) {
            case C_LOGIN:
                return "C_LOGIN";
            case C_LOGOUT:
                return "C_LOGOUT";
            default:
                return "";
        }
    }
}


