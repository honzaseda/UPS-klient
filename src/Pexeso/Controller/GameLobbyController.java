package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.MsgTables;
import Pexeso.TCPClient.TCP;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by seda on 28/10/16.
 */
public class GameLobbyController implements Initializable {
    public TCP tcpConn;

    @FXML
    private GridPane gameLobbyPane;
    @FXML
    public Text roomNameText, numPlayingText, maxPlayingText, roomStatusText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
    }

    public void setReady(){

    }

    public void leaveLobby() throws IOException{
        tcpConn.leaveRoom(Main.clientInfo.getActiveRoom());
        setServerLobbyScene();

    }

    public void setServerLobbyScene() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage gameLobbyStage = (Stage) gameLobbyPane.getScene().getWindow();
                    gameLobbyStage.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pexeso/Stage/ServerLobby.fxml"));
                    Parent serverLobbyRoot = fxmlLoader.load();
                    Stage serverLobbyStage = new Stage();
                    serverLobbyStage.setScene(new Scene(serverLobbyRoot, 1024, 768));
                    serverLobbyStage.setTitle("Čupr Pexeso - Server Lobby");
                    serverLobbyStage.setResizable(false);
                    serverLobbyStage.show();
                    Main.FXMLLOADER_SERVERLOBBY = fxmlLoader;

                    ServerLobbyController s = Main.FXMLLOADER_SERVERLOBBY.getController();
                    s.refreshTable();

                    serverLobbyStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            Main.tcpi.disconnect();
                            System.exit(0);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setRoomInfo(final String roomId, final String numPlaying, final String maxPlaying, final String roomStatus){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                roomNameText.setText("Herní místnost " + roomId);
                numPlayingText.setText(numPlaying);
                maxPlayingText.setText(maxPlaying);
                roomStatusText.setText(MsgTables.resolveRoomStatus(roomStatus));
            }
        });
    }
}
