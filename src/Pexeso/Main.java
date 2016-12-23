package Pexeso;

import Pexeso.TCPClient.ClientInfo;
import Pexeso.TCPClient.TCP;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static FXMLLoader FXMLLOADER_LOGIN;
    public static FXMLLoader FXMLLOADER_SERVERLOBBY;
    public static FXMLLoader FXMLLOADER_GAMELOBBY;
    public static FXMLLoader FXMLLOADER_GAME;
    public static TCP tcpi;
    public static ClientInfo clientInfo;

    public static Stage parentWindow;

    @Override
    public void start(Stage loginStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Stage/Login.fxml"));
        Parent root = fxmlLoader.load();
        loginStage.setTitle("Čupr Pexeso - Login");
        loginStage.setScene(new Scene(root, 350, 440));
        loginStage.setResizable(false);
        loginStage.show();
        FXMLLOADER_LOGIN = fxmlLoader;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
