package view.startScreen;

import controller.Register;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    VBox singleBox;
    @FXML
    Button startServerButton;
    @FXML
    Button joinServerButton;
    @FXML
    Button play;
    @FXML
    ImageView catanLogo;
    Image catanLogoImage = new Image("Catan.png");
    @FXML
    BorderPane myContainer;
    BackgroundImage myBI = new BackgroundImage(new Image("Hintergrund.png", 1000, 700, false, true),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAudio();
        catanLogo.setImage(catanLogoImage);
        myContainer.setBackground(new Background(myBI));
//serverBox.getScene().getStylesheets().add("view/openingWindow.css");
    }


    public void initAudio() {
        boolean b = Register.getAudioClips().getIntroSong().isPlaying();
        if (b) {
            play.setId("stop");
        } else {
            play.setId("play");
        }
        play.setOnAction(e -> {
            if (Register.getAudioClips().getIntroSong().isPlaying()) {
                Register.getAudioClips().getIntroSong().stop();
                play.setId("play");
            } else {
                Register.getAudioClips().getIntroSong().play();
                play.setId("stop");
            }
        });
    }

    /**
     * @param event which Button, this case: startButton
     *              if startScreen Button is clicked, scene will be switched to gaming-scene
     */
    @FXML
    private void startOrJoinHandler(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;
        if (event.getSource() == startServerButton) {
            Register.getAudioClips().getClick().play();
            stage = (Stage) startServerButton.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/view/startServer/startServer.fxml"));
            Scene scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } else if (event.getSource() == joinServerButton) {
            Register.getAudioClips().getClick().play();
            stage = (Stage) startServerButton.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/view/joinServer/joinServer.fxml"));
            Scene scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }

    }

    /**
     * Multi Player Mode: Play only with real Players
     * Choice between: Start ServerTest and Join ServerTest
     *
     * @see @method joinServerHandler, startServerHandler
     */

}