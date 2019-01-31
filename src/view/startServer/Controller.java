package view.startServer;

import controller.Register;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import network.server.Server;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Controller.class.getName());
    @FXML
    ComboBox<Integer> comboBoxPlayers;
    @FXML
    ComboBox<String> comboBoxBoard;
    @FXML
    ComboBox<Integer> comboBoxAi;
    @FXML
    Button goBack;
    @FXML
    Button startServer;
    @FXML
    TextArea console;
    @FXML
    BorderPane myContainer;
    BackgroundImage myBI = new BackgroundImage(new Image("Hintergrund.png", 1000, 700, false, true),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    @FXML
    Button play;
    private Server server;
    private Thread s;
    private boolean popup = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAudio();
        Register.setViewStartServerController(this);
        Register.setRelevantTextArea(2);
        console.textProperty().addListener((observable, oldValue, newValue) -> console.setScrollTop(Double.MAX_VALUE));
        comboBoxPlayers.setStyle("-fx-font-size: 20px;");
        comboBoxPlayers.getItems().removeAll(comboBoxPlayers.getItems());
//        comboBoxPlayers.getItems().addAll(3, 4, 5, 6); // TODO expand game
        comboBoxPlayers.getItems().addAll(3, 4);
        comboBoxBoard.setStyle("-fx-font-size: 20px;");
        comboBoxBoard.getItems().removeAll(comboBoxBoard.getItems());
//        comboBoxBoard.getItems().addAll("Default","Lumber","Random");
        comboBoxBoard.getItems().addAll("Default", "Lumber");
        comboBoxAi.setStyle("-fx-font-size: 20px;");
        comboBoxAi.getItems().removeAll(comboBoxAi.getItems());
        comboBoxAi.getItems().addAll(1, 2, 3, 4);
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

    @FXML
    private void startOrJoinHandler(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/startServer/startServer.fxml"));
        if(event.getSource() == goBack){
            Register.getAudioClips().getClick().play();
            resetServer();
            stage = (Stage) goBack.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/view/startScreen/startScreen.fxml"));
        }
        Scene scene = new Scene(root, 1000, 700);
/*        String css = this.getClass().getResource("/css/openingWindow.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(css);*/
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void startServer(ActionEvent event) throws IOException {
        if (event.getSource() == startServer && !comboBoxPlayers.getSelectionModel().isEmpty() && !comboBoxBoard.getSelectionModel().isEmpty()) {
            if (comboBoxAi.getSelectionModel().isEmpty()) {
                Register.getAudioClips().getClick().play();
                Register.getAudioClips().getIntroSong().stop();
                resetServer();
                int maxClientsCount = comboBoxPlayers.getSelectionModel().getSelectedItem();
                String map = comboBoxBoard.getSelectionModel().getSelectedItem();
                server = new Server(maxClientsCount, map);
                s = new Thread(server);
                s.start();
            } else {
                Register.getAudioClips().getClick().play();
                Register.getAudioClips().getIntroSong().stop();
                resetServer();
                int maxClientsCount = comboBoxPlayers.getSelectionModel().getSelectedItem();
                String map = comboBoxBoard.getSelectionModel().getSelectedItem();
                int aiCount = comboBoxAi.getSelectionModel().getSelectedItem();
                server = new Server(maxClientsCount, map, aiCount);
                s = new Thread(server);
                s.start();
            }
        }
    }

    public synchronized void appendConsoleOutputToTextArea(String s) {
        javafx.application.Platform.runLater(() -> {
            try {
                if (console.getParagraphs().size() > 200) {
                    console.clear();
                }
                console.appendText(s + "\n");
            } catch (Exception e) {
                LOGGER.catching(Level.ERROR, e);
            }
        });
    }

    private void resetServer(){
        if(server != null){
            server.setServerStatus(false);
            server = null;
            Register.setNtwrkServer(null);
            s = null;
            console.setText("");
        }
    }

    @FXML
    public void connectionLost() {
        javafx.application.Platform.runLater(() -> {
            if (popup) {
                popup = false;
                Register.setAlertPopUpServer(this);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Connection lost");
                alert.setContentText("One player left the game.\n");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Platform.exit();
                    System.exit(0);
                }
            }
        });
    }
}