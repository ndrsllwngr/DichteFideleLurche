package view.client.trade.receive;

import controller.Register;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Resource;
import network.client.ClientWriter;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TradeRequestController implements Initializable {
    @FXML //Images of rs
            ImageView woolImgV, grainImgV, oreImgV, brickImgV, lumberImgV;
    @FXML //Images of rs
            ImageView woolImgV1, grainImgV1, oreImgV1, brickImgV1, lumberImgV1;
    @FXML //Opponent
            Label woolLabel, grainLabel, oreLabel, brickLabel, lumberLabel;
    @FXML //Client
            Label woolLabel1, grainLabel1, oreLabel1, brickLabel1, lumberLabel1;
    @FXML
    Button acceptButton, declineButton;
    Image in = new Image("icons/tradeArrowDown.png");
    Image out = new Image("icons/tradeArrowUp.png");
    private int woolOpp, grainOpp, oreOpp, brickOpp, lumberOpp;
    private int woolCli, grainCli, oreCli, brickCli, lumberCli;
    private int existingLumber, existingBrick, existingGrain, existingOre, existingWool;
    private Stage stage;
    private Image brickImg = new Image("/Ressourcen/rs_brick3.png");
    private Image grainImg = new Image("/Ressourcen/rs_grain3.png");
    private Image lumberImg = new Image("/Ressourcen/rs_lumber3.png");
    private Image oreImg = new Image("/Ressourcen/rs_ore3.png");
    private Image woolImg = new Image("/Ressourcen/rs_wool3.png");
    @FXML
    private ImageView arrowIn, arrowOut;

    private int tradeID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        existingLumber = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber();
        existingBrick = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick();
        existingGrain = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain();
        existingOre = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre();
        existingWool = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool();
        drawImg();
        Register.setViewTradeReceiveController(this);
    }

    /**
     * images will be loaded
     */
    private void drawImg() {
        woolImgV.setImage(woolImg);
        grainImgV.setImage(grainImg);
        oreImgV.setImage(oreImg);
        brickImgV.setImage(brickImg);
        lumberImgV.setImage(lumberImg);
        woolImgV1.setImage(woolImg);
        grainImgV1.setImage(grainImg);
        oreImgV1.setImage(oreImg);
        brickImgV1.setImage(brickImg);
        lumberImgV1.setImage(lumberImg);
        arrowIn.setImage(in);
        arrowOut.setImage(out);
    }

    /**
     * @param tradeIdFromServer
     */
    public void setTradeIdForServer(int tradeIdFromServer) {
        javafx.application.Platform.runLater(() -> {
            this.tradeID = tradeIdFromServer;
        });
    }

    /**
     * amound of offered/requested resources will be sent to server
     * @param offer amount of offered resources
     * @param request amount of requested resources
     */
    public void showTradeRequest(ArrayList<Resource> offer, ArrayList<Resource> request) {
        javafx.application.Platform.runLater(() -> {
            for (Resource res : request) {
                switch (res) {
                    case ORE:
                        oreCli++;
                        disableTrade(oreCli, existingOre, oreLabel1, oreImgV1);
                        break;
                    case WOOL:
                        woolCli++;
                        disableTrade(woolCli, existingWool, woolLabel1, woolImgV1);
                        break;
                    case GRAIN:
                        grainCli++;
                        disableTrade(grainCli, existingGrain, grainLabel1, grainImgV1);
                        break;
                    case BRICK:
                        brickCli++;
                        disableTrade(brickCli, existingBrick, brickLabel1, brickImgV1);
                        break;
                    case LUMBER:
                        lumberCli++;
                        disableTrade(lumberCli, existingLumber, lumberLabel1, lumberImgV1);
                        break;
                }
            }
            for (Resource r : offer) {
                switch (r) {
                        case WOOL:
                            woolOpp++;
                            break;
                    case ORE:
                        oreOpp++;
                            break;
                        case GRAIN:
                            grainOpp++;
                            break;
                        case BRICK:
                            brickOpp++;
                            break;
                        case LUMBER:
                            lumberOpp++;
                            break;
                }
            }
            //only declinable if you don'thave enough
            if ((brickCli > existingBrick) || (lumberCli > existingLumber) || (grainCli > existingGrain) || (oreCli > existingOre) || (woolCli > existingWool)) {
                acceptButton.setDisable(true);
            } else {
                acceptButton.setDisable(false);
            }
            setText(woolLabel, woolOpp, woolImgV);    //opp
            setText(grainLabel, grainOpp, grainImgV);
            setText(oreLabel, oreOpp, oreImgV);
            setText(brickLabel, brickOpp, brickImgV);
            setText(lumberLabel, lumberOpp, lumberImgV);
            setText(woolLabel1, woolCli, woolImgV1);    //client
            setText(grainLabel1, grainCli, grainImgV1);
            setText(oreLabel1, oreCli, oreImgV1);
            setText(brickLabel1, brickCli, brickImgV1);
            setText(lumberLabel1, lumberCli, lumberImgV1);
        });
    }

    /**
     * requested amount will be shown. if 0 -> disabled -> less visible
     * @param l Label
     * @param n amount
     * @param i image which belongs to label
     */
    private void setText(Label l, int n, ImageView i) {
        l.setText("x " + n);
        if (n == 0) {
            l.setDisable(true);
            i.setOpacity(0.25);
        } else {
            i.setOpacity(1);
        }
    }

    /**
     * trade will be only decline-able if not enough resources
     * @param req requested resources from client
     * @param exi resources in posession
     * @param l associated label
     * @param i associated image view
     */
    private void disableTrade(int req, int exi, Label l, ImageView i) {
        if (req > exi) {
            l.setDisable(true);
            i.setOpacity(0.25);
        } else {
            l.setDisable(false);
            i.setOpacity(1);
        }
    }

    /**
     * accepted / declined will be sent to server
     * @param ev
     */
    @FXML
    public void buttonHandler(ActionEvent ev){
        Register.getAudioClips().getClick().play();
        if (ev.getSource().equals(acceptButton)){
            new ClientWriter().acceptTrade(tradeID, true);
            stage = (Stage) acceptButton.getScene().getWindow();
            stage.close();
        }
        else {
            new ClientWriter().cancelTrade(tradeID);
            stage = (Stage) declineButton.getScene().getWindow();
            stage.close();
        }
    }

}
