package view.client.trade.send;

import controller.Register;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Resource;
import network.client.ClientWriter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WithoutHabourController implements Initializable {
    public GraphicsContext myGc; //for trading
    Stage stage;
    @FXML
    Button confirmButton;

    Image brickImg = new Image("/Ressourcen/rs_brick3.png");
    Image grainImg = new Image("/Ressourcen/rs_grain3.png");
    Image lumberImg = new Image("/Ressourcen/rs_lumber3.png");
    Image oreImg = new Image("/Ressourcen/rs_ore3.png");
    Image woolImg = new Image("/Ressourcen/rs_wool3.png");


    @FXML
    Spinner<Integer> woolCnt, grainCnt, oreCnt, brickCnt, lumberCnt;

    @FXML
    ImageView woolImgV, grainImgV, oreImgV, brickImgV, lumberImgV;
    @FXML
    ImageView woolImgV1, grainImgV1, oreImgV1, brickImgV1, lumberImgV1;

    @FXML
    Label woolLabel, grainLabel, oreLabel, brickLabel, lumberLabel;
    @FXML
    Spinner<Integer> woolCnt1, grainCnt1, oreCnt1, brickCnt1, lumberCnt1;
    @FXML
    Label woolLabel1, grainLabel1, oreLabel1, brickLabel1, lumberLabel1;
    @FXML
    Label managementLabel, requiredLabel, leftLabel, selectedLabel;
    private int mngmnt, req, sel, left;
    private int woolNum1, grainNum1, oreNum1, brickNum1, lumberNum1 = 0; //client
    private int woolNum, grainNum, oreNum, brickNum, lumberNum = 0;

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
        drawImg();
        //disableButtons();
        activateSpinners();
        addToolTips();
    }

    /**
     * images will be drawn
     */
    private void drawImg() {
        woolImgV.setImage(woolImg);
        woolImgV1.setImage(woolImg);
        grainImgV.setImage(grainImg);
        grainImgV1.setImage(grainImg);
        oreImgV.setImage(oreImg);
        oreImgV1.setImage(oreImg);
        brickImgV.setImage(brickImg);
        brickImgV1.setImage(brickImg);
        lumberImgV.setImage(lumberImg);
        lumberImgV1.setImage(lumberImg);

    }

    /**
     * initialization of spinners
     */
    private void activateSpinners() {
        final int initialValue = 0;
        //rs of client
        int maxWool = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getWool();
        int maxGrain = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getGrain();
        int maxOre = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getOre();
        int maxBrick = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBrick();
        int maxLumber = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getLumber();

        //management
        int available = (maxWool + maxGrain + maxOre + maxBrick + maxLumber) / 4;
        SpinnerValueFactory<Integer> woolRangeMng =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, available, initialValue);
        SpinnerValueFactory<Integer> grainRangeMng =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, available, initialValue);
        SpinnerValueFactory<Integer> oreRangeMng =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, available, initialValue);
        SpinnerValueFactory<Integer> brickRangeMng =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, available, initialValue);
        SpinnerValueFactory<Integer> lumberRangeMng =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, available, initialValue);

        woolCnt.setValueFactory(woolRangeMng);
        woolCnt.setEditable(true);
        grainCnt.setValueFactory(grainRangeMng);
        grainCnt.setEditable(true);
        oreCnt.setValueFactory(oreRangeMng);
        oreCnt.setEditable(true);
        brickCnt.setValueFactory(brickRangeMng);
        brickCnt.setEditable(true);
        lumberCnt.setValueFactory(lumberRangeMng);
        lumberCnt.setEditable(true);

        woolCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setWoolNumMng(newValue);
            updateInfo();
            disableAllMngSpinners(true);
            woolCnt.setDisable(false);
            if (newValue == 0) {
                disableAllMngSpinners(false);
            }

        });
        grainCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setGrainNumMng(newValue);
            updateInfo();
            disableAllMngSpinners(true);
            grainCnt.setDisable(false);
            if (newValue == 0) {
                disableAllMngSpinners(false);
            }

        });
        oreCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setOreNumMng(newValue);
            updateInfo();
            disableAllMngSpinners(true);
            oreCnt.setDisable(false);
            if (newValue == 0) {
                disableAllMngSpinners(false);
            }

        });
        brickCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setBrickNumMng(newValue);
            updateInfo();
            disableAllMngSpinners(true);
            brickCnt.setDisable(false);
            if (newValue == 0) {
                disableAllMngSpinners(false);
            }

        });
        lumberCnt.valueProperty().addListener((observable, oldValue, newValue) -> {
            setLumberNumMng(newValue);
            updateInfo();
            disableAllMngSpinners(true);
            lumberCnt.setDisable(false);
            if (newValue == 0) {
                disableAllMngSpinners(false);
            }

        });

        //client
        SpinnerValueFactory<Integer> woolRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxWool, initialValue, 4);
        SpinnerValueFactory<Integer> grainRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxGrain, initialValue, 4);
        SpinnerValueFactory<Integer> oreRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxOre, initialValue, 4);
        SpinnerValueFactory<Integer> brickRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxBrick, initialValue, 4);
        SpinnerValueFactory<Integer> lumberRange =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxLumber, initialValue, 4);
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
            setWoolNum(newValue);
            updateInfo();
            disableAllSpinners(true);
            woolCnt1.setDisable(false);
            if (newValue == 0) {
                disableAllSpinners(false);
            }

        });
        grainCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setGrainNum(newValue);
            updateInfo();
            disableAllSpinners(true);
            grainCnt1.setDisable(false);
            if (newValue == 0) {
                disableAllSpinners(false);
            }

        });
        oreCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setOreNum(newValue);
            updateInfo();
            disableAllSpinners(true);
            oreCnt1.setDisable(false);
            if (newValue == 0) {
                disableAllSpinners(false);
            }

        });
        brickCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setBrickNum(newValue);
            updateInfo();
            disableAllSpinners(true);
            brickCnt1.setDisable(false);
            if (newValue == 0) {
                disableAllSpinners(false);
            }

        });
        lumberCnt1.valueProperty().addListener((observable, oldValue, newValue) -> {
            setLumberNum(newValue);
            updateInfo();
            disableAllSpinners(true);
            lumberCnt1.setDisable(false);
            if (newValue == 0) {
                disableAllSpinners(false);
            }
        });


    }

    /**
     * updates after changing amoung in spinners
     */
    private void updateInfo() {
        mngmnt = getSelMng();
        req = mngmnt * 4;
        int a = req - getSel();
        managementLabel.setText("" + getSelMng());
        requiredLabel.setText("" + req);
        selectedLabel.setText("" + getSel());
        leftLabel.setText("" + a);
        if (getSel() == req) {
            confirmButton.setDisable(false);
        } else {
            confirmButton.setDisable(true);
        }

    }

    /**
     * all spinners of client will be enabled/deactivated
     *
     * @param b setDisable
     */
    public void disableAllSpinners(boolean b) {
        grainCnt1.setDisable(b);
        oreCnt1.setDisable(b);
        brickCnt1.setDisable(b);
        lumberCnt1.setDisable(b);
        woolCnt1.setDisable(b);
    }

    /**
     * all spinners of management will be enabled/deactivated
     * @param b setDisable
     */
    public void disableAllMngSpinners(boolean b) {
        grainCnt.setDisable(b);
        oreCnt.setDisable(b);
        brickCnt.setDisable(b);
        lumberCnt.setDisable(b);
        woolCnt.setDisable(b);
    }

    /**
     * informaton will be shown, when hovered
     */
    private void addToolTips() {
        confirmButton.setTooltip(new Tooltip("Click amount you want to trade"));
    }

    /**
     * offered and requested resources will be sent to server
     */
    @FXML
    public void confirm() {
        Register.getAudioClips().getClick().play();
        ArrayList<Resource> offeredResourcesList = new ArrayList<>();
        ArrayList<Resource> requestedResourcesList = new ArrayList<>();

        for (int i = 0; i < getBrickNum(); i++) {
            offeredResourcesList.add(Resource.BRICK);
        }
        for (int i = 0; i < getGrainNum(); i++) {
            offeredResourcesList.add(Resource.GRAIN);
        }
        for (int i = 0; i < getLumberNum(); i++) {
            offeredResourcesList.add(Resource.LUMBER);
        }
        for (int i = 0; i < getOreNum(); i++) {
            offeredResourcesList.add(Resource.ORE);
        }
        for (int i = 0; i < getWoolNum(); i++) {
            offeredResourcesList.add(Resource.WOOL);
        }
        //--
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
        new ClientWriter().tradeSea(offeredResourcesList, requestedResourcesList);
        stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    private int getSelMng() {
        return getBrickNumMng() + getGrainNumMng() + getOreNumMng() + getWoolNumMng() + getLumberNumMng();
    }

    private int getWoolNumMng() {
        return woolNum1;
    }

    private void setWoolNumMng(int woolNum1) {
        this.woolNum1 = woolNum1;
    }

    private int getBrickNumMng() {
        return brickNum1;
    }

    private void setBrickNumMng(int brickNum1) {
        this.brickNum1 = brickNum1;
    }

    private int getGrainNumMng() {
        return grainNum1;
    }

    private void setGrainNumMng(int grainNum1) {
        this.grainNum1 = grainNum1;
    }

    private int getOreNumMng() {
        return oreNum1;
    }

    private void setOreNumMng(int oreNum1) {
        this.oreNum1 = oreNum1;
    }

    private int getLumberNumMng() {
        return lumberNum1;
    }

    private void setLumberNumMng(int lumberNum1) {
        this.lumberNum1 = lumberNum1;
    }

    //client
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

    /**
     * go back to trade selection
     * @throws IOException when fxml file is incorrect
     */
    @FXML
    public void goBack() throws IOException {
        Register.getAudioClips().getClick().play();
        stage = (Stage) woolImgV.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/view/client/trade/send/tradeselector.fxml"));
        Scene scene = new Scene(root, 650, 550);
        //scene.getStylesheets().add("view/client/catanBoxes.css");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}

