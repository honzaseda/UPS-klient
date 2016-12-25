package Pexeso.TCPClient;

public enum MsgTypes {
    C_LOGIN, //přihlášení uživatele
    C_LOGOUT, //odhlášení uživatele
    C_GET_TABLE, //zažádání o seznam místostí
    C_JOIN_ROOM, //připojení do místosti
    C_ROOM_USERS,
    C_LEAVE_ROOM,
    C_USR_READY,
    C_USR_NREADY,
    C_ROW_UPDATE, //potvrzení o přijetí zprávy s info o místosti
    C_USER_UPDATE,
    C_CHAT,
    C_TURN_CARD,
    C_TURN_ACK,
    NO_CODE
}