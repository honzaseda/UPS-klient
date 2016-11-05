package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.TCP;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable{
    @FXML
    public GridPane gamePane;

    //Game, Deck UI
    @FXML
    public ImageView i00, i01, i02, i03, i04, i10, i11, i12, i13, i14,  i20, i21, i22, i23, i24,  i30, i31, i32, i33, i34,  i40, i41, i42, i43, i44;
    @FXML
    public Text statusText;
    private static final Image cardBackImg = new Image("/Pexeso/Public/Img/card-back.png");

    //Chatting UI
    @FXML
    private TextArea chatWindow;
    @FXML
    private TextField chatMsg;
    @FXML
    private Button sendChatMsg;

    public TCP tcpConn;
    private String thisRoomId;
    private String newLine = "\n\r";

    private int deckRows = 5, deckCols = 5;
    private ImageView[][] deck;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
        chatWindow.setEditable(false);
    }

    public void setThisRoomId(String id){
        thisRoomId = id;
    }

    public void initChatWindow(String oldMsg){
        chatWindow.setText(oldMsg);;
    }

    public void setServerLobbyScene() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage gameStage = (Stage) gamePane.getScene().getWindow();
                    gameStage.close();

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

    public void setCardDeck(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                deck = new ImageView[][] {{i00, i01, i02, i03, i04},{i10, i11, i12, i13, i14},{i20, i21, i22, i23, i24},{i30, i31, i32, i33, i34},{i40, i41, i42, i43, i44}};
                //i11.setImage(cardBackImg);
                for(int i = 0; i < deckRows; i++){
                    for(int j = 0; j < deckCols; j++){
                        deck[i][j].setImage(cardBackImg);
                    }
                }

            }
        });

    }

    public void sendNewMsg(){
        String msg = chatMsg.getText();
        if(!msg.equals("") && msg.length() < 64) {
            tcpConn.sendChatMsg(thisRoomId, msg);
        }
        chatMsg.clear();
    }

    public void appendUsrMsg(final String userName, final String msg){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.appendText(userName + ": " + msg + newLine);
            }
        });
    }

    public void appendSrvrMsg(final String msg){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.appendText("-- Server -- " + msg + newLine);
            }
        });
    }

}
