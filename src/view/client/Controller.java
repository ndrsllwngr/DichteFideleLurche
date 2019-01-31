package view.client;

import controller.Register;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import model.Resource;
import model.cards.*;
import network.client.ClientWriter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.AudioClips;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class Controller implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(Controller.class.getName());
    /**
     * layer1 - tiles, number tokens, harbors
     * layer2 - roads, settlements, cities
     * layer3 - robber
     * layer4 - listeners & boxes for possible buildings
     */
    @FXML
    public Canvas layer1, layer2road, layer2settlement, layer2city, layer3, layer4;
    public ArrayList<Paint> devCardStack = new ArrayList<>();  //DevelopmentCards you possess
    @FXML
    public Label vpTrans, rsTrans, dcTrans, knTrans, stTrans;
    @FXML
    public Label brickTrans, lumberTrans, grainTrans, woolTrans, oreTrans; //for transistions
    GraphicsContext gc1, gc2road, gc2settlement, gc2city, gc3, gc4;
    @FXML
    BorderPane pane;
    @FXML
    Label pl1, state;
    @FXML
    Rectangle myColor, pl2c, pl3c, pl4c;
    @FXML
    Canvas diceCanvas;     //cards in possession
    @FXML
    TextArea chatWindow;
    @FXML
    TextField chatField;
    @FXML
    Button sendButton, rollButton, endButton, tradeButton, devButton;
    @FXML
    VBox bigBox, leftBox, rightBox, pl2Box, pl3Box, pl4Box, pl5Box, pl6Box, chatBox;
    @FXML
    Rectangle fstDeve, scdDeve, trdDeve, army1, street1, army2, street2, army3, street3, army4, street4, army5, street5, army6, street6;
    // names of all players
    @FXML
    Label pl2, pl3, pl4, pl5, pl6, vp1, rs1, dv1, kn1, ls1, vp2, rs2, dv2, kn2, ls2, vp3, rs3, dv3, kn3, ls3, vp4, rs4, dv4, kn4, ls4;
    @FXML
    Label vp5, rs5, dv5, kn5, ls5, vp6, rs6, dv6, kn6, ls6;
    @FXML
    Label brickAmount, oreAmount, lumberAmount, woolAmount, grainAmount;
    Image lumberImg = new Image("/Ressourcen/rs_lumber2.png");
    Image oreImg = new Image("/Ressourcen/rs_ore2.png");
    Image woolImg = new Image("/Ressourcen/rs_wool2.png");
    //DevelopmentCards
    Image knightImg = new Image("/devcards/Knight2.png");
    Image monopolyImg = new Image("/devcards/Monopoly2.png");
    Image roadBuildingImg = new Image("/devcards/RoadBuilding2.png");
    Image libraryImg = new Image("/devcards/Library2.png");
    Image chapelImg = new Image("/devcards/Chapel2.png");
    Image marketImg = new Image("/devcards/Market2.png");
    Image greatHallImg = new Image("/devcards/GreatHall2.png");
    Image universityImg = new Image("/devcards/University2.png");
    Image yearOfPlentyImg = new Image("/devcards/YearOfPlanty2.png");
    Image blueSettlement = new Image("/BuildingBlocks/Settlement_blue3.png");
    Image redSettlement = new Image("/BuildingBlocks/Settlement_red3.png");
    Image orangeSettlement = new Image("/BuildingBlocks/Settlement_orange3.png");
    Image whiteSettlement = new Image("/BuildingBlocks/Settlement_white2.png");
    ImagePattern blueSettle = new ImagePattern(blueSettlement);
    ImagePattern redSettle = new ImagePattern(redSettlement);
    ImagePattern orangeSettle = new ImagePattern(orangeSettlement);
    ImagePattern whiteSettle = new ImagePattern(whiteSettlement);
    @FXML
    ImageView VPImg1, VPImg2, VPImg3, VPImg4, VPImg5, VPImg6;
    @FXML
    ImageView KNImg1, KNImg2, KNImg3, KNImg4, KNImg5, KNImg6;
    @FXML
    ImageView RSImg1, RSImg2, RSImg3, RSImg4, RSImg5, RSImg6;
    @FXML
    ImageView DCImg1, DCImg2, DCImg3, DCImg4, DCImg5, DCImg6;
    @FXML
    ImageView PLSImg1, PLSImg2, PLSImg3, PLSImg4, PLSImg5, PLSImg6;
    @FXML
    Circle status1, status2, status3, status4, status5, status6;    //1: client
    @FXML
    Stage stage;
    @FXML
    Parent root;
    @FXML
    Pane replace1;
    @FXML
    HBox replace;
    @FXML
    ImageView statusPop, devCardTrans;
    @FXML
    Button play;
    int cnt = 0;
    //Dice
    private Image diceOneImg = new Image("/dice/1.png");
    private Image diceTwoImg = new Image("/dice/2.png");
    private Image diceThreeImg = new Image("/dice/3.png");
    private Image diceFourImg = new Image("/dice/4.png");
    private Image diceFiveImg = new Image("/dice/5.png");
    private Image diceSixImg = new Image("/dice/6.png");
    private Image vicPointsImg = new Image("/Information/Siegpunkte.png");
    private Image activeKnightsImg = new Image("/Information/Rittermacht.png");
    private Image deveCardsImg = new Image("/Information/Entwicklungskarten.png");
    private Image resourcesImg = new Image("/Information/Ressourcen.png");
    private Image largestArmyImg = new Image("/Information/GrRittermacht2.png");
    ImagePattern army = new ImagePattern(largestArmyImg);
    private Image longestRoadImg = new Image("/Information/LHandelsstrasse2.png");
    ImagePattern road = new ImagePattern(longestRoadImg);
    private Image personalRoadImg = new Image("/Information/Strassenlaenge.png");
    private ArrayList<ImagePattern> victoryCards = new ArrayList<>();
    private Image brickImg = new Image("/Ressourcen/rs_brick2.png");
    private Image grainImg = new Image("/Ressourcen/rs_grain2.png");
    private ImagePattern diceOne = new ImagePattern(diceOneImg);
    private ImagePattern diceTwo = new ImagePattern(diceTwoImg);
    private ImagePattern diceThree = new ImagePattern(diceThreeImg);
    private ImagePattern diceFour = new ImagePattern(diceFourImg);
    private ImagePattern diceFive = new ImagePattern(diceFiveImg);
    private ImagePattern diceSix = new ImagePattern(diceSixImg);
    private ArrayList<VBox> playerBoxes = new ArrayList<>();
    private ArrayList<Rectangle> playerColors = new ArrayList<>();
    private ArrayList<Label> playerNames = new ArrayList<>();
    private ArrayList<Circle> playerStatus = new ArrayList<>();
    private ArrayList<Label> playerVPs = new ArrayList<>();
    private ArrayList<Label> playerResCount = new ArrayList<>();
    private ArrayList<Label> playerKnights = new ArrayList<>();
    private ArrayList<Label> playerDevCards = new ArrayList<>();
    private ArrayList<Label> playerLongestRoad = new ArrayList<>();
    private ArrayList<String> developmentCardsCon = new ArrayList<>();
    private ArrayList<String> developmentCardsMoveCon = new ArrayList<>();
    private Boolean devButtonOnOff = true;
    private Boolean cardDev = true;
    private Boolean cardDevStreetOn = false;
    private int fstDeveCounter;
    private int trdDeveCounter;
    private ImagePattern knightCard, monopolyCard, roadBuildingCard, yearOfPlentyCard, libraryCardVP, chapelCardVP, marketCardVP, greatHallCardVP, universityCardVP;
    private GraphicsContext diceGc;
    private CanvasBoard canvasBoard;
    private ArrayList<Tooltip> infoToDeveCards = new ArrayList<>(); //Infos for developmentCards


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAudio();
        try {
            buildHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Register.setViewClientController(this);
        Register.setRelevantTextArea(1);
        initLists();
        chatWindow.textProperty().addListener((observable, oldValue, newValue) -> chatWindow.setScrollTop(Double.MAX_VALUE));
        canvasBoard = new CanvasBoard();

        addToolTips();
        chatWindow();
        devButton.setVisible(false);

        knightCard = new ImagePattern(knightImg);
        monopolyCard = new ImagePattern(monopolyImg);
        roadBuildingCard = new ImagePattern(roadBuildingImg);
        libraryCardVP = new ImagePattern(libraryImg);
        chapelCardVP = new ImagePattern(chapelImg);
        marketCardVP = new ImagePattern(marketImg);
        greatHallCardVP = new ImagePattern(greatHallImg);
        universityCardVP = new ImagePattern(universityImg);
        yearOfPlentyCard = new ImagePattern(yearOfPlentyImg);


        colorStatus();
        gc1 = layer1.getGraphicsContext2D();
        gc2road = layer2road.getGraphicsContext2D();
        gc2settlement = layer2settlement.getGraphicsContext2D();
        gc2city = layer2city.getGraphicsContext2D();
        gc3 = layer3.getGraphicsContext2D();
        gc4 = layer4.getGraphicsContext2D();
        diceGc = diceCanvas.getGraphicsContext2D();
        Register.getController().notifyObserverCanvasboard();
        setPlayerNames();
        giveIcons();
        removeNonExistingPlayers();
        bindResourceAmount();
        fstDeve.setFill(null);
        scdDeve.setFill(null);
        trdDeve.setFill(null);
        layer4.toFront();
        Register.getNtwrkClient().toggleReady();
    }

    /**
     * Music starts as soon as the game starts
     * with options to stop and play
     */
    public void initAudio() {
        if (Register.getAudioClips().getIntroSong().isPlaying()) {
            Register.getAudioClips().getIntroSong().stop();
        }
        Register.setAudioClips(new AudioClips());
        Register.getAudioClips().getThemeSong().setCycleCount(AudioClip.INDEFINITE);
        Register.getAudioClips().getThemeSong().play();
        play.setId("stop");
        play.setOnAction(e -> {
            if (Register.getAudioClips().getThemeSong().isPlaying()) {
                Register.getAudioClips().getThemeSong().stop();
                play.setId("play");
            } else {
                Register.getAudioClips().getThemeSong().play();
                play.setId("stop");
            }
        });
    }

    /**
     * sets information icons for all players
     */
    public void giveIcons() {
        VPImg1.setImage(vicPointsImg);
        VPImg2.setImage(vicPointsImg);
        VPImg3.setImage(vicPointsImg);
        VPImg4.setImage(vicPointsImg);
        VPImg5.setImage(vicPointsImg);
        VPImg6.setImage(vicPointsImg);
        KNImg1.setImage(activeKnightsImg);
        KNImg2.setImage(activeKnightsImg);
        KNImg3.setImage(activeKnightsImg);
        KNImg4.setImage(activeKnightsImg);
        KNImg5.setImage(activeKnightsImg);
        KNImg6.setImage(activeKnightsImg);
        RSImg1.setImage(resourcesImg);
        RSImg2.setImage(resourcesImg);
        RSImg3.setImage(resourcesImg);
        RSImg4.setImage(resourcesImg);
        RSImg5.setImage(resourcesImg);
        RSImg6.setImage(resourcesImg);
        DCImg1.setImage(deveCardsImg);
        DCImg2.setImage(deveCardsImg);
        DCImg3.setImage(deveCardsImg);
        DCImg4.setImage(deveCardsImg);
        DCImg5.setImage(deveCardsImg);
        DCImg6.setImage(deveCardsImg);
        PLSImg1.setImage(personalRoadImg);
        PLSImg2.setImage(personalRoadImg);
        PLSImg3.setImage(personalRoadImg);
        PLSImg4.setImage(personalRoadImg);
        PLSImg5.setImage(personalRoadImg);
        PLSImg6.setImage(personalRoadImg);
    }

    /**
     * several arraylists will be filled for later accesses
     */
    @FXML
    public void initLists() {
        playerColors.add(myColor);
        playerColors.add(pl2c);
        playerColors.add(pl3c);
        playerColors.add(pl4c);
        playerBoxes.add(bigBox);
        playerBoxes.add(pl2Box);
        playerBoxes.add(pl3Box);
        playerBoxes.add(pl4Box);
        playerBoxes.add(pl5Box);
        playerBoxes.add(pl6Box);
        playerNames.add(pl1);
        playerNames.add(pl2);
        playerNames.add(pl3);
        playerNames.add(pl4);
        playerNames.add(pl5);
        playerNames.add(pl6);
        playerStatus.add(status1);
        playerStatus.add(status2);
        playerStatus.add(status3);
        playerStatus.add(status4);
        playerStatus.add(status5);
        playerStatus.add(status6);
        playerResCount.add(rs1);
        playerResCount.add(rs2);
        playerResCount.add(rs3);
        playerResCount.add(rs4);
        playerResCount.add(rs5);
        playerResCount.add(rs6);
        playerVPs.add(vp1);
        playerVPs.add(vp2);
        playerVPs.add(vp3);
        playerVPs.add(vp4);
        playerVPs.add(vp5);
        playerVPs.add(vp6);
        playerKnights.add(kn1);
        playerKnights.add(kn2);
        playerKnights.add(kn3);
        playerKnights.add(kn4);
        playerKnights.add(kn5);
        playerKnights.add(kn6);
        playerDevCards.add(dv1);
        playerDevCards.add(dv2);
        playerDevCards.add(dv3);
        playerDevCards.add(dv4);
        playerDevCards.add(dv5);
        playerDevCards.add(dv6);
        playerLongestRoad.add(ls1);
        playerLongestRoad.add(ls2);
        playerLongestRoad.add(ls3);
        playerLongestRoad.add(ls4);
        playerLongestRoad.add(ls5);
        playerLongestRoad.add(ls6);
    }

    /**
     * shows whose turn it is and your possibilities when it's your turn
     */
    @FXML
    public void colorStatus() {
        javafx.application.Platform.runLater(() -> {
            deactivateButtons();
            for (int i = 0; i < Register.getController().getSequence().size(); i++) {
                boolean yourTurn = Register.getController().getSequence().get(i).getActive();
                if (i == 0 && yourTurn) {
                    playerStatus.get(0).setFill(Color.GREEN);
                    switch (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getStatus()) {
                        case BUILD_SETTLEMENT:
                            state.setText("[Set-up phase] Build one settlement");
                            break;
                        case BUILD_STREET:
                            state.setText("[Set-up phase] Build one street");
                            break;
                        case ROLL_DICE:
                            state.setText("Your turn! Roll the dices");
                            rollButton.setDisable(false);
                            break;
                        case HAND_IN_CARDS_BECAUSE_OF_ROBBER:
                            state.setText("Hand in half of your cards");
                            break;
                        case MOVE_ROBBER:
                            state.setText("Move the robber to a new position");
                            break;
                        case TRADE_OR_BUILD:
                            state.setText("Now trade, build or end your turn");
                            break;
                        default:
                            state.setText("null");
                    }
                    //opponents' turn
                } else if (i != 0 && yourTurn) {
                    playerStatus.get(i).setFill(Color.GREEN);
                    state.setText("Please wait");
                    //others: red
                } else {
                    playerStatus.get(i).setFill(Color.RED);
                }
            }
        });
    }

    /**
     * illegal actions will be avoided by deactivating buttons
     */
    @FXML
    public void deactivateButtons() {
        Platform.runLater(() -> {
            Register.getViewBuildController().deactivateButtons();
            devButton.setDisable(true);
            rollButton.setDisable(true);
            tradeButton.setDisable(true);
            endButton.setDisable(true);
            if (!Register.getNtwrkClient().getAi()) {
                switch (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getStatus()) {
                    case ROLL_DICE:
                        rollButton.setDisable(false);
                        devButton.setDisable(false);
                        break;
                    case TRADE_OR_BUILD:
                        tradeButton.setDisable(false);
                        devButton.setDisable(false);
                        endButton.setDisable(false);
                        break;
                    default:
                        LOGGER.info("Case not covered!");

                }
            }
        });
    }

    /**
     * all other players get identical background colors
     */
    @FXML
    public void setPlayerNames() {
        for (int i = 0; i < Register.getController().getSequence().size(); i++) {
            playerNames.get(i).setText(Register.getController().getSequence().get(i).getName());
            colors(i, playerColors.get(i));
            if (i != 0) {
                playerBoxes.get(i).setStyle("-fx-background-color: #FFDEAD;");
            }
        }
    }

    /**
     * Each player gets an icon with their chosen color
     */
    public void colors(int i, Rectangle r) {
        switch (Register.getController().getSequence().get(i).getColor()) {
            case RED:
                r.setFill(redSettle);
                break;
            case ORANGE:
                r.setFill(orangeSettle);
                break;
            case BLUE:
                r.setFill(blueSettle);
                break;
            case WHITE:
                r.setFill(whiteSettle);
                break;
            case NULL:
                r.setFill(Color.BLACK);
                break;

        }
    }


    /**
     * chat window is not editable
     */
    @FXML
    public void chatWindow() {
        chatWindow.setEditable(false);
        chatWindow.setDisable(false);
    }

    @FXML
    public void bindPlayerInfoVP() {
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < Register.getController().getSequence().size(); i++) {
                playerVPs.get(i).setText(String.valueOf(Register.getController().getSequence().get(i).getVictoryPoints()));
            }
        });
    }

    @FXML
    public void bindPlayerInfoDevCards() {
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < Register.getController().getSequence().size(); i++) {
                playerDevCards.get(i).setText(String.valueOf(Register.getController().getSequence().get(i).getDevelopmentDev()));
            }
        });
    }

    @FXML
    public void bindPlayerInfoRC() {
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < Register.getController().getSequence().size(); i++) {
                if (i == 0) {
                    playerResCount.get(i).setText(String.valueOf(Register.getController().getSequence().get(i).getAllResourceList().size()));
                } else {
                    playerResCount.get(i).setText(String.valueOf(Register.getController().getSequence().get(i).getResource()));
                }
            }
        });
    }

    @FXML
    public void bindPlayerKnightsCount() {
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < Register.getController().getSequence().size(); i++) {
                playerKnights.get(i).setText(String.valueOf(Register.getController().getSequence().get(i).getKnight()));
            }
        });
    }

    @FXML
    public void bindPayerInfoLongestRoad() {
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < Register.getController().getSequence().size(); i++) {
                if (Register.getController().getSequence().get(i).getLongestRoad()) {
                    giveRoadCard(i);
                }
            }
        });
    }

    @FXML
    public void bindPlayerInfoCertainLR() {
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < Register.getController().getSequence().size(); i++) {
                playerLongestRoad.get(i).setText(String.valueOf(Register.getController().getBoard().getLongestStreet(Register.getController().getSequence().get(i).getId())));
            }
        });
    }

    @FXML
    public void bindPlayerInfoLargestArmy() {
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < Register.getController().getSequence().size(); i++) {
                if (Register.getController().getSequence().get(i).getLargestArmy()) {
                    giveArmyCard(i);
                }
            }
        });
    }

    /**
     * longest road will be given, all others will be cleared
     */
    public void giveRoadCard(int i) {
        street1.setFill(Color.TRANSPARENT);
        street2.setFill(Color.TRANSPARENT);
        street3.setFill(Color.TRANSPARENT);
        street4.setFill(Color.TRANSPARENT);
        street5.setFill(Color.TRANSPARENT);
        street6.setFill(Color.TRANSPARENT);
        switch (i) {
            case 0:
                street1.setFill(road);
                break;
            case 1:
                street2.setFill(road);
                break;
            case 2:
                street3.setFill(road);
                break;
            case 3:
                street4.setFill(road);
                break;
            case 4:
                street5.setFill(road);
                break;
            case 5:
                street6.setFill(road);
                break;
            default:
                LOGGER.info("Case not covered");
        }
    }

    /**
     * largest army card will be given, all others will be cleared
     */
    public void giveArmyCard(int i) {
        army1.setFill(Color.TRANSPARENT);
        army2.setFill(Color.TRANSPARENT);
        army3.setFill(Color.TRANSPARENT);
        army4.setFill(Color.TRANSPARENT);
        army5.setFill(Color.TRANSPARENT);
        army6.setFill(Color.TRANSPARENT);
        switch (i) {
            case 0:
                army1.setFill(army);
                break;
            case 1:
                army2.setFill(army);
                break;
            case 2:
                army3.setFill(army);
                break;
            case 3:
                army4.setFill(army);
                break;
            case 4:
                army5.setFill(army);
                break;
            case 5:
                army6.setFill(army);
                break;
            default:
                LOGGER.info("Case not covered");
        }

    }

    /**
     * draw Edges with mouseClick Event
     */
    @FXML
    public void removeNonExistingPlayers() {
        int maxSize = 600;
        switch (Register.getController().getSequence().size()) {
            case 6:
                break;
            case 5:
                rightBox.getChildren().remove(pl6Box);
                chatBox.setPrefHeight(chatBox.getPrefHeight() + 100);
                chatWindow.setPrefHeight(chatWindow.getPrefHeight() + 100);
                break;
            case 4:
                rightBox.getChildren().removeAll(pl6Box, pl5Box);
                chatWindow.setPrefHeight(maxSize);
                chatBox.setPrefHeight(maxSize);
                break;
            case 3:
                rightBox.getChildren().removeAll(pl6Box, pl5Box);
                leftBox.getChildren().remove(pl4Box);
                leftBox.setPadding(new Insets(100, 8, 8, 8));
                chatWindow.setPrefHeight(maxSize);
                chatBox.setPrefHeight(maxSize);
                pl2Box.setPrefHeight(100);
                pl3Box.setPrefHeight(100);
                break;
        }
    }

    /**
     * Calls contains method and if the contains returns true,
     * the method calculates the edge associated with the click and gets the BitSet with the
     * 2 adjacent hexagons.
     *
     * @param check boolean
     * @param a
     */
    @FXML
    public void roadBuildingCardListener(boolean check, BitSet a) {
        javafx.application.Platform.runLater(() -> {
            layer4.setOnMouseClicked(event -> {
                // get point where the mouse clicked
                double x = event.getX();
                double y = event.getY();
                Point point = new Point(x, y);
                Edge tmp = new Edge();
                BitSet tmpBitSet;
                if (contains(point)) {
                    tmp = tmp.getTheRightEdge(point, this);
                    tmpBitSet = tmp.getAdjacentHexagonsForEdges(tmp);
                    if (!(canvasBoard.getBoard().getAllEdges().containsKey(tmpBitSet))) {
                        String all = "MouseEvent - edge not allowed: " + x + ", " + y;
                        LOGGER.info(all);
                    } else {
                        if (check) {
                            if (a.cardinality() < 2) {
                                gc4.clearRect(0, 0, gc4.getCanvas().getWidth(), gc4.getCanvas().getHeight());
                                Register.getViewBuildController().show = true;
                                String all = "MouseEvent - Clicked at: " + x + ", " + y + "\n" + tmp + tmpBitSet; //+ edgeNumber;
                                LOGGER.info(all);
                                Register.getViewClientController().getCanvasBoard().roadBuildingPainter(tmpBitSet);
                                roadBuildingCardListener(true, tmpBitSet);
                            } else {
                                new ClientWriter().roadBuildingCard(a, tmpBitSet);
                            }
                        } else {
                            new ClientWriter().roadBuildingCard(tmpBitSet);
                        }
                        gc4.clearRect(0, 0, gc4.getCanvas().getWidth(), gc4.getCanvas().getHeight());
                        Register.getViewBuildController().show = true;
                        String all = "MouseEvent - Clicked at: " + x + ", " + y + "\n" + tmp + tmpBitSet; //+ edgeNumber;
                        LOGGER.info(all);
                    }
                }
            });
        });
    }

    public void setEdgeIDsForServer(BitSet street) {
        new ClientWriter().tryToBuild(street, "Street");
    }

    /**
     * Returns a whether the settlement has already been built.
     *
     * @param ids
     * @return
     */
    public Boolean containsSettlement(BitSet ids) {
        Boolean unoccupied = false;
        ArrayList<model.board.Corner> unoccupiedSettlements = new ArrayList<>();
        unoccupiedSettlements = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableStartSettlements();
        unoccupiedSettlements.addAll(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableSettlements());
        for (int i = 0; i < unoccupiedSettlements.size(); i++) {
            if (unoccupiedSettlements.get(i).getBitSetID().equals(ids)) {
                unoccupied = true;
            }
        }
        return unoccupied;
    }

    /**
     * Builds settlement on clicked coordinates, by converting the area of the click into
     * the correct point. Then the method calculates the 3 adjacent hexagons, if they refer to
     * an existing and empty Corner the information will be given to the serve to build a settlement.
     */
    @FXML
    public void buildSettlement() {
        LOGGER.info("entered buildSettlement()");
        javafx.application.Platform.runLater(() -> {
            layer4.setOnMouseClicked(event -> {
                double x = event.getX();
                double y = event.getY();
                Point point = new Point(x, y);
                BitSet ids;
                if (contains(point)) {
                    try {
                        Point mPoint;
                        double xUpperCornerLeft = x - 12;
                        double yUpperCornerLeft = y - 12;
                        double xLowerCornerRight = x + 12;
                        double yLowerCornerRight = y + 12;
                        mPoint = point.getTheRightPoint(xUpperCornerLeft, xLowerCornerRight,
                                yUpperCornerLeft, yLowerCornerRight, this);
                        ids = mPoint.getAdjacentHexagons(mPoint, this);
                        if (!(canvasBoard.getBoard().getAllCorners().containsKey(ids))) {
                            String all = "MouseEvent - Clicked near Sea at or occupied location : " + x + ", " + y;
                            LOGGER.info(all);
                        } else {
                            if (containsSettlement(ids)) {
                                setCornerIDsForServer(ids);
                                gc4.clearRect(0, 0, gc4.getCanvas().getWidth(), gc4.getCanvas().getHeight());
                                String all = "MouseEvent - Clicked at: " + x + ", " + y + "\n" +
                                        "Will build settlement";
                                Register.getViewBuildController().show = true;
                                LOGGER.info(all);
                            } else {
                                LOGGER.info("Occupied: " + x + " , " + y);
                            }
                        }
                    } catch (NullPointerException ex) {
                        LOGGER.catching(Level.ERROR, ex);
                    }
                } else {
                    String all = "Clicked at unallowed coordinates";
                    LOGGER.info(all);
                }
            });
        });
    }

    @FXML
    public void buildCity() {
        javafx.application.Platform.runLater(() -> {
            layer4.setOnMouseClicked(event -> {
                double x = event.getX();
                double y = event.getY();
                Point point = new Point(x, y);
                BitSet ids;
                if (contains(point) && clickedOnSettlement(point)) {
                    try {
                        Point mPoint;
                        double xUpperCornerLeft = x - 12;
                        double yUpperCornerLeft = y - 12;
                        double xLowerCornerRight = x + 12;
                        double yLowerCornerRight = y + 12;
                        mPoint = point.getTheRightPoint(xUpperCornerLeft, xLowerCornerRight,
                                yUpperCornerLeft, yLowerCornerRight, this);
                        ids = mPoint.getAdjacentHexagons(mPoint, this);
                        if (!(canvasBoard.getBoard().getAllCorners().containsKey(ids))) {
                            String all = "MouseEvent - Clicked near Sea at: " + x + ", " + y;
                            LOGGER.info(all);
                        } else {
                            setCityCornerIDsForServer(ids);
                            gc4.clearRect(0, 0, gc4.getCanvas().getWidth(), gc4.getCanvas().getHeight());
                            String all = "MouseEvent - Clicked at: " + x + ", " + y + "\n" +
                                    "Will build settlement";
                            Register.getViewBuildController().show = true;
                            LOGGER.info(all);
                        }
                    } catch (NullPointerException ex) {
                        LOGGER.catching(Level.ERROR, ex);
                    }
                } else {
                    String all = "Clicked at unallowed coordinates";
                    LOGGER.info(all);
                }
            });
        });

    }

    /**
     * finds out whether or not the user clicked on his own settlement to replace it with fstDeveCounter city
     *
     * @param test
     * @return
     */
    public Boolean clickedOnSettlement(Point test) {
        ArrayList<Point> points = new ArrayList<>();
        Boolean result = false;
        for (model.board.Corner corner : Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableCities()) {
            Point generalPoint = Register.getViewClientController().getCanvasBoard().convertToPoint(corner.getBitSetID());
            points.add(generalPoint);
        }
        for (int i = 0; i < points.size(); i++) {
            if ((test.getX() > points.get(i).getX() - 12 && test.getY() > points.get(i).getY() - 12) &&
                    (test.getX() < points.get(i).getX() + 12 && test.getY() < points.get(i).getY() + 12)) {
                result = true;
            }

        }
        return result;
    }

    /**
     * Sets the corner ids for the settlements and gives them to the server.
     *
     * @param settlement BitSet
     */
    public void setCornerIDsForServer(BitSet settlement) {
        new ClientWriter().tryToBuild(settlement, "Settlement");
    }

    /**
     * Sets the corner ids for the cities and gives them to the server.
     *
     * @param city BitSet
     */
    public void setCityCornerIDsForServer(BitSet city) {
        new ClientWriter().tryToBuild(city, "City");
    }

    /**
     * Test if point is in polygon
     *
     * @param test point clicked
     * @return boolean (is clicked position equal to area?)
     */
    public boolean contains(Point test) {
        String all = "\n";
        int i;
        int j;
        ArrayList<Point> points = new ArrayList<>();
        for (i = 0; i < canvasBoard.getAllEdges().size(); i++) {
            Point corner1 = canvasBoard.getAllEdges().get(i).getCorner1();
            points.add(corner1);
            Point corner2 = canvasBoard.getAllEdges().get(i).getCorner2();
            points.add(corner2);
            Point corner3 = canvasBoard.getAllEdges().get(i).getCorner3();
            points.add(corner3);
            Point corner4 = canvasBoard.getAllEdges().get(i).getCorner4();
            points.add(corner4);
        }
        for (i = 0; i < canvasBoard.getAllHex().size(); i++) {
            for (j = 0; j < 6; j++) {
                Point generalCorner = canvasBoard.getAllHex().get(i).getAllPoints().get(j);
                points.add(generalCorner);
                Point cornerWithRange = new Point
                        (canvasBoard.getAllHex().get(i).getAllPoints().get(j).getX() + 10,
                                canvasBoard.getAllHex().get(i).getAllPoints().get(j).getY() + 10);
                points.add(cornerWithRange);
                Point cornerWithRange2 = new Point
                        (canvasBoard.getAllHex().get(i).getAllPoints().get(j).getX() - 10,
                                canvasBoard.getAllHex().get(i).getAllPoints().get(j).getY() - 10);
                points.add(cornerWithRange2);
            }
        }
        boolean result = false;
        for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if ((points.get(i).getY() > test.getY()) != (points.get(j).getY() > test.getY()) &&
                    (test.getX() < (points.get(j).getX() - points.get(i).getX()) * (test.getY() -
                            points.get(i).getY()) / (points.get(j).getY() - points.get(i).getY()) +
                            points.get(i).getX())) {
                //result = !result;
                result = true;
            }
        }
        LOGGER.info(all);
        return result;
    }

    /**
     * Build a street at the klicked area, only when it's possible
     */
    @FXML
    public void buildStreets() {
        javafx.application.Platform.runLater(() -> {

            layer4.setOnMouseClicked(event -> {
                if (!cardDevStreetOn) {
                    double x = event.getX();
                    double y = event.getY();
                    Point point = new Point(x, y);
                    Edge tmp = new Edge();
                    BitSet tmpBitSet;
                    for (int i = 0; i < canvasBoard.getPossibleStreetArea().size(); i = i + 4) {
                        double xStart = canvasBoard.getPossibleStreetArea().get(i);
                        double yStart = canvasBoard.getPossibleStreetArea().get(i + 1);
                        double xEnd = canvasBoard.getPossibleStreetArea().get(i + 2);
                        double yEnd = canvasBoard.getPossibleStreetArea().get(i + 3);
                        if (x >= xStart && x <= xEnd && y >= yStart && y <= yEnd) {
                            LOGGER.info("Inside -:" + i / 4 + " street");
                            tmp = tmp.getTheRightEdge(point, this);
                            tmpBitSet = tmp.getAdjacentHexagonsForEdges(tmp);
                            setEdgeIDsForServer(tmpBitSet);
                            gc4.clearRect(0, 0, gc4.getCanvas().getWidth(), gc4.getCanvas().getHeight());
                            Register.getViewBuildController().show = true;
                            return;
                        }
                    }
                } else if (cardDevStreetOn) {

                }

            });
        });
    }


    public CanvasBoard getCanvasBoard() {
        return canvasBoard;
    }

    /**
     * button to roll the dices
     */
    @FXML
    public void rollDicesHandler() {
        Register.getAudioClips().getDiceSound().play();
        new ClientWriter().rollDice();

    }

    /**
     * setting images of the dices
     *
     * @param x value one
     * @param y value two
     */
    @FXML
    public void rollDicesImages(int x, int y) {
        javafx.application.Platform.runLater(() -> {

            ArrayList<ImagePattern> diceImages = new ArrayList<>();
            diceImages.add(0, diceOne);
            diceImages.add(1, diceTwo);
            diceImages.add(2, diceThree);
            diceImages.add(3, diceFour);
            diceImages.add(4, diceFive);
            diceImages.add(5, diceSix);

            diceGc.setStroke(Color.BLACK);
            for (int i = 1; i <= 6; i++) {
                if (i == x) {
                    diceGc.setFill(diceImages.get(i - 1));
                    diceGc.strokeRoundRect(5, 15, 60, 60, 10, 10);
                    diceGc.fillRoundRect(5, 15, 60, 60, 10, 10);
                }
                if (i == y) {
                    diceGc.setFill(diceImages.get(i - 1));
                    diceGc.fillRoundRect(75, 15, 60, 60, 10, 10);
                    diceGc.strokeRoundRect(75, 15, 60, 60, 10, 10);
                }
            }

        });
    }

    @FXML
    public void buildHandler() throws Exception {
        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/view/client/build/build.fxml"));
        List<Node> parentChildren = ((Pane) replace1.getParent()).getChildren();
        parentChildren.set(parentChildren.indexOf(replace1), newLoadedPane);
        replace1 = newLoadedPane;
    }


    /**
     * Amount of each ressource will be shown
     *
     */
    @FXML
    public void bindResourceAmount() {
        javafx.application.Platform.runLater(() -> {
            brickAmount.setText(" " + String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick()));
            grainAmount.setText(" " + String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain()));
            woolAmount.setText(" " + String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool()));
            oreAmount.setText(" " + String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre()));
            lumberAmount.setText(" " + String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber()));
        });
    }

    /**
     * Add card to devCardStack when clients receive one, via observer
     */
    public void addDevCardToStack() {
        Platform.runLater(() -> {
            victoryCards = new ArrayList<>();
            victoryCards.add(chapelCardVP);
            victoryCards.add(marketCardVP);
            victoryCards.add(greatHallCardVP);
            victoryCards.add(universityCardVP);
            victoryCards.add(libraryCardVP);
            devCardStack = new ArrayList<>();

            for (int i = 0; i < Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getKnightsDev(); i++) {
                devCardStack.add(knightCard);
            }
            for (int i = 0; i < Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getMonopolyDev(); i++) {
                devCardStack.add(monopolyCard);
            }
            for (int i = 0; i < Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getRoadBuildingDev(); i++) {
                devCardStack.add(roadBuildingCard);
            }
            for (int i = 0; i < Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getYearOfPlentyDev(); i++) {
                devCardStack.add(yearOfPlentyCard);
            }
            for (int i = 0; i < Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getVictoryPointDev(); i++) {
                devCardStack.add(victoryCards.get(i));
            }
            paintDevStack();


        });

    }

    /**
     * Choose development cards of stack and play it
     *
     * @param rectangle fstDev or scdDeve or trdDeve
     */
    public void chooseDevCardOfStack(Rectangle rectangle) {
        rectangle.setOnMouseClicked(event -> {
            if (rectangle.getFill().equals(knightCard) && cardDev && ifPlayableCard("Ritter")) {
                Register.getViewClientController().getCanvasBoard().changeRobberPosition();
                paintDevStack();
            } else if (rectangle.getFill().equals(monopolyCard) && cardDev && ifPlayableCard("Monopol")) {
                openMonopol();
                paintDevStack();
            } else if (rectangle.getFill().equals(roadBuildingCard) && cardDev && ifPlayableCard("Straßenbau")) {
                Register.getViewClientController().getCanvasBoard().roadBuildingCardHelpMethod();
                paintDevStack();
            } else if (rectangle.getFill().equals(yearOfPlentyCard) && cardDev && ifPlayableCard("Erfindung")) {
                openYearOfPlenty();
                paintDevStack();
            } else if ((rectangle.getFill().equals(universityCardVP) || rectangle.getFill().equals(marketCardVP) || rectangle.getFill().equals(libraryCardVP) || rectangle.getFill().equals(chapelCardVP) ||
                    rectangle.getFill().equals(greatHallCardVP)) && (!cardDev || cardDev)) {
                appendConsoleOutputToTextArea("(i) No valid action. A Victorypoint Card can't be played. Please choose another card!\n");
            } else if (!cardDev) {
                appendConsoleOutputToTextArea("(i) No valid action. You can only play one development card per turn!\n");
            } else {
                appendConsoleOutputToTextArea("(i) No valid action. You can't play a bought development card in the same turn!\n");

            }
            fstDeve.setOnMouseClicked(this::switchChooseDevCard);
            scdDeve.setOnMouseClicked(null);
            trdDeve.setOnMouseClicked(this::switchChooseDevCard);
            devButton.setEffect(null);
            devButtonOnOff = true;

        });

    }

    /**
     * Test for playable development card. It's against the rules to play a card in the same turn.
     *
     * @param devCard String
     * @return if card is playable
     */
    public Boolean ifPlayableCard(String devCard) {
        Boolean temp = false;
        for (String string : developmentCardsCon) {
            if (devCard.equals(string)) {
                temp = true;
            }
        }
        return temp;
    }

    /**
     * Paint stack of development cards.
     *
     */
    public void paintDevStack() {
        if (devCardStack.isEmpty()) {
            fstDeve.setFill(Color.TRANSPARENT);
            scdDeve.setFill(null);
            trdDeve.setFill(null);
            devButton.setVisible(false);
        } else if (devCardStack.size() == 1) {
            devButton.setVisible(true);
            fstDeve.setFill(devCardStack.get(0));
            scdDeve.setFill(Color.TRANSPARENT);
            trdDeve.setFill(null);
            giveTooltip(fstDeve);
        } else if (devCardStack.size() == 2) {
            devButton.setVisible(true);
            fstDeve.setFill(devCardStack.get(0));
            scdDeve.setFill(devCardStack.get(1));
            trdDeve.setFill(null);
            giveTooltip(fstDeve);
            giveTooltip(scdDeve);
        } else {
            devButton.setVisible(true);
            fstDeve.setFill(devCardStack.get(0));
            scdDeve.setFill(devCardStack.get(1));
            trdDeve.setFill(devCardStack.get(2));
            giveTooltip();
            fstDeveCounter = 3;
            trdDeveCounter = devCardStack.size() - 1;
        }
    }

    /**
     * Switch cards in stack and choose one card. Deactivate and activate development button
     *
     * @param event setOnMouseClick
     */
    @FXML
    public void switchChooseDevCard(MouseEvent event) {
        Register.getAudioClips().getClick().play();
        if (event.getSource().equals(devButton) && devButtonOnOff) {
            DropShadow shadow = new DropShadow();
            devButton.setEffect(shadow);
            devButtonOnOff = false;
            gc4.clearRect(0, 0, gc4.getCanvas().getWidth(), gc4.getCanvas().getHeight());
            layer4.setOnMouseClicked(null);
            chooseDevCardOfStack(fstDeve);
            chooseDevCardOfStack(scdDeve);
            chooseDevCardOfStack(trdDeve);
        } else if (event.getSource().equals(devButton) && !devButtonOnOff) {
            devButton.setEffect(null);
            devButtonOnOff = true;
            gc4.clearRect(0, 0, gc4.getCanvas().getWidth(), gc4.getCanvas().getHeight());
            layer4.setOnMouseClicked(null);
            fstDeve.setOnMouseClicked(this::switchChooseDevCard);
            scdDeve.setOnMouseClicked(null);
            trdDeve.setOnMouseClicked(this::switchChooseDevCard);

        } else if (event.getSource().equals(fstDeve)) {
            if (devCardStack.size() > 3) {
                int passages = devCardStack.size();
                fstDeve.setFill(scdDeve.getFill());
                scdDeve.setFill(trdDeve.getFill());

                if (fstDeveCounter >= 3 && fstDeveCounter < passages) {
                    trdDeve.setFill(devCardStack.get(fstDeveCounter));
                    fstDeveCounter++;
                    giveTooltip();
                } else if (fstDeveCounter == passages) {
                    fstDeveCounter = 0;
                    trdDeve.setFill(devCardStack.get(fstDeveCounter));
                    fstDeveCounter++;
                    giveTooltip();
                } else {
                    trdDeve.setFill(devCardStack.get(fstDeveCounter));
                    fstDeveCounter++;
                    giveTooltip();
                }
                trdDeveCounter++;
                if (trdDeveCounter > devCardStack.size() - 1) {
                    trdDeveCounter = 0;
                }
            }

        } else if (event.getSource().equals(trdDeve)) {

            if (devCardStack.size() > 3) {
                int passages = devCardStack.size();
                trdDeve.setFill(scdDeve.getFill());
                scdDeve.setFill(fstDeve.getFill());
                if (trdDeveCounter >= 0) {
                    fstDeve.setFill(devCardStack.get(trdDeveCounter));
                    trdDeveCounter--;
                    giveTooltip();
                } else if (trdDeveCounter < 0) {
                    trdDeveCounter = passages - 1;
                    fstDeve.setFill(devCardStack.get(trdDeveCounter));
                    trdDeveCounter--;
                    giveTooltip();
                }
                fstDeveCounter--;
                if (fstDeveCounter < 0) {
                    fstDeveCounter = devCardStack.size() - 1;
                }
            }
        }
    }

    /**
     * Add tooltipps infos
     */
    public void addToolTips() {
        //DevelopmentCards
        infoToDeveCards.add(0, new Tooltip("Knight \nMove the Robber"));
        infoToDeveCards.add(1, new Tooltip("Road Building \nPlace 2 Roads as if you just built them"));
        infoToDeveCards.add(2, new Tooltip("Years of Plenty \nDraw 2 resource cards of your choice from the bank"));
        infoToDeveCards.add(3, new Tooltip("Monopoly \nClaim all resource cards of fstDeveCounter specific declared type"));
        infoToDeveCards.add(4, new Tooltip("Victory Point \nOne additional Victory Point is added to your total amount and doesn't need to be played to win."));
        infoToDeveCards.add(5, new Tooltip("First buy fstDeveCounter development card!"));
        //playerBoxes
        Tooltip.install(pl2Box, new Tooltip("These are information about player 2"));
        Tooltip.install(pl3Box, new Tooltip("These are information about player 3"));
        Tooltip.install(pl4Box, new Tooltip("These are information about player 4"));
        Tooltip.install(pl5Box, new Tooltip("These are information about player 5"));
        Tooltip.install(pl6Box, new Tooltip("These are information about player 6"));
        //Buttons
        sendButton.setTooltip(new Tooltip("Send entered text to other players"));
        rollButton.setTooltip(new Tooltip("Roll the dices when it's your turn"));
        endButton.setTooltip(new Tooltip("When you're done please end your turn"));
    }

    /**
     * Add tooltips to the development stack
     */
    private void giveTooltip() {
        giveTooltip(fstDeve);
        giveTooltip(scdDeve);
        giveTooltip(trdDeve);
    }

    /**
     * Setting development cards tooltips
     *
     * @param r rectangle
     */
    private void giveTooltip(Rectangle r) {
        if (r.getFill().equals(knightCard)) {
            Tooltip.install(r, infoToDeveCards.get(0));
        } else if (r.getFill().equals(monopolyCard)) {
            Tooltip.install(r, infoToDeveCards.get(3));
        } else if (r.getFill().equals(universityCardVP) || r.getFill().equals(marketCardVP) || r.getFill().equals(libraryCardVP) || r.getFill().equals(chapelCardVP) ||
                r.getFill().equals(greatHallCardVP)) {
            Tooltip.install(r, infoToDeveCards.get(4));
        } else if (r.getFill().equals(roadBuildingCard)) {
            Tooltip.install(r, infoToDeveCards.get(1));
        } else if (r.getFill().equals(yearOfPlentyCard)) {
            Tooltip.install(r, infoToDeveCards.get(2));
        } else {
            Tooltip.install(r, infoToDeveCards.get(5));
        }
    }

    /**
     * End of turn handler
     */
    @FXML
    public void endHandler() {
        Register.getAudioClips().getClick().play();
        gc4.clearRect(0, 0, gc4.getCanvas().getWidth(), gc4.getCanvas().getHeight());
        layer4.setOnMouseClicked(null);
        cardDev = true;
        removeDevCardMove();
        Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).moveDevelopmentcards();
        new ClientWriter().endTurn();
    }

    /**
     * Move the bought development cards of the current turn to the playable stack of development cards
     */
    public void removeDevCardMove() {
        if (developmentCardsMoveCon.size() > 0) {
            developmentCardsCon.add(developmentCardsMoveCon.get(0));
            developmentCardsMoveCon = new ArrayList<>();
        }
    }

    /**
     * Open popup to rob from others
     */
    public void openRobFromOthers() {
        javafx.application.Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                root = FXMLLoader.load(getClass().getResource("/view/client/robFromOthersPopUP/robFromOthers.fxml"));
                Scene scene = new Scene(root);
                stage.initStyle((StageStyle.DECORATED));          // system bar only displays close button
                stage.setOnCloseRequest(Event::consume);        // disable close button
                stage.initModality(Modality.APPLICATION_MODAL); // events only registered at popup
                stage.setResizable(false);                      // disable resizing
                stage.setTitle(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getName() + " rob a resource from another player");
                stage.setScene(scene);                          // set scene to stage
                stage.showAndWait();
            } catch (IOException e) {
                LOGGER.catching(Level.ERROR, e);
            }
        });
    }

    /**
     * Handle connection lost
     *
     */
    @FXML
    public void connectionLost() {
        javafx.application.Platform.runLater(() -> {
            Register.setAlertPopUp(this);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Connection lost");
            alert.setContentText("One player left the game.\n");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * Open trade popup
     *
     * @throws IOException Exception when fxml file is not correct
     */
    @FXML
    public void openTrade() throws IOException {
        javafx.application.Platform.runLater(() -> {
            try {
                Register.getAudioClips().getClick().play();
                stage = new Stage();
                root = FXMLLoader.load(getClass().getResource("/view/client/trade/send/tradeselector.fxml"));
                Scene scene = new Scene(root);
                stage.initStyle((StageStyle.DECORATED));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle("Trade resources");
                stage.setScene(scene);
                stage.showAndWait();

            } catch (IOException e) {
                LOGGER.catching(Level.ERROR, e);
            }
        });
    }

    /**
     * Send chat message with ENTER pressed
     *
     * @param ev KeyEvent
     */
    @FXML
    public void sendEnterHandler(KeyEvent ev) {
        if (ev.getCode().equals(KeyCode.ENTER)) {
            if (!chatField.getText().equals("")) {
                Register.getNtwrkClient().sendMsg(chatField.getCharacters().toString());
                chatField.clear();
            }
        }
    }

    /**
     * Send chat message with button pressed
     *
     */
    @FXML
    public void sendHandler() {
        Register.getAudioClips().getClick().play();
        if (!chatField.getText().equals("")) {
            Register.getNtwrkClient().sendMsg(chatField.getCharacters().toString());
            chatField.clear();
        }
    }

    /**
     * Append text to textarea
     *
     * @param text text (string)
     */
    public synchronized void appendConsoleOutputToTextArea(String text) {
        javafx.application.Platform.runLater(() -> {
            try {
                chatWindow.appendText(text);
            } catch (Exception e) {
                LOGGER.catching(Level.ERROR, e);
            }
        });
    }

    /**
     * Open popup "hand in cards" -> player > 7 resources
     *
     */
    public void handInCards() {
        javafx.application.Platform.runLater(() -> {
            try {
                stage = new Stage();
                root = FXMLLoader.load(getClass().getResource("/view/client/robberPopUp/robber.fxml"));
                Scene scene = new Scene(root);
                stage.initStyle((StageStyle.DECORATED));          // system bar only displays close button
                stage.setOnCloseRequest(Event::consume);        // disable close button
                stage.initModality(Modality.APPLICATION_MODAL); // events only registered at popup
                stage.setResizable(false);                      // disable resizing
                stage.setTitle(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getName() + " hand in half of your resources");
                stage.setScene(scene);                          // set scene to stage
                stage.showAndWait();                            // wait until popup gets closed
            } catch (IOException ex) {
                LOGGER.catching(Level.ERROR, ex);
            }
        });
    }

    /**
     * Open popup "trade request" if one player offers a trade
     *
     */
    public void incomingTradeRequest() {
        javafx.application.Platform.runLater(() -> {
            try {
                stage = new Stage();
                root = FXMLLoader.load(getClass().getResource("/view/client/trade/receive/tradeRequest.fxml"));
                Scene scene = new Scene(root);
                stage.initStyle((StageStyle.DECORATED));          // system bar only displays close button
                stage.setOnCloseRequest(Event::consume);        // disable close button
                stage.initModality(Modality.APPLICATION_MODAL); // events only registered at popup
                stage.setResizable(false);                      // disable resizing
                stage.setTitle(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getName() + " you got an incoming trade request");
                stage.setScene(scene);                          // set scene to stage
                stage.showAndWait();                            // wait until popup gets closed
            } catch (IOException ex) {
                LOGGER.catching(Level.ERROR, ex);
            }
        });
    }

    /**
     * Open popup "endgame" when game is over
     *
     */
    @FXML
    public void openEndGame() {
        javafx.application.Platform.runLater(() -> {
            try {
                stage = new Stage();
                root = FXMLLoader.load(getClass().getResource("/view/client/endGamePopUp/endGame.fxml"));
                Scene scene = new Scene(root);
                stage.initStyle((StageStyle.DECORATED));          // system bar only displays close button
                stage.setOnCloseRequest(Event::consume);          // disable close button
                stage.initModality(Modality.APPLICATION_MODAL); // events only registered at popup
                stage.setResizable(false);                      // disable resizing
                stage.setTitle(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getName() + " - Game Over!");
                stage.setScene(scene);                          // set scene to stage
                stage.showAndWait();
            } catch (IOException ex) {
                LOGGER.catching(Level.ERROR, ex);
            }
        });
    }

    /**
     * Open popup "development card monopol" to choose a resource for monopol
     *
     */
    @FXML
    public void openMonopol() {
        javafx.application.Platform.runLater(() -> {
            try {
                stage = new Stage();
                root = FXMLLoader.load(getClass().getResource("/view/client/devCardsPopUp/monopoly.fxml"));
                Scene scene = new Scene(root);
                stage.initStyle((StageStyle.DECORATED));          // system bar only displays close button
                stage.setOnCloseRequest(Event::consume);        // disable close button
                stage.initModality(Modality.APPLICATION_MODAL); // events only registered at popup
                stage.setResizable(false);                      // disable resizing
                stage.setTitle(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getName() + " demand one resource of all fellow players");
                stage.setScene(scene);                          // set scene to stage
                stage.showAndWait();
            } catch (IOException ex) {
                LOGGER.catching(Level.ERROR, ex);
            }
        });
    }

    /**
     * Open popup "development card year of plenty" to choose two resources
     *
     */
    @FXML
    public void openYearOfPlenty() {
        javafx.application.Platform.runLater(() -> {
            try {
                stage = new Stage();
                root = FXMLLoader.load(getClass().getResource("/view/client/devCardsPopUp/yearOfPlenty.fxml"));
                Scene scene = new Scene(root);
                stage.initStyle((StageStyle.DECORATED));
                stage.setOnCloseRequest(Event::consume);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getName() + " select two resources of your choosing");
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException ex) {
                LOGGER.catching(Level.ERROR, ex);
            }
        });
    }

    /**
     * Fades in given label.
     *
     * @param label
     */
    public void fadeIn(Label label) {
        FadeTransition fadeInTransition = new FadeTransition(Duration.millis(2000), label);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(1.0);
        fadeInTransition.play();
    }

    /**
     * Fades out given label
     *
     */
    public void fadeOut(Label label) {
        FadeTransition fadeInTransition = new FadeTransition(Duration.millis(5000), label);
        fadeInTransition.setFromValue(1.0);
        fadeInTransition.setToValue(0.0);
        fadeInTransition.play();
    }

    /**
     * Fades in and out status updates.
     *
     * @param types
     * @param size
     * @param farbe
     */
    public void triggerOwnStatus(EventTypes types, int size, Boolean farbe) {
        Platform.runLater(() -> {

            switch (types) {
                case ROAD:
                    stTrans.setTextFill(Color.LIGHTGREEN);
                    stTrans.setText("+ " + size);
                    fadeIn(stTrans);
                    fadeOut(stTrans);
                    break;
                case RESOURCES:
                    if (farbe) {
                        rsTrans.setTextFill(Color.LIGHTGREEN);
                        rsTrans.setText("+ " + size);
                    } else {
                        rsTrans.setTextFill(Color.RED);
                        rsTrans.setText("- " + size);
                    }
                    fadeIn(rsTrans);
                    fadeOut(rsTrans);
                    break;
                case DEVELOPMENTCARDS:
                    if (farbe) {
                        dcTrans.setTextFill(Color.LIGHTGREEN);
                        dcTrans.setText("+ " + size);
                    } else {
                        dcTrans.setTextFill(Color.RED);
                        dcTrans.setText("- " + size);
                    }
                    fadeIn(dcTrans);
                    fadeOut(dcTrans);
                    break;
                case VICTORYPOINTS:
                    if (farbe) {
                        vpTrans.setTextFill(Color.LIGHTGREEN);
                        vpTrans.setText("+ " + size);
                    } else {
                        vpTrans.setTextFill(Color.RED);
                        vpTrans.setText("- " + size);
                    }
                    fadeIn(vpTrans);
                    fadeOut(vpTrans);
                    break;
                case KNIGHTS:
                    knTrans.setTextFill(Color.LIGHTGREEN);
                    knTrans.setText("+ " + size);
                    fadeIn(knTrans);
                    fadeOut(knTrans);
                    break;
                default:
                    LOGGER.info(types);
            }
        });
    }

    /**
     * Fades in and out number of resources.
     *
     * @param resources
     * @param size
     * @param b
     * @param id
     */
    public void triggerFading(ArrayList<Resource> resources, int size, Boolean b, int id) {
        Platform.runLater(() -> {
            LOGGER.info("Fade in Resources");
            int brick = 0;
            int grain = 0;
            int lumber = 0;
            int wool = 0;
            int ore = 0;
            if (id == Register.getNtwrkClient().getId()) {
                for (int i = 0; i < resources.size(); i++) {
                    switch (resources.get(i)) {
                        case BRICK:
                            brick++;
                            break;
                        case GRAIN:
                            grain++;
                            break;
                        case LUMBER:
                            lumber++;
                            break;
                        case ORE:
                            ore++;
                            break;
                        case WOOL:
                            wool++;
                            break;
                    }
                }
                Color color;
                String sign;
                if (b) {
                    color = Color.LIGHTGREEN;
                    sign = "+ ";
                } else {
                    color = Color.RED;
                    sign = "- ";
                }
                if (brick != 0) {
                    brickTrans.setTextFill(color);
                    brickTrans.setText(sign + brick);
                    fadeIn(brickTrans);
                    fadeOut(brickTrans);
                }
                if (grain != 0) {
                    grainTrans.setTextFill(color);
                    grainTrans.setText(sign + grain);
                    fadeIn(grainTrans);
                    fadeOut(grainTrans);
                }
                if (wool != 0) {
                    woolTrans.setTextFill(color);
                    woolTrans.setText(sign + wool);
                    fadeIn(woolTrans);
                    fadeOut(woolTrans);
                }
                if (lumber != 0) {
                    lumberTrans.setTextFill(color);
                    lumberTrans.setText(sign + lumber);
                    fadeIn(lumberTrans);
                    fadeOut(lumberTrans);


                }
                if (ore != 0) {
                    oreTrans.setTextFill(color);
                    oreTrans.setText(sign + ore);
                    fadeIn(oreTrans);
                    fadeOut(oreTrans);
                }
            }
        });
    }

    /**
     * Play different kind of sounds. Enums EventType
     *
     * @param event type of sound
     */
    public void playTune(EventTypes event) {
        Platform.runLater(() -> {
            switch (event) {
                case BUILDINGS:
                    Register.getAudioClips().getBuildSound().play();
                    break;
                case CITY:
                    Register.getAudioClips().getBuildSound().play();
                    break;
                case LARGESTARMY:
                    Register.getAudioClips().getMarchSound().play();
                    break;
                case RESOURCES:
                    break;
                case ROBBER:
                    Register.getAudioClips().getRobberSound().play();
                    break;
                case ROAD:
                    Register.getAudioClips().getBuildSound().play();
                    break;
                case DEVELOPMENTCARDS:
                    Register.getAudioClips().getDevSound().play();
                    break;
                case KNIGHTS:
                    Register.getAudioClips().getKnightSound().play();
                    break;
                case LONGESTROAD:
                    Register.getAudioClips().getRoadSound().play();
                    break;
                case SETTLEMENT:
                    Register.getAudioClips().getBuildSound().play();
                    break;
                case VICTORYPOINTS:
                    Register.getAudioClips().getVicSound().play();
                default:
                    LOGGER.info(event);
            }
        });
    }

    /**
     * Transition / animation for the developments cards
     *
     * @param dc development card
     * @param id player
     */
    public void devCardAnimation(DevelopmentCard dc, int id) {
        Platform.runLater(() -> {
            int tmp = Register.getNtwrkClient().getId();
            if (id == Register.getNtwrkClient().getId()) {
                LOGGER.info("activate animation");
                Rectangle r = new Rectangle(1000, 700, 65, 80);
                pane.getChildren().add(r);
                if (dc instanceof KnightCard) {
                    r.setFill(new ImagePattern(new Image("devcards/Knight2.png")));
                    animateCardMovement(r);
                } else if (dc instanceof YearOfPlentyCard) {
                    r.setFill(new ImagePattern(new Image("devcards/YearOfPlanty2.png")));
                    animateCardMovement(r);
                } else if (dc instanceof MonopolyCard) {
                    r.setFill(new ImagePattern(new Image("devcards/Monopoly2.png")));
                    animateCardMovement(r);
                } else if (dc instanceof RoadBuildingCard) {
                    r.setFill(new ImagePattern(new Image("devcards/RoadBuilding2.png")));
                    animateCardMovement(r);
                    //TODO: different cardTypes via swicth? server?
                } else if (dc instanceof VictoryPointCard) {
                    r.setFill(new ImagePattern(new Image("devcards/University2.png")));
                    animateCardMovement(r);
                }
            } else {
            }
        });
    }

    //movement devCard

    /**
     * Buy process development card animaton
     *
     * @param dc rectangle
     */

    public void animateCardMovement(Rectangle dc) {
        Platform.runLater(() -> {
            FadeTransition fadeInTransition = new FadeTransition(Duration.millis(200), dc);
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);
            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setDuration(Duration.seconds(2.0));
            translateTransition.setToX(-500);
            translateTransition.setToY(-350);
            translateTransition.setNode(dc);
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2.0), dc);
            scaleTransition.setToX(3);
            scaleTransition.setToY(3);
            TranslateTransition translateTransition2 = new TranslateTransition(Duration.seconds(1), dc);
            translateTransition2.setToX(0);
            translateTransition2.setToY(350);
            ScaleTransition scaleTransition2 = new ScaleTransition(Duration.seconds(0.5), dc);
            scaleTransition2.setToX(0.5);
            scaleTransition2.setToY(0.5);
            ParallelTransition parallelTransition = new ParallelTransition(fadeInTransition, translateTransition, scaleTransition);
            SequentialTransition sequentialTransition = new SequentialTransition(dc, parallelTransition, scaleTransition2, translateTransition2);
            sequentialTransition.play();
            Thread t = new HelpAnimation(3500, dc);
            t.start();
        });
    }

    /**
     * Animates given node.
     *
     * @param i Rectangle
     */
    public void popRotate(Rectangle i) {
        Platform.runLater(() -> {
            FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), i);
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);
            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setDuration(Duration.seconds(2));
            translateTransition.setToX(500);
            translateTransition.setToY(350);
            translateTransition.setNode(i);
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2.0), i);
            scaleTransition.setToX(3);
            scaleTransition.setToY(3);
            FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(2500), i);
            fadeOutTransition.setFromValue(1.0);
            fadeOutTransition.setToValue(0.0);
            ParallelTransition parallelTransition = new ParallelTransition(i, fadeInTransition, translateTransition, scaleTransition);
            SequentialTransition sequentialTransition = new SequentialTransition(i, parallelTransition, fadeOutTransition);
            sequentialTransition.play();
            Thread t = new HelpAnimation(5000, i);
            t.start();
        });
    }


    public void statusAnimation(EventTypes event, int id) {
        Platform.runLater(() -> {
            if (id == Register.getNtwrkClient().getId()) {
                Rectangle rectangle = new Rectangle(0, 0);
                Rectangle rectangle2 = new Rectangle(0, 0);
                switch (event) {
                    case LONGESTROAD:
                        rectangle = new Rectangle(0, 0, 80, 80);
                        rectangle.setFill(new ImagePattern(new Image("Information/LHandelsstrasse2.png")));
                        pane.getChildren().add(rectangle);
                        popRotate(rectangle);
                        break;
                    case LARGESTARMY:
                        rectangle2 = new Rectangle(0, 0, 80, 80);
                        rectangle2.setFill(new ImagePattern(new Image("Information/GrRittermacht2.png")));
                        pane.getChildren().add(rectangle2);
                        popRotate(rectangle2);
                        break;

                }
            }
        });
    }

    @FXML
    /**
     * Clears animation, necessary for canvas to register clicks again.
     *
     */
    public void clearAnimation(Rectangle rectangle) {
        javafx.application.Platform.runLater(() -> {
            pane.getChildren().remove(rectangle);
            LOGGER.info("\nCleared Animation");
        });
    }


    /**
     * Getter developmentCardsMove
     *
     * @return card which was bought in the same turn
     */
    public ArrayList<String> getDevelopmentCardsMoveCon() {
        return developmentCardsMoveCon;
    }

    /**
     * Getter developmentCard
     *
     * @return cards which can be played
     */
    public ArrayList<String> getDevelopmentCardsCon() {
        return developmentCardsCon;
    }

    public void setCardDev(Boolean cardDev) {
        this.cardDev = cardDev;
    }

    public GraphicsContext getGc4() {
        return gc4;
    }

}

