package network.client;

import controller.Register;
import javafx.application.Platform;
import model.Management;
import model.Resource;
import model.board.CustomisedBoard;
import model.board.Sea;
import model.cards.*;
import model.players.Ai;
import model.players.PColor;
import model.players.Player;
import model.players.Status;
import network.Interpreter;
import network.JsonLib;
import network.MathOp;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import view.client.EventTypes;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * The ClientListener class handles income JSONObject messages and process them
 */

class ClientListener {
    private static final Logger LOGGER = LogManager.getLogger(ClientListener.class.getName());
    static String info = "(i) ";
    static String warn = "??? ";
    static String action = "!!! ";
    private static boolean checkDice;
    private Client c;
    private JSONObject inMsg;
    private String protocolVersion = "1.0";
    private boolean kesseJojos = true;
    private String cmdLineName;
    private String cmdLineColor;


    /**
     * Constructor
     */
    ClientListener(Client c) {
        this.c = c;
    }

    /**
     * Handle message with the help of the first key
     *
     * @param msg message
     */
    synchronized void handleMsg(String msg) {
        String type = "";
        try {
            this.inMsg = new JSONObject(msg);
            type = this.inMsg.names().get(0).toString();
        } catch (JSONException e) {
            LOGGER.catching(Level.ERROR, e);
            JSONObject error = new JSONObject();
            error.put("Serverantwort", e);
            c.sendMsg(error);
        }
        if (Register.getNtwrkServer() == null) {
            switch (type) {
                case "Hallo":
                    startConnection();
                    c.toggleReady();
                    break;
                case "Willkommen":
                    myId();
                    c.toggleReady();
                    break;
                case "Serverantwort":
                    serverResponse();
                    c.toggleReady();
                    break;
                case "Fehler":
                    error();
                    c.toggleReady();
                    break;
                case "Chatnachricht":
                    chatIncoming();
                    c.toggleReady();
                    break;
                case "Statusupdate":
                    updatePlayer();
                    if (new Interpreter().ntwrkConversion(inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getString("Status")) != Status.CONNECTION_LOST) {
                        updateResourceToPlayer();
                        updateVictoryPoints();
                        updateKnights();
                        updateDevelopmentToPlayer();
                        updateLongestStreetAndLargestArmy();
                    }
                    c.toggleReady();
                    break;
                case "Spiel gestartet":
                    gameStarted();
                    break;
                case "Würfelwurf":
                    updateDicsRolls();
                    c.toggleReady();
                    break;
                case "Bauvorgang":
                    build();
                    c.toggleReady();
                    break;
                case "Kosten":
                    cost();
                    c.toggleReady();
                    break;
                case "Entwicklungskarte gekauft":
                    incomeDevelopmentcard();
                    c.setCounterDevCard(c.getCounterDevCard() + 1);
                    c.toggleReady();
                    break;
                case "Ertrag":
                    incomeRessource();
                    c.toggleReady();
                    break;
                case "Handelsangebot":
                    if (inMsg.getJSONObject("Handelsangebot").has("Handel id")) {
                        if (c.getAi()) {
                            new ClientWriter(c).cancelTrade(inMsg.getJSONObject("Handelsangebot").getInt("Handel id"));
                        } else {
                            LOGGER.info("Handelsangebot");
                            Register.getViewClientController().incomingTradeRequest();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (inMsg.getJSONObject("Handelsangebot").has("Angebot") && inMsg.getJSONObject("Handelsangebot").has("Nachfrage")) {
                                Register.getViewTradeReceiveController().showTradeRequest(new JsonLib().countResJSONObj(inMsg.getJSONObject("Handelsangebot").getJSONObject("Angebot")), new JsonLib().countResJSONObj(inMsg.getJSONObject("Handelsangebot").getJSONObject("Nachfrage")));
                                Register.getViewTradeReceiveController().setTradeIdForServer(inMsg.getJSONObject("Handelsangebot").getInt("Handel id"));
                            }
                        }
                    }
                    c.toggleReady();
                    break;
                case "Handelsangebot angenommen":
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (Register.getViewTradeReceiveLobbyController() != null) {
                        Register.getViewTradeReceiveLobbyController().setTradeIdForServer(inMsg.getJSONObject("Handelsangebot angenommen").getInt("Handel id"));
                        Register.getViewTradeReceiveLobbyController().updatePlayer(inMsg.getJSONObject("Handelsangebot angenommen").getInt("Mitspieler"), inMsg.getJSONObject("Handelsangebot angenommen").getBoolean("Annehmen"));
                    }
                    c.toggleReady();
                    break;
                case "Handel ausgeführt":
                    c.toggleReady();
                    break;
                case "Handelsangebot abgebrochen":
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (Register.getViewTradeReceiveLobbyController() != null) {
                        Register.getViewTradeReceiveLobbyController().updatePlayer(inMsg.getJSONObject("Handelsangebot abgebrochen").getInt("Spieler"), false);
                        Register.getViewTradeReceiveLobbyController().setTradeIdForServer(inMsg.getJSONObject("Handelsangebot abgebrochen").getInt("Handel id"));
                    }
                    c.toggleReady();
                    break;
                case "Cheat":
                    psst();
                    c.toggleReady();
                    break;
                case "Längste Handelsstraße":
                    longestStreet();
                    c.toggleReady();
                    break;
                case "Räuber versetzt":
                    movedRobber();
                    c.toggleReady();
                    break;
                case "Ritter ausspielen":
                    movedKnight();

                    c.toggleReady();
                    break;
                case "Monopol":
                    monopolCard();
                    c.toggleReady();
                    break;
                case "Erfindung":
                    yearOfPlenty();
                    c.toggleReady();
                    break;
                case "Straßenbaukarte ausspielen":
                    roadBuilding();
                    c.toggleReady();
                    break;
                case "Größte Rittermacht":
                    largestArmy();
                    c.toggleReady();
                    break;
                case "Spiel beendet":
                    endGame();
                    c.toggleReady();
                    break;
                default:
                    LOGGER.warn("Case not covered!");
                    c.toggleReady();
            }
        } else {
            switch (type) {
                case "Hallo":
                    startConnection();
                    c.toggleReady();
                    break;
                case "Willkommen":
                    c.setId(inMsg.getJSONObject("Willkommen").getInt("id"));
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (Player p : Register.getController().getAllPlayersId().values()) {
                        if (p instanceof Ai && p.getId() == c.getId()) {
                            c.setThisAi((Ai) p);
                            ((Ai) p).setClient(c);
                            LOGGER.info(c.getThisAi());
                        }
                    }
                    c.toggleReady();
                    break;
                case "Statusupdate":
                    internalAi();
                    c.toggleReady();
                    break;
                case "Handelsangebot":
                    if (inMsg.getJSONObject("Handelsangebot").has("Handel id")) {
                        if (c.getAi()) {
                            new ClientWriter(c).cancelTrade(inMsg.getJSONObject("Handelsangebot").getInt("Handel id"));
                        }
                    }
                    c.toggleReady();
                    break;
                case "Spiel beendet":
                    endGame();
                    c.toggleReady();
                    break;
                default:
                    LOGGER.warn("Case not covered!");
                    c.toggleReady();
            }
        }
    }

