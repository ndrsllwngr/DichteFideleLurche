package network.server;

import controller.DomesticTradeObj;
import controller.Register;
import model.Resource;
import model.Robber;
import model.board.Board;
import model.board.Sea;
import model.board.Terrain;
import model.board.Tile;
import model.cards.*;
import model.players.PColor;
import model.players.Player;
import model.players.Status;
import model.players.Test;
import network.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

class ClientThreadEngine {
    private static final Logger LOGGER = LogManager.getLogger(ClientThreadEngine.class.getName());
    private JSONObject msg;
    private ClientThread clientThread;
    private Boolean devcardCheat = false;

    /**
     * Default Constructor
     */
    ClientThreadEngine(ClientThread clientThread) {
        this.clientThread = clientThread;
    }

    /**
     * JSONObject incoming message from ClientThread
     *
     * @param clientThread ClientThread
     * @param msg          JSONObject incoming message
     */
    ClientThreadEngine(ClientThread clientThread, JSONObject msg) {
        this.clientThread = clientThread;
        this.msg = msg;
    }

    /**
     * Send short message with group identification and network protocol version to client
     */
    void welcome() {
        JSONObject innerObj = new JSONObject();
        innerObj.put("Version", "Catan-Server (DichteFideleForste");
        innerObj.put("Protokoll", "1.0");
        JSONObject obj = new JSONObject();
        obj.put("Hallo", innerObj);
        clientThread.msgThisClient(obj);

    }

    /**
     * Send client PLAYER id in a short welcome message to client
     *
     */
    protected void assignID() {
        JSONObject innerId = new JSONObject();
        innerId.put("id", clientThread.getClientID());
        JSONObject id = new JSONObject();
        id.put("Willkommen", innerId);
        clientThread.msgThisClient(id);
        if (!Register.getController().getAllPlayersId().containsKey(clientThread.getClientID())) {
            String ki = "(KI)";
            if (msg.getJSONObject("Hallo").getString("Version").toLowerCase().contains(ki.toLowerCase())) {
                Register.getController().addPlayer(clientThread.getClientID(), "", PColor.NULL, false);
            } else {
                Register.getController().addPlayer(clientThread.getClientID(), "", PColor.NULL, true);
            }
            Register.getController().getAllPlayersId().get(clientThread.getClientID()).setStatus(Status.START_GAME);
            Register.getController().getAllPlayersId().get(clientThread.getClientID()).setBoard(Register.getController().getBoard());
            Register.getController().getAllPlayersId().get(clientThread.getClientID()).setManagement(Register.getController().getManagement());
            Register.getController().getAllPlayersId().get(clientThread.getClientID()).setAllPlayers(Register.getController().getAllPlayersId());
        }
        statusUpdate();
        for (int key : clientThread.getThreads().keySet()) {
            if (key != clientThread.getClientID()) {
                clientThread.msgThisClient(jsonStatusUpdate(key)); // TODO ?
            }
        }
    }

    /**
     * Forward chat message to everyone
     *
     */
    protected void chat() {
        JSONObject innerObj = new JSONObject();
        innerObj.put("Absender", clientThread.getClientID());
        if (msg.getJSONObject("Chatnachricht senden").getString("Nachricht").equalsIgnoreCase("cheatON")) {
            Server.setCheatMode(true);
            return;
        } else if (msg.getJSONObject("Chatnachricht senden").getString("Nachricht").equalsIgnoreCase("cheatOFF")) {
            Server.setCheatMode(false);
            return;
        }
        if (Server.isCheatMode()) {
            cheatMode();
            return;
        }
        innerObj.put("Nachricht", msg.getJSONObject("Chatnachricht senden").getString("Nachricht"));
        JSONObject chat = new JSONObject();
        chat.put("Chatnachricht", innerObj);
        clientThread.msgAll(chat);
    }

    /**
     * Cheatmode with different kinds of cheats over chat function
     *
     */
    private void cheatMode() {
        String cheat = msg.getJSONObject("Chatnachricht senden").getString("Nachricht");
        Player p = Register.getController().getAllPlayersId().get(clientThread.getClientID());
        if (Server.isCheatMode() && cheat.equalsIgnoreCase("+2")) {
            p.cheatPLUSres(2);
            statusUpdate();

        }
        if (Server.isCheatMode() && cheat.equalsIgnoreCase("+10")) {
            p.cheatPLUSres(10);
            statusUpdate();
        }
        if (Server.isCheatMode() && cheat.equalsIgnoreCase("psst")) {
            for (Integer id : Register.getController().getAllPlayersId().keySet()) {
                if (clientThread.getClientID() != id) {
                    Player puh = Register.getController().getAllPlayersId().get(id);
                    JSONObject obj = new JSONObject();
                    JSONObject innerObj = new JSONObject();
                    innerObj.put("name", puh.getName());
                    innerObj.put("lumber", puh.getLumber());
                    innerObj.put("brick", puh.getBrick());
                    innerObj.put("wool", puh.getWool());
                    innerObj.put("ore", puh.getOre());
                    innerObj.put("grain", puh.getGrain());
                    obj.put("Cheat", innerObj);
                    clientThread.msgThisClient(obj);
                }
            }
        }
        if (Server.isCheatMode() && cheat.equalsIgnoreCase("devcard")) {
            p.cheatDevCard();
            statusUpdate();
        }
        if (Server.isCheatMode() && cheat.equalsIgnoreCase("mop")) {
            devcardCheat = true;
            buyDevCardSendJsonToClient();
            statusUpdate();
        }


    }

    /**
     * Get name, color of client, safe it to model logic and forward it to everyone
     *
     */
    protected void initPlayer() {
        int id = clientThread.getClientID();
        String name = msg.getJSONObject("Spieler").getString("Name");
        String color = msg.getJSONObject("Spieler").getString("Farbe");
        Register.getController().getAllPlayersId().get(id).setColor(new Interpreter().stringToPColor(color));
        Register.getController().getAllPlayersId().get(id).setName(name);
        serverOK();
        statusUpdate();
    }

    /**
     * Let client switch from START_GAME to WAIT_FOR_GAME_START if chosen color is still available
     * If client is last player switching to WAIT_FOR_GAME_START the map will be forwarded to everyone
     *
     */
    protected void startPlayer() {
        for (Player player : Register.getController().getAllPlayersId().values()) {
            if (player != Register.getController().getAllPlayersId().get(clientThread.getClientID())) {
                if (!(player.getColor() == PColor.NULL)) {
                    if (player.getColor().equals(Register.getController().getAllPlayersId().get(clientThread.getClientID()).
                            getColor())) {
                        JSONObject message = new JSONObject();
                        message.put("Meldung", "Farbe bereits vergeben");
                        JSONObject error = new JSONObject();
                        error.put("Fehler", message);
                        clientThread.msgThisClient(error);
                        return;

                    }
                }
            }
        }
        Register.getController().getAllPlayersId().get(clientThread.getClientID()).setStatus(Status.WAIT_FOR_GAME_START);
        serverOK();
        statusUpdate();
        if (clientThread.getThreads().size() == Register.getNtwrkServer().getMaxClientsCount()) {
            for (ClientThread clientThread : clientThread.getThreads().values()) {
                if (Register.getController().getAllPlayersId().containsKey(clientThread.getClientID())) {
                    if (!(Register.getController().getAllPlayersId().get(clientThread.getClientID()).
                            getStatus() == Status.WAIT_FOR_GAME_START)) {
                        return;
                    }
                }
            }
            ClientThreadSequence.setLevel(Level.FIRST_ROUND);
            sendMap();
            new ClientThreadSequence(clientThread).setSequence();
        }
    }

