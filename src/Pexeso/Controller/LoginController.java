package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.Thread.ClientListener;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import Pexeso.TCPClient.TCP;
import javafx.stage.WindowEvent;

import java.net.InetAddress;
import java.net.URL;
import java.rmi.server.ExportException;
import java.util.ResourceBundle;

import static java.lang.Thread.currentThread;
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

    private TCP tcp;
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

    public void setGameUi() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage loginStage = (Stage) loginPane.getScene().getWindow();
                    loginStage.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pexeso/Stage/Game.fxml"));
                    Parent gameRoot = fxmlLoader.load();
                    Stage gameStage = new Stage();
                    gameStage.setScene(new Scene(gameRoot, 1024, 768));
                    gameStage.setTitle("Čupr Pexeso - game");
                    gameStage.setResizable(false);
                    gameStage.show();
                    Main.FXMLLOADER_GAME = fxmlLoader;


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

    public static LoginController getController() {
        return LoginController.getController();
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
