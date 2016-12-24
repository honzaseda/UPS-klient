package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.TCP;
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
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.InetAddress;

import static java.lang.Thread.sleep;

public class LoginController {
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
            setStatusText("Neplatný server/port", 3000);
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
                    Parent serverLobbyRoot = fxmlLoader.load();
                    final Stage serverLobbyStage = new Stage();
                    serverLobbyStage.setScene(new Scene(serverLobbyRoot, 1024, 768));
                    serverLobbyStage.setTitle("Čupr Pexeso - Server Lobby");
                    serverLobbyStage.getIcons().add(new Image("Pexeso/Public/Img/icon.png"));
                    serverLobbyStage.setResizable(false);
                    serverLobbyStage.show();
                    Main.parentWindow = serverLobbyStage;
                    Main.FXMLLOADER_SERVERLOBBY = fxmlLoader;

                    ServerLobbyController s = Main.FXMLLOADER_SERVERLOBBY.getController();
                    s.refreshTable();
                    s.setStatusText("Přihlášen na server", false);

                    serverLobbyStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
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

    public void setDiscLoginUi() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Main.parentWindow.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pexeso/Stage/Login.fxml"));
                    Parent loginRoot = fxmlLoader.load();
                    final Stage loginStage = new Stage();
                    loginStage.setTitle("Čupr Pexeso - Login");
                    loginStage.setScene(new Scene(loginRoot, 350, 440));
                    loginStage.getIcons().add(new Image("Pexeso/Public/Img/icon.png"));
                    loginStage.setResizable(false);
                    loginStage.show();
                    Main.FXMLLOADER_LOGIN = fxmlLoader;
                    Main.parentWindow = loginStage;

                    LoginController l = Main.FXMLLOADER_LOGIN.getController();
                    l.setStatusText("Spojení se serverem bylo přerušeno", 8000);

                    loginStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setStatusText(final String text, final int duration) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
                statusTextVBox.setAlignment(Pos.CENTER);
                statusText.setTextAlignment(TextAlignment.CENTER);
                Thread timedText = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(duration);
                            statusText.setText("");
                        } catch (InterruptedException e) {
                        }
                    }
                };
                timedText.start();
            }
        });
    }


}
