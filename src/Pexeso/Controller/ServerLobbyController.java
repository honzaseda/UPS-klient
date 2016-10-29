package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.TCP;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by seda on 28/10/16.
 */
public class ServerLobbyController implements Initializable{
    public TCP tcpConn;

    @FXML
    public Text statusText;
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
    @FXML
    public void initialize(){
        this.tcpConn = Main.tcpi;
        setLobbyTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
        setLobbyTable();
    }

    public void refreshTable(){
        lobbyTable.getItems().clear();
        tcpConn.getRoomsTable();
    }

    public void updateTableRow(String rId, String rName, String cPl, String mPl, String rStatus){
        data.add(new Room(rId, rName, cPl, mPl, rStatus));
        lobbyTable.setItems(data);
    }

    public void setLobbyTable(){
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

    public void setGameLobbyScene() throws IOException{
        Parent window;
        window = FXMLLoader.load(getClass().getResource("/Pexeso/Stage/GameLobby.fxml"));

        Stage activeStage;
        activeStage = Main.parentWindow;
        activeStage.getScene().setRoot(window);
        activeStage.setTitle("Čupr Pexeso - Game Room");
    }

    public void setStatusText(final String text){
        statusText.setText(text);
    }

    public void assign(){
        try {
            Room room = lobbyTable.getSelectionModel().getSelectedItem();
            if(room.connPlayers != room.maxPlayers)
            tcpConn.joinRoom(Integer.parseInt(room.getRoomId()));
            else setStatusText("Místnost '" + room.getRoomName() + "' je plná");
        } catch (NullPointerException e){}
    }
}
