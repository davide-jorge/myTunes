/**
 * =======================================
 * Compulsory Assignment #3 myTunes
 * EASV CSe2024 Group 1 Int
 * =======================================
 */

package dk.easv.mytunes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class myTunesApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(myTunesApplication.class.getResource("/dk/easv/mytunes/views/my-tunes.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("myTunes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}