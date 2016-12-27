package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.MsgTables;
import Pexeso.TCPClient.TCP;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
    public static String thisRoomId;

    private String newLine = "\n\r";

    //Room Info
    @FXML
    private GridPane gameLobbyPane;
    @FXML
    public Text roomNameText, numPlayingText, maxPlayingText, roomStatusText;
    @FXML
    public Button readyBtn;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatWindow.setEditable(false);
        this.tcpConn = Main.tcpi;

        chatMsg.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    sendNewMsg();
                }
            }
        });
    }

    @FXML
    public void addNewUserUi(final int userIndex, final String name, final int ready,final String score) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final Image cardBackImg = new Image("/Public/Img/user-icon.png");
                ImageView userIcon = new ImageView();
                userIcon.setImage(cardBackImg);
                Text userName = new Text();
                userName.setText(name.toUpperCase());
                userName.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                userName.setWrappingWidth(500);
                userName.setTextAlignment(TextAlignment.CENTER);
                Text userScore = new Text();
                userScore.setText("Skóre: " + score);
                userScore.setFill(Color.rgb(117, 117, 117, .99));
                Text userReady = new Text();
                if(ready == 1){
                    userReady.setText("Připraven!");
                }
                else {
                    userReady.setText("");
                }
                userReady.setFill(Color.rgb(33, 150, 243, .99));
                userReady.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

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
                vbox.setMaxWidth(500);
                vbox.getChildren().add(0, userIcon);
                vbox.getChildren().add(1, userName);
                vbox.getChildren().add(2, userScore);
                vbox.getChildren().add(3, userReady);
                vbox.setAlignment(Pos.CENTER);
                vbox.setSpacing(5);
            }
        });
    }

    @FXML
    public void removeUserUi(final int userIndex) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                VBox vbox;
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
                    default:
                        vbox = null;
                        break;
                }
                if (vbox != null) vbox.getChildren().clear();
            }
        });
    }

    @FXML
    public void updateUserReadyUi(final int userIndex, final boolean ready) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Node readyNode;
                switch (userIndex) {
                    case 0:
                        readyNode = vboxU0.getChildren().get(3);
                        break;
                    case 1:
                        readyNode = vboxU1.getChildren().get(3);
                        break;
                    case 2:
                        readyNode = vboxU2.getChildren().get(3);
                        break;
                    case 3:
                        readyNode = vboxU3.getChildren().get(3);
                        break;
                    default:
                        readyNode = null;
                }
                if (readyNode instanceof Text) {
                    if (!ready) {
                        ((Text) readyNode).setText(" ");
                    } else if (ready) {
                        ((Text) readyNode).setText("Připraven!");
                    }
                }

            }
        });
    }

    public String getUserNameUi(final int userIndex) {
        Node readyNode;
        switch (userIndex) {
            case 0:
                readyNode = vboxU0.getChildren().get(1);
                break;
            case 1:
                readyNode = vboxU1.getChildren().get(1);
                break;
            case 2:
                readyNode = vboxU2.getChildren().get(1);
                break;
            case 3:
                readyNode = vboxU3.getChildren().get(1);
                break;
            default:
                readyNode = null;
        }
        if (readyNode instanceof Text) {
            return ((Text) readyNode).getText();
        } else return null;
    }

    @FXML
    public void updateRoomUi(final String numPlaying, final String roomStatus) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                numPlayingText.setText(numPlaying);
                roomStatusText.setText(MsgTables.resolveRoomStatus(roomStatus));
            }
        });
    }

    public void setJoinedUsers() {
        tcpConn.getRoomUsers(thisRoomId);
    }

    public void leaveLobby() throws IOException {
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

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Stage/ServerLobby.fxml"));
                    Parent serverLobbyRoot = fxmlLoader.load();
                    Stage serverLobbyStage = new Stage();
                    serverLobbyStage.setScene(new Scene(serverLobbyRoot, 1024, 768));
                    serverLobbyStage.setTitle("Čupr Pexeso - Server Lobby");
                    serverLobbyStage.getIcons().add(new Image("Public/Img/icon.png"));
                    serverLobbyStage.setResizable(false);
                    serverLobbyStage.show();
                    Main.FXMLLOADER_SERVERLOBBY = fxmlLoader;
                    Main.parentWindow = serverLobbyStage;

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

    public void setRoomInfo(final String roomId, final String numPlaying, final String maxPlaying, final String roomStatus) {
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

    public void setReady() {
        tcpConn.userReady(thisRoomId, true);
    }

    public void unsetReady() {
        tcpConn.userReady(thisRoomId, false);
    }

    public void setReadyBtn() {
        readyBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                unsetReady();
            }
        });
        readyBtn.setStyle("-fx-background-color: #616161;");
    }

    public void unsetReadyBtn() {
        readyBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                setReady();
            }
        });
        readyBtn.setStyle("-fx-background-color: #2196F3;");
    }

    public void setGameScene() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    String oldChatMsg = chatWindow.getText();
                    Stage gameLobbyStage = (Stage) gameLobbyPane.getScene().getWindow();
                    gameLobbyStage.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Stage/Game.fxml"));
                    Parent gameRoot = fxmlLoader.load();
                    Stage gameStage = new Stage();
                    gameStage.setScene(new Scene(gameRoot, 1024, 768));
                    gameStage.setTitle("Čupr Pexeso - Game");
                    gameStage.getIcons().add(new Image("Public/Img/icon.png"));
                    gameStage.setResizable(false);
                    gameStage.show();
                    Main.FXMLLOADER_GAME = fxmlLoader;
                    Main.parentWindow = gameStage;

                    final GameController g = Main.FXMLLOADER_GAME.getController();
                    g.setThisRoomId(thisRoomId);
                    g.setCardDeck();
                    g.initChatWindow(oldChatMsg);
                    for (int i = 0; i < (Integer.parseInt(numPlayingText.getText())); i++) {
                        if (i == 0)
                            g.addNewUserUi(i, getUserNameUi(i), "0", "Na tahu");
                        else
                            g.addNewUserUi(i, getUserNameUi(i), "0", "");
                    }

                    gameStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            try {
                                leaveLobby();
                            } catch (IOException e) {
                            }
                        }
                    });
                    tcpConn.turnAck(thisRoomId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendNewMsg() {
        String msg = chatMsg.getText();
        if (!msg.equals("") && msg.length() < 32) {
            String correctedMsg = msg.replaceAll("#", "?");
            tcpConn.sendChatMsg(thisRoomId, correctedMsg);
        }
        chatMsg.clear();
        chatMsg.requestFocus();
    }


    public void appendUsrMsg(final String userName, final String msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.appendText(userName + ": " + msg + newLine);
            }
        });
    }

    public void appendSrvrMsg(final String msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.appendText("[Server] " + msg + newLine);
            }
        });
    }
}
