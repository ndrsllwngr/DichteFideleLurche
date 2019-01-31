package view.client.trade.send.harborTrade;

import controller.Register;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Resource;
import network.client.ClientWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class oneResourceHarborController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(oneResourceHarborController.class.getName());
    Stage stage;
    @FXML
    Button confirmButton;
    Image brickImg = new Image("/Ressourcen/rs_brick3.png");
    Image grainImg = new Image("/Ressourcen/rs_grain3.png");
    Image lumberImg = new Image("/Ressourcen/rs_lumber3.png");
    Image oreImg = new Image("/Ressourcen/rs_ore3.png");
    Image woolImg = new Image("/Ressourcen/rs_wool3.png");
    Image in = new Image("/icons/tradeArrowDown.png");
    Image out = new Image("/icons/tradeArrowUp.png");
    @FXML
    Spinner<Integer> resourceCnt;
    @FXML
    ImageView resourceImg;
    @FXML
    Spinner<Integer> woolCnt1, grainCnt1, oreCnt1, brickCnt1, lumberCnt1;
    @FXML
    ImageView woolImgV1, grainImgV1, oreImgV1, brickImgV1, lumberImgV1;
    @FXML
    Label managementLabel, requiredLabel, leftLabel, selectedLabel;
    @FXML
    private ImageView arrowIn, arrowOut;
    private int mngmnt, req, sel, left;
    private int resourceClient;

    private int woolNumMng, grainNumMng, oreNumMng, brickNumMng, lumberNumMng = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mngmnt = 0;
        req = 0;
        sel = 0;
        left = 0;
        managementLabel.setText("" + mngmnt);
        requiredLabel.setText("" + req);
        selectedLabel.setText("" + sel);
        leftLabel.setText("" + left);
        Register.setViewTradeSendHarborTradeBrickController(this);
        drawImg();
        activateSpinners();
        addToolTips();
    }

    /**
     * images will be loaded
     * switch: depending on which harbor you have, associated resource will be loaded
     */
    private void drawImg() {
        //all
        woolImgV1.setImage(woolImg);
        grainImgV1.setImage(grainImg);
        oreImgV1.setImage(oreImg);
        brickImgV1.setImage(brickImg);
        lumberImgV1.setImage(lumberImg);
        arrowIn.setImage(in);
        arrowOut.setImage(out);
        //resource
        switch (Register.getHarborTrade()) {
            case BRICK:
                resourceImg.setImage(brickImg);
                resourceClient = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick();
                break;
            case GRAIN:
                resourceImg.setImage(grainImg);
                resourceClient = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain();
                break;
            case LUMBER:
                resourceImg.setImage(lumberImg);
                resourceClient = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber();
                break;
            case WOOL:
                resourceImg.setImage(woolImg);
                resourceClient = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool();
                break;
            case ORE:
                resourceImg.setImage(oreImg);
                resourceClient = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre();
                break;
            default:
                LOGGER.info("Case not covered!");
        }
    }

    /**
     * Spinners will be initialized
     */
    private void activateSpinners() {
        int step = 2;
        final int initialValue = 0;
        //rs of mng
        int maxWool = resourceClient / 2;
        int maxGrain = resourceClient / 2;
        int maxOre = resourceClient / 2;
        int maxBrick = resourceClient / 2;
        int maxLumber = resourceClient / 2;

        //client
        SpinnerValueFactory<Integer> resourceRangeClient =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, resourceClient, initialValue, step);
        resourceCnt.setValueFactory(resourceRangeClient);
        resourceCnt.setEditable(true);
        resourceCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setResourceNumClient(newValue);
            updateInfo();
        });

        //management
        SpinnerValueFactory<Integer> woolRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxWool, initialValue);
        SpinnerValueFactory<Integer> grainRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxGrain, initialValue);
        SpinnerValueFactory<Integer> oreRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxOre, initialValue);
        SpinnerValueFactory<Integer> brickRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxBrick, initialValue);
        SpinnerValueFactory<Integer> lumberRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxLumber, initialValue);
        woolCnt1.setValueFactory(woolRange);
        woolCnt1.setEditable(true);
        grainCnt1.setValueFactory(grainRange);
        grainCnt1.setEditable(true);
        oreCnt1.setValueFactory(oreRange);
        oreCnt1.setEditable(true);
        brickCnt1.setValueFactory(brickRange);
        brickCnt1.setEditable(true);
        lumberCnt1.setValueFactory(lumberRange);
        lumberCnt1.setEditable(true);

        woolCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setWoolNumMng(newValue);
            updateInfo();
            if (newValue != 0) {
                disableMngButtons(true);
                woolCnt1.setDisable(false);
            } else {
                disableMngButtons(false);
            }

        });
        grainCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setGrainNumMng(newValue);
            updateInfo();
            if (newValue != 0) {
                disableMngButtons(true);
                grainCnt1.setDisable(false);
            } else {
                disableMngButtons(false);
            }

        });
        oreCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setOreNumMng(newValue);
            updateInfo();
            if (newValue != 0) {
                disableMngButtons(true);
                oreCnt1.setDisable(false);
            } else {
                disableMngButtons(false);
            }

        });
        brickCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setBrickNumMng(newValue);
            updateInfo();
            if (newValue != 0) {
                disableMngButtons(true);
                brickCnt1.setDisable(false);
            } else {
                disableMngButtons(false);
            }

        });
        lumberCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setLumberNumMng(newValue);
            updateInfo();
            if (newValue != 0) {
                disableMngButtons(true);
                lumberCnt1.setDisable(false);
            } else {
                disableMngButtons(false);
            }
        });


    }

    /**
     * if spinners used, information will be updated
     */
    private void updateInfo() {
        mngmnt = getSel();
        req = mngmnt * 2;
        int left = req - getResourceNumClient();
        managementLabel.setText("" + mngmnt);
        requiredLabel.setText("" + req);
        selectedLabel.setText("" + getResourceNumClient());
        leftLabel.setText("" + left);
        if (left == 0) {
            confirmButton.setDisable(false);
        } else {
            confirmButton.setDisable(true);
        }
    }

    /**
     * all buttons will be enabled/disabled
     *
     * @param b boolean disable
     */
    private void disableMngButtons(boolean b) {
        woolCnt1.setDisable(b);
        grainCnt1.setDisable(b);
        lumberCnt1.setDisable(b);
        brickCnt1.setDisable(b);
        oreCnt1.setDisable(b);
    }

    /**
     * information will be shown when hovered
     */
    private void addToolTips() {
        confirmButton.setTooltip(new Tooltip("Click amount you want to trade"));
    }

    /**
     * when clicked on, amount of resources will be sent to server
     */
    @FXML
    public void confirm() {
        Register.getAudioClips().getClick().play();
        ArrayList<Resource> offeredResourcesList = new ArrayList<>();
        ArrayList<Resource> requestedResourcesList = new ArrayList<>();

        for (int i = 0; i < getBrickNumMng(); i++) {
            requestedResourcesList.add(Resource.BRICK);
        }
        for (int i = 0; i < getGrainNumMng(); i++) {
            requestedResourcesList.add(Resource.GRAIN);
        }
        for (int i = 0; i < getLumberNumMng(); i++) {
            requestedResourcesList.add(Resource.LUMBER);
        }
        for (int i = 0; i < getOreNumMng(); i++) {
            requestedResourcesList.add(Resource.ORE);
        }
        for (int i = 0; i < getWoolNumMng(); i++) {
            requestedResourcesList.add(Resource.WOOL);
        }
        for (int i = 0; i < getResourceNumClient(); i++) {
            offeredResourcesList.add(Register.getHarborTrade());

        }

        new ClientWriter().tradeSea(offeredResourcesList, requestedResourcesList);
        stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    private int getResourceNumClient() {
        return resourceClient;
    }

    private void setResourceNumClient(int resourceClient) {
        this.resourceClient = resourceClient;
    }

    private int getSel() {
        return getBrickNumMng() + getGrainNumMng() + getOreNumMng() + getWoolNumMng() + getLumberNumMng();
    }

    private int getWoolNumMng() {
        return woolNumMng;
    }

    private void setWoolNumMng(int woolNumMng) {
        this.woolNumMng = woolNumMng;
    }

    private int getBrickNumMng() {
        return brickNumMng;
    }

    private void setBrickNumMng(int brickNumMng) {
        this.brickNumMng = brickNumMng;
    }

    private int getGrainNumMng() {
        return grainNumMng;
    }

    private void setGrainNumMng(int grainNumMng) {
        this.grainNumMng = grainNumMng;
    }

    private int getOreNumMng() {
        return oreNumMng;
    }

    private void setOreNumMng(int oreNumMng) {
        this.oreNumMng = oreNumMng;
    }

    private int getLumberNumMng() {
        return lumberNumMng;
    }

    private void setLumberNumMng(int lumberNumMng) {
        this.lumberNumMng = lumberNumMng;
    }

    /**
     * go back to trade menu
     * @throws IOException when fxml is incorrect
     */
    @FXML
    public void goBack() throws IOException {
        Register.getAudioClips().getClick().play();
        stage = (Stage) woolImgV1.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/view/client/trade/send/tradeselector.fxml"));
        Scene scene = new Scene(root, 650, 550);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}

