package network.server;

import controller.Register;
import model.cards.*;
import model.players.Player;
import model.players.Status;
import model.players.Test;
import network.Interpreter;
import network.JsonLib;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

class ClientThreadLevelVerification implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(ClientThreadLevelVerification.class.getName());
    private int id;
    private ClientThread clientThread;
    private Player p;
    private HashMap<Integer, Player> allPlayers = new HashMap<>();
    private String tempString;
    private JSONObject obj;

    ClientThreadLevelVerification(ClientThread clientThread, String tempString) {
        this.clientThread = clientThread;
        this.tempString = tempString;
        id = clientThread.getClientID();
        p = Register.getController().getAllPlayersId().get(id);
        allPlayers = Register.getController().getAllPlayersId();
        new Thread(this).start();
    }

    /**
     * Handle income client messages and check if its correct and recieved in the right player status
     */
    @Override
    public void run() {
        String type = "";
        try {
            this.obj = new JSONObject(tempString);
            type = this.obj.names().get(0).toString();
        } catch (JSONException e) {
            JSONObject lvl1 = new JSONObject();
            lvl1.put("Serverantwort", e);
            clientThread.msgThisClient(lvl1);
            LOGGER.catching(Level.ERROR, e);
        }
        if (type.equals("Chatnachricht senden")) {
            new ClientThreadEngine(clientThread, obj).chat();
        } else {
            switch (ClientThreadSequence.getLevel()) {
                case SETUP:
                    switch (type) {
                        case "Hallo":
                            new ClientThreadEngine(clientThread, obj).assignID();
                            break;
                        case "Spieler":
                            new ClientThreadEngine(clientThread, obj).initPlayer();
                            break;
                        case "Spiel starten":
                            if (allPlayers.get(id).getStatus() == Status.START_GAME) {
                                new ClientThreadEngine(clientThread, obj).startPlayer();
                            }
                            break;
                        default:
                            wrongMove();
                            LOGGER.warn("Case not covered!" + " " + type);
                    }
                    break;
                case FIRST_ROUND:
                case SECOND_ROUND:
                    switch (p.getStatus()) {
                        case BUILD_SETTLEMENT:
                            if (type.equals("Bauen") && obj.getJSONObject("Bauen").get("Typ").equals("Dorf")) {
                                if (p.testBuildings(Test.SETTLEMENT, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")))) {
                                    new ClientThreadEngine(clientThread, obj).build();
                                    new ClientThreadEngine(clientThread).statusUpdate();
                                } else {
                                    boolean location = p.testPlace(Test.SETTLEMENT, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")));
                                    if (!location) {
                                        sendReason("You can't build a settlement at this place.");
                                    }
                                    wrongMove();
                                }
                            } else {
                                sendReason("You have to build a settlement first.");
                                wrongMove();
                            }
                            break;
                        case BUILD_STREET:
                            if (type.equals("Bauen") && obj.getJSONObject("Bauen").get("Typ").equals("Straße")) {
                                if (p.testBuildings(Test.STREET, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")))) {
                                    new ClientThreadEngine(clientThread, obj).build();
                                    new ClientThreadSequence(clientThread).nextPlayer();
                                } else {
                                    boolean location = p.testPlace(Test.STREET, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")));
                                    if (!location) {
                                        sendReason("You can't build a road at this place.");
                                    }
                                    wrongMove();
                                }
                            } else {
                                sendReason("You have to build a road first.");
                                wrongMove();
                            }
                            break;
                        default:
                            wrongMove();
                            LOGGER.warn("Case not covered!" + " " + type);
                    }
                    break;
                case OTHER_ROUNDS:
                    switch (p.getStatus()) {
                        case ROLL_DICE:
                            switch (type) {
                                case "Würfeln":
                                    new ClientThreadEngine(clientThread, obj).rollDice();
                                    break;
                                case "Ritter ausspielen":
                                    if (ClientThreadSequence.isDevCard() && checkDevCard("knight")) {
                                        new ClientThreadEngine(clientThread, obj).moveKnight();
                                        ClientThreadSequence.setDevCard(false);
                                    } else {
                                        wrongMove();
                                        sendReason("You can only play one development card per turn.");

                                    }
                                    break;
                                case "Erfindung":
                                    if (ClientThreadSequence.isDevCard() && checkDevCard("yearOf")) {
                                        new ClientThreadEngine(clientThread, obj).yearOfPlenty();
                                        ClientThreadSequence.setDevCard(false);
                                    } else {
                                        wrongMove();
                                        sendReason("You can only play one development card per turn.");
                                    }
                                    break;
                                case "Monopol":
                                    if (ClientThreadSequence.isDevCard() && checkDevCard("monopoly")) {
                                        new ClientThreadEngine(clientThread, obj).acceptMonopol();
                                        ClientThreadSequence.setDevCard(false);
                                    } else {
                                        wrongMove();
                                        sendReason("You can only play one development card per turn.");
                                    }
                                    break;
                                case "Straßenbaukarte ausspielen": // TODO specifc cases one-turn, no cards left
                                    if (ClientThreadSequence.isDevCard() && checkDevCard("roadBuilding")) {
                                        new ClientThreadEngine(clientThread, obj).roadBuildingCard();
                                    } else {
                                        wrongMove();
                                        sendReason("You can only play one development card per turn.");
                                    }
                                    break;
                                default:
                                    wrongMove();
                            }
                            break;
                        case HAND_IN_CARDS_BECAUSE_OF_ROBBER:
                            if (type.equals("Karten abgeben")) {
                                if (obj.getJSONObject("Karten abgeben").has("Abgeben") && new JsonLib().checkIfPlayerHasEnoughRes(new JsonLib().countResJSONObj(obj.getJSONObject("Karten abgeben").getJSONObject("Abgeben")), id)) {
                                    new ClientThreadEngine(clientThread, obj).handInCards();
                                } else {
                                    sendReason("Wrong amount of resources.");
                                }
                            } else {
                                wrongMove();
                            }
                            break;
                        case MOVE_ROBBER:
                            if (type.equals("Räuber versetzen")) {
                                if (new ClientThreadEngine(clientThread, obj).checkNewRobberPosition()) {
                                    new ClientThreadEngine(clientThread, obj).moveRobber();
                                } else {
                                    sendReason("You can't move the robber to this place.");
                                    wrongMove();
                                }
                            } else {
                                wrongMove();
                            }
                            break;
                        case TRADE_OR_BUILD:
                            switch (type) {
                                case "Bauen":
                                    build(obj);
                                    break;
                                case "Zug beenden":
                                    p.moveDevelopmentcards();
                                    new ClientThreadSequence(clientThread).setPlayerWait(clientThread.getClientID());
                                    new ClientThreadSequence(clientThread).nextPlayer();
                                    break;
                                case "Entwicklungskarte kaufen":
                                    if (p.testCards()) {
                                        new ClientThreadEngine(clientThread, obj).buyDevCardSendJsonToClient();
                                        checkEndGame();
                                    } else {
                                        if (!(Register.getController().getAllPlayersId().get(clientThread.getClientID()).testEnoughDevelopmentCards())) {
                                            sendReason("There are no more development cards left.");
                                        }
                                        if (!(Register.getController().getAllPlayersId().get(clientThread.getClientID()).testEnoughRessourcesDevCard())) {
                                            sendReason("You don't have enough resources.");
                                        }
                                    }
                                    break;
                                case "Ritter ausspielen":
                                    if (ClientThreadSequence.isDevCard() && checkDevCard("knight")) {
                                        new ClientThreadEngine(clientThread, obj).moveKnight();
                                        ClientThreadSequence.setDevCard(false);
                                    } else {
                                        wrongMove();
                                        sendReason("You can only play one development card per turn.");
                                    }
                                    break;
                                case "Erfindung":
                                    if (ClientThreadSequence.isDevCard() && checkDevCard("yearOf")) {
                                        new ClientThreadEngine(clientThread, obj).yearOfPlenty();
                                        ClientThreadSequence.setDevCard(false);
                                    } else {
                                        wrongMove();
                                        sendReason("You can only play one development card per turn.");
                                    }
                                    break;
                                case "Monopol":
                                    if (ClientThreadSequence.isDevCard() && checkDevCard("monopoly")) {
                                        new ClientThreadEngine(clientThread, obj).acceptMonopol();
                                        ClientThreadSequence.setDevCard(false);
                                    } else {
                                        wrongMove();
                                        sendReason("You can only play one development card per turn.");
                                    }
                                    break;
                                case "Straßenbaukarte ausspielen": // TODO specifc cases one-turn, no cards left
                                    if (ClientThreadSequence.isDevCard() && checkDevCard("roadBuilding")) {
                                        new ClientThreadEngine(clientThread, obj).roadBuildingCard();
                                    } else {
                                        wrongMove();
                                        sendReason("You can only play one development card per turn.");
                                    }
                                    break;
                                case "Handel anbieten":
                                    new ClientThreadEngine(clientThread, obj).startDomesticTrade();
                                    break;
                                case "Handel abschließen":
                                    new ClientThreadEngine(clientThread, obj).completeTrade();
                                    break;
                                case "Handel abbrechen":
                                    new ClientThreadEngine(clientThread, obj).cancelTrade();
                                    break;
                                case "Handel annehmen":
                                    new ClientThreadEngine(clientThread, obj).acceptTrade();
                                    break;
                                case "Seehandel":
                                    new ClientThreadEngine(clientThread, obj).seaTrade();
                                    break;
                                default:
                                    wrongMove();
                            }
                            break;
                        case BUILD:
                            // TODO implementation missing
                            break;
                        case WAIT:
                            switch (type) {
                                case "Handel annehmen":
                                    new ClientThreadEngine(clientThread, obj).acceptTrade();
                                    break;
                                case "Handel abbrechen":
                                    new ClientThreadEngine(clientThread, obj).cancelTrade();
                                    break;
                                default:
                                    wrongMove();
                                    sendReason("It's not your turn.");
                            }
                            break;
                        default:
                            wrongMove();
                            LOGGER.warn("Case not covered!" + " " + type);
                    }
                    break;
                case END_GAME:
                    LOGGER.warn("The game is over.");
                    break;
                default:
                    LOGGER.warn("Case not covered!" + " " + type);
            }
        }
        clientThread.toggleReady();
    }

    /**
     * Test's if the played development card is not played in the same turn
     *
     * @param devcard development card which has to be checked
     * @return true when bought card is not played in the same turn
     */
    private Boolean checkDevCard(String devcard) {
        if (Register.getController().getAllPlayersId().get(clientThread.getClientID()).getDevelopmentCards().size() > 0) {
            for (DevelopmentCard card : Register.getController().getAllPlayersId().get(clientThread.getClientID()).getDevelopmentCards()) {
                switch (devcard) {
                    case "knight":
                        if (card instanceof KnightCard) {
                            return true;
                        }
                        break;
                    case "monopoly":
                        if (card instanceof MonopolyCard) {
                            return true;
                        }
                        break;
                    case "roadBuilding":
                        if (card instanceof RoadBuildingCard) {
                            return true;
                        }
                        break;
                    case "yearOf":
                        if (card instanceof YearOfPlentyCard) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    /**
     * Send answer to client
     *
     * @param string reaseon
     */
    private void sendReason(String string) {
        new ClientThreadEngine(clientThread).sendChatMsg(string);
    }

    /**
     * Inform client if its a wrong move
     *
     */
    protected void wrongMove() {
        new ClientThreadEngine(clientThread).sendResponse("Illegal action.");
    }

    /**
     * Check if the game ended
     *
     */
    private void checkEndGame() {
        int id = Register.getController().getManagement().finish();
        if (id > 0) {
            new ClientThreadEngine(clientThread).gameEnd(id);
            ClientThreadSequence.setLevel(network.Level.END_GAME);
        }
    }

    // todo check? DELETE this
    private void checkLongestStreet() {
        int tmp = Register.getController().getManagement().longestRoad();
        if (tmp < 0) {
            new ClientThreadEngine(clientThread).lostLongestStreet();
        } else if (tmp > 0) {
            new ClientThreadEngine(clientThread).longestStreet(Register.getController().getAllPlayersId().get(tmp));
        }
    }

    /**
     * handle income build request from client and check if its a valid action
     *
     * @param obj JSONObject
     */
    private void build(JSONObject obj) {
        if (obj.getJSONObject("Bauen").has("Typ")) {
            boolean check = false;
            boolean location = false;
            boolean enoughRes = false;
            boolean hasEnoughBuildings = false;
            Test type = Test.NULL;
            switch (obj.getJSONObject("Bauen").getString("Typ")) {
                case "Dorf":
                    check = p.testBuildings(Test.SETTLEMENT, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")));
                    location = p.testPlace(Test.SETTLEMENT, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")));
                    enoughRes = p.testRessources(Test.SETTLEMENT);
                    hasEnoughBuildings = p.testEnoughBuildings(Test.SETTLEMENT);
                    type = Test.SETTLEMENT;
                    break;
                case "Stadt":
                    check = p.testBuildings(Test.CITY, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")));
                    location = p.testPlace(Test.CITY, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")));
                    enoughRes = p.testRessources(Test.CITY);
                    hasEnoughBuildings = p.testEnoughBuildings(Test.CITY);
                    type = Test.CITY;
                    break;
                case "Straße":
                    check = p.testBuildings(Test.STREET, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")));
                    location = p.testPlace(Test.STREET, new Interpreter().axialToEdgeOrCorner(obj.getJSONObject("Bauen").getJSONArray("Ort")));
                    enoughRes = p.testRessources(Test.STREET);
                    hasEnoughBuildings = p.testEnoughBuildings(Test.STREET);
                    type = Test.STREET;
                    break;
            }
            LOGGER.info(check + " " + obj.getJSONObject("Bauen").getString("Typ"));
            if (check) {
                new ClientThreadEngine(clientThread, obj).build();
            } else {
                if (!location) {
                    switch (type) {
                        case STREET:
                            sendReason("You can't build a road at this place.");
                            break;
                        case SETTLEMENT:
                            sendReason("You can't build a settlement at this place.");
                            break;
                        case CITY:
                            sendReason("You can't build a city at this place.");
                            break;
                        case NULL:
                        default:
                            LOGGER.info("Case not covered!");
                    }
                }
                if (!enoughRes) {
                    sendReason("You don't have enough resources.");
                }
                if (!hasEnoughBuildings) {
                    sendReason("You don't have anymore buildings of this kind left.");
                }
                wrongMove();
            }
        }
    }
}