    public synchronized void handleCMDLine(String msg) {
        String type = "";
        try {
            this.inMsg = new JSONObject(msg);
            type = this.inMsg.names().get(0).toString();
        } catch (JSONException e) {
            LOGGER.catching(Level.ERROR, e);
            JSONObject error = new JSONObject();
            error.put("Serverantwort", e);
            c.sendMsg(error);
        }
        switch (type) {
            case "Hallo":
                startConnection();
                c.toggleReady();
                break;
            case "Willkommen":
                c.setId(inMsg.getJSONObject("Willkommen").getInt("id"));
                Register.getController().addPlayer(c.getId(), "", PColor.NULL, false);
                ((Ai) Register.getController().getAllPlayersId().get(c.getId())).setClient(c);
                LOGGER.info("Player: " + c.getAi() + " , " + c.getId());
                new ClientWriter(c).setPlayer(cmdLineName, cmdLineColor);
                c.toggleReady();
                break;
            case "Serverantwort":
                serverResponse();
                c.toggleReady();
                break;
            case "Fehler":
                error();
                c.toggleReady();
                break;
            case "Chatnachricht":
                chatIncoming();
                c.toggleReady();
                break;
            case "Statusupdate":
                updatePlayer();
                if (new Interpreter().ntwrkConversion(inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getString("Status")) != Status.CONNECTION_LOST) {
                    if (Register.getController().getAllPlayersId().containsKey(getId())) {
                        Player p = Register.getController().getAllPlayersId().get(getId());
                        if (p != null) {
                            JSONObject resource = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getJSONObject("Rohstoffe");
                            new JsonLib().changePlayerRes(resource, MathOp.SET, getId());
                            p.setVictoryPoints(inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getInt("Siegpunkte"));
                            if (inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").has("Rittermacht")) {
                                int knights = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getInt("Rittermacht");
                                p.setKnight(knights);
                            }
                            JSONObject developmentCards = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getJSONObject("Entwicklungskarten");
                            new JsonLib().dev(developmentCards, MathOp.SET, getId());
                            if (inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").has("Längste Handelsstraße")) {
                                boolean road = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getBoolean("Längste Handelsstraße");
                                p.setLongestRoad(road);
                            }
                            if (inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").has("Größte Rittermacht")) {
                                boolean army = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getBoolean("Größte Rittermacht");
                                p.setLargestArmy(army);
                            }
                        }
                    }
                }
                if (c.getId() == getId() && Register.getController().getAllPlayersId().get(c.getId()).getStatus() == Status.START_GAME) {
                    new ClientWriter(c).register();
                }
                c.toggleReady();
                break;
            case "Spiel gestartet":
                gameStarted();
                break;
            case "Würfelwurf":
                JSONObject wurf = inMsg.getJSONObject("Würfelwurf");
                int tmpIDx = wurf.getInt("Spieler");
                JSONArray result = wurf.getJSONArray("Wurf");
                int a = result.getInt(0);
                int b = result.getInt(1);
                Register.getController().getAllPlayersId().get(tmpIDx).setDiceValue(a + b);
                c.toggleReady();
                break;
            case "Bauvorgang":
                JSONObject building = inMsg.getJSONObject("Bauvorgang");
                JSONObject info = building.getJSONObject("Gebäude");
                Player player = Register.getController().getAllPlayersId().get(info.getInt("Eigentümer"));
                JSONArray loc = info.getJSONArray("Ort");
                if (info.getString("Typ").equals("Straße")) {
                    player.buildStreetFORCE(new Interpreter().axialToEdgeOrCorner(loc));
                }
                if (info.getString("Typ").equals("Dorf")) {
                    player.buildSettlementFORCE(new Interpreter().axialToEdgeOrCorner(loc));
                }
                if (info.getString("Typ").equals("Stadt")) {
                    player.buildCityFORCE(new Interpreter().axialToEdgeOrCorner(loc));
                }
                c.toggleReady();
                // KesseJojos Specific
                if (kesseJojos && player.getId() == c.getId()) {
                    ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                }
                break;
            case "Kosten":
                JSONObject costs = inMsg.getJSONObject("Kosten");
                int tmpID0 = costs.getInt("Spieler");
                if (costs.getJSONObject("Rohstoffe").has("Unbekannt")) {
                    int oldRes = Register.getController().getAllPlayersId().get(tmpID0).getResource();
                    Register.getController().getAllPlayersId().get(tmpID0).setResource(oldRes - costs.getJSONObject("Rohstoffe").getInt("Unbekannt"));
                } else {
                    if (c.getId() == tmpID0) {
                        new JsonLib().changePlayerRes(costs.getJSONObject("Rohstoffe"), MathOp.SUBTRACT, tmpID0);
                    } else {
                        new JsonLib().unknownResMINUS(costs.getJSONObject("Rohstoffe"), tmpID0);
                    }
                }
                c.toggleReady();
                if (kesseJojos && tmpID0 == c.getId() && Register.getController().getAllPlayersId().get(c.getId()).getStatus() == Status.TRADE_OR_BUILD) {
                    ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                }
                break;
            case "Entwicklungskarte gekauft":
                JSONObject devIncoming = inMsg.getJSONObject("Entwicklungskarte gekauft");
                int tmpID1 = devIncoming.getInt("Spieler");
                String value = devIncoming.getString("Entwicklungskarte");
                new JsonLib().dev(devIncoming, MathOp.ADD, tmpID1);
                switch (value) {
                    case "Ritter":
                        Register.getController().getAllPlayersId().get(tmpID1).getDevelopmentCardsMove().add(new KnightCard());
                        break;
                    case "Straßenbau":
                        Register.getController().getAllPlayersId().get(tmpID1).getDevelopmentCardsMove().add(new RoadBuildingCard());
                        break;
                    case "Monopol":
                        Register.getController().getAllPlayersId().get(tmpID1).getDevelopmentCardsMove().add(new MonopolyCard(Register.getController().getManagement()));
                        break;
                    case "Erfindung":
                        Register.getController().getAllPlayersId().get(tmpID1).getDevelopmentCardsMove().add(new YearOfPlentyCard(Register.getController().getManagement()));
                        break;
                    case "Siegpunkt":
                        Register.getController().getAllPlayersId().get(tmpID1).getDevelopmentCardsMove().add(new VictoryPointCard(VictoryPointCardType.MARKET));
                        break;
                }
                // KesseJojos Specific
                if (kesseJojos && tmpID1 == c.getId()) {
                    ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                }
                c.toggleReady();
                break;
            case "Ertrag":
                JSONObject income = inMsg.getJSONObject("Ertrag");
                int tmpID2 = income.getInt("Spieler");
                if (c.getId() == tmpID2) {
                    new JsonLib().changePlayerRes(income.getJSONObject("Rohstoffe"), MathOp.ADD, tmpID2);
                } else {
                    new JsonLib().unknownResPLUS(income.getJSONObject("Rohstoffe"), tmpID2);
                }
                c.toggleReady();
                // KesseJojos Specific
                if (kesseJojos && tmpID2 == c.getId() && Register.getController().getAllPlayersId().get(c.getId()).getStatus() == Status.TRADE_OR_BUILD) {
                    ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                }
                break;
            case "Handelsangebot":
                if (inMsg.getJSONObject("Handelsangebot").has("Handel id")) {
                    new ClientWriter(c).cancelTrade(inMsg.getJSONObject("Handelsangebot").getInt("Handel id"));
                }
                c.toggleReady();
                break;
            case "Handelsangebot angenommen":
                c.toggleReady();
                break;
            case "Handel ausgeführt":
                c.toggleReady();
                break;
            case "Handelsangebot abgebrochen":
                c.toggleReady();
                break;
            case "Cheat":
                c.toggleReady();
                break;
            case "Längste Handelsstraße":
                JSONObject street = inMsg.getJSONObject("Längste Handelsstraße");
                for (Player p : Register.getController().getAllPlayersId().values()) {
                    if (p.getLongestRoad()) {
                        p.setLongestRoad(false);
                    }
                }
                if (street.has("Spieler")) {
                    int tmpID9 = street.getInt("Spieler");
                    Register.getController().getAllPlayersId().get(tmpID9).setLongestRoad(true);
                }
                c.toggleReady();
                break;
            case "Räuber versetzt":
                JSONObject lvl1Robber = inMsg.getJSONObject("Räuber versetzt");
                Register.getController().getManagement().getRobber().changePosition(new Interpreter().axialToTile(lvl1Robber.getJSONObject("Ort")));
                c.toggleReady();
                ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                break;
            case "Ritter ausspielen":
                JSONObject knight = inMsg.getJSONObject("Ritter ausspielen");
                Register.getController().getManagement().getRobber().changePosition(new Interpreter().axialToTile(knight.getJSONObject("Ort")));
                if (knight.getInt("Spieler") == c.getId()) {
                    for (DevelopmentCard card : Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards()) {
                        if (card instanceof KnightCard) {
                            Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards().remove(card);
                            break;
                        }
                    }
                }
                c.toggleReady();
                if (kesseJojos && knight.getInt("Spieler") == c.getId()) {
                    ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                }
                break;
            case "Monopol":
                JSONObject monopol = inMsg.getJSONObject("Monopol");
                if (monopol.getInt("Spieler") == c.getId()) {
                    for (DevelopmentCard card : Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards()) {
                        if (card instanceof MonopolyCard) {
                            Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards().remove(card);
                            break;
                        }
                    }
                }
                c.toggleReady();
                if (kesseJojos && monopol.getInt("Spieler") == c.getId()) {
                    ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                }
                break;
            case "Erfindung":
                JSONObject bulp = inMsg.getJSONObject("Erfindung");
                if (bulp.getInt("Spieler") == c.getId()) {
                    for (DevelopmentCard card : Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards()) {
                        if (card instanceof YearOfPlentyCard) {
                            Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards().remove(card);
                            break;
                        }
                    }
                }
                c.toggleReady();
                if (kesseJojos && bulp.getInt("Spieler") == c.getId()) {
                    ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                }
                break;
            case "Straßenbaukarte ausspielen":
                JSONObject roadBuilding = inMsg.getJSONObject("Straßenbaukarte ausspielen");
                if (roadBuilding.getInt("Spieler") == c.getId()) {
                    for (DevelopmentCard card : Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards()) {
                        if (card instanceof RoadBuildingCard) {
                            Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards().remove(card);
                            break;
                        }
                    }
                }
                c.toggleReady();
                if (kesseJojos && roadBuilding.getInt("Spieler") == c.getId()) {
                    ((Ai) Register.getController().getAllPlayersId().get(c.getId())).makeMove();
                }
                break;
            case "Größte Rittermacht":
                JSONObject army = inMsg.getJSONObject("Größte Rittermacht");
                for (Player p : Register.getController().getAllPlayersId().values()) {
                    if (p.getLargestArmy()) {
                        p.setLargestArmy(false);
                    }
                }
                if (army.has("Spieler")) {
                    int id = army.getInt("Spieler");
                    Register.getController().getAllPlayersId().get(id).setLargestArmy(true);
                }
                c.toggleReady();
                break;
            case "Spiel beendet":
                Platform.exit();
                System.exit(0);
                c.toggleReady();
                break;
            default:
                LOGGER.warn("Case not covered!");
                c.toggleReady();
        }

    }

