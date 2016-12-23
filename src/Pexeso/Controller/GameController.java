package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.TCP;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable{
    @FXML
    public GridPane gamePane;

    //Game, Deck UI
    @FXML
    public ImageView i00, i01, i02, i03, i04, i10, i11, i12, i13, i14,  i20, i21, i22, i23, i24,  i30, i31, i32, i33, i34;
    @FXML
    public Text statusText;
    @FXML
    public Text turnIndicator;
    @FXML
    public ProgressBar timeIndicator;
    private static final Image cardBackImg = new Image("/Pexeso/Public/Img/card-back.png");

    //Chatting UI
    @FXML
    private TextArea chatWindow;
    @FXML
    private TextField chatMsg;
    @FXML
    private Button sendChatMsg;

    //Joined Users UI
    @FXML
    private VBox vboxU0, vboxU1, vboxU2, vboxU3;

    public TCP tcpConn;
    private String thisRoomId;
    private String newLine = "\n\r";

    private int deckRows = 4, deckCols = 5;
    private ImageView[][] deck;

    private static final Integer STARTTIME   = 15;

    private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME * 100);

    private Timeline timeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
        chatWindow.setEditable(false);
        chatMsg.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    sendNewMsg();
                }
            }
        });
    }

    public void setThisRoomId(String id){
        thisRoomId = id;
    }

    public void initChatWindow(String oldMsg){
        chatWindow.setText(oldMsg);
    }

    @FXML
    public void addNewUserUi(final int userIndex, final String name, final String score, final String isOnTurn) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Text userName = new Text();
                userName.setText(name.toUpperCase());
                userName.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                Text userScore = new Text();
                userScore.setText("Skóre: " + score);
                Text onTurn = new Text();
                onTurn.setText(isOnTurn);
                onTurn.setFill(Color.rgb(33, 150, 243, .99));
                onTurn.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

                VBox vbox = new VBox();
                switch (userIndex) {
                    case 0:
                        vbox = vboxU0;
                        break;
                    case 1:
                        vbox = vboxU1;
                        break;
                    case 2:
                        vbox = vboxU2;
                        break;
                    case 3:
                        vbox = vboxU3;
                        break;
                }
                vbox.getChildren().add(0, userName);
                vbox.getChildren().add(1, userScore);
                vbox.getChildren().add(2, onTurn);
                vbox.setAlignment(Pos.CENTER_RIGHT);
                vbox.setSpacing(5);
            }
        });
    }

    public void updateOnTurn(){
        timeIndicator.setVisible(true);
        timeIndicator.progressProperty().bind(timeSeconds.divide(STARTTIME * 100.0).subtract(1).multiply(-1));
        //TODO: update in right side panel graphics
        turnIndicator.setText("Jsi na tahu!");
        if (timeline != null)
        {
            timeline.stop();
        }
        timeSeconds.set((STARTTIME + 1) * 100);
        timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(STARTTIME + 1), new KeyValue(timeSeconds, 0)));
        timeline.playFromStart();
    }

    public void updateTurnWait(){
        turnIndicator.setText("Čekej na tah protihráče");
        timeIndicator.setVisible(false);
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
                deck = new ImageView[][] {{i00, i01, i02, i03, i04},{i10, i11, i12, i13, i14},{i20, i21, i22, i23, i24},{i30, i31, i32, i33, i34}};
                for(int i = 0; i < deckRows; i++){
                    for(int j = 0; j < deckCols; j++){
                        deck[i][j].setImage(cardBackImg);
                        final int row = i;
                        final int col = j;
                        deck[i][j].setOnMouseClicked(new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent arg0) {
                                onCardClicked(row, col);
                            }
                        });
                    }
                }

            }
        });
    }

    public void onCardClicked(int row, int col){
        tcpConn.pickedCard(thisRoomId, row, col);
    }

    public void flipCard(int row, int col, int imgId){
        Image flippedCard = new Image("/Pexeso/Public/Img/cards/" + imgId + ".png");
        deck[row][col].setImage(flippedCard);
    }

    public void sendNewMsg(){
        String msg = chatMsg.getText();
        if(!msg.equals("") && msg.length() < 32) {
            String correctedMsg = msg.replaceAll("#", "?");
            tcpConn.sendChatMsg(thisRoomId, correctedMsg);
        }
        chatMsg.clear();
    }

    public void quitRoom(){
        try {
            GameLobbyController g = Main.FXMLLOADER_GAMELOBBY.getController();
            g.leaveLobby();
            setServerLobbyScene();
        } catch (IOException e){

        }
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
