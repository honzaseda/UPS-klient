package Pexeso.TCPClient;

public class MsgTables {
    public static String getType(MsgTypes msgType) {
        switch (msgType) {
            case C_LOGIN:
                return "C_LOGIN";
            case C_LOGOUT:
                return "C_LOGOUT";
            case C_GET_TABLE:
                return "C_GET_TABLE";
            case C_JOIN_ROOM:
                return "C_JOIN_ROOM";
            default:
                return "";
        }
    }
}