    /**
     * Update longest street and largest army
     */
    private void updateLongestStreetAndLargestArmy() {
        Player p = Register.getController().getAllPlayersId().get(getId());
        if (inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").has("Längste Handelsstraße")) {
            boolean road = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getBoolean("Längste Handelsstraße");
            p.setLongestRoad(road);

        }
        if (inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").has("Größte Rittermacht")) {
            boolean army = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getBoolean("Größte Rittermacht");
            p.setLargestArmy(army);
        }

    }

    /**
     * Update moved robber to client model
     */
    private void movedRobber() {
        JSONObject lvl1 = inMsg.getJSONObject("Räuber versetzt");
        Register.getController().getManagement().getRobber().changePosition(new Interpreter().axialToTile(lvl1.getJSONObject("Ort")));
        if (lvl1.has("Ziel") && lvl1.getInt("Ziel") > 0) {
            writeToConsole(Register.getController().getAllPlayersId().get(lvl1.getInt("Ziel")).getName() + " was robbed.");
        }
        if (Register.getViewClientController() != null) {
            Register.getViewClientController().playTune(EventTypes.ROBBER);
        }
    }

    /**
     * Update played knight card to client model
     */
    private void movedKnight() {
        JSONObject lvl1 = inMsg.getJSONObject("Ritter ausspielen");
        Register.getController().getManagement().getRobber().changePosition(new Interpreter().axialToTile(lvl1.getJSONObject("Ort")));
        if (lvl1.has("Ziel") && lvl1.getInt("Ziel") > 0) {
            writeToConsole(Register.getController().getAllPlayersId().get(lvl1.getInt("Ziel")).getName() + " was robbed.");
        }
        if (Register.getViewClientController() != null) {
            Register.getViewClientController().playTune(EventTypes.KNIGHTS);
        }
        if (lvl1.getInt("Spieler") == c.getId()) {
            writeToConsole("Played Knightcard");
            Register.getViewClientController().getDevelopmentCardsCon().remove("Ritter");
            for (DevelopmentCard card : Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards()) {
                if (card instanceof KnightCard) {
                    Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards().remove(card);
                    break;
                }
            }
            Register.getViewClientController().layer4.setOnMouseClicked(null);
            Register.getViewClientController().setCardDev(false);
            Register.getViewClientController().getGc4().clearRect(0, 0, Register.getViewClientController().getGc4().getCanvas().getWidth(), Register.getViewClientController().getGc4().getCanvas().getHeight());
        }
    }

