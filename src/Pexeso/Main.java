package Pexeso;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import Pexeso.TCPClient.TCP;

public class Main extends Application {

    public static FXMLLoader FXMLLOADER_LOGIN;
    public static FXMLLoader FXMLLOADER_GAME;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Stage/Login.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Čupr Pexeso - Login");
        primaryStage.setScene(new Scene(root, 350, 440));
        primaryStage.setResizable(false);
        primaryStage.show();
        FXMLLOADER_LOGIN = fxmlLoader;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
