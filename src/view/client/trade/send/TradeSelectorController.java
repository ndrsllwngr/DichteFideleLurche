package view.client.trade.send;

import controller.Register;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Resource;
import model.board.PortType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TradeSelectorController implements Initializable {
    Stage stage;
    Parent root;
    Boolean show = false;
    @FXML
    private Button bank, player;
    @FXML
    private Button brickHarbor, grainHarbor, lumberHarbor, oreHarbor, woolHarbor, genericHarbor;
    @FXML
    private Label lumberRatio, brickRatio, grainRatio, oreRatio, woolRatio, genRatio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showTradeOptions();
    }

    /**
     * trade window will be opened depending on source
     */
    @FXML
    public void trade(ActionEvent e) throws IOException {
        Register.getAudioClips().getClick().play();
        stage = (Stage) player.getScene().getWindow();
        boolean b = false;
        if (e.getSource().equals(player)) {
            root = FXMLLoader.load(getClass().getResource("/view/client/trade/send/trade.fxml"));
        } else if (e.getSource().equals(bank)) {
            root = FXMLLoader.load(getClass().getResource("/view/client/trade/send/withoutHabour.fxml"));
        } else if (e.getSource().equals(genericHarbor)) {
            root = FXMLLoader.load(getClass().getResource("/view/client/trade/send/genericHarbor.fxml"));
        }
        if (e.getSource().equals(brickHarbor)) {
            Register.setHarborTrade(Resource.BRICK);
            b = true;
        } else if (e.getSource().equals(grainHarbor)) {
            Register.setHarborTrade(Resource.GRAIN);
            b = true;
        } else if (e.getSource().equals(lumberHarbor)) {
            Register.setHarborTrade(Resource.LUMBER);
            b = true;
        } else if (e.getSource().equals(woolHarbor)) {
            Register.setHarborTrade(Resource.WOOL);
            b = true;
        } else if (e.getSource().equals(oreHarbor)) {
            Register.setHarborTrade(Resource.ORE);
            b = true;
        }
        if (b) {
            root = FXMLLoader.load(getClass().getResource("/view/client/trade/send/harborTrade/oneResourceHarbor.fxml"));
        }
        stage.setTitle("Select cards to trade");
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * only available Trade Options (harbor) will be shown
     */
    public void showTradeOptions() {
        if (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getSettlements() > 0 ||
                Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getCities() > 0) {
            setOptions();
        }
    }

    /**
     * only available Trade Options (harbor) will be shown - others will be invisible
     */
    public void setOptions() {
        Platform.runLater(() -> {
            brickHarbor.setVisible(show);
            grainHarbor.setVisible(show);
            lumberHarbor.setVisible(show);
            oreHarbor.setVisible(show);
            woolHarbor.setVisible(show);
            genericHarbor.setVisible(show);
            brickRatio.setVisible(show);
            grainRatio.setVisible(show);
            lumberRatio.setVisible(show);
            oreRatio.setVisible(show);
            woolRatio.setVisible(show);
            genRatio.setVisible(show);
            for (PortType portType : Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getHarborTyps()) {
                show = true;
                switch (portType) {
                    case BRICK:
                        brickHarbor.setVisible(show);
                        brickRatio.setVisible(show);
                        show = false;
                        break;
                    case GRAIN:
                        grainHarbor.setVisible(show);
                        grainRatio.setVisible(show);
                        show = false;
                        break;
                    case LUMBER:
                        lumberHarbor.setVisible(show);
                        lumberRatio.setVisible(show);
                        show = false;
                        break;
                    case ORE:
                        oreHarbor.setVisible(show);
                        oreRatio.setVisible(show);
                        show = false;
                        break;
                    case WOOL:
                        woolHarbor.setVisible(show);
                        woolRatio.setVisible(show);
                        show = false;
                        break;
                    case GENERIC:
                        genericHarbor.setVisible(show);
                        genRatio.setVisible(show);
                        show = false;
                        break;
                }

            }
        });
    }


}


