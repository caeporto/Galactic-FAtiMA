/**
 * Created by doyle on 02/01/2017.
 */

import FAtiMA.Core.AgentCore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class View extends Application {

    public static String[] arguments;

    public static void main(String[] args) {
        System.err.println(args.length);
        arguments = args;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws  Exception {

        Pane root = FXMLLoader.load(getClass().getResource("Layout.fxml"));

        Scene firstScene = new Scene(root, 600, 400);

        primaryStage.setTitle("Prototype");

        primaryStage.setResizable(false);

        primaryStage.setScene(firstScene);

        primaryStage.show();

    }
}
