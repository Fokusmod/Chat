package ChatClientHomework;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage stage1;


    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Сетевой Чат");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scene.fxml"));
        Parent root1 = loader.load();
        Scene scene2 = new Scene(root1);
        stage.setScene(scene2);
        stage.setAlwaysOnTop(false);
        stage1 = stage;
        stage.setResizable(false);
        stage.show();




    }
}

