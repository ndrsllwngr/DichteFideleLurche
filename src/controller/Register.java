package controller;

import model.Resource;
import model.players.Player;
import network.Interpreter;
import network.client.Client;
import network.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.AudioClips;
import view.client.CanvasBoard;
import view.client.build.BuildController;
import view.client.endGamePopUp.endGameController;
import view.client.trade.receive.TradeRequestController;
import view.client.trade.receive.tradeLobbyController;
import view.client.trade.send.harborTrade.oneResourceHarborController;

import java.util.HashMap;

public class Register {

    private static final Logger LOGGER = LogManager.getLogger(Register.class.getName());
    private static Controller controller;
    private static CanvasBoard canvasBoard;
    private static Client client;
    private static Server server;
    private static view.joinServer.Controller viewJoinServerController;
    private static view.startServer.Controller viewStartServerController;
    private static view.client.Controller viewClientController;
    private static view.client.build.BuildController viewBuildController;
    private static view.client.endGamePopUp.endGameController viewEndGameController;
    private static view.client.trade.receive.TradeRequestController viewTradeReceiveController;
    private static view.client.trade.receive.tradeLobbyController viewTradeReceiveLobbyController;
    private static oneResourceHarborController viewTradeSendHarborTradeBrickController;
    private static view.client.Controller alertPopUp;
    private static view.startServer.Controller alertPopUpServer;
    private static view.AudioClips audioClips;
    private static Resource harborTrade;
    private static int relevantTextArea;
    private static HashMap<Integer, DomesticTradeObj> trades = new HashMap<>();
    private static int tradeID = 0;
    private static HashMap<Integer, Player> handInQueue = new HashMap<>();
    private static int moveRobberHold;

    /**
     * Get logic controller
     *
     * @return controller.Controller object
     */
    public static Controller getController() {
        return controller;
    }

    /**
     * Set logic controller (invoked by network)
     *
     * @param c logic controller
     */
    public static void setController(Controller c) {
        LOGGER.debug(c.toString());
        new Interpreter().initCharBitSet();
        new Interpreter().initTranslations();
        new Interpreter().initHarbor();
        new Interpreter().initConversions();
        new Interpreter().initPColors();
        new Interpreter().initResourceStringHashMap();
        controller = c;
    }

    /**
     * Set CanvasBoard (invoked by view.client)
     *
     * @param cb CanvasBoard
     */
    public static void setCanvasBoard(CanvasBoard cb) {
        LOGGER.debug(cb.toString());
        canvasBoard = cb;
    }

    /**
     * Add observer (canvasBoard) to controller
     */
    public static void addObserver() {
        controller.addObserver(canvasBoard);
        for (model.players.Player player : Register.getController().getAllPlayersId().values()) {
            player.addObserver(canvasBoard);
        }
        controller.getManagement().getRobber().addObserver(canvasBoard);
    }

    /**
     * Get network server object
     *
     * @return network.server.Server object
     */
    public static Server getNtwrkServer() {
        return server;
    }

    /**
     * Set Server (invoked by network)
     *
     */
    public static void setNtwrkServer(Server s) {
        LOGGER.debug(s);
        server = s;
    }

    /**
     * Get network client object
     *
     * @return network.client.Client object
     */
    public static Client getNtwrkClient() {
        return client;
    }

    /**
     * Set Client (invoked by network)
     *
     */
    public static void setNtwrkClient(Client c) {
        LOGGER.debug(c.toString());
        client = c;
    }

    /**
     * Get view.joinServer.Controller object
     *
     * @return view.joinServer.Controller object
     */
    public static view.joinServer.Controller getViewJoinServerController() {
        return viewJoinServerController;
    }

    /**
     * Set view.joinServer.Controller
     *
     */
    public static void setViewJoinServerController(view.joinServer.Controller c) {
        LOGGER.debug(c.toString());
        viewJoinServerController = c;
    }

    /**
     * Get view.startServer.Controller object
     *
     * @return view.startServer.Controller object
     */
    public static view.startServer.Controller getViewStartServerController() {
        return viewStartServerController;
    }

    /**
     * Set view.startServer.Controller
     *
     */
    public static void setViewStartServerController(view.startServer.Controller c) {
        LOGGER.debug(c.toString());
        viewStartServerController = c;
    }