    /**
     * Send OK to client
     *
     */
    protected void serverOK() {
        sendResponse("OK");
    }

    /**
     * Send different status updates to client and more general ones to everyone except this client
     *
     */ // TODO only when status changes!
    protected void statusUpdate() {
        Player me = Register.getController().getAllPlayersId().get(clientThread.getClientID());
        JSONObject info = new JSONObject();
        info.put("id", me.getId());
        if (!(me.getColor() == PColor.NULL)) {
            info.put("Farbe", new Interpreter().pColorToString(me.getColor()));
        }
        String name = null;
        if (!(me.getName().isEmpty())) {
            name = me.getName();
        }
        info.put("Name", name);
        info.put("Status", new Interpreter().modelConversion(me.getStatus()));
        info.put("Längste Handelsstraße", me.getLongestRoad());
        info.put("Größte Rittermacht", me.getLargestArmy());
        info.put("Rittermacht", me.getKnight());

        for (int x = 0; x < 2; x++) {
            if (x == 0) {
                info.put("Rohstoffe", resObj(me, true));
                info.put("Entwicklungskarten", devObj(me, true));
                int vp = me.getVictoryPoints() + me.getVictoryPointDev();
                info.put("Siegpunkte", vp);
            } else if (x == 1) {
                info.put("Rohstoffe", resObj(me, false));
                info.put("Entwicklungskarten", devObj(me, false));
                info.put("Siegpunkte", me.getVictoryPoints());
            }
            JSONObject player = new JSONObject();
            player.put("Spieler", info);
            JSONObject update = new JSONObject();
            update.put("Statusupdate", player);
            if (x == 0) {
                clientThread.msgThisClient(update);
            } else if (x == 1) {
                clientThread.msgAllButThisClient(update);
            }
        }
    }

    /**
     * Send status updates to player via JSON objects
     *
     * @param id player id
     * @return JSONObject status update
     */
    protected JSONObject jsonStatusUpdate(int id) {
        Player p = Register.getController().getAllPlayersId().get(id);
        JSONObject info = new JSONObject();
        info.put("id", p.getId());
        if (!(p.getColor() == PColor.NULL)) {
            info.put("Farbe", new Interpreter().pColorToString(p.getColor()));
        }
        String name = null;
        if (!(p.getName().isEmpty())) {
            name = p.getName();
        }
        info.put("Name", name);
        String status = new Interpreter().modelConversion(p.getStatus());
        info.put("Status", status);
        if (id == clientThread.getClientID()) {
            int vp = p.getVictoryPoints() + p.getVictoryPointDev();
            info.put("Siegpunkte", vp);
        } else {
            info.put("Siegpunkte", p.getVictoryPoints());
        }
        info.put("Rohstoffe", resObjCheck(p, id));
        info.put("Längste Handelsstraße", p.getLongestRoad());
        info.put("Größte Rittermacht", p.getLargestArmy());
        info.put("Rittermacht", p.getKnight());
        info.put("Entwicklungskarten", devObjCheck(p, id));

        JSONObject player = new JSONObject();
        player.put("Spieler", info);
        JSONObject update = new JSONObject();
        update.put("Statusupdate", player);
        return update;
    }

    /**
     * Resource information of players via JSON object
     *
     * @param player selected player
     * @param check boolean
     * @return JSON object
     */
    private JSONObject resObj(Player player, boolean check) {
        JSONObject res = new JSONObject();
        if (check) {
            res.put("Holz", player.getLumber());
            res.put("Lehm", player.getBrick());
            res.put("Wolle", player.getWool());
            res.put("Getreide", player.getGrain());
            res.put("Erz", player.getOre());
        } else {
            res.put("Unbekannt", player.getAllResourceList().size());
        }
        return res;
    }

    /**
     * DevelopmentCards information of players via JSON object
     *
     * @param player selected player
     * @param check boolean
     * @return JSON object
     */
    private JSONObject devObj(Player player, boolean check) {
        JSONObject dev = new JSONObject();
        if (check) {
            dev.put("Ritter", player.getKnightsDev());
            dev.put("Straßenbau", player.getRoadBuildingDev());

            dev.put("Monopol", player.getMonopolyDev());
            dev.put("Erfindung", player.getYearOfPlentyDev());
            dev.put("Siegpunkt", player.getVictoryPointDev());
        } else {
            dev.put("Unbekannt", player.getDevelopmentDev());
        }
        return dev;
    }

    /**
     * Check if its the own player for resource cards
     *
     * @param player selected player
     * @param id     player id
     * @return boolean
     */
    private JSONObject resObjCheck(Player player, int id) {
        if (id == clientThread.getClientID()) {
            return resObj(player, true);
        } else {
            return resObj(player, false);
        }
    }

    /**
     * check if its the own player for development cards
     *
     * @param player selected player
     * @param id     player id
     * @return boolean
     */
    private JSONObject devObjCheck(Player player, int id) {
        if (id == clientThread.getClientID()) {
            return devObj(player, true);
        } else {
            return devObj(player, false);
        }
    }

    /**
     * Send map to everyone
     *
     */
    protected void sendMap() {
        JSONArray harbors = new JSONArray();
        for (Tile tile : Register.getController().getBoard().getAllTiles().values()) {
            if (tile instanceof model.board.Sea && ((Sea) tile).getPort() != null) {
                JSONObject oneHarbor = new JSONObject();
                oneHarbor.put("Typ", new Interpreter().modelTranslateH(((Sea) tile).getPort().toString()));
                oneHarbor.put("Ort", new Interpreter().bitSetToAxial(new Interpreter().makeEdgeBitSet(tile.getId(), Register.getController().
                        getBoard().getTerrainneighborOfSeaharbor(tile.getId()))));
                harbors.put(oneHarbor);
            }
        }
        JSONArray buildings = new JSONArray();
        JSONArray tiles = new JSONArray();
        for (Tile tile : Register.getController().getBoard().getAllTiles().values()) {
            JSONObject oneTile = new JSONObject();
            oneTile.put("Ort", new Interpreter().tileToAxial(tile.getId()));
            oneTile.put("Typ", new Interpreter().modelTranslate(getType(tile)));
            if (!(tile instanceof model.board.Sea) && !(tile instanceof model.board.Desert)) {
                oneTile.put("Zahl", ((Terrain) tile).getNumberToken());
            }
            tiles.put(oneTile);
        }
        JSONObject map = new JSONObject();
        map.put("Felder", tiles);
        map.put("Gebäude", buildings);
        map.put("Häfen", harbors);
        map.put("Räuber", new Interpreter().tileToAxial(Register.getController().getManagement().getRobber().getPosition()));
        JSONObject gameStarted = new JSONObject();
        gameStarted.put("Karte", map);
        JSONObject obj = new JSONObject();
        obj.put("Spiel gestartet", gameStarted);
        clientThread.msgAll(obj);
    }

