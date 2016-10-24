package Pexeso.TCPClient;

import Pexeso.Controller.LoginController;
import Pexeso.Main;

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
            controller.setStatusText("Připojení k serveru " + IP + ":" + Port + " odepřeno");
            return false;
        }
        catch (IllegalArgumentException e){
            LoginController controller = Main.FXMLLOADER_LOGIN.getController();
            controller.setStatusText("Nesprávné číslo portu: číslo v rozmezí 0 - 65535");
            return false;
        }
        catch (NullPointerException e){
            LoginController controller = Main.FXMLLOADER_LOGIN.getController();
            controller.setStatusText("Hostitelský server nerozpoznán");
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

    private void sendMsg(String data) {
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
                return br.readLine();
            } else {
                return "chybná zpráva#";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void getConnectedUsers(){

    }

    public Socket getSocket() {
        return socket;
    }
}
