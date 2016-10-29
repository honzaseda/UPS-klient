package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.Thread.ClientListener;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import Pexeso.TCPClient.TCP;
import javafx.stage.WindowEvent;

import java.net.InetAddress;

import static java.lang.Thread.sleep;

public class LoginController{
    @FXML
    public GridPane loginPane;
    @FXML
    public GridPane gamePane;
    @FXML
    public TextField ipField;
    @FXML
    public TextField portField;
    @FXML
    public TextField nickField;
    @FXML
    public Button loginBtn;
    @FXML
    public VBox statusTextVBox;
    @FXML
    public Text statusText;

    public TCP tcp;
    private ClientListener clientListener;

    public void attemptLogin() {
        statusText.setText("");
        try {
            InetAddress inetAddress = InetAddress.getByName(ipField.getText());
            int port = Integer.parseInt(portField.getText());
            tcp = new TCP(inetAddress, port);
            if (tcp.connect(inetAddress, port)) {
                clientListener = new ClientListener(tcp);
                Thread thread = new Thread(clientListener);
                thread.start();
                sleep(100);
                tcp.loginUser(nickField.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLobbyUi() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage loginStage = (Stage) loginPane.getScene().getWindow();
                    loginStage.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pexeso/Stage/ServerLobby.fxml"));
                    Parent gameRoot = fxmlLoader.load();
                    Stage gameStage = new Stage();
                    gameStage.setScene(new Scene(gameRoot, 1024, 768));
                    gameStage.setTitle("Čupr Pexeso - Server Lobby");
                    gameStage.setResizable(false);
                    gameStage.show();
                    Main.parentWindow = gameStage;
                    Main.FXMLLOADER_SERVERLOBBY = fxmlLoader;
                    ServerLobbyController serverLobbyController = Main.FXMLLOADER_SERVERLOBBY.getController();

                    gameStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                        @Override
                        public void handle(WindowEvent event) {
                                    //currentThread().interrupt();
                                    tcp.disconnect();
                                    System.exit(0);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setStatusText(final String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
                statusTextVBox.setAlignment(Pos.CENTER);
                statusText.setTextAlignment(TextAlignment.CENTER);
            }
        });
    }


}