    /**
     * Get english term of one board tile
     *
     * @param tile one tile
     * @return english term of tile type
     */
    protected String getType(Tile tile) {
        if (tile instanceof model.board.Sea) {
            return "Sea";
        } else if (tile instanceof model.board.Field) {
            return "Field";
        } else if (tile instanceof model.board.Hill) {
            return "Hill";
        } else if (tile instanceof model.board.Pasture) {
            return "Pasture";
        } else if (tile instanceof model.board.Forest) {
            return "Forest";
        } else if (tile instanceof model.board.Mountain) {
            return "Mountain";
        } else if (tile instanceof model.board.Desert) {
            return "Desert";
        }
        return null;
    }

    /**
     * Roll dice and send result to everyone
     *
     */
    protected void rollDice() {
        LOGGER.traceEntry();
        JSONObject event = new JSONObject();
        event.put("Spieler", clientThread.getClientID());
        int[] dices = Register.getController().getManagement().rollDices();
        Register.getController().getAllPlayersId().get(clientThread.getClientID()).setDiceValue(dices[0] + dices[1]);
        event.put("Wurf", new JSONArray(dices));
        JSONObject dice = new JSONObject();
        dice.put("Würfelwurf", event);
        serverOK();
        clientThread.msgAll(dice);
        if (dices[0] + dices[1] != 7) {
            if (Register.getController().getManagement().getDice() != 7) {
                HashMap<Player, ArrayList<Resource>> map = Register.getController().getManagement().bigHarvest(Register.getController().getManagement().getDice());
                for (Player id : map.keySet()) {
                    if (map.get(id).size() != 0) {
                        JSONObject inn = new JSONObject();
                        inn.put("Spieler", id.getId());
                        inn.put("Rohstoffe", (new JsonLib()).countRes(map.get(id)));
                        JSONObject out = new JSONObject();
                        out.put("Ertrag", inn);
                        clientThread.msgAll(out);
                    }
                }
            }
            Register.getController().getAllPlayersId().get(clientThread.getClientID()).setStatus(Status.TRADE_OR_BUILD);
            statusUpdate();
        } else {
            Register.setMoveRobberHold(clientThread.getClientID());
            for (Player player : Register.getController().getAllPlayersId().values()) {
                if (player.getResource() > 7) {
                    Register.addPlayerToQueue(player.getId());
                    player.setActive(true);
                    player.setStatus(Status.HAND_IN_CARDS_BECAUSE_OF_ROBBER);
                    new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(player.getId()))).statusUpdate();
                } else {
                    player.setActive(false);
                    player.setStatus(Status.WAIT);
                    new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(player.getId()))).statusUpdate();
                }
            }
            if (Register.getHandInQueue().size() == 0) {
                Register.getController().getAllPlayersId().get(clientThread.getClientID()).setActive(true);
                Register.getController().getAllPlayersId().get(clientThread.getClientID()).setStatus(Status.MOVE_ROBBER);
                statusUpdate();
            }
        }
    }

    /**
     * Play robber and send new location
     *
     */
    public void moveRobber() {
        LOGGER.traceEntry();
        JSONObject moveRobber = msg.getJSONObject("Räuber versetzen");
        if (moveRobber.has("Ort")) {
            // TODO check if terrain and not old position
            if (Register.getController().getManagement().getRobber().changePosition(new Interpreter().axialToTile(moveRobber.getJSONObject("Ort")))) {
                Resource res = null;
                JSONObject lvl1 = new JSONObject();
                JSONObject lvl2 = new JSONObject();
                lvl2.put("Spieler", clientThread.getClientID());
                lvl2.put("Ort", moveRobber.get("Ort"));
                serverOK();
                if (moveRobber.has("Ziel")) { // TODO check if getrobable > 0 and not me
                    res = Register.getController().getManagement().getRobber().robCard(Register.getController().getAllPlayersId().get(clientThread.getClientID()), Register.getController().getAllPlayersId().get(moveRobber.getInt("Ziel")));
                    if (res == null) {
                        sendResponse("Player has no Resources.");
                    } else {
                        sendCostAndGain(res, moveRobber.getInt("Ziel"), clientThread.getClientID());
                    }
                    lvl2.put("Ziel", moveRobber.get("Ziel"));
                } else {
                    lvl2.put("Ziel", -1);
                }
                lvl1.put("Räuber versetzt", lvl2);
                clientThread.msgAll(lvl1);
                Register.getController().getAllPlayersId().get(Register.getMoveRobberHold()).setActive(true);
                Register.getController().getAllPlayersId().get(Register.getMoveRobberHold()).setStatus(Status.TRADE_OR_BUILD);
                statusUpdate();
            }
        } else {
            sendResponse("Wrong!");
        }
    }

    /**
     * Check if new robber position is valid
     *
     * @return boolean
     */
    public boolean checkNewRobberPosition() {
        JSONObject moveRobber = msg.getJSONObject("Räuber versetzen");
        Robber r = Register.getController().getManagement().getRobber();
        Board board = Register.getController().getBoard();
        if (moveRobber.has("Ort")) {
            BitSet newLocation = new Interpreter().axialToTile(moveRobber.getJSONObject("Ort"));
            BitSet oldLocation = r.getPosition();
            return !(newLocation.equals(oldLocation)) && board.getAllTiles().get(newLocation) instanceof Terrain;
        } else {
            return false;
        }
    }

    /**
     * Play knight and send new position
     *
     */
    public void moveKnight() {
        LOGGER.traceEntry();
        JSONObject moveRobber = msg.getJSONObject("Ritter ausspielen");
        if (moveRobber.has("Ort")) { // TODO check if terrain and not old position
            if (Register.getController().getManagement().getRobber().changePosition(new Interpreter().axialToTile(moveRobber.getJSONObject("Ort")))) {
                Resource res = null;
                JSONObject lvl1 = new JSONObject();
                JSONObject lvl2 = new JSONObject();
                lvl2.put("Spieler", clientThread.getClientID());
                lvl2.put("Ort", moveRobber.get("Ort"));
                serverOK();
                if (moveRobber.has("Ziel")) { // TODO check if getrobable > 0 and not me
                    res = Register.getController().getManagement().getRobber().robCard(Register.getController().getAllPlayersId().get(clientThread.getClientID()), Register.getController().getAllPlayersId().get(moveRobber.getInt("Ziel")));
                    if (res == null) {
                        sendResponse("Player has no Resources.");
                    } else {
                        sendCostAndGain(res, moveRobber.getInt("Ziel"), clientThread.getClientID());
                    }
                    lvl2.put("Ziel", moveRobber.get("Ziel"));
                } else {
                    lvl2.put("Ziel", -1);
                }
                lvl1.put("Ritter ausspielen", lvl2);
                clientThread.msgAll(lvl1);
                Player p = Register.getController().getAllPlayersId().get(clientThread.getClientID());
                p.setActive(true);
                p.setKnight(p.getKnight() + 1);
                p.setKnightsDev(p.getKnightsDev() - 1);
                p.setDevelopmentDev(p.getDevelopmentDev() - 1);
                for (DevelopmentCard card : p.getDevelopmentCards()) {
                    if (card instanceof KnightCard) {
                        p.getDevelopmentCards().remove(card);
                        break;
                    }
                }
                checkLargesArmy();
                statusUpdate();
                checkEndGame();
            }
        } else {
            sendResponse("Wrong!");
        }
    }

    /**
     * VERSION 1
     *
     * Play monopoly card
     * Sending transparent gain and cost (1.0 network protocol)
     */
    public void acceptMonopol() {

        JSONObject playMonpol = msg.getJSONObject("Monopol");
        ArrayList<Resource> res = new ArrayList<>();
        int temp0 = 0;
        int temp;

        if (playMonpol.has("Rohstoff")) {
            String resource = playMonpol.getString("Rohstoff");
            String resourceEng = null;
            serverOK();
            JSONObject temp1 = new JSONObject();
            JSONObject temp2 = new JSONObject();
            temp1.put("Monopol", temp2);
            temp2.put("Rohstoff", resource);
            temp2.put("Spieler", clientThread.getClientID());
            clientThread.msgAll(temp1);
            for (Player player : Register.getController().getAllPlayersId().values()) {
                switch (resource) {
                    case "Holz":
                        if (player.getLumber() > 0) {
                            temp = player.getLumber();
                            temp0 += temp;
                            helpMonopol(temp, player, resource);
                            res.add(Resource.LUMBER);
                            resourceEng = "Lumber";
                        }
                        break;
                    case "Lehm":
                        if (player.getBrick() > 0) {
                            temp = player.getBrick();
                            temp0 += temp;
                            helpMonopol(temp, player, resource);
                            res.add(Resource.BRICK);
                            resourceEng = "Brick";
                        }
                        break;
                    case "Wolle":
                        if (player.getWool() > 0) {
                            temp = player.getWool();
                            temp0 += temp;
                            helpMonopol(temp, player, resource);
                            res.add(Resource.WOOL);
                            resourceEng = "Wool";
                        }
                        break;
                    case "Getreide":
                        if (player.getGrain() > 0) {
                            temp = player.getGrain();
                            temp0 += temp;
                            helpMonopol(temp, player, resource);
                            res.add(Resource.GRAIN);
                            resourceEng = "Grain";
                        }
                        break;
                    case "Erz":
                        if (player.getOre() > 0) {
                            temp = player.getOre();
                            temp0 += temp;
                            helpMonopol(temp, player, resource);
                            res.add(Resource.ORE);
                            resourceEng = "Ore";
                        }
                        break;
                    default:
                        sendResponse("(i) Wrong monopol resource! " + resourceEng + " doesn't exist.");
                        break;
                }
            }
            JSONObject lvl1 = new JSONObject();
            JSONObject lvl2 = new JSONObject();
            JSONObject lvl3 = new JSONObject();

            lvl1.put("Ertrag", lvl2);
            lvl2.put("Spieler", clientThread.getClientID());
            lvl2.put("Rohstoffe", lvl3);
            lvl3.put(resource, temp0);


            Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(clientThread.getClientID())).msgThisClient(lvl1);
            Player p = Register.getController().getAllPlayersId().get(clientThread.getClientID());
            if (res.size() > 0) {
                new MonopolyCard(Register.getController().getManagement()).playCard(p, res.get(0));
            }
            p.setActive(true);
            p.setMonopolyDev(p.getMonopolyDev() - 1);
            p.setDevelopmentDev(p.getDevelopmentDev() - 1);
            for (DevelopmentCard card : p.getDevelopmentCards()) {
                if (card instanceof MonopolyCard) {
                    p.getDevelopmentCards().remove(card);
                    break;
                }
            }
            sendResponse("(Received) " + resourceEng + ": " + "+" + temp0);
            statusUpdate();
        } else {
            sendResponse("(i) JSON Object failure. Can't play Monopolcard.");
        }
    }

    /**
     * Method just for acceptMonopol() to create a cost JSONObject
     *
     * @param temp     resource amount
     * @param player selected player
     * @param resource selected resource
     */
    public void helpMonopol(int temp, Player player, String resource) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        JSONObject lvl3 = new JSONObject();
        lvl2.put("Spieler", player.getId());
        lvl2.put("Rohstoffe", lvl3);
        lvl3.put(resource, temp);
        if (player.getId() != clientThread.getClientID()) {

            lvl1.put("Kosten", lvl2);

            Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(player.getId())).msgThisClient(lvl1);
        } else {

        }
    }

    /**
     * VERSION 2
     *
     * Play monopoly card
     * Sending transparent gain and cost (1.0 network protocol)
     */
    protected void monopolyCard() {
        JSONObject playMonpol = msg.getJSONObject("Monopol");
        Player me = Register.getController().getAllPlayersId().get(clientThread.getClientID());
        if (playMonpol.has("Rohstoff")) {
            Resource r = new Interpreter().stringToResource(playMonpol.getString("Rohstoff"));
            JSONObject lvl1 = new JSONObject();
            JSONObject lvl2 = new JSONObject();
            lvl2.put("Rohstoff", new Interpreter().resourceToString(r));
            lvl2.put("Spieler", clientThread.getClientID());
            lvl1.put("Monopol", lvl2);
            serverOK();
            clientThread.msgAll(lvl1);
            int allRes = 0;
            for (Player p : Register.getController().getAllPlayersId().values()) {
                if (p.getId() != me.getId()) {
                    switch (r) {
                        case BRICK:
                            if (p.getBrick() > 0) {
                                allRes += p.getBrick();
                                me.setBrick(me.getBrick() + p.getBrick());
                                sendTransparentCost(new JsonLib().countRes(new JsonLib().createResList(r, p.getBrick())), p);
                                p.setBrick(0);
                            }
                            break;
                        case WOOL:
                            if (p.getWool() > 0) {
                                allRes += p.getWool();
                                me.setWool(me.getWool() + p.getWool());
                                sendTransparentCost(new JsonLib().countRes(new JsonLib().createResList(r, p.getWool())), p);
                                p.setWool(0);
                            }
                            break;
                        case GRAIN:
                            if (p.getGrain() > 0) {
                                allRes += p.getGrain();
                                me.setGrain(me.getGrain() + p.getGrain());
                                sendTransparentCost(new JsonLib().countRes(new JsonLib().createResList(r, p.getGrain())), p);
                                p.setGrain(0);
                            }
                            break;
                        case ORE:
                            if (p.getOre() > 0) {
                                allRes += p.getOre();
                                me.setOre(me.getOre() + p.getOre());
                                sendTransparentCost(new JsonLib().countRes(new JsonLib().createResList(r, p.getOre())), p);
                                p.setOre(0);
                            }
                            break;
                        case LUMBER:
                            if (p.getOre() > 0) {
                                allRes += p.getLumber();
                                me.setLumber(me.getLumber() + p.getLumber());
                                sendTransparentCost(new JsonLib().countRes(new JsonLib().createResList(r, p.getLumber())), p);
                                p.setLumber(0);
                            }
                            break;
                        case NULL:
                        default:
                            LOGGER.info("Case not covered!");
                    }
                }
            }
            if (allRes > 0) {
                sendTransparentGain(new JsonLib().countRes(new JsonLib().createResList(r, allRes)), me);
            }
            me.setActive(true);
            me.setMonopolyDev(me.getMonopolyDev() - 1);
            me.setDevelopmentDev(me.getDevelopmentDev() - 1);
            for (DevelopmentCard card : me.getDevelopmentCards()) {
                if (card instanceof MonopolyCard) {
                    me.getDevelopmentCards().remove(card);
                    break;
                }
            }
            statusUpdate();
        } else {
            sendResponse("Illegal action.");
        }
    }

    /**
     * Send transparent costs to players
     *
     * @param res resource
     * @param p   player
     */
    private void sendTransparentCost(JSONObject res, Player p) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Spieler", p.getId());
        lvl2.put("Rohstoffe", res);
        lvl1.put("Kosten", lvl2);
        clientThread.msgAll(lvl1);
    }

    /**
     * Play developmentcard "YearOfPlenty" and remove card from stack
     *
     */

    public void yearOfPlenty() { // TODO more checks!
        JSONObject cancelTrade = msg.getJSONObject("Erfindung");
        ArrayList<Resource> res = new JsonLib().countResJSONObj(cancelTrade.getJSONObject("Rohstoffe"));
        Player p = Register.getController().getAllPlayersId().get(clientThread.getClientID());
        new YearOfPlentyCard(Register.getController().getManagement()).playCard(p, res.get(0), res.get(1));
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Spieler", p.getId());
        lvl2.put("Rohstoffe", cancelTrade.getJSONObject("Rohstoffe"));
        lvl1.put("Erfindung", lvl2);
        clientThread.msgAll(lvl1);
        sendGain(cancelTrade.getJSONObject("Rohstoffe"), p.getId());
        p.setActive(true);
        p.setYearOfPlentyDev(p.getYearOfPlentyDev() - 1);
        p.setDevelopmentDev(p.getDevelopmentDev() - 1);
        for (DevelopmentCard card : p.getDevelopmentCards()) {
            if (card instanceof YearOfPlentyCard) {
                p.getDevelopmentCards().remove(card);
                break;
            }
        }
        statusUpdate();
    }

    /**
     * Send costs to player
     *
     * @param res resource
     * @param id  player
     */
    private void sendCost(JSONObject res, int id) {
        for (int i = 0; i < 2; i++) {
            JSONObject lvl1 = new JSONObject();
            JSONObject lvl2 = new JSONObject();
            lvl2.put("Spieler", id);
            if (i == 0) {
                lvl2.put("Rohstoffe", res);
                lvl1.put("Kosten", lvl2);
                Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(id)).msgThisClient(lvl1);
            } else {
                JSONObject unknown = new JSONObject();
                unknown.put("Unbekannt", new JsonLib().countResJSONObj(res).size());
                lvl2.put("Rohstoffe", unknown);
                lvl1.put("Kosten", lvl2);
                Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(id)).msgAllButThisClient(lvl1);
            }

        }
    }

    /**
     * Play developmentcard "RoadBuilding"
     *
     */
    public void roadBuildingCard() {
        JSONObject cancelTrade = msg.getJSONObject("Straßenbaukarte ausspielen");
        Player p = Register.getController().getAllPlayersId().get(clientThread.getClientID());
        BitSet a = new Interpreter().axialToEdgeOrCorner(cancelTrade.getJSONArray("Straße 1"));
        BitSet b = new BitSet();
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        if (cancelTrade.has("Straße 2")) {
            b = new Interpreter().axialToEdgeOrCorner(cancelTrade.getJSONArray("Straße 2"));
            lvl2.put("Straße 2", new Interpreter().edgeToAxial(b));
        }
        lvl2.put("Straße 1", new Interpreter().edgeToAxial(a));
        lvl2.put("Spieler", p.getId());
        lvl1.put("Straßenbaukarte ausspielen", lvl2);
        if (!b.isEmpty()) {
            if (p.testEnoughBuildingsRoadBuildingCard()) {
                if (p.testPlaceRoadBuildingCard(a, b)) {
                    p.buildRoadBuildingCardRoad(a);
                    p.buildRoadBuildingCardRoad(b);
                    serverOK();
                    ClientThreadSequence.setDevCard(false);
                    clientThread.msgAll(lvl1);
                    sendBuildProcess("Straße", a);
                    sendBuildProcess("Straße", b);
                } else {
                    sendResponse("(i) You can't build roads at these places.");
                    return;
                }
            } else {
                sendResponse("(i) You don't have 2 roads left.");
                return;
            }
        } else {
            if (p.testEnoughBuildings(Test.STREET)) {
                if (p.testPlace(Test.STREET, a)) {
                    p.buildRoadBuildingCardRoad(a);
                    serverOK();
                    ClientThreadSequence.setDevCard(false);
                    clientThread.msgAll(lvl1);
                    sendBuildProcess("Straße", a);
                } else {
                    sendResponse("You can't build a road at this place.");
                    return;
                }
            } else {
                sendResponse("You don't have anymore buildings of this kind left.");
                return;
            }
        }
        p.setActive(true);
        p.setRoadBuildingDev(p.getRoadBuildingDev() - 1);
        p.setDevelopmentDev(p.getDevelopmentDev() - 1);
        for (DevelopmentCard card : p.getDevelopmentCards()) {
            if (card instanceof RoadBuildingCard) {
                p.getDevelopmentCards().remove(card);
                break;
            }
        }
        statusUpdate();
        checkLongestStreet();
        checkEndGame();
    }

    /**
     * Send cost and gain to player
     *
     * @param res        resource
     * @param playerCost cost for player
     * @param playerGain gain for player
     */

    public void sendCostAndGain(Resource res, int playerCost, int playerGain) {
        JSONObject lvl2 = new JSONObject();
        switch (res) {
            case BRICK:
                lvl2.put("Lehm", 1);
                break;
            case GRAIN:
                lvl2.put("Getreide", 1);
                break;
            case WOOL:
                lvl2.put("Wolle", 1);
                break;
            case LUMBER:
                lvl2.put("Holz", 1);
                break;
            case ORE:
                lvl2.put("Erz", 1);
                break;
        }
        sendCost(lvl2, playerCost);
        sendGain(lvl2, playerGain);
    }

    /**
     * If player has > 7 cards. player has to hand in cards to server
     *
     */
    public void handInCards() {
        LOGGER.traceEntry();
        JSONObject handIn = msg.getJSONObject("Karten abgeben");
        JSONObject res = handIn.getJSONObject("Abgeben");
        Player p = Register.getController().getAllPlayersId().get(clientThread.getClientID());
        Register.getController().getManagement().getRobber().startRobbing(p, new JsonLib().countResJSONObj(res));
        sendCost(res, clientThread.getClientID());
        Register.removePlayerFromQueue(clientThread.getClientID());
        serverOK();
        if (checkAllForHandIn()) {
            Register.getController().getAllPlayersId().get(Register.getMoveRobberHold()).setActive(true);
            Register.getController().getAllPlayersId().get(Register.getMoveRobberHold()).setStatus(Status.MOVE_ROBBER);
            if (clientThread.getClientID() != Register.getMoveRobberHold()) {
                Register.getController().getAllPlayersId().get(clientThread.getClientID()).setActive(false);
                Register.getController().getAllPlayersId().get(clientThread.getClientID()).setStatus(Status.WAIT);
                statusUpdate();
                new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(Register.getMoveRobberHold()))).statusUpdate();
            } else {
                statusUpdate();
            }
        } else {
            Register.getController().getAllPlayersId().get(clientThread.getClientID()).setActive(false);
            Register.getController().getAllPlayersId().get(clientThread.getClientID()).setStatus(Status.WAIT);
            statusUpdate();
        }
    }

    /**
     * number of players who has to hand in cards
     *
     * @return number of players
     */
    private boolean checkAllForHandIn() {
        LOGGER.info(Register.getHandInQueue().size());
        return Register.getHandInQueue().size() == 0;
    }

    /**
     * Send gain to players
     *
     * @param resources gained resources
     * @param id        player
     */
    private void sendGain(JSONObject resources, int id) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Spieler", id);
        lvl2.put("Rohstoffe", resources);
        lvl1.put("Ertrag", lvl2);
        Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(id)).msgThisClient(lvl1);
        lvl1 = new JSONObject();
        lvl2 = new JSONObject();
        lvl2.put("Spieler", id);
        JSONObject lvl3 = new JSONObject();
        lvl3.put("Unbekannt", new JsonLib().countResJSONObj(resources).size());
        lvl2.put("Rohstoffe", lvl3);
        lvl1.put("Ertrag", lvl2);
        Register.getNtwrkServer().getThreads().get(Register.getNtwrkServer().getClientThreadID(id)).msgAllButThisClient(lvl1);
    }

    /**
     * Send transparant gain to player
     *
     * @param resources resource
     * @param p         player
     */
    private void sendTransparentGain(JSONObject resources, Player p) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Spieler", p.getId());
        lvl2.put("Rohstoffe", resources);
        lvl1.put("Ertrag", lvl2);
        clientThread.msgAll(lvl1);
    }

    /**
     * Try building settlement, city or street, if successful send message to everyone and update model logic else send
     * error to client
     *
     */
    protected void build() {
        boolean forFree = false;
        switch (ClientThreadSequence.getLevel()) {
            case FIRST_ROUND:
            case SECOND_ROUND:
                forFree = true;
                break;
            case OTHER_ROUNDS:
                forFree = false;
                break;
            default:
                LOGGER.warn("Case not covered!" + " " + ClientThreadSequence.getLevel());
        }
        JSONObject build = msg.getJSONObject("Bauen");
        String type = build.getString("Typ");
        BitSet loc = new Interpreter().axialToEdgeOrCorner(build.getJSONArray("Ort"));
        serverOK();
        switch (type) {
            case "Straße":
                if (forFree) {
                    Register.getController().getAllPlayersId().get(clientThread.getClientID()).buildStartRoad(loc);
                    sendBuildProcess(type, loc);
                } else {
                    Register.getController().getAllPlayersId().get(clientThread.getClientID()).buildRoad(loc);
                    sendBuildProcess(type, loc);
                    sendCostProcess("Straße");
                    statusUpdate();
                }
                break;
            case "Dorf":
                if (forFree) {
                    ArrayList<Resource> list = Register.getController().getAllPlayersId().get(clientThread.getClientID()).buildStartSettlement(loc);
                    Register.getController().getAllPlayersId().get(clientThread.getClientID()).setStatus(Status.BUILD_STREET);
                    sendBuildProcess(type, loc);
                    if (list.size() != 0) {
                        sendTransparentGain(new JsonLib().countRes(list), Register.getController().getAllPlayersId().get(clientThread.getClientID()));
                    }
                } else {
                    Register.getController().getAllPlayersId().get(clientThread.getClientID()).buildSettlement(loc);
                    sendBuildProcess(type, loc);
                    sendCostProcess("Dorf");
                    statusUpdate();
                }
                break;
            case "Stadt":
                if (forFree) {
                    sendResponse("No valid action!");
                    LOGGER.warn("Case not covered!" + " " + type);
                } else {
                    Register.getController().getAllPlayersId().get(clientThread.getClientID()).buildCity(loc);
                    sendBuildProcess(type, loc);
                    sendCostProcess("Stadt");
                    statusUpdate();
                }
                break;
            default:
                sendResponse("No valid action!");
                LOGGER.warn("Case not covered!" + " " + type);
        }
        checkLongestStreet();
        checkEndGame();
    }

    /**
     * Check if player reached end game
     *
     */
    private void checkEndGame() {
        int id = Register.getController().getManagement().finish();
        if (id > 0) {
            gameEnd(id);
            ClientThreadSequence.setLevel(network.Level.END_GAME);
        }
    }

    /**
     * Check longest street
     *
     */
    private void checkLongestStreet() {
        int tmp = Register.getController().getManagement().longestRoad();
        if (tmp < 0) {
            lostLongestStreet();
        } else if (tmp > 0) {
            longestStreet(Register.getController().getAllPlayersId().get(tmp));
        }
    }

    /**
     * Send building process
     *
     * @param type of building
     * @param loc  location
     */
    private void sendBuildProcess(String type, BitSet loc) {
        JSONObject information = new JSONObject();
        information.put("Eigentümer", clientThread.getClientID());
        information.put("Typ", type);
        information.put("Ort", new Interpreter().bitSetToAxial(loc));
        JSONObject building = new JSONObject();
        building.put("Gebäude", information);
        JSONObject process = new JSONObject();
        process.put("Bauvorgang", building);
        clientThread.msgAll(process);
    }

    /**
     * Send cost of build process to clientID
     *
     * @param type String settlement or city or street or development card
     */
    private void sendCostProcess(String type) {

        JSONObject cost = new JSONObject();
        JSONObject player = new JSONObject();
        JSONObject ressource = new JSONObject();
        JSONObject unknown = new JSONObject();


        switch (type) {
            case "Straße":
                ressource.put("Holz", 1);
                ressource.put("Lehm", 1);
                unknown.put("Unbekannt", 2);
                break;
            case "Dorf":
                ressource.put("Holz", 1);
                ressource.put("Lehm", 1);
                ressource.put("Wolle", 1);
                ressource.put("Getreide", 1);
                unknown.put("Unbekannt", 4);
                break;
            case "Stadt":
                ressource.put("Getreide", 2);
                ressource.put("Erz", 3);
                unknown.put("Unbekannt", 5);
                break;
            case "Entwicklungskarte":
                ressource.put("Getreide", 1);
                ressource.put("Erz", 1);
                ressource.put("Wolle", 1);
                unknown.put("Unbekannt", 3);
                break;
        }
        cost.put("Kosten", player);
        player.put("Spieler", clientThread.getClientID());
        player.put("Rohstoffe", ressource);
        clientThread.msgThisClient(cost);
        player = new JSONObject();
        player.put("Spieler", clientThread.getClientID());
        player.put("Rohstoffe", unknown);
        cost = new JSONObject();
        cost.put("Kosten", player);
        clientThread.msgAllButThisClient(cost);
    }


    /**
     * Handle buy - development cards and inform clients
     *
     */
    protected void buyDevCardSendJsonToClient() {
        if (!devcardCheat) {
            Player p = Register.getController().getAllPlayersId().get(clientThread.getClientID());

            if (p.getDevelopmentCardsMove().size() > 0) {
                JSONObject obj = new JSONObject();
                sendResponse("No valid action! Just one development card per turn.");

            } else {
                p.buyDevelopmentCard(p);
                ArrayList<DevelopmentCard> cards = p.getDevelopmentCardsMove();
                DevelopmentCard card = cards.get(0);

                JSONObject objClient = new JSONObject();
                JSONObject innerObjClient = new JSONObject();

                objClient.put("Entwicklungskarte gekauft", innerObjClient);
                innerObjClient.put("Spieler", clientThread.getClientID());


                JSONObject obj = new JSONObject();
                JSONObject innerObj = new JSONObject();

                obj.put("Entwicklungskarte gekauft", innerObj);
                innerObj.put("Spieler", clientThread.getClientID());
                innerObj.put("Entwicklungskarte", "Unbekannt");


                if (card instanceof KnightCard) {
                    innerObjClient.put("Entwicklungskarte", "Ritter");
                    p.setKnightsDev(p.getKnightsDev() + 1);
                }
                if (card instanceof MonopolyCard) {
                    innerObjClient.put("Entwicklungskarte", "Monopol");
                    p.setMonopolyDev(p.getMonopolyDev() + 1);
                }
                if (card instanceof RoadBuildingCard) {
                    innerObjClient.put("Entwicklungskarte", "Straßenbau");
                    p.setRoadBuildingDev(p.getRoadBuildingDev() + 1);
                }
                if (card instanceof VictoryPointCard) {
                    innerObjClient.put("Entwicklungskarte", "Siegpunkt");
                    p.setVictoryPointDev(p.getVictoryPointDev() + 1);
                }
                if (card instanceof YearOfPlentyCard) {
                    innerObjClient.put("Entwicklungskarte", "Erfindung");
                    p.setYearOfPlentyDev(p.getYearOfPlentyDev() + 1);
                }

                p.setDevelopmentDev(p.getDevelopmentDev() + 1);
                clientThread.msgThisClient(objClient);
                clientThread.msgAllButThisClient(obj);
                sendCostProcess("Entwicklungskarte");
                statusUpdate();
            }
        } else {
            //CHEAT "mop" just for faster testing to get development cards

            Player p = Register.getController().getAllPlayersId().get(clientThread.getClientID());
            JSONObject objClient = new JSONObject();
            JSONObject innerObjClient = new JSONObject();

            objClient.put("Entwicklungskarte gekauft", innerObjClient);
            innerObjClient.put("Spieler", clientThread.getClientID());

            JSONObject obj = new JSONObject();
            JSONObject innerObj = new JSONObject();

            obj.put("Entwicklungskarte gekauft", innerObj);
            innerObj.put("Spieler", clientThread.getClientID());
            innerObj.put("Entwicklungskarte", "Unbekannt");


            // code-block for knight card

//            innerObjClient.put("Entwicklungskarte", "Ritter");
//            p.setKnightsDev(p.getKnightsDev() + 1);
//            p.setDevelopmentDev(p.getDevelopmentDev() + 1);
//            p.getDevelopmentCardsMove().add(new KnightCard(Register.getController().getManagement()));
//            clientThread.msgThisClient(objClient);
//            clientThread.msgAllButThisClient(obj);
//            sendCostProcess("Entwicklungskarte");

            // code-block for monopol card

            innerObjClient.put("Entwicklungskarte", "Monopol");
            p.setMonopolyDev(p.getMonopolyDev() + 1);
            p.setDevelopmentDev(p.getDevelopmentDev() + 1);
            p.getDevelopmentCardsMove().add(new MonopolyCard(Register.getController().getManagement()));
            clientThread.msgThisClient(objClient);
            clientThread.msgAllButThisClient(obj);
            sendCostProcess("Entwicklungskarte");

            // code-block for roadbuilding card

//            innerObjClient.put("Entwicklungskarte", "Straßenbau");
//            p.setRoadBuildingDev(p.getRoadBuildingDev() + 1);
//            p.setDevelopmentDev(p.getDevelopmentDev() + 1);
//            p.getDevelopmentCardsMove().add(new RoadBuildingCard(Register.getController().getManagement()));
//            clientThread.msgThisClient(objClient);
//            clientThread.msgAllButThisClient(obj);
//            sendCostProcess("Entwicklungskarte");

            // code-block for yearofplenty card

//            innerObjClient.put("Entwicklungskarte", "Erfindung");
//            p.setYearOfPlentyDev(p.getYearOfPlentyDev() + 1);
//            p.setDevelopmentDev(p.getDevelopmentDev() + 1);
//            p.getDevelopmentCardsMove().add(new YearOfPlentyCard(Register.getController().getManagement()));
//            clientThread.msgThisClient(objClient);
//            clientThread.msgAllButThisClient(obj);
//            sendCostProcess("Entwicklungskarte");


            devcardCheat = false;
            statusUpdate();
        }

    }

    /**
     * Send "Serverantwort" to Client
     *
     * @param s response
     */
    void sendResponse(String s) {
        JSONObject response = new JSONObject();
        response.put("Serverantwort", s);
        clientThread.msgThisClient(response);
    }

    /**
     * Send "Chatnachricht" to Client
     *
     * @param s response
     */
    void sendChatMsg(String s) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Nachricht", s);
        lvl1.put("Chatnachricht", lvl2);
        clientThread.msgThisClient(lvl1);
    }

    /**
     * Update player longest street
     *
     * @param p player
     */
    void longestStreet(Player p) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Spieler", p.getId());
        lvl1.put("Längste Handelsstraße", lvl2);
        clientThread.msgAll(lvl1);
        statusUpdate();
    }

    /**
     * Update player lost longest street
     *
     */
    void lostLongestStreet() {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl1.put("Längste Handelsstraße", lvl2);
        clientThread.msgAll(lvl1);
        statusUpdate();
    }

    /**
     * Check largest player
     *
     */
    void checkLargesArmy() {
        int type = Register.getController().getManagement().largestArmy();
        if (type > 0) {
            largestArmy(Register.getController().getAllPlayersId().get(type));
        }
        Register.getController().getManagement().finish();
    }

    /**
     * Update player largest army
     *
     * @param p player
     */
    void largestArmy(Player p) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Spieler", p.getId());
        lvl1.put("Größte Rittermacht", lvl2);
        clientThread.msgAll(lvl1);
    }

    /**
     * Inform players who has won
     *
     * @param id player id
     */
    void gameEnd(int id) {
        Player winner = Register.getController().getAllPlayersId().get(id);
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Spieler", winner.getId());
        lvl2.put("Nachricht", "Spieler " + winner.getName() + " hat das Spiel gewonnen.");
        lvl1.put("Spiel beendet", lvl2);
        clientThread.msgAll(lvl1);
    }

    /**
     * Handle trade offer of players and check before if it's possible
     *
     */

    void startDomesticTrade() {
        JSONObject domesticTrade = msg.getJSONObject("Handel anbieten");
        ArrayList<Resource> offer = new JsonLib().countResJSONObj(domesticTrade.getJSONObject("Angebot"));
        ArrayList<Resource> request = new JsonLib().countResJSONObj(domesticTrade.getJSONObject("Nachfrage"));
        LOGGER.info(offer + ", " + request);
        Register.tradeIDPLUS();
        DomesticTradeObj tradeObj = new DomesticTradeObj(Register.getTradeID(), clientThread.getClientID(), offer, request);
        Register.openTrade(tradeObj.getTradeID(), tradeObj);
        if (!(new JsonLib().checkIfPlayerHasEnoughRes(tradeObj.getOffer(), clientThread.getClientID()))) {
            Register.closeTrade(tradeObj.getTradeID());
            sendResponse("You don't have these resources.");
            return;
        } else if (!(new JsonLib().checkDomesticTrade(offer, request))) {
            Register.closeTrade(tradeObj.getTradeID());
            sendResponse("This kind of trade is prohibited.");
            return;
        } else {
            serverOK();
            JSONObject lvl1 = new JSONObject();
            JSONObject lvl2 = new JSONObject();
            lvl2.put("Spieler", clientThread.getClientID());
            lvl2.put("Handel id", tradeObj.getTradeID());
            LOGGER.debug(new JsonLib().countRes(tradeObj.getOffer()));
            LOGGER.debug(new JsonLib().countRes(tradeObj.getRequest()));
            lvl2.put("Angebot", new JsonLib().countRes(tradeObj.getOffer()));
            lvl2.put("Nachfrage", new JsonLib().countRes(tradeObj.getRequest()));
            lvl1.put("Handelsangebot", lvl2);
            clientThread.msgAllButThisClient(lvl1);
        }
    }

    /**
     * Handle "sea trade" of players and check before if it's possible
     *
     */
    void seaTrade() {
        JSONObject domesticTrade = msg.getJSONObject("Seehandel");
        JSONObject offer = domesticTrade.getJSONObject("Angebot");
        JSONObject request = domesticTrade.getJSONObject("Nachfrage");
        if (new JsonLib().checkIfManagementHasEnoughRes(new JsonLib().countResJSONObj(request))) {
            if (new JsonLib().checkIfPlayerHasEnoughRes(new JsonLib().countResJSONObj(offer), clientThread.getClientID())) {
                if (new JsonLib().checkAllOfOneKind(new JsonLib().countResJSONObj(offer))) {
                    Ratio ratio = new JsonLib().checkRatio(new JsonLib().countResJSONObj(offer), new JsonLib().countResJSONObj(request));
                    if (ratio != Ratio.NULL) {
                        if (new JsonLib().checkHarbor(clientThread.getClientID(), new JsonLib().countResJSONObj(offer).get(0), ratio)) {
                            new JsonLib().makeSeaTrade(clientThread.getClientID(), new JsonLib().countResJSONObj(offer), new JsonLib().countResJSONObj(request));
                            serverOK();
                            sendCost(offer, clientThread.getClientID());
                            sendGain(request, clientThread.getClientID());
                            statusUpdate();
                        } else {
                            sendResponse("You don't own this kind harbor.");
                        }
                    } else {
                        sendResponse("Ratio of resources is wrong.");
                    }
                } else {
                    sendResponse("Offered resources aren't all of one kind.");
                }
            } else {
                sendResponse("You don't have these resources.");
            }
        } else {
            sendResponse("There are not enough resources left.");
        }
    }

    /**
     * Handle "cancel trade" and inform player
     *
     */
    void cancelTrade() {
        JSONObject cancelTrade = msg.getJSONObject("Handel abbrechen");
        int tradeID = cancelTrade.getInt("Handel id");
        if (Register.getTrades().containsKey(tradeID)) {
            serverOK();
            if (Register.getTrades().get(tradeID).getPlayerID() == clientThread.getClientID()) {
                Register.closeTrade(tradeID);
            } else {
                Register.getTrades().get(tradeID).removePartner(clientThread.getClientID());
            }
            JSONObject lvl1 = new JSONObject();
            JSONObject lvl2 = new JSONObject();
            lvl2.put("Spieler", clientThread.getClientID());
            lvl2.put("Handel id", tradeID);
            lvl1.put("Handelsangebot abgebrochen", lvl2);
            clientThread.msgAll(lvl1);
        } else {
            sendResponse("This trade isn't available anymore.");
        }
    }

    /**
     * Handle "accept trade" and inform player
     *
     */
    void acceptTrade() {
        JSONObject cancelTrade = msg.getJSONObject("Handel annehmen");
        int tradeID = cancelTrade.getInt("Handel id");
        boolean accept = cancelTrade.getBoolean("Annehmen");
        serverOK();
        if (Register.getTrades().containsKey(tradeID)) {
            JSONObject lvl1 = new JSONObject();
            JSONObject lvl2 = new JSONObject();
            if (accept) {
                if (new JsonLib().checkIfPlayerHasEnoughRes(Register.getTrades().get(tradeID).getRequest(), clientThread.getClientID())) {
                    Register.getTrades().get(tradeID).makePartnerAccept(clientThread.getClientID());
                    lvl2.put("Annehmen", accept);
                } else {
                    serverOK();
                    sendResponse("You don't have these resources.");
                    Register.getTrades().get(tradeID).removePartner(clientThread.getClientID());
                    lvl2.put("Annehmen", false);
                }
            } else {
                if (Register.getTrades().get(tradeID).getPlayerID() == clientThread.getClientID()) {
                    lvl2.put("Annehmen", accept);
                    Register.closeTrade(tradeID);
                } else {
                    lvl2.put("Annehmen", accept);
                    Register.getTrades().get(tradeID).removePartner(clientThread.getClientID());
                }
            }
            lvl2.put("Mitspieler", clientThread.getClientID());
            lvl2.put("Handel id", tradeID);
            lvl1.put("Handelsangebot angenommen", lvl2);
            clientThread.msgAll(lvl1);
        } else {
            sendResponse("This trade isn't available anymore.");
        }
    }

    /**
     * Handle "complete trade" and inform player
     *
     */
    void completeTrade() {
        JSONObject cancelTrade = msg.getJSONObject("Handel abschließen");
        int tradeID = cancelTrade.getInt("Handel id");
        int partnerID = cancelTrade.getInt("Mitspieler");
        if (Register.getTrades().containsKey(tradeID) && Register.getTrades().get(tradeID).getAcceptedPartners().containsKey(partnerID)) {
            Player a = Register.getController().getAllPlayersId().get(Register.getTrades().get(tradeID).getPlayerID());
            Player b = Register.getTrades().get(tradeID).getAcceptedPartners().get(partnerID);
            if (new JsonLib().checkIfPlayerHasEnoughRes(Register.getTrades().get(tradeID).getOffer(), a.getId()) &&
                    new JsonLib().checkIfPlayerHasEnoughRes(Register.getTrades().get(tradeID).getRequest(), b.getId())) {
                serverOK();
                JSONObject lvl1 = new JSONObject();
                JSONObject lvl2 = new JSONObject();
                lvl2.put("Mitspieler", b.getId());
                lvl2.put("Spieler", a.getId());
                lvl1.put("Handel ausgeführt", lvl2);
                clientThread.msgAll(lvl1);
                DomesticTradeObj tradeObj = Register.getTrades().get(tradeID);
                new JsonLib().changePlayerRes(new JsonLib().countRes(tradeObj.getOffer()), MathOp.SUBTRACT, a.getId());
                sendCost(new JsonLib().countRes(tradeObj.getOffer()), a.getId());
                new JsonLib().changePlayerRes(new JsonLib().countRes(tradeObj.getOffer()), MathOp.ADD, b.getId());
                sendGain(new JsonLib().countRes(tradeObj.getOffer()), b.getId());
                new JsonLib().changePlayerRes(new JsonLib().countRes(tradeObj.getRequest()), MathOp.SUBTRACT, b.getId());
                sendCost(new JsonLib().countRes(tradeObj.getRequest()), b.getId());
                new JsonLib().changePlayerRes(new JsonLib().countRes(tradeObj.getRequest()), MathOp.ADD, a.getId());
                sendGain(new JsonLib().countRes(tradeObj.getRequest()), a.getId());
                jsonStatusUpdate(b.getId());
                statusUpdate();
            }
        } else {
            sendResponse("This trade isn't available anymore.");
        }
    }
}
