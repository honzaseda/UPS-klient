package Pexeso.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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

    public void attemptLogin(){
        Stage loginStage = (Stage) loginPane.getScene().getWindow();
        loginStage.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pexeso/Stage/Game.fxml"));
            Parent gameRoot = fxmlLoader.load();
            Stage gameStage = new Stage();
            gameStage.setScene(new Scene(gameRoot, 800, 600));
            gameStage.setResizable(false);
            gameStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