    /**
     * Update played monopol card to client model
     */
    private void monopolCard() {
        JSONObject lvl1 = inMsg.getJSONObject("Monopol");
        int id = lvl1.getInt("Spieler");

        if (id == c.getId()) {
            writeToConsole("Played Monopolcard");
            for (DevelopmentCard card : Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards()) {
                if (card instanceof MonopolyCard) {
                    Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards().remove(card);
                    break;
                }
            }
            Register.getViewClientController().getDevelopmentCardsCon().remove("Monopol");
            Register.getViewClientController().setCardDev(false);
        }
    }

    /**
     * update played year of plenty card
     */
    private void yearOfPlenty() {
        JSONObject lvl1 = inMsg.getJSONObject("Erfindung");
        int id = lvl1.getInt("Spieler");

        if (id == c.getId()) {
            writeToConsole("Played Year-of-Plenty card");
            for (DevelopmentCard card : Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards()) {
                if (card instanceof YearOfPlentyCard) {
                    Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards().remove(card);
                    break;
                }
            }
            Register.getViewClientController().getDevelopmentCardsCon().remove("Erfindung");
            Register.getViewClientController().setCardDev(false);
        }
    }

    /**
     * update played roadbuilding card
     */
    private void roadBuilding() {
        JSONObject lvl1 = inMsg.getJSONObject("Straßenbaukarte ausspielen");
        int id = lvl1.getInt("Spieler");

        if (id == c.getId()) {
            writeToConsole("Played Roadbuilding card");
            for (DevelopmentCard card : Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards()) {
                if (card instanceof RoadBuildingCard) {
                    Register.getController().getAllPlayersId().get(c.getId()).getDevelopmentCards().remove(card);
                    break;
                }
            }
            Register.getViewClientController().getDevelopmentCardsCon().remove("Straßenbau");
            Register.getViewClientController().setCardDev(false);
        }
    }


    /**
     * Write text to textarea (joinServer and client)
     */
    public synchronized void writeToConsole(String msg) {
        if (!c.getCmdLine()) {
            switch (Register.getRelevantTextArea()) {
                case 0:
                    Register.getViewJoinServerController().appendConsoleOutputToTextArea(msg + "\n");
                    break;
                case 1:
                    Register.getViewClientController().appendConsoleOutputToTextArea(msg + "\n");
                    break;
                default:
                    LOGGER.warn("Case not covered!");
            }
        }
    }

    /**
     * Print incoming chat messages to textarea
     */
    private void chatIncoming() {
        JSONObject innerObj = inMsg.getJSONObject("Chatnachricht");
        if (innerObj.has("Nachricht")) {
            if (innerObj.has("Absender")) {
                if (innerObj.has("Absender") && innerObj.has("Nachricht")) {
                    String name;
                    if (Register.getController().getAllPlayersId().containsKey(innerObj.getInt("Absender"))) {
                        if (Register.getController().getAllPlayersId().get(innerObj.getInt("Absender")).
                                getName() != null) {
                            name = Register.getController().getAllPlayersId().get(innerObj.getInt("Absender")).
                                    getName();
                        } else {
                            name = "id: " + String.valueOf(innerObj.getInt("Absender"));
                        }
                    } else {
                        name = "id: " + String.valueOf(innerObj.getInt("Absender"));
                    }
                    String msg = innerObj.getString("Nachricht");
                    writeToConsole("[ " + name + " ] " + msg);
                }
            } else {
                writeToConsole(warn + innerObj.getString("Nachricht"));
            }
        }
    }

    /**
     * Cheat to get infos about opponents
     */
    private void psst() {
        JSONObject innerObj = inMsg.getJSONObject("Cheat");
        String string = innerObj.toString();
        writeToConsole(string);
    }

