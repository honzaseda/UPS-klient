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
                if (message != null && !message.equals("")) {
                    processMessage(message);
                }
                else {
                    LoginController l = Main.FXMLLOADER_LOGIN.getController();
                    ClientListenerRunning = false;
                    l.setDiscLoginUi();
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
                loginController.setStatusText("Uživatel se jménem " + splittedMsg[1] + " již existuje", 3000);
                break;
            case "S_SERVER_FULL":
                loginController.setStatusText("Server je plný", 3000);
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
                gameLobbyController.updateUserReadyUi(Integer.parseInt(splittedMsg[2]), true);
                if(splittedMsg[1].equals("1")) {
                    gameLobbyController.setReadyBtn();
                }
                break;
            case "S_USR_NREADY":
                gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
                gameLobbyController.updateUserReadyUi(Integer.parseInt(splittedMsg[2]), false);
                if(splittedMsg[1].equals("1")) {
                    gameLobbyController.unsetReadyBtn();
                }
                break;
            case "S_ROOM_READY":
                try {
                    gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
                    gameLobbyController.appendSrvrMsg("Všichni hráči připraveni, hra začíná!");
                    gameLobbyController.setGameScene();
                } catch(IOException e){
                    //TODO
                }
                break;
            case "S_CHAT_USR":
                if(splittedMsg[3].equals("0")) {
                    gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
                    gameLobbyController.appendUsrMsg(splittedMsg[1], splittedMsg[2]);
                    break;
                }
                else if(splittedMsg[3].equals("1")){
                    gameController = Main.FXMLLOADER_GAME.getController();
                    gameController.appendUsrMsg(splittedMsg[1], splittedMsg[2]);
                    break;
                }
            case "S_ROOM_USER_INFO":
                gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
                gameLobbyController.addNewUserUi(Integer.parseInt(splittedMsg[1]), splittedMsg[2], "0");
                String connString2 = MsgTables.getType(MsgTypes.C_USER_UPDATE) + ":" + splittedMsg[1] + "#";
                tcpInfo.sendMsg(connString2);
                break;
            case "S_ROOM_UPDATE":
                gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
                gameLobbyController.updateRoomUi(splittedMsg[1], splittedMsg[2]);
                if(splittedMsg[3].equals("1")){
                    gameLobbyController.addNewUserUi(Integer.parseInt(splittedMsg[4]), splittedMsg[5], "0");
                    gameLobbyController.appendSrvrMsg("Hráč " + splittedMsg[5] + " se připojil.");
                }
                if(splittedMsg[3].equals("0")){
                    gameLobbyController.removeUserUi(Integer.parseInt(splittedMsg[4]));
                    gameLobbyController.appendSrvrMsg("Hráč " + splittedMsg[5] + " odešel z místnosti.");
                }
                break;
            case "S_TURNED":
                gameController = Main.FXMLLOADER_GAME.getController();
                gameController.flipCard(Integer.parseInt(splittedMsg[1]), Integer.parseInt(splittedMsg[2]), Integer.parseInt(splittedMsg[3]));
                break;
            case "S_ON_TURN":
                gameController = Main.FXMLLOADER_GAME.getController();
                gameController.updateOnTurn();
                break;
            case "S_TIME":
                gameController = Main.FXMLLOADER_GAME.getController();
                gameController.updateTurnWait();
                break;
        }
    }
}

