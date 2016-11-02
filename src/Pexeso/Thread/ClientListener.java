package Pexeso.Thread;

import Pexeso.Controller.GameController;
import Pexeso.Controller.LoginController;
import Pexeso.Controller.ServerLobbyController;
import Pexeso.Main;
import Pexeso.TCPClient.MsgTables;
import Pexeso.TCPClient.MsgTypes;
import Pexeso.TCPClient.TCP;

import java.io.*;

import static java.lang.Thread.sleep;

public class ClientListener implements Runnable{
    private TCP tcpInfo;
    //private DataInputStream dataInputStream;
    private LoginController loginController;
    private ServerLobbyController serverLobbyController;
    private GameController gameController;
    private boolean ClientListenerRunning = true;

    public ClientListener(TCP tcpInfo) {
        this.tcpInfo = tcpInfo;
        Main.tcpi = tcpInfo;
        loginController = Main.FXMLLOADER_LOGIN.getController();
        //gameController = Main.FXMLLOADER_GAME.getController();
    }

    @Override
    public void run() {
        if(!ClientListenerRunning){
            if (tcpInfo != null) {
                tcpInfo.disconnect();
            }
        }
        while (ClientListenerRunning) {
            try {
                String message = tcpInfo.receiveMsg();
                //sleep(100);
                if (message != null && !message.equals("")) {
                    processMessage(message);
                }
            }
            catch (Exception ex){
                tcpInfo.disconnect();
                ex.printStackTrace();
                //TODO
            }
        }
    }

    private void processMessage(String message) {
        message = message.replace("#", "");
        String[] splittedMsg = message.split(":");

        switch (splittedMsg[0]) {
            case "S_LOGGED":
                loginController.setLobbyUi();
                //gameController.setLobbyStatus("Přihlášen na server jako uživatel " + splittedMsg[1]);
                break;
            case "S_NAME_EXISTS":
                loginController.setStatusText("Uživatel se jménem " + splittedMsg[1] + " již existuje");
                break;
            case "S_SERVER_FULL":
                loginController.setStatusText("Server je plný");
                break;
            case "S_USR_TBL":
                break;
            case "S_ROOM_INFO":
                serverLobbyController = Main.FXMLLOADER_SERVERLOBBY.getController();
                serverLobbyController.updateTableRow(splittedMsg[1], splittedMsg[2], splittedMsg[3], splittedMsg[4], splittedMsg[5]);
                String connString = MsgTables.getType(MsgTypes.C_ROW_UPDATE) + ":" + splittedMsg[1] + "#";
                tcpInfo.sendMsg(connString);
                break;
            case "S_USR_JOINED":
                try {
                    serverLobbyController = Main.FXMLLOADER_SERVERLOBBY.getController();
                    serverLobbyController.setGameLobbyScene();
                } catch(IOException e){}
        }
    }
}

