package Pexeso.TCPClient;

public enum MsgTypes {
        C_LOGIN, //přihlášení uživatele
        C_LOGOUT, //odhlášení uživatele
        C_GET_TABLE, //zažádání o seznam místostí
        C_JOIN_ROOM, //připojení do místosti
        C_ROW_UPDATE, //potvrzení o přijetí zprávy s info o místosti
        NO_CODE
}