    /**
     * Get view.client.Controller object
     *
     * @return view.joinServer.Controller object
     */
    public static view.client.Controller getViewClientController() {
        return viewClientController;
    }

    /**
     * Set view.client.Controller
     *
     */
    public static void setViewClientController(view.client.Controller c) {
        LOGGER.debug(c.toString());
        viewClientController = c;
    }

    /**
     * Get relevant textarea
     *
     * @return int value which specifies a certain stage
     */
    public static int getRelevantTextArea() {
        return relevantTextArea;
    }

    /**
     * Set relevant textarea
     * 0 = joinServer
     * 1 = client
     * 2 = startServer
     *
     * @param i int value specifies a certain stage
     */
    public static void setRelevantTextArea(int i) {
        LOGGER.debug(i);
        relevantTextArea = i;
    }

    /**
     * Get ViewBuildController
     *
     * @return view.client.build.BuildController object
     */
    public static BuildController getViewBuildController() {
        return viewBuildController;
    }

    /**
     * Set ViewBuildController
     *
     * @param c view.client.build.BuildController object
     */
    public static void setViewBuildController(view.client.build.BuildController c) {
        LOGGER.debug(c.toString());
        viewBuildController = c;
    }

    public static HashMap<Integer, DomesticTradeObj> getTrades() {
        return trades;
    }

    public static void openTrade(int tradeID, DomesticTradeObj newtrade) {
        Register.trades.put(tradeID, newtrade);
    }

    public static void closeTrade(int oldTradeID) {
        Register.trades.remove(oldTradeID);
    }

    public static int getTradeID() {
        return tradeID;
    }

    public static void tradeIDPLUS() {
        Register.tradeID += 1;
    }

    public static HashMap<Integer, Player> getHandInQueue() {
        return handInQueue;
    }

    public static void removePlayerFromQueue(int id) {
        Register.handInQueue.remove(id);
    }

    public static void addPlayerToQueue(int id) {
        Register.handInQueue.put(id, Register.getController().getAllPlayersId().get(id));
    }

    public static int getMoveRobberHold() {
        return moveRobberHold;
    }

    public static void setMoveRobberHold(int moveRobberHold) {
        Register.moveRobberHold = moveRobberHold;
    }

    public static endGameController getViewEndGameController() {
        return viewEndGameController;
    }

    public static void setViewEndGameController(endGameController viewEndGameController) {
        Register.viewEndGameController = viewEndGameController;
    }

    public static TradeRequestController getViewTradeReceiveController() {
        return viewTradeReceiveController;
    }

    public static void setViewTradeReceiveController(TradeRequestController viewTradeReceiveController) {
        Register.viewTradeReceiveController = viewTradeReceiveController;
    }

    public static tradeLobbyController getViewTradeReceiveLobbyController() {
        return viewTradeReceiveLobbyController;
    }

    public static void setViewTradeReceiveLobbyController(tradeLobbyController viewTradeReceiveLobbyController) {
        Register.viewTradeReceiveLobbyController = viewTradeReceiveLobbyController;
    }

    public static oneResourceHarborController getViewTradeSendHarborTradeBrickController() {
        return viewTradeSendHarborTradeBrickController;
    }

    public static void setViewTradeSendHarborTradeBrickController(oneResourceHarborController viewTradeSendHarborTradeBrickController) {
        Register.viewTradeSendHarborTradeBrickController = viewTradeSendHarborTradeBrickController;
    }

    public static Resource getHarborTrade() {
        return harborTrade;
    }

    public static void setHarborTrade(Resource harborTrade) {
        Register.harborTrade = harborTrade;
    }

    public static AudioClips getAudioClips() {
        return audioClips;
    }

    public static void setAudioClips(AudioClips audioClips) {
        Register.audioClips = audioClips;
    }

    public static view.client.Controller getAlertPopUp() {
        return alertPopUp;
    }

    public static void setAlertPopUp(view.client.Controller alertPopUp) {
        Register.alertPopUp = alertPopUp;
    }

    public static view.startServer.Controller getAlertPopUpServer() {
        return alertPopUpServer;
    }

    public static void setAlertPopUpServer(view.startServer.Controller alertPopUpServer) {
        Register.alertPopUpServer = alertPopUpServer;
    }
}
