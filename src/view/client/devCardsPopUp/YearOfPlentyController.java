package view.client.devCardsPopUp;

import controller.Register;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import model.Resource;
import network.client.ClientWriter;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class YearOfPlentyController implements Initializable {
    @FXML
    ComboBox<String> comboResource, comboResource1;
    @FXML
    Button confirmButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboResource.getItems().addAll("Lumber", "Brick", "Grain", "Wool", "Ore");
        comboResource1.getItems().addAll("Lumber", "Brick", "Grain", "Wool", "Ore");
    }

    /**
     * Send the selected cards to the method yearOfPlenty([ArrayList])
     */
    @FXML
    public void send() {
        if (!comboResource.getSelectionModel().isEmpty() && !comboResource1.getSelectionModel().isEmpty()) {
            Register.getAudioClips().getClick().play();
            Stage s = (Stage) confirmButton.getScene().getWindow();
            ArrayList<Resource> res = new ArrayList<>();
            res = addRes(res, comboResource.getSelectionModel().getSelectedItem());
            res = addRes(res, comboResource1.getSelectionModel().getSelectedItem());
            new ClientWriter().yearOfPlenty(res);
            s.close();
        } else {
            Stage s = (Stage) confirmButton.getScene().getWindow();
            s.close();
        }
    }

    /**
     * Switch case to select the right resource
     *
     * @param res ArrayList<Resource>
     * @param string String
     * @return ArrayList<Resource>
     */
    private ArrayList<Resource> addRes(ArrayList<Resource> res, String string) {
        switch (string) {
            case "Lumber":
                res.add(Resource.LUMBER);
                break;
            case "Brick":
                res.add(Resource.BRICK);
                break;
            case "Grain":
                res.add(Resource.GRAIN);
                break;
            case "Wool":
                res.add(Resource.WOOL);
                break;
            case "Ore":
                res.add(Resource.ORE);
                break;
        }
        return res;
    }
}
