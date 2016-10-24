package Pexeso.Thread;

import Pexeso.Controller.LoginController;
import Pexeso.Main;
import Pexeso.TCPClient.TCP;

import java.io.*;

import static java.lang.Thread.sleep;

public class ClientListener implements Runnable{
    private TCP tcpInfo;
    //private DataInputStream dataInputStream;
    private LoginController loginController;
    private boolean ClientListenerRunning = true;

    public ClientListener(TCP tcpInfo) {
        this.tcpInfo = tcpInfo;
        loginController = Main.FXMLLOADER_LOGIN.getController();
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
                sleep(100);
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
                loginController.setGameUi();
                break;
            case "S_NAME_EXISTS":
                loginController.setStatusText("Uživatel se jménem " + splittedMsg[1] + " již existuje");
                break;
            case "S_SERVER_FULL":
                loginController.setStatusText("Server je plný");
                break;
            case "S_USR_TBL":

        }
    }
}

