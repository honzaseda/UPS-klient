package Pexeso.Controller;

import Pexeso.Main;
import Pexeso.TCPClient.TCP;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable{
    @FXML
    public ImageView i00, i01, i02, i03, i04, i10, i11, i12, i13, i14,  i20, i21, i22, i23, i24,  i30, i31, i32, i33, i34,  i40, i41, i42, i43, i44;
    @FXML
    public Text gameStatus;
    private static final Image cardBackImg = new Image("/Pexeso/Public/Img/card-back.png");
    @FXML
    private TextArea chatWindow;
    @FXML
    private TextField chatMsg;
    @FXML
    private Button sendChatMsg;

    public TCP tcpConn;

    private int deckRows = 5, deckCols = 5;
    private ImageView[][] deck;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
        chatWindow.setEditable(false);
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

}
