package PenguinBulkReport;

import java.io.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PenguinBulkReportMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Path to the FXML File
        Parent parent = FXMLLoader.<Parent>load(getClass().getResource("scene.fxml"));

        // Create the Scene
        Scene scene = new Scene(parent);
        // Set the Scene to the Stage
        stage.setScene(scene);
        // Set the Title to the Stage
        stage.setTitle("Penguin Stats Bulk Report");
        // Display the Stage
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

}