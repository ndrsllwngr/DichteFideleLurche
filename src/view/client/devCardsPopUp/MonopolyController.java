package view.client.devCardsPopUp;

import controller.Register;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import network.client.ClientWriter;

import java.net.URL;
import java.util.ResourceBundle;

public class MonopolyController implements Initializable {
    @FXML
    ComboBox<String> comboResource;
    @FXML
    Button confirmButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboResource.getItems().addAll("Lumber", "Brick", "Grain", "Wool", "Ore");
    }

    /**
     * if a resource is selected, monopolycard will be play. else: nothing will happen
     */
    @FXML
    public void send(){
        Register.getAudioClips().getClick().play();
        if(!comboResource.getSelectionModel().isEmpty()){
            Stage s = (Stage) confirmButton.getScene().getWindow();
            new ClientWriter().sendMonopol(comboResource.getSelectionModel().getSelectedItem());
            s.close();
        } else {
            Stage s = (Stage) confirmButton.getScene().getWindow();
            s.close();
        }
    }
}
