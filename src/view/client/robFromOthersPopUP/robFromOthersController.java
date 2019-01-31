package view.client.robFromOthersPopUP;


import controller.Register;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.players.Status;
import network.client.ClientWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class robFromOthersController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(robFromOthersController.class.getName());
    @FXML
    public Button okButton;
    @FXML
    public ComboBox<String> playerChoices;
    Image in = new Image("/icons/tradeArrowDown.png");
    private ArrayList<String> otherPlayersColor = Register.getViewClientController().getCanvasBoard().getOtherPlayersColor();
    @FXML
    private ImageView arrowIn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        activateChoiceBox();
        okButton.setOnAction(e -> getChoice());
        arrowIn.setImage(in);
    }

    /**
     * Sets all choices = names of other players for the choice box, default value = "select"
     *
     */
    private void activateChoiceBox() {
        playerChoices.getItems().addAll(otherPlayersColor);
    }

    /**
     * When the ok button is pushed, the selected value = selected player user would like to rob from is
     * sent to the server including the new selected robber position
     *
     */
    public void getChoice() {
        Register.getAudioClips().getClick().play();
        String player = playerChoices.getSelectionModel().getSelectedItem();
        for (int id : Register.getController().getAllPlayersId().keySet()) {
            if (player.contains(Register.getController().getAllPlayersId().get(id).getName())) {
                if(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getStatus() == Status.MOVE_ROBBER){
                new ClientWriter().moveRobber(Register.getViewClientController().getCanvasBoard().getNewRobberPos(), id);
                Stage stage = (Stage) okButton.getScene().getWindow();
                stage.close();
                LOGGER.info("New robber position and the id of the robbed player are given to the server");
                return;}
                else {
                    new ClientWriter().moveKnight(Register.getViewClientController().getCanvasBoard().getNewRobberPos(), id);
                    Stage stage = (Stage) okButton.getScene().getWindow();
                    stage.close();
                    LOGGER.info("New knight position and the id of the robbed player are given to the server. Due knightCard");
                    return;
                }
            }
        }
    }
}
