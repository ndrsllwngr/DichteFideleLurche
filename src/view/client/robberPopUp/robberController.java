package view.client.robberPopUp;

import controller.Register;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Resource;
import network.client.ClientWriter;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class robberController implements Initializable {
    public GraphicsContext myGc; //for trading
    Stage stage;
    @FXML
    Button confirmButton;
    Image brickImg = new Image("/Ressourcen/rs_brick3.png");
    Image grainImg = new Image("/Ressourcen/rs_grain3.png");
    Image lumberImg = new Image("/Ressourcen/rs_lumber3.png");
    Image oreImg = new Image("/Ressourcen/rs_ore3.png");
    Image woolImg = new Image("/Ressourcen/rs_wool3.png");
    Image out = new Image("/icons/tradeArrowUp.png");


    @FXML
    Spinner<Integer> woolCnt, grainCnt, oreCnt, brickCnt, lumberCnt;
    @FXML
    ImageView woolImgV, grainImgV, oreImgV, brickImgV, lumberImgV, arrowOut;
    @FXML
    Label woolLabel, grainLabel, oreLabel, brickLabel, lumberLabel;
    @FXML
    Label sumLabel, leftLabel, selectedLabel;
    private int sum, sel, left;
    private int woolNum, grainNum, oreNum, brickNum, lumberNum = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sum = Math.floorDiv(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getAllResourceList().size(), 2);
        sel = 0;
        left = Math.floorDiv(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getAllResourceList().size(), 2);
        sumLabel.setText("" + sum);
        selectedLabel.setText("" + sel);
        leftLabel.setText("" + left);
        drawImg();
        disableButtons();
        activateSpinners();
        addToolTips();
        setAmountOfResources();
    }

    /**
     * images will be drawn
     */
    private void drawImg() {
        woolImgV.setImage(woolImg);
        grainImgV.setImage(grainImg);
        oreImgV.setImage(oreImg);
        brickImgV.setImage(brickImg);
        lumberImgV.setImage(lumberImg);
        arrowOut.setImage(out);
    }

    /**
     * your amount of resources will be shown
     */
    private void setAmountOfResources() {
        lumberLabel.setText(String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber()));
        grainLabel.setText(String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain()));
        oreLabel.setText(String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre()));
        brickLabel.setText(String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick()));
        woolLabel.setText(String.valueOf(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool()));
    }

    /**
     * spinners will have a range from 0 to maximum of what you possess
     */
    private void activateSpinners() {
        final int initialValue = 0;
        int maxWool = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool();
        int maxGrain = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain();
        int maxOre = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre();
        int maxBrick = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick();
        int maxLumber = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber();
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
        woolCnt.setValueFactory(woolRange);
        woolCnt.setEditable(true);
        grainCnt.setValueFactory(grainRange);
        grainCnt.setEditable(true);
        oreCnt.setValueFactory(oreRange);
        oreCnt.setEditable(true);
        brickCnt.setValueFactory(brickRange);
        brickCnt.setEditable(true);
        lumberCnt.setValueFactory(lumberRange);
        lumberCnt.setEditable(true);
        woolCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setWoolNum(newValue);
            updateInfo();
        });
        grainCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setGrainNum(newValue);
            updateInfo();
        });
        oreCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setOreNum(newValue);
            updateInfo();
        });
        brickCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setBrickNum(newValue);
            updateInfo();
        });
        lumberCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setLumberNum(newValue);
            updateInfo();
        });

    }

    /**
     * information which updates how many resources you've selected/ are missing. 'OK' only clickable when selected righ amount
     */
    private void updateInfo() {
        int a = sum - getSel();
        selectedLabel.setText("" + getSel());
        leftLabel.setText("" + a);
        if (getSel() == sum) {
            confirmButton.setDisable(false);
        } else {
            confirmButton.setDisable(true);
        }
    }

    /**
     * unnecessary buttons will be disabled
     */
    private void disableButtons() {
        confirmButton.setDisable(true);
    }

    /**
     * information will be shown when hovered
     */
    private void addToolTips() {
        confirmButton.setTooltip(new Tooltip("Click ok to let robber steal these cards"));
    }

    /**
     * selected amount of each resource will be sent to server
     */
    @FXML
    public void confirm() {
        Register.getAudioClips().getClick().play();
        ArrayList<Resource> resources = new ArrayList<>();
        for (int i = 0; i < getBrickNum(); i++) {
            resources.add(Resource.BRICK);
        }
        for (int i = 0; i < getGrainNum(); i++) {
            resources.add(Resource.GRAIN);
        }
        for (int i = 0; i < getLumberNum(); i++) {
            resources.add(Resource.LUMBER);
        }
        for (int i = 0; i < getOreNum(); i++) {
            resources.add(Resource.ORE);
        }
        for (int i = 0; i < getWoolNum(); i++) {
            resources.add(Resource.WOOL);
        }
        new ClientWriter().handInResources(resources);
        stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    private int getSel() {
        return getBrickNum() + getGrainNum() + getOreNum() + getWoolNum() + getLumberNum();
    }

    private int getWoolNum() {
        return woolNum;
    }

    private void setWoolNum(int woolNum) {
        this.woolNum = woolNum;
    }

    private int getBrickNum() {
        return brickNum;
    }

    private void setBrickNum(int brickNum) {
        this.brickNum = brickNum;
    }

    private int getGrainNum() {
        return grainNum;
    }

    private void setGrainNum(int grainNum) {
        this.grainNum = grainNum;
    }

    private int getOreNum() {
        return oreNum;
    }

    private void setOreNum(int oreNum) {
        this.oreNum = oreNum;
    }

    private int getLumberNum() {
        return lumberNum;
    }

    private void setLumberNum(int lumberNum) {
        this.lumberNum = lumberNum;
    }

}

