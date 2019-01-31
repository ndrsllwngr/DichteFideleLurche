package view.client.trade.receive;

import controller.Register;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.players.Player;
import network.client.ClientWriter;

import java.net.URL;
import java.util.ResourceBundle;

public class tradeLobbyController implements Initializable {

    @FXML
    Button tradeP1, tradeP2, tradeP3;
    @FXML
    Label status1, status2, status3;
    @FXML
    Label player1, player2, player3;
    @FXML
    Label id1, id2, id3;
    @FXML
    HBox playerOne, playerTwo, playerThree;
    private int tradeId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Register.setViewTradeReceiveLobbyController(this);
        setBeginning();
        setVisiblePlayers();
    }

    /**
     * Sets the trade id
     * @param tradeId
     */
    public void setTradeIdForServer(int tradeId) {
        javafx.application.Platform.runLater(() -> {
            this.tradeId = tradeId;
        });
    }

    /**
     *Sets a player's status to "Denied" or "Accepted,"
     *by checking for the right player id and the boolean value of check.
     * @param playerId
     * @param check
     */
    public void updatePlayer(int playerId, boolean check) {
        javafx.application.Platform.runLater(() -> {
            String tmp = "Denied";
            if (check) {
                tmp = "Accepted";
            }
            if (Integer.toString(playerId).equals(id1.getText())) {
                status1.setText(tmp);
                tradeP1.setDisable(!check);
                return;
            }
            if (Integer.toString(playerId).equals(id2.getText())) {
                status2.setText(tmp);
                tradeP2.setDisable(!check);
                return;
            }
            if (Integer.toString(playerId).equals(id3.getText())) {
                status3.setText(tmp);
                tradeP3.setDisable(!check);

                return;
            }
        });
    }

    /**
     * Handles the trade by checking which player the Client chooses to trade
     * with, by retracing the Button clicked and giving the information to the Server.
     * @param ev
     */
    @FXML
    public void handleTrade(ActionEvent ev) {
        Register.getAudioClips().getClick().play();
        if (ev.getSource().equals(tradeP1)) {
            int playerId1 = Integer.parseInt(id1.getText());
            new ClientWriter().performTrade(tradeId, playerId1);
            Stage stage = (Stage) tradeP1.getScene().getWindow();
            Register.setViewTradeReceiveLobbyController(null);
            stage.close();
        } else if (ev.getSource().equals(tradeP2)) {
            int playerId2 = Integer.parseInt(id2.getText());
            new ClientWriter().performTrade(tradeId, playerId2);
            Stage stage = (Stage) tradeP2.getScene().getWindow();
            Register.setViewTradeReceiveLobbyController(null);
            stage.close();
        } else if (ev.getSource().equals(tradeP3)) {
            int playerId3 = Integer.parseInt(id3.getText());
            new ClientWriter().performTrade(tradeId, playerId3);
            Stage stage = (Stage) tradeP3.getScene().getWindow();
            Register.setViewTradeReceiveLobbyController(null);
            stage.close();
        }
    }

    /**
     * Sets the statuses shown in the beginning. Waiting is default.
     */
    public void setBeginning() {
        playerOne.setDisable(true);
        playerTwo.setDisable(true);
        playerThree.setDisable(true);
        status1.setText("Waiting");
        status2.setText("Waiting");
        status3.setText("Waiting");
        tradeP1.setDisable(true);
        tradeP2.setDisable(true);
        tradeP3.setDisable(true);


    }

    /**
     * Sets the the information of the other players.
     */
    public void setVisiblePlayers() {
        for (int i = 0; i < Register.getController().getSequence().size(); i++) {
            Player p = Register.getController().getSequence().get(i);
            if (i == 1) {
                playerOne.setDisable(false);
                player1.setText(p.getName());
                id1.setText(Integer.toString(p.getId()));
            }
            if (i == 2) {
                playerTwo.setDisable(false);
                player2.setText(p.getName());
                id2.setText(Integer.toString(p.getId()));
            }
            if (i == 3) {
                playerThree.setDisable(false);
                player3.setText(p.getName());
                id3.setText(Integer.toString(p.getId()));
            }

        }

    }

    /**
     * Sends the status "Cancel Trade" to the Server and closes the window.
     */
    @FXML
    public void cancelTrade() {
        Register.getAudioClips().getClick().play();
        new ClientWriter().cancelTrade(tradeId);
        Stage stage = (Stage) tradeP1.getScene().getWindow();
        Register.setViewTradeReceiveLobbyController(null);
        stage.close();
    }
}



