package view.client.build;

import controller.Register;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import network.client.ClientWriter;
import org.apache.logging.log4j.LogManager;
import view.client.Controller;

import java.net.URL;
import java.util.ResourceBundle;

public class BuildController implements Initializable {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Controller.class.getName());
    public Boolean show = true;

    @FXML
    Button settleButton, cityButton, streetButton, developmentCardButton;
    @FXML
    ImageView settleCost, settleCost1, settleCost2, settleCost3, settlementVp;
    @FXML
    ImageView cityCost, cityCost1, cityCost2, cityCost3, cityCost4, cityVp;
    @FXML
    ImageView streetCost, streetCost1, streetVp;
    @FXML
    ImageView deveCost, deveCost1, deveCost2, devCardVp;//, buildImg;

    Image brick = new Image("/Ressourcen/rs_brick3.png");
    Image grain = new Image("/Ressourcen/rs_grain3.png");
    Image lumber = new Image("/Ressourcen/rs_lumber3.png");
    Image ore = new Image("/Ressourcen/rs_ore3.png");
    Image wool = new Image("/Ressourcen/rs_wool3.png");
    Image settlement = new Image("/icons/buildSettlementButton.png");
    Image city = new Image("/icons/buildCityButton.png");
    Image street = new Image("/icons/buildStreetButton.png");
    Image development = new Image("/icons/buyDevCardButton.png");
    Image devCardVpImage = new Image("/Information/SiegpunkteUnknown.png");
    Image settlementVpImage = new Image("/Information/Siegpunkte.png");
    Image cityVpImage = new Image("/Information/SiegpunkteDoppelt.png");
    Image streetVpImage = new Image("/Information/SiegpunkteNull.png");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addToolTips();
        loadCosts();
        Register.setViewBuildController(this);
    }


    /**
     * button buildAnything, buildSettlement, buildCity, buildStreet
     *
     * @param event
     */
    @FXML
    public void buildAnything(ActionEvent event) {
        switch (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getStatus()) {
            case TRADE_OR_BUILD:
                buildCity(event);
                buildSettlements(event);
                buildStreets(event);
                buyDevelopCardButton(event);
                break;
        }
    }

    /**
     * illegal actions will be disabled
     */
    @FXML
    public void deactivateButtons() {
        Platform.runLater(() -> {
            settleButton.setDisable(true);
            cityButton.setDisable(true);
            streetButton.setDisable(true);
            developmentCardButton.setDisable(true);
            switch (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getStatus()) {
                case TRADE_OR_BUILD:
                    if (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber() > 0 &&
                            Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick() > 0 &&
                            Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getRoads() > 0) {
                        streetButton.setDisable(false);
                        if (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain() > 0 &&
                                Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool() > 0 &&
                                Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableSettlementsMove().size() > 0 &&
                                Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getSettlements() > 0)
                            settleButton.setDisable(false);
                    }
                    if (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain() > 1 &&
                            Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre() > 2 &&
                            Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableCities().size() > 0 &&
                            Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getCities() > 0) {
                        cityButton.setDisable(false);
                    }
                    if (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain() > 0 &&
                            Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool() > 0 &&
                            Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre() > 0 && Register.getNtwrkClient().getCounterDevCard() < 25) {
                        developmentCardButton.setDisable(false);
                    }
                default:
                    LOGGER.info("Case not covered!");
            }
        });
    }


    /**
     * possible spots for settlement will be shown. if correct it will be built
     *
     * @param event
     */
    public void buildSettlements(ActionEvent event) {
        Register.getAudioClips().getClick().play();
        if (event.getSource().equals(settleButton) && show) {
            LOGGER.info("settlement button was clicked");
            Register.getViewClientController().getCanvasBoard().showPossibleSettlements();
            show = false;
            Register.getViewClientController().buildSettlement();
        } else {
            Register.getViewClientController().layer4.getGraphicsContext2D().clearRect(0, 0, Register.getViewClientController().layer4.getWidth(), Register.getViewClientController().layer4.getHeight());
            show = true;
        }
    }

    /**
     *  possible spots for city will be shown. if correct it will be built
     * @param event
     */
    public void buildCity(ActionEvent event) {
        Register.getAudioClips().getClick().play();
        if (event.getSource().equals(cityButton) && show) {
            LOGGER.info("city button was clicked");
            Register.getViewClientController().getCanvasBoard().showPossibleCities();
            show = false;
            Register.getViewClientController().buildCity();
        } else {
            Register.getViewClientController().layer4.getGraphicsContext2D().clearRect(0, 0, Register.getViewClientController().layer4.getWidth(), Register.getViewClientController().layer4.getHeight());
            show = true;
        }
    }

    /**
     *  possible spots for street will be shown. if correct it will be built
     * @param event
     */
    public void buildStreets(ActionEvent event) {
        Register.getAudioClips().getClick().play();
        if (event.getSource().equals(streetButton) && show) {
            LOGGER.info("street button was clicked");
            Register.getViewClientController().getCanvasBoard().showPossibleStreets2();
            show = false;
            Register.getViewClientController().buildStreets();

        } else {
            Register.getViewClientController().layer4.getGraphicsContext2D().clearRect(0, 0, Register.getViewClientController().layer4.getWidth(), Register.getViewClientController().layer4.getHeight());
            show = true;
        }
    }

    /**
     * button buyDevelopment card
     * @param event buyDevelopmentCard button
     */
    public void buyDevelopCardButton(ActionEvent event) {
        Register.getAudioClips().getClick().play();
        if (event.getSource().equals(developmentCardButton)) {
            new ClientWriter().buyDevelopmentCardButton();
        }
    }

    /**
     *  building / buying costs will be shown
     */
    public void loadCosts() {
        settleCost.setImage(lumber);
        settleCost1.setImage(brick);
        settleCost2.setImage(grain);
        settleCost3.setImage(wool);
        cityCost.setImage(grain);
        cityCost1.setImage(grain);
        cityCost2.setImage(ore);
        cityCost3.setImage(ore);
        cityCost4.setImage(ore);
        streetCost.setImage(lumber);
        streetCost1.setImage(brick);
        deveCost.setImage(grain);
        deveCost1.setImage(wool);
        deveCost2.setImage(ore);
        devCardVp.setImage(devCardVpImage);
        settlementVp.setImage(settlementVpImage);
        streetVp.setImage(streetVpImage);
        cityVp.setImage(cityVpImage);


    }

    /**
     *  information will be shown when hovered
     */
    public void addToolTips() {
        settleButton.setTooltip(new Tooltip("Build a settlement if your resources are enough"));
        cityButton.setTooltip(new Tooltip("Build a city if your resources are enough"));
        streetButton.setTooltip(new Tooltip("Build a street if your resources are enough"));
        developmentCardButton.setTooltip(new Tooltip("Buy a Card if your resources are enough"));
    }
}
