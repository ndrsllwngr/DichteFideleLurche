package view.joinServer;


import controller.Register;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import network.client.Client;
import network.client.ClientWriter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class Controller implements Initializable {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Controller.class.getName());
    private final ObservableList<String> allOptions =
            FXCollections.observableArrayList("Rot", "Orange", "Blau", "Weiß");
    @FXML
    ComboBox<String> comboBoxColors;
    @FXML
    Button goBack;
    @FXML
    Button startGame;
    @FXML
    Button connect;
    @FXML
    Button join;
    @FXML
    TextField hostname;
    @FXML
    TextField port;
    @FXML
    TextField name;
    @FXML
    CheckBox ai;
    @FXML
    TextArea console;
    @FXML
    TextField messageField;
    @FXML
    Button sendMsg;
    @FXML
    BorderPane myContainer;
    BackgroundImage myBI = new BackgroundImage(new Image("Hintergrund.png", 1000, 700, false, true),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    @FXML
    Button play;
    private Client client;
    private Thread c;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAudio();
        Register.setViewJoinServerController(this);
        Register.setRelevantTextArea(0);
        console.textProperty().addListener((observable, oldValue, newValue) -> console.setScrollTop(Double.MAX_VALUE));
        comboBoxColors.setStyle("-fx-font-size: 20px;");
//        comboBoxColors.getItems().removeAll(comboBoxColors.getItems());
        comboBoxColors.getItems().addAll(allOptions);
        int r = ThreadLocalRandom.current().nextInt(0, allOptions.size());
        comboBoxColors.getSelectionModel().select(r);
        hostname.setText("aruba.dbs.ifi.lmu.de");
        port.setText("10003");
        myContainer.setBackground(new Background(myBI));

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

    public void startNow() {
        javafx.application.Platform.runLater(() -> {
            try {
                Stage stage = (Stage) startGame.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/view/client/client.fxml"));
                Scene scene = new Scene(root);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.setTitle("Catan - DichteFideleLurche : " + Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getName());
                stage.show();
            } catch (IOException ex) {
                LOGGER.catching(Level.ERROR, ex);
            }
        });
    }

    @FXML
    private void startOrJoinHandler(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/joinServer/joinServer.fxml"));
        if (event.getSource() == goBack) {
            Register.getAudioClips().getClick().play();
            resetClientConnection();
            stage = (Stage) goBack.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/view/startScreen/startScreen.fxml"));
        }
        Scene scene = new Scene(root);
        //String css = this.getClass().getResource("/css/openingWindow.css").toExternalForm();
        //scene.getStylesheets().addAll(css);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void enterSend(KeyEvent ev) {
        if (ev.getCode().equals(KeyCode.ENTER)) {
            client.sendMsg(messageField.getCharacters().toString());
            messageField.clear();
        }
    }

    @FXML
    void connectHandler(ActionEvent e) {
        if (e.getSource() == connect) {
            Register.getAudioClips().getClick().play();
            resetClientConnection();
            client = new Client(hostname.getCharacters().toString(), Integer.parseInt(port.getCharacters().toString()), ai.isSelected());
            c = new Thread(client);
            c.start();
        } else if (e.getSource() == join) {
            Register.getAudioClips().getClick().play();
            new ClientWriter().setPlayer(name.getCharacters().toString(), comboBoxColors.getSelectionModel().getSelectedItem());
        } else if (e.getSource() == sendMsg) {
            client.sendMsg(messageField.getCharacters().toString());
            Register.getAudioClips().getClick().play();
            messageField.clear();
        } else if (e.getSource() == startGame) {
            Register.getAudioClips().getClick().play();
            Register.getAudioClips().getIntroSong().stop();
            new ClientWriter().register();
        }
    }

    /**
     * Append text to textarea
     *
     * @param text text (string)
     */
    public synchronized void appendConsoleOutputToTextArea(String text) {
        javafx.application.Platform.runLater(() -> {
            try {
                console.appendText(text);
            } catch (Exception e) {
                LOGGER.catching(Level.ERROR, e);
            }
        });
    }


    private void resetClientConnection() {
        if (client != null) {
            client.disconnect();
            client = null;
            c = null;
            console.setText("");
        }
    }
}