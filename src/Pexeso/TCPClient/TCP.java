package Pexeso.TCPClient;

import Pexeso.Controller.LoginController;
import Pexeso.Main;
import Pexeso.Thread.ClientListener;
import javafx.fxml.FXML;

import java.net.*;
import java.io.*;

public class TCP {
    private Socket socket;
    private InetAddress serverIP;
    private int serverPort;

    public TCP(InetAddress IP, int Port){
        this.serverIP = IP;
        this.serverPort = Port;
    }



    public boolean connect(InetAddress IP, int Port){
        try {
            socket = new Socket(IP, Port);
            return true;
        }
        catch (IOException e) {
            LoginController controller = Main.FXMLLOADER_LOGIN.getController();
            controller.setStatusText("Připojení k serveru " + IP + ":" + Port + " se nezdařilo", 3000);
            return false;
        }
        catch (IllegalArgumentException e){
            LoginController controller = Main.FXMLLOADER_LOGIN.getController();
            controller.setStatusText("Nesprávné číslo portu: číslo v rozmezí 0 - 65535", 3000);
            return false;
        }
        catch (NullPointerException e){
            LoginController controller = Main.FXMLLOADER_LOGIN.getController();
            controller.setStatusText("Hostitelský server nerozpoznán", 3000);
            return false;
        }
    }

    public void disconnect(){
        try {
            sendMsg(MsgTables.getType(MsgTypes.C_LOGOUT) + "#");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int loginUser(String name) {
        String connString = MsgTables.getType(MsgTypes.C_LOGIN) + ":" + name + "#";
        sendMsg(connString);
        return 0;
    }

    @FXML
    public void getRoomsTable(){
        String connString = MsgTables.getType(MsgTypes.C_GET_TABLE) + "#";
        sendMsg(connString);
    }

    public void joinRoom(int roomId){
        String connString = MsgTables.getType(MsgTypes.C_JOIN_ROOM) + ":" + roomId + "#";
        sendMsg(connString);
    }

    public void getRoomUsers(String roomId){
        String connString = MsgTables.getType(MsgTypes.C_ROOM_USERS) + ":" + roomId + "#";
        sendMsg(connString);
    }

    public void leaveRoom(int roomId){
        String connString = MsgTables.getType(MsgTypes.C_LEAVE_ROOM) + ":" + roomId + "#";
        sendMsg(connString);
    }

    public void userReady(String roomId, boolean ready){
        if(ready) {
            String connString = MsgTables.getType(MsgTypes.C_USR_READY) + ":" + roomId + "#";
            sendMsg(connString);
        }
        else {
            String connString = MsgTables.getType(MsgTypes.C_USR_NREADY) + ":" + roomId + "#";
            sendMsg(connString);
        }
    }

    public void sendChatMsg(String roomId, String msg){
        String connString = MsgTables.getType(MsgTypes.C_CHAT) + ":" + roomId + ":" + msg + "#";
        sendMsg(connString);
    }

    public void sendMsg(String data) {
        try {
            if (socket != null) {
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.write(data.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMsg() {
        try {
            if (socket != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg;
                if((msg = br.readLine()) != null){
                    return msg;
                }
                else {
                    br.close();
                    return null;
                }

            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public void getConnectedUsers(){

    }

    public Socket getSocket() {
        return socket;
    }
}
