package view.client.endGamePopUp;

import controller.Register;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class endGameController implements Initializable {
    @FXML
    public Button endButton;
    @FXML
    public Label winner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleCloseButtonAction();
        Register.setViewEndGameController(this);
    }

    /**
     * Ends the whole program, when endButton is clicked
     *
     */
    @FXML
    public void handleCloseButtonAction() {
        endButton.setOnMouseClicked(e -> {
            Register.getAudioClips().getClick().play();
            Platform.exit();
            System.exit(0);

        });
    }

    /**
     * Server sets the name of the winner
     * @param winnerName String
     */
    public void setWinnerLabel(String winnerName) {
        javafx.application.Platform.runLater(() -> {
            winner.setText(winnerName);
            Register.getAudioClips().getThemeSong().stop();
            if (winnerName.toLowerCase().contains(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getName().toLowerCase())) {
                Register.getAudioClips().getWinSound().play();
            } else {
                Register.getAudioClips().getLoseSound().play();
            }
        });
    }
    }


