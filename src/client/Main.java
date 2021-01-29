package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("sample.fxml"));
        //  Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Network network = new Network();
        Controller controller = loader.getController();

        controller.setNetwork(network);
        network.setController(controller);

        if (!network.connect())
            System.exit(1);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
