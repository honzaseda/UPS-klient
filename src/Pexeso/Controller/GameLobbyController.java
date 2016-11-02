package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.TCP;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by seda on 28/10/16.
 */
public class GameLobbyController implements Initializable {
    public TCP tcpConn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
    }

    public void setReady(){

    }

    public void leaveLobby() throws IOException{
        setServerLobbyScene();

    }

    public void setServerLobbyScene() throws IOException {
        Parent window;
        window = FXMLLoader.load(getClass().getResource("/Pexeso/Stage/ServerLobby.fxml"));

        Stage activeStage;
        activeStage = Main.parentWindow;
        activeStage.getScene().setRoot(window);
        activeStage.setTitle("Čupr Pexeso - Server Lobby");

        //ServerLobbyController slc = Main.FXMLLOADER_SERVERLOBBY.getController();
        //slc.initialize();

    }
}
