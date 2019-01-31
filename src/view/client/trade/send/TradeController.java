package view.client.trade.send;

import controller.Register;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Resource;
import network.client.ClientWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TradeController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ClientWriter.class.getName());
    @FXML
    ImageView arrowIn, arrowOut;
    @FXML //for sending trade requests: Opp
            Button woolOppButton, grainOppButton, lumberOppButton, oreOppButton, brickOppButton;
    @FXML //for sending trade requests: Me
            Button myWool, myGrain, myLumber, myOre, myBrick;
    @FXML
    Canvas oppLayer, myLayer;   //trading
    @FXML
    Button clearButton, delMyButton, delOppButton, confirmButton;
    private GraphicsContext oppGc; //for trading
    private GraphicsContext myGc; //for trading
    private Stage stage;
    private Image brickImg = new Image("/Ressourcen/rs_brick3.png");
    private ImagePattern brick = new ImagePattern(brickImg);
    private Image grainImg = new Image("/Ressourcen/rs_grain3.png");
    private ImagePattern grain = new ImagePattern(grainImg);
    private Image lumberImg = new Image("/Ressourcen/rs_lumber3.png");
    private ImagePattern lumber = new ImagePattern(lumberImg);
    private Image oreImg = new Image("/Ressourcen/rs_ore3.png");
    private ImagePattern ore = new ImagePattern(oreImg);
    private Image woolImg = new Image("/Ressourcen/rs_wool3.png");
    private ImagePattern wool = new ImagePattern(woolImg);
    private int oppCounter = 0;
    private int myCounter = 0;
    private int brickAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick();
    private int oreAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre();
    private int lumberAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber();
    private int grainAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain();
    private int woolAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool();
    private ArrayList<Resource> offer = new ArrayList<>();
    private ArrayList<Resource> request = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image arr1 = new Image("/icons/tradeArrowDown.png");
        arrowIn.setImage(arr1);
        Image arr2 = new Image("/icons/tradeArrowUp.png");
        arrowOut.setImage(arr2);
        oppGc = oppLayer.getGraphicsContext2D();
        myGc = myLayer.getGraphicsContext2D();
        disableButtons();
        addToolTips();
        disableResources(brickAmount, myBrick);
        disableResources(lumberAmount, myLumber);
        disableResources(grainAmount, myGrain);
        disableResources(oreAmount, myOre);
        disableResources(woolAmount, myWool);
    }

    /**
     * all buttons will be disabled
     */
    private void disableButtons() {
        clearButton.setDisable(true);
        delMyButton.setDisable(true);
        delOppButton.setDisable(true);
        confirmButton.setDisable(true);
    }

    /**
     * when no resource in posession, button will be disabled
     *
     * @param ex resource in posession
     * @param b  associated button
     */
    private void disableResources(int ex, Button b) {
        if (ex == 0) {
            b.setDisable(true);
        } else {
            b.setDisable(false);
        }
    }

    /**
     * when either requested nor offered resource are 0, trade is confirm-able
     */
    private void enableConfirmButton() {
        if (myCounter > 0 && oppCounter > 0) {
            confirmButton.setDisable(false);
        }
    }

    /**
     * all resource you request/ offer are tradeable
     * @param event button source
     */
    @FXML
    void tradeWithAnything(ActionEvent event) {
        Register.getAudioClips().getClick().play();
        //what you want
        if (event.getSource().equals(woolOppButton)) {
            oppGc.setFill(wool);
            if (getOppCounter() < 11) {
                setOppCounter();
            }
            drawOppCardsToExchange();
            request.add(Resource.WOOL);
        } else if (event.getSource().equals(grainOppButton)) {
            oppGc.setFill(grain);
            if (getOppCounter() < 11) {
                setOppCounter();
            }
            drawOppCardsToExchange();
            request.add(Resource.GRAIN);
        } else if (event.getSource().equals(brickOppButton)) {
            oppGc.setFill(brick);
            if (getOppCounter() < 11) {
                setOppCounter();
            }
            drawOppCardsToExchange();
            request.add(Resource.BRICK);
        } else if (event.getSource().equals(oreOppButton)) {
            oppGc.setFill(ore);
            if (getOppCounter() < 11) {
                setOppCounter();
            }
            drawOppCardsToExchange();
            request.add(Resource.ORE);
        } else if (event.getSource().equals(lumberOppButton)) {
            oppGc.setFill(lumber);
            if (getOppCounter() < 11) {
                setOppCounter();
            }
            drawOppCardsToExchange();
            request.add(Resource.LUMBER);
            //what you will excahnge for
        } else if (event.getSource().equals(myWool) && woolAmount > 0) {
            myGc.setFill(wool);
            if (getMyCounter() < 11) {
                increaseMyCounter();
            }
            drawMyCardsToExchange();
            woolAmount--;
            offer.add(Resource.WOOL);
        } else if (event.getSource().equals(myGrain) && grainAmount > 0) {
            myGc.setFill(grain);
            if (getMyCounter() < 11) {
                increaseMyCounter();
            }
            drawMyCardsToExchange();
            offer.add(Resource.GRAIN);
            grainAmount--;
        } else if (event.getSource().equals(myBrick) && brickAmount > 0) {
            myGc.setFill(brick);
            if (getMyCounter() < 11) {
                increaseMyCounter();
            }
            drawMyCardsToExchange();
            offer.add(Resource.BRICK);
            brickAmount--;
        } else if (event.getSource().equals(myOre) && oreAmount > 0) {
            myGc.setFill(ore);
            if (getMyCounter() < 11) {
                increaseMyCounter();
            }
            drawMyCardsToExchange();
            offer.add(Resource.ORE);
            oreAmount--;
        } else if (event.getSource().equals(myLumber) && lumberAmount > 0) {
            myGc.setFill(lumber);
            if (getMyCounter() < 11) {
                increaseMyCounter();
            }
            drawMyCardsToExchange();
            offer.add(Resource.LUMBER);
            lumberAmount--;
        }
        enableConfirmButton();
    }

    public void setOppCounter() {
        oppCounter++;
    }

    public int getOppCounter() {
        return oppCounter;
    }

    /**
     * opponent resource will be drawn
     */
    public void drawOppCardsToExchange() {
        if (getOppCounter() >= 11) {
            LOGGER.info("Nope, to many cards");
        } else {
            if (getOppCounter() == 0 && getMyCounter() == 0) {
                clearButton.setDisable(true);
            }
            delOppButton.setDisable(false);
            clearButton.setDisable(false);
            int xAxis = (getOppCounter() - 1) * 42; //21
            oppGc.fillRect(xAxis + 15, 5, 40, 40);
        }
    }

    /**
     * last drawn resource will be removed
     */
    @FXML
    void undoOppCard() {
        Register.getAudioClips().getClick().play();
        if (oppCounter > 0) {
            if (oppCounter == 1) {
                delOppButton.setDisable(true);
            }
            int xAxis = (getOppCounter() - 1) * 42;
            oppGc.clearRect(xAxis + 15, 5, 40, 40);
            oppCounter = oppCounter - 1;
            Resource removed = request.get(request.size() - 1);
            request.remove(request.size() - 1);
            LOGGER.info("Resource: " + removed + " has been removed from requested resources.");
        } else {
            delOppButton.setDisable(true);
        }
        if (getOppCounter() == 0 && getMyCounter() == 0) {
            clearButton.setDisable(true);
        }
        enableConfirmButton();
    }

    /**
     * help method
     */
    public void increaseMyCounter() {
        myCounter++;
        setMyCounter(myCounter);
    }

    /**
     * help method
     */
    public void decreaseMyCounter() {
        myCounter--;
        setMyCounter(myCounter);
    }

    public int getMyCounter() {
        return myCounter;
    }

    public void setMyCounter(int myCounter) {
        this.myCounter = myCounter;
    }

    /**
     * client resources will be drawn
     */
    public void drawMyCardsToExchange() {
        if (getMyCounter() >= 11) {
            LOGGER.info("Nope, to many cards");
        } else {
            delMyButton.setDisable(false);
            clearButton.setDisable(false);
            int xAxis = (getMyCounter() - 1) * 42;
            myGc.fillRect(xAxis + 15, 5, 40, 40);
        }
    }

    /**
     * last drawn card of client will be removed
     */
    @FXML
    void undoMyCard() {
        Register.getAudioClips().getClick().play();
        if (getMyCounter() >= 0) {
            if (getMyCounter() == 1) {
                delMyButton.setDisable(true);
            }
            int xAxis = (getMyCounter() - 1) * 42;
            myGc.clearRect(xAxis + 15, 5, 40, 40);
            decreaseMyCounter();
            Resource removed = offer.get(offer.size() - 1);
            offer.remove(offer.size() - 1);
            switch (removed) {
                case BRICK:
                    brickAmount++;
                    break;
                case ORE:
                    oreAmount++;
                    break;
                case GRAIN:
                    grainAmount++;
                    break;
                case WOOL:
                    woolAmount++;
                    break;
                case LUMBER:
                    lumberAmount++;
                    break;
            }
            LOGGER.info("Resource: " + removed + " has been removed from offered resources.");
        } else {
            delMyButton.setDisable(true);
        }
        if (getOppCounter() == 0 && getMyCounter() == 0) {
            clearButton.setDisable(true);
            confirmButton.setDisable(true);
        }
    }

    /**
     * clears all drawn cards
     */
    @FXML
    public void clearBothExchangeField() {
        Register.getAudioClips().getClick().play();
        oppGc.clearRect(0, 0, oppGc.getCanvas().getWidth(), oppGc.getCanvas().getHeight());
        myGc.clearRect(0, 0, myGc.getCanvas().getWidth(), myGc.getCanvas().getHeight());
        delOppButton.setDisable(true);
        delMyButton.setDisable(true);
        myCounter = 0;
        oppCounter = 0;
        offer.clear();
        request.clear();
        woolAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool();
        brickAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick();
        lumberAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber();
        oreAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre();
        grainAmount = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain();
        clearButton.setDisable(true);
        confirmButton.setDisable(true);
        disableResources(brickAmount, myBrick);
        disableResources(lumberAmount, myLumber);
        disableResources(grainAmount, myGrain);
        disableResources(oreAmount, myOre);
        disableResources(woolAmount, myWool);
    }

    /**
     * information will be shown when hovered
     */
    public void addToolTips() {
        woolOppButton.setTooltip(new Tooltip("add Wool, if desired"));
        grainOppButton.setTooltip(new Tooltip("add Grain, if desired"));
        brickOppButton.setTooltip(new Tooltip("add Brick, if desired"));
        lumberOppButton.setTooltip(new Tooltip("add Lumber, if desired"));
        oreOppButton.setTooltip(new Tooltip("add Ore, if desired"));
        delOppButton.setTooltip(new Tooltip("remove last added Card on Opponent's side"));
        myWool.setTooltip(new Tooltip("add Wool, if you have enough to spare"));
        myGrain.setTooltip(new Tooltip("add Grain, if you have enough to spare"));
        myBrick.setTooltip(new Tooltip("add Brick, if you have enough to spare"));
        myLumber.setTooltip(new Tooltip("add Lumber, if you have enough to spare"));
        myOre.setTooltip(new Tooltip("add Ore, if you have enough to spare"));
        delMyButton.setTooltip(new Tooltip("remove last added Card on your side"));
        clearButton.setTooltip(new Tooltip("Clear all added Cards"));
        confirmButton.setTooltip(new Tooltip("send trade request to Opponents"));
    }

    /**
     * go back to trade menu
     * @throws IOException when fxml is incorrect
     */
    @FXML
    public void goBack() throws IOException {
        Register.getAudioClips().getClick().play();
        stage = (Stage) confirmButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/view/client/trade/send/tradeselector.fxml"));
        Scene scene = new Scene(root, 650, 550);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * will be sent out to server -> lobby
     * @throws IOException
     */
    @FXML
    public void sendOut() throws IOException {
        Register.getAudioClips().getClick().play();
        new ClientWriter().offerTrade(offer, request);
        Stage s = (Stage) confirmButton.getScene().getWindow();
        s.close();
        Stage s2 = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/client/trade/receive/tradeLobby.fxml"));
        Scene scene = new Scene(root);
        s2.initStyle((StageStyle.DECORATED));          // system bar only displays close button
        s2.setOnCloseRequest(Event::consume);        // disable close button
        s2.initModality(Modality.APPLICATION_MODAL); // events only registered at popup
        s2.setResizable(false);                      // disable resizing
        s2.setTitle("Trade lobby");
        s2.setScene(scene);                          // set scene to stage
        s2.show();
    }


}
