package Pexeso.Thread;

import Pexeso.Controller.GameController;
import Pexeso.Controller.GameLobbyController;
import Pexeso.Controller.LoginController;
import Pexeso.Controller.ServerLobbyController;
import Pexeso.Main;
import Pexeso.TCPClient.ClientInfo;
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
    private GameLobbyController gameLobbyController;
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
                Main.clientInfo = new ClientInfo(splittedMsg[1], -1);
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
                    serverLobbyController.setGameLobbyScene(splittedMsg[1], splittedMsg[2], splittedMsg[3], splittedMsg[4]);
                    Main.clientInfo.setActiveRoom(Integer.parseInt(splittedMsg[1]));
                } catch(IOException e){
                    //TODO
                }
                break;
            case "S_JOIN_ERR":
                serverLobbyController = Main.FXMLLOADER_SERVERLOBBY.getController();
                serverLobbyController.setStatusText("Připojení k místnosti " + splittedMsg[1] + " se nezdařilo", true);
                break;
            case "S_USR_READY":
                gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
                gameLobbyController.setReadyBtn();
                break;
            case "S_USR_NREADY":
                gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
                gameLobbyController.unsetReadyBtn();
                break;
            case "S_ROOM_READY":
                try {
                    gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
                    gameLobbyController.setGameScene();
                } catch(IOException e){
                    //TODO
                }
        }
    }
}

