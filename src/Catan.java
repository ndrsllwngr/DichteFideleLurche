import controller.Register;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import network.client.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.AudioClips;

public class Catan extends Application {

    private static final Logger LOGGER = LogManager.getLogger(Catan.class.getName());

    /**
     * Catan main
     *
     * @param args none
     */
    public static void main(String[] args) {
        LOGGER.traceEntry();
        if (args.length == 0) {
            launch(args);
        } else {
            Client client = new Client(args);
            Thread c = new Thread(client);
            c.start();
        }
        LOGGER.traceExit();

    }

    /**
     * Start application
     * @param primaryStage primary stage
     * @throws Exception catch possible exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.traceEntry(primaryStage.toString());
        Register.setAudioClips(new AudioClips());
        Register.getAudioClips().getIntroSong().setCycleCount(AudioClip.INDEFINITE);
        Register.getAudioClips().getIntroSong().play();
        Parent root = FXMLLoader.load(getClass().getResource("/view/startScreen/startScreen.fxml"));
        Scene startScreen = new Scene(root, 990, 690);
        primaryStage.initStyle((StageStyle.DECORATED));          // system bar only displays close button
        primaryStage.setResizable(false);          // system bar only displays close button
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setTitle("Catan - DichteFideleLurche");
        primaryStage.setScene(startScreen);
        primaryStage.show();
        LOGGER.traceExit(primaryStage.toString());
    }
}
