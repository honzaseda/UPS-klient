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
            case C_LEAVE_ROOM:
                return "C_LEAVE_ROOM";
            case C_USR_READY:
                return "C_USR_READY";
            case C_USR_NREADY:
                return "C_USR_NREADY";
            case C_ROW_UPDATE:
                return "C_ROW_UPDATE";
            case C_CHAT:
                return "C_CHAT";
            default:
                return "";
        }
    }

    public static String resolveRoomStatus(String roomStatus){
        switch (roomStatus) {
            case "ROOM_WAIT":
                return "Čeká se na hráče";
            case "ROOM_READY":
                return "Místnost plná, čeká na spuštění";
            case "ROOM_PLAYING":
                return "Hra probíhá";
            default:
                return "";
        }
    }
}


