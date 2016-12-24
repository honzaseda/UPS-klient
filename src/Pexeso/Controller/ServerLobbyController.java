package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.MsgTables;
import Pexeso.TCPClient.TCP;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by seda on 28/10/16.
 */
public class ServerLobbyController implements Initializable {
    public TCP tcpConn;

    @FXML
    public Text statusText;
    @FXML
    public Button joinRoom, refreshList;
    @FXML
    public GridPane serverLobbyPane;
    @FXML
    public TableView<Room> lobbyTable;
    @FXML
    public TableColumn<Room, String> tableRoomId, tableRoomName, tableConnPlayers, tableMaxPlayers, tableRoomStatus;

    public static class Room {
        private final SimpleStringProperty roomId;
        private final SimpleStringProperty roomName;
        private final SimpleStringProperty connPlayers;
        private final SimpleStringProperty maxPlayers;
        private final SimpleStringProperty roomStatus;

        private Room(String rId, String rName, String cPl, String mPl, String rStatus) {
            this.roomId = new SimpleStringProperty(rId);
            this.roomName = new SimpleStringProperty(rName);
            this.connPlayers = new SimpleStringProperty(cPl);
            this.maxPlayers = new SimpleStringProperty(mPl);
            this.roomStatus = new SimpleStringProperty(rStatus);
        }


        public String getRoomId() {
            return roomId.get();
        }

        public void setRoomId(String rId) {
            roomId.set(rId);
        }

        public String getRoomName() {
            return roomName.get();
        }

        public void setRoomName(String rName) {
            roomName.set(rName);
        }

        public String getConnPlayers() {
            return connPlayers.get();
        }

        public void setConnPlayers(String cPl) {
            connPlayers.set(cPl);
        }

        public String getMaxPlayers() {
            return maxPlayers.get();
        }

        public void setMaxPlayers(String mPl) {
            maxPlayers.set(mPl);
        }

        public String getRoomStatus() {
            return roomStatus.get();
        }

        public void setRoomStatus(String rStatus) {
            roomStatus.set(rStatus);
        }
    }

    private ObservableList<Room> data =
            FXCollections.observableArrayList();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
        setLobbyTable();
        joinRoom.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                assign();
            }
        });
        refreshList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                refreshTable();
            }
        });
        lobbyTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    assign();
                }
            }
        });
    }

    public void refreshTable() {
        lobbyTable.getItems().clear();
        tcpConn.getRoomsTable();
    }

    public void updateTableRow(String rId, String rName, String cPl, String mPl, String rStatus) {
        data.add(new Room(rId, rName, cPl, mPl, MsgTables.resolveRoomStatus(rStatus)));
        lobbyTable.setItems(data);
    }

    public void setLobbyTable() {
        tableRoomId.setCellValueFactory(
                new PropertyValueFactory<Room, String>("roomId"));
        tableRoomName.setCellValueFactory(
                new PropertyValueFactory<Room, String>("roomName"));
        tableConnPlayers.setCellValueFactory(
                new PropertyValueFactory<Room, String>("connPlayers"));
        tableMaxPlayers.setCellValueFactory(
                new PropertyValueFactory<Room, String>("maxPlayers"));
        tableRoomStatus.setCellValueFactory(
                new PropertyValueFactory<Room, String>("roomStatus"));
    }

    public void setGameLobbyScene(final String roomId, final String numPlaying, final String maxPlaying, final String roomStatus) throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage serverLobbyStage = (Stage) serverLobbyPane.getScene().getWindow();
                    serverLobbyStage.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pexeso/Stage/GameLobby.fxml"));
                    Parent gameLobbyRoot = fxmlLoader.load();
                    Stage gameLobbyStage = new Stage();
                    gameLobbyStage.setScene(new Scene(gameLobbyRoot, 1024, 768));
                    gameLobbyStage.setTitle("Čupr Pexeso - Game Room Lobby - Herní místnost " + roomId);
                    gameLobbyStage.getIcons().add(new Image("Pexeso/Public/Img/icon.png"));
                    gameLobbyStage.setResizable(false);
                    gameLobbyStage.show();
                    Main.FXMLLOADER_GAMELOBBY = fxmlLoader;

                    GameLobbyController g = Main.FXMLLOADER_GAMELOBBY.getController();
                    GameLobbyController.thisRoomId = roomId;
                    g.setRoomInfo(roomId, numPlaying, maxPlaying, roomStatus);
                    g.setJoinedUsers();

                    gameLobbyStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
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

    public void setStatusText(final String text, final boolean err) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (err) {
                    statusText.setFill(Color.RED);
                } else {
                    statusText.setFill(Color.BLACK);
                }
                statusText.setText(text);
                Thread timedText = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            statusText.setText("");
                        } catch (InterruptedException e) {
                        }
                    }
                };
                timedText.start();
            }
        });

    }

    public void assign() {
        try {
            Room room = lobbyTable.getSelectionModel().getSelectedItem();
            if (room.connPlayers != room.maxPlayers)
                tcpConn.joinRoom(Integer.parseInt(room.getRoomId()));
        } catch (NullPointerException e) {
        }
    }
}