    /**
     * Set fields, buildings, harbors at model logic
     */
    private void gameStarted() {
        JSONObject innerObject = inMsg.getJSONObject("Spiel gestartet");
        JSONObject secondInnerObject = innerObject.getJSONObject("Karte");
        JSONArray fields = secondInnerObject.getJSONArray("Felder");
        JSONArray buildings = secondInnerObject.getJSONArray("Gebäude");
        JSONArray harbors = secondInnerObject.getJSONArray("Häfen");
        // Fields
        Register.getController().initCustomisedBoard();
        if (fields.length() != 0) {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject tmp = fields.getJSONObject(i);
                BitSet id = new Interpreter().axialToTile(tmp.getJSONObject("Ort"));
                String term = new Interpreter().ntwrkTranslate(tmp.getString("Typ"));
                if (tmp.has("Zahl") && tmp.getInt("Zahl") > 0) {
                    int numberToken = tmp.getInt("Zahl");
                    ((CustomisedBoard) Register.getController().getBoard()).setTile(id, term, numberToken);
                } else {
                    ((CustomisedBoard) Register.getController().getBoard()).setTile(id, term, 0);
                }
            }
            if (fields.length() < 36) {
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(0), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(1), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(2), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(3), "Sea", 0);

                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(4), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(8), "Sea", 0);

                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(9), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(14), "Sea", 0);

                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(15), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(21), "Sea", 0);

                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(22), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(27), "Sea", 0);

                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(28), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(32), "Sea", 0);

                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(33), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(34), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(35), "Sea", 0);
                ((CustomisedBoard) Register.getController().getBoard()).setTile(new Interpreter().makeBitSet(36), "Sea", 0);


            }
        }
        ((CustomisedBoard) Register.getController().getBoard()).initBoard();
        // Buildings
        if (buildings.length() != 0) {
            // TODO ?
        }
        // Harbors
        if (harbors.length() != 0) {
            for (int i = 0; i < harbors.length(); i++) {
                JSONObject tmp = harbors.getJSONObject(i);
                String harborType = new Interpreter().ntwrkTranslateH(tmp.getString("Typ"));
                BitSet edge = new Interpreter().axialToEdgeOrCorner(tmp.getJSONArray("Ort"));
                int tile1 = edge.nextSetBit(0);
                int tile2 = edge.nextSetBit(tile1 + 1);
                LOGGER.info("H1: " + tile1 + ", H2: " + tile2);
                LOGGER.info("H1: " + Register.getController().getBoard().getAllTiles().get(new Interpreter().makeBitSet(tile1)) + ", H2: " + Register.getController().getBoard().getAllTiles().get(new Interpreter().makeBitSet(tile2)));
                if (Register.getController().getBoard().getAllTiles().get(new Interpreter().makeBitSet(tile1)) instanceof Sea) {
                    ((CustomisedBoard) Register.getController().getBoard()).setHarbor(new Interpreter().makeBitSet(tile1), new Interpreter().makeBitSet(tile2), harborType);
                } else {
                    ((CustomisedBoard) Register.getController().getBoard()).setHarbor(new Interpreter().makeBitSet(tile2), new Interpreter().makeBitSet(tile1), harborType);
                }
            }
        }
        Register.getController().getBoard().printBoardIDs();
        Register.getController().getBoard().printNumberTokens();
        Register.getController().getBoard().printBoardSpecialCo();
        Register.getController().getBoard().printTileNeighbors();
        Register.getController().getBoard().printPorts();
        Register.getController().setManagement(new Management());
        Register.getController().getManagement().setBoard(Register.getController().getBoard());
        Register.getController().getManagement().setRobber(Register.getController().getBoard(), new Interpreter().axialToTile(secondInnerObject.getJSONObject("Räuber")));
        Register.getController().getManagement().setAllPlayersId(Register.getController().getAllPlayersId());
        Register.getController().setSequence(Register.getController().getAllPlayersId().get(c.getId()));
        for (model.players.Player player : Register.getController().getAllPlayersId().values()) {
            if (player.getId() != c.getId()) {
                Register.getController().setSequence(Register.getController().getAllPlayersId().get(player.getId()));
            }
            player.setBoard(Register.getController().getBoard());
            player.setAllPlayers(Register.getController().getAllPlayersId());
            player.setManagement(Register.getController().getManagement());
        }
        if (!c.getCmdLine()) {
            Register.getViewJoinServerController().startNow();
        } else {
            c.toggleReady();
        }
    }

    /**
     * Set assigned id (network.client.Client)
     */
    private void myId() {
        c.setId(inMsg.getJSONObject("Willkommen").getInt("id"));
        if (c.getAi()) {
            LOGGER.info("Ai.");
            Register.getController().addPlayer(c.getId(), "", PColor.NULL, false);
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((Ai) Register.getController().getAllPlayersId().get(c.getId())).setClient(c);
        } else {
            LOGGER.info("Human.");
            Register.getController().addPlayer(c.getId(), "", PColor.NULL, true);
        }
        Register.getController().getAllPlayersId().get(c.getId()).setStatus(Status.START_GAME);
        if (Register.getController().getAllPlayersId().get(c.getId()).getStatus() != null) {
            writeToConsole(info + "Your [ id: " + c.getId() + " ] & [ status: " + Register.getController().
                    getAllPlayersId().get(c.getId()).getStatus().toString() + " ]");
        } else {
            writeToConsole(info + "Your [ id: " + c.getId() + " ] & [ status: " + "null" + " ]");
        }
    }

    /**
     * Send short message with group identification and network protocol version to server
     */
    private void startConnection() {
        if (inMsg.getJSONObject("Hallo").getString("Protokoll").equalsIgnoreCase(protocolVersion)) {
            JSONObject innerObject = new JSONObject();
            if (c.getAi()) {
                innerObject.put("Version", "JavaFXClient " + protocolVersion + " (DichteFideleLurche) (KI)");
            } else {
                innerObject.put("Version", "JavaFXClient " + protocolVersion + " (DichteFideleLurche)");
            }
            JSONObject myJsonObject = new JSONObject();
            myJsonObject.put("Hallo", innerObject);
            c.sendMsg(myJsonObject);
        } else {
            writeToConsole(warn + "Client doesn't support protocol " + inMsg.getJSONObject("Hallo").
                    getString("Protokoll"));
            LOGGER.info("Client doesn't support protocol " + inMsg.getJSONObject("Hallo").
                    getString("Protokoll") + "!");
            c.disconnect();
        }

    }

    /**
     * Display error
     */
    private void error() {
        JSONObject error = inMsg.getJSONObject("Fehler");
        String type = error.getString("Meldung");
        writeToConsole(action + type);
        LOGGER.info(type);
    }

    /**
     * Handle server responses
     */
    private void serverResponse() {
        String type = inMsg.getString("Serverantwort");
        switch (type) {
            case "OK":
                if (Register.getViewClientController() != null) {
                    Register.getViewClientController().layer4.setOnMouseClicked(null);
                }
                break;
            case "Unzulässige Aktion":
                writeToConsole(info + "Unzulässige Aktion");
                break;
            default:
                writeToConsole(inMsg.getString("Serverantwort"));
                LOGGER.warn("Case not covered!");

        }
    }

    /**
     * set status of player
     *
     * @param status player
     * @param id     player
     */
    private void setStatus(Status status, int id) {
        Register.getController().getAllPlayersId().get(id).setStatus(status);
    }

    /**
     * ClientListener status updates update model
     */
    private void updatePlayer() {
        Status status = new Interpreter().ntwrkConversion(inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getString("Status"));
        int id = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getInt("id");
        boolean isMe = (id == c.getId());
        switch (status) {
            case START_GAME:
                statusStart(status, id);
                setStatus(status, id);
                writeToConsole(info + "[ id: " + id + " ] " + Register.getController().getAllPlayersId().get(id).getName() + ", " + new Interpreter().pColorToString(Register.getController().getAllPlayersId().get(id).getColor()) + ", " + Register.
                        getController().getAllPlayersId().get(id).getStatus());
                break;
            case WAIT_FOR_GAME_START:
                statusStart(status, id);
                setStatus(status, id);
                writeToConsole(info + "[ id: " + id + " ] " + Register.getController().getAllPlayersId().get(id).getName() + ", " + new Interpreter().pColorToString(Register.getController().getAllPlayersId().get(id).getColor()) + ", " + Register.
                        getController().getAllPlayersId().get(id).getStatus());
                break;
            case BUILD_SETTLEMENT:
                setStatus(status, id);
                setPlayerActive(getId());
                if (isMe) {
                    if (c.getAi()) {
                        ((Ai) Register.getController().getAllPlayersId().get(id)).buildStartSettlement();
                    } else {
                        Register.getViewClientController().getCanvasBoard().showPossibleSettlements();
                        Register.getViewClientController().buildSettlement();
                    }
                }
                break;
            case BUILD_STREET:
                setStatus(status, id);
                setPlayerActive(getId());
                if (isMe) {
                    if (c.getAi()) {
                        ((Ai) Register.getController().getAllPlayersId().get(id)).buildStartRoad();
                    } else {
                        Register.getViewClientController().getCanvasBoard().showPossibleStreets2();
                        Register.getViewClientController().buildStreets();
                    }
                }
                break;
            case ROLL_DICE:
                checkDice = true;
                setStatus(status, id);
                setPlayerActive(getId());
                if (isMe) {
                    if (c.getAi()) {
                        new ClientWriter(c).rollDice();
                    }
                }
                break;
            case HAND_IN_CARDS_BECAUSE_OF_ROBBER:
                setStatus(status, id);
                Register.getController().getAllPlayersId().get(id).setActive(true);
                if (!c.getCmdLine()) {
                    Register.getViewClientController().colorStatus();
                    if (isMe) {
                        writeToConsole(action + "Hand in half of your cards");
                        if (c.getAi()) {
                            ((Ai) Register.getController().getAllPlayersId().get(id)).startRobbing();
                        } else {
                            Register.getViewClientController().handInCards();
                        }
                    } else {
                        writeToConsole(info + Register.getController().getAllPlayersId().get(id).getName() + " has to hand in half of his cards");
                    }
                } else {
                    if (isMe) {
                        ((Ai) Register.getController().getAllPlayersId().get(id)).startRobbing();
                    }
                }
                break;
            case MOVE_ROBBER:
                setStatus(status, id);
                setPlayerActive(id);
                if (isMe) {
                    if (c.getAi()) {
                        ((Ai) Register.getController().getAllPlayersId().get(id)).robber();
                    } else {
                        Register.getViewClientController().getCanvasBoard().changeRobberPosition();
                    }
                }
                break;
            case TRADE_OR_BUILD:
                setStatus(status, id);
                setPlayerActive(id);
                if (isMe) {
                    if (c.getAi()) {
                        ((Ai) Register.getController().getAllPlayersId().get(id)).makeMove();
                    }
                }
                break;
            case BUILD: // TODO ???
                setStatus(status, id);
                break;
            case WAIT:
                setStatus(status, id);
                Register.getController().getAllPlayersId().get(id).setActive(false);
                if (Register.getViewClientController() != null) {
                    Register.getViewClientController().colorStatus();
                }
                break;
            case CONNECTION_LOST:
                try {
                    setStatus(status, id);
                    writeToConsole(info + "Lost connection to " + id);
                    Register.getController().getAllPlayersId().remove(id);
                    writeToConsole(info + Register.getController().getAllPlayersId().size()
                            + " waiting for game start");
                } catch (NullPointerException e) {
                    LOGGER.catching(Level.ERROR, e);
                }
                break;
            case BOOBS: // TODO ???
                setStatus(status, id);
            default:
                LOGGER.warn("Case not covered!");

        }
    }

    /**
     * Sequence for the ai
     */
    private void internalAi() {

        Status status = new Interpreter().ntwrkConversion(inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getString("Status"));
        int id = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getInt("id");
        boolean isMe = (id == c.getId());
        switch (status) {
            case START_GAME:
                if (isMe && c.getAi() && c.getThisAi().getColor() == PColor.NULL) {
                    String name = "KI (" + c.getId() + ")";
                    PColor color;
                    switch (c.getId()) {
                        case 1:
                            color = PColor.BLUE;
                            break;
                        case 2:
                            color = PColor.RED;
                            break;
                        case 3:
                            color = PColor.ORANGE;
                            break;
                        case 4:
                            color = PColor.WHITE;
                            break;
                        default:
                            color = PColor.NULL;
                    }
                    new ClientWriter(c).setPlayer(name, new Interpreter().pColorToString(color));
                }
                if (isMe && c.getAi() && c.getThisAi().getColor() != PColor.NULL) {
                    new ClientWriter(c).register();
                }
                break;
            case BUILD_SETTLEMENT:
                if (isMe && c.getAi()) {
                    c.getThisAi().buildStartSettlement();
                }
                break;
            case BUILD_STREET:
                if (isMe && c.getAi()) {
                    c.getThisAi().buildStartRoad();
                }
                break;
            case ROLL_DICE:
                checkDice = true;
                if (isMe && c.getAi()) {
                    new ClientWriter(c).rollDice();
                }
                break;
            case HAND_IN_CARDS_BECAUSE_OF_ROBBER:
                if (isMe && c.getAi()) {
                    c.getThisAi().startRobbing();
                }
                break;
            case MOVE_ROBBER:
                if (isMe && c.getAi()) {
                    c.getThisAi().robber();
                }
                break;
            case TRADE_OR_BUILD:
                if (isMe && c.getAi()) {
                    c.getThisAi().makeMove();
                }
                break;
            default:
                LOGGER.warn("Case not covered!");

        }
    }

    /**
     * Help method to set one player active and all others false
     *
     * @param id player id
     */
    private void setPlayerActive(int id) {
        for (Player p : Register.getController().getAllPlayersId().values()) {
            if (p.getId() == id) {
                p.setActive(true);
            } else {
                p.setActive(false);
            }
        }
        if (Register.getRelevantTextArea() == 1) {
            Register.getViewClientController().colorStatus();
        }
    }


    /**
     * Help method to update players
     *
     * @param status model.players.Status enum
     */
    private void statusStart(Status status, int id) {
        String name = "";
        if (inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").has("Name")) {
            if (!(inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").get("Name").equals(null))) {
                name = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getString("Name");
            }
        }
        String color = "";
        if (inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").has("Farbe")) {
            color = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getString("Farbe");
        }
        if (!Register.getController().getAllPlayersId().containsKey(id)) {
            if (id == c.getId() && c.getAi()) {
                Register.getController().addPlayer(id, name, new Interpreter().stringToPColor(color), false);
            } else {
                Register.getController().addPlayer(id, name, new Interpreter().stringToPColor(color), true);
            }
        } else {
            Register.getController().getAllPlayersId().get(id).setName(name);
            Register.getController().getAllPlayersId().get(id).setColor(new Interpreter().stringToPColor(color));
        }
    }

    /**
     * Get Id from json object player
     *
     * @return ID Player
     */
    private Integer getId() {
        return inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getInt("id");
    }

    /**
     * ClientListener status update updates resources in model
     */
    private void updateResourceToPlayer() {
        JSONObject resource = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getJSONObject("Rohstoffe");
        new JsonLib().changePlayerRes(resource, MathOp.SET, getId());
    }

    /**
     * Add received resources
     */
    private void incomeRessource() {
        JSONObject income = inMsg.getJSONObject("Ertrag");
        int id = income.getInt("Spieler");
        if (c.getId() == id) {
            new JsonLib().changePlayerRes(income.getJSONObject("Rohstoffe"), MathOp.ADD, id);
            ArrayList<Resource> tmp = new JsonLib().countResJSONObj(income.getJSONObject("Rohstoffe"));
            Register.getViewClientController().triggerFading(tmp, 0, true, id);
            Register.getViewClientController().triggerOwnStatus(EventTypes.RESOURCES, tmp.size(), true);
        } else {
            new JsonLib().unknownResPLUS(income.getJSONObject("Rohstoffe"), id);
        }
    }

    /**
     * Add recieved development card
     */
    private void incomeDevelopmentcard() {
        JSONObject income = inMsg.getJSONObject("Entwicklungskarte gekauft");
        int id = income.getInt("Spieler");
        String value = income.getString("Entwicklungskarte");
        new JsonLib().dev(income, MathOp.ADD, id);
        Register.getViewClientController().getDevelopmentCardsMoveCon().clear();
        switch (value) {
            case "Ritter":
                Register.getViewClientController().getDevelopmentCardsMoveCon().add(0, "Ritter");
                Register.getController().getAllPlayersId().get(id).getDevelopmentCardsMove().add(new KnightCard());
                Register.getViewClientController().appendConsoleOutputToTextArea("(Received) Knightcard\n");
                Register.getViewClientController().devCardAnimation(new KnightCard(), id);
                break;
            case "Straßenbau":
                Register.getViewClientController().getDevelopmentCardsMoveCon().add(0, "Straßenbau");
                Register.getController().getAllPlayersId().get(id).getDevelopmentCardsMove().add(new RoadBuildingCard());
                Register.getViewClientController().appendConsoleOutputToTextArea("(Received) Roadbuildingcard\n");
                Register.getViewClientController().devCardAnimation(new RoadBuildingCard(), id);
                break;
            case "Monopol":
                Register.getViewClientController().getDevelopmentCardsMoveCon().add(0, "Monopol");
                Register.getController().getAllPlayersId().get(id).getDevelopmentCardsMove().add(new MonopolyCard(Register.getController().getManagement()));
                Register.getViewClientController().appendConsoleOutputToTextArea("(Received) Monopolcard\n");
                Register.getViewClientController().devCardAnimation(new MonopolyCard(Register.getController().getManagement()), id);
                break;
            case "Erfindung":
                Register.getViewClientController().getDevelopmentCardsMoveCon().add(0, "Erfindung");
                Register.getController().getAllPlayersId().get(id).getDevelopmentCardsMove().add(new YearOfPlentyCard(Register.getController().getManagement()));
                Register.getViewClientController().appendConsoleOutputToTextArea("(Received) Year-of-Plenty card\n");
                Register.getViewClientController().devCardAnimation(new YearOfPlentyCard(Register.getController().getManagement()), id);
                break;
            case "Siegpunkt":
                Register.getViewClientController().getDevelopmentCardsMoveCon().add(0, "Siegpunkt");
                Register.getController().getAllPlayersId().get(id).getDevelopmentCardsMove().add(new VictoryPointCard(VictoryPointCardType.MARKET));
                Register.getViewClientController().appendConsoleOutputToTextArea("(Received) Victorypointcard\n");
                Register.getViewClientController().devCardAnimation(new VictoryPointCard(VictoryPointCardType.MARKET), id);
                break;
        }
        if (id == c.getId()) {
            if (Register.getViewClientController() != null) {
                Register.getViewClientController().playTune(EventTypes.DEVELOPMENTCARDS);
            }
        }
    }

    /**
     * ClientListener status update updates developmentCards in model
     */
    private void updateDevelopmentToPlayer() {
        JSONObject developmentCards = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getJSONObject("Entwicklungskarten");
        new JsonLib().dev(developmentCards, MathOp.SET, getId());
    }

    /**
     * ClientListener status update updates developmentCards in model
     */
    private void updateKnights() {
        if (inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").has("Rittermacht")) {
            Player p = Register.getController().getAllPlayersId().get(getId());
            int knights = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getInt("Rittermacht");
            if (p != null) {
                p.setKnight(knights);
            }
        }
    }

    /**
     * Subtract resources
     */
    private void cost() {
        JSONObject income = inMsg.getJSONObject("Kosten");
        int id = income.getInt("Spieler");
        if (income.getJSONObject("Rohstoffe").has("Unbekannt")) {
            int oldRes = Register.getController().getAllPlayersId().get(id).getResource();
            Register.getController().getAllPlayersId().get(id).setResource(oldRes - income.getJSONObject("Rohstoffe").getInt("Unbekannt"));
        } else {
            if (c.getId() == id) {
                new JsonLib().changePlayerRes(income.getJSONObject("Rohstoffe"), MathOp.SUBTRACT, id);
                ArrayList<Resource> tmp = new JsonLib().countResJSONObj(income.getJSONObject("Rohstoffe"));
                Register.getViewClientController().triggerFading(tmp, 0, false, id);
                Register.getViewClientController().triggerOwnStatus(EventTypes.RESOURCES, tmp.size(), false);

            } else {
                new JsonLib().unknownResMINUS(income.getJSONObject("Rohstoffe"), id);
            }
        }
    }

    /**
     * Update victory points of all players
     */
    private void updateVictoryPoints() {
        if (Register.getController().getAllPlayersId().containsKey(getId())) {
            if (getId() == c.getId()) {
                if (Register.getViewClientController() != null) {
                    int oldInt = Register.getController().getAllPlayersId().get(getId()).getVictoryPoints();
                    int newInt = inMsg.getJSONObject("Statusupdate").getJSONObject("Spieler").getInt("Siegpunkte");
                    int tmp = newInt - oldInt;
                    if (tmp > 0) {
                        Register.getViewClientController().triggerOwnStatus(EventTypes.VICTORYPOINTS, tmp, true);
                    }
                    if (tmp < 0) {
                        Register.getViewClientController().triggerOwnStatus(EventTypes.VICTORYPOINTS, tmp, false);
                    }
                }
            }
            Register.getController().getAllPlayersId().get(getId()).setVictoryPoints(inMsg.
                    getJSONObject("Statusupdate").getJSONObject("Spieler").getInt("Siegpunkte"));
        }
    }


    /**
     * Update dice roll result
     */
    private void updateDicsRolls() {
        JSONObject wurf = inMsg.getJSONObject("Würfelwurf");
        int id = wurf.getInt("Spieler");
        JSONArray result = wurf.getJSONArray("Wurf");
        int a = result.getInt(0);
        int b = result.getInt(1);
        Register.getController().getAllPlayersId().get(id).setDiceValue(a + b);
        if (Register.getRelevantTextArea() == 1 && checkDice) {
            Register.getViewClientController().rollDicesImages(a, b);
        } else {
            if (id == c.getId()) {
                writeToConsole(info + "You diced " + (a + b));
            } else {
                String name = Register.getController().getAllPlayersId().get(id).getName();
                writeToConsole(info + name + " diced " + (a + b));
            }
        }
    }

    /**
     * Update longest street
     */
    private void longestStreet() {
        JSONObject street = inMsg.getJSONObject("Längste Handelsstraße");
        for (Player p : Register.getController().getAllPlayersId().values()) {
            if (p.getLongestRoad()) {
                p.setLongestRoad(false);
            }
        }
        if (street.has("Spieler")) {
            int id = street.getInt("Spieler");
            Register.getController().getAllPlayersId().get(id).setLongestRoad(true);
            writeToConsole(Register.getController().getAllPlayersId().get(id).getName() + " has the longest road.");
            Register.getViewClientController().statusAnimation(EventTypes.LONGESTROAD, id);
            if (id == c.getId()) {
                if (Register.getViewClientController() != null) {
                    Register.getViewClientController().playTune(EventTypes.LONGESTROAD);
                }
            }
        }
    }

    /**
     * Update largest army
     */
    private void largestArmy() {
        JSONObject army = inMsg.getJSONObject("Größte Rittermacht");
        for (Player p : Register.getController().getAllPlayersId().values()) {
            if (p.getLargestArmy()) {
                p.setLargestArmy(false);
            }
        }
        if (army.has("Spieler")) {
            int id = army.getInt("Spieler");
            Register.getController().getAllPlayersId().get(id).setLargestArmy(true);
            writeToConsole(Register.getController().getAllPlayersId().get(id).getName() + " has the largest Army.");
            Register.getViewClientController().statusAnimation(EventTypes.LARGESTARMY, id);
            if (id == c.getId()) {
                if (Register.getViewClientController() != null) {
                    Register.getViewClientController().playTune(EventTypes.LARGESTARMY);
                }
            }
        }
    }

    /**
     * Quit game as soon as one player disconnects
     */
    private void endGame() {
        LOGGER.traceEntry();
        if (inMsg.getJSONObject("Spiel beendet").has("Spieler") && inMsg.getJSONObject("Spiel beendet").getInt("Spieler") > 0) {
            writeToConsole(inMsg.getJSONObject("Spiel beendet").getString("Nachricht"));
            if (Register.getViewClientController() != null) {
                Register.getViewClientController().openEndGame();
                while (Register.getViewEndGameController() == null) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Register.getViewEndGameController().setWinnerLabel(inMsg.getJSONObject("Spiel beendet").getString("Nachricht"));
            }
        } else {
            Status status = Register.getController().getAllPlayersId().get(c.getId()).getStatus();
            if (status == Status.START_GAME || status == Status.WAIT_FOR_GAME_START) {

            } else {
                if (Register.getViewClientController() != null) {
                    if (Register.getViewEndGameController() == null) {
                        if (Register.getAlertPopUp() == null) {
                            Register.getViewClientController().connectionLost();
                        }
                    }
                } else {
                    if (Register.getAlertPopUpServer() == null) {
                        Register.getViewStartServerController().connectionLost();
                    }
                }
            }
        }
    }

    /**
     * Update buildings in model logic
     */
    public void build() {
        JSONObject building = inMsg.getJSONObject("Bauvorgang");
        JSONObject info = building.getJSONObject("Gebäude");
        Player player = Register.getController().getAllPlayersId().get(info.getInt("Eigentümer"));
        JSONArray loc = info.getJSONArray("Ort");
        if (info.getString("Typ").equals("Straße")) {
            player.buildStreetFORCE(new Interpreter().axialToEdgeOrCorner(loc));
            if (player.getId() == c.getId()) {
                if (Register.getViewClientController() != null) {
                    Register.getViewClientController().playTune(EventTypes.ROAD);
                }
            }
        }
        if (info.getString("Typ").equals("Dorf")) {
            player.buildSettlementFORCE(new Interpreter().axialToEdgeOrCorner(loc));
            if (player.getId() == c.getId()) {
                if (Register.getViewClientController() != null) {
                    Register.getViewClientController().playTune(EventTypes.SETTLEMENT);
                }
            }
        }
        if (info.getString("Typ").equals("Stadt")) {
            player.buildCityFORCE(new Interpreter().axialToEdgeOrCorner(loc));
            if (player.getId() == c.getId()) {
                if (Register.getViewClientController() != null) {
                    Register.getViewClientController().playTune(EventTypes.CITY);
                }
            }
        }
        LOGGER.info(info.getString("Typ") + " - " + loc.toString() + " - " + new Interpreter().axialToEdgeOrCorner(loc).toString());
    }

    public String getCmdLineName() {
        return cmdLineName;
    }

    public void setCmdLineName(String cmdLineName) {
        this.cmdLineName = cmdLineName;
    }

    public String getCmdLineColor() {
        return cmdLineColor;
    }

    public void setCmdLineColor(String cmdLineColor) {
        this.cmdLineColor = cmdLineColor;
    }
}



