/**
 * Created by doyle on 02/01/2017.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class View extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws  Exception {

        Pane root = FXMLLoader.load(getClass().getResource("Layout.fxml"));

        Scene firstScene = new Scene(root, 800, 600);

        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        primaryStage.setScene(firstScene);

        primaryStage.show();

    }
}
