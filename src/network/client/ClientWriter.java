package network.client;

import controller.Register;
import model.Resource;
import network.Interpreter;
import network.JsonLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * The ClientWriter class creates different kinds of JSONObject messages and send them to the server.
 *
 */


public class ClientWriter {
    private static final Logger LOGGER = LogManager.getLogger(ClientWriter.class.getName());
    private Client c;

    /**
     * Constructor ClientWriter
     *
     */
    public ClientWriter() {
        c = Register.getNtwrkClient();
    }

    public ClientWriter(Client c){
        this.c = c;
    }

    /**
     * Set name and color of player
     *
     * @param name  player name
     * @param color selected color
     */
    public void setPlayer(String name, String color) {
        JSONObject innerObject2 = new JSONObject();
        innerObject2.put("Name", name);
        innerObject2.put("Farbe", color);
        JSONObject myJsonObject = new JSONObject();
        myJsonObject.put("Spieler", innerObject2);
        c.sendMsg(myJsonObject);
    }

    /**
     * JSOMObject "start game"
     *
     */
    public void register() {
        JSONObject innerObject3 = new JSONObject();
        JSONObject myJsonObject = new JSONObject();
        myJsonObject.put("Spiel starten", innerObject3);
        c.sendMsg(myJsonObject);
    }

    /**
     * Try to build a building
     * Send location and type of building to server
     *
     * @param id BitSet location
     * @param s  type of building (settlement, city, street)
     */
    public void tryToBuild(BitSet id, String s) {
        JSONObject building = new JSONObject();
        JSONObject build = new JSONObject();
        switch (s) {
            case ("Settlement"):
                building.put("Ort", new Interpreter().cornerToAxial(id));
                building.put("Typ", "Dorf");
                break;
            case ("City"):
                building.put("Ort", new Interpreter().cornerToAxial(id));
                building.put("Typ", "Stadt");
                break;
            case ("Street"):
                building.put("Ort", new Interpreter().edgeToAxial(id));
                building.put("Typ", "Straße");
                break;
            default:
                LOGGER.info("Case not covered!");
                break;
        }
        build.put("Bauen", building);
        c.sendMsg(build);
    }

    /**
     * JSONObject "buy development card"
     *
     */
    public void buyDevelopmentCardButton() {

        JSONObject obj = new JSONObject();
        JSONObject innerObj = new JSONObject();
        obj.put("Entwicklungskarte kaufen", innerObj);
        c.sendMsg(obj);
    }

    /**
     * JSONObject "Roll dice"
     *
     */
    public void rollDice() {
        JSONObject innerObj = new JSONObject();
        JSONObject obj = new JSONObject();
        obj.put("Würfeln", innerObj);
        c.sendMsg(obj);
    }

    /**
     * JSONObject "endTurn"
     *
     */
    public void endTurn() {
        JSONObject innerObj = new JSONObject();
        JSONObject obj = new JSONObject();
        obj.put("Zug beenden", innerObj);
        c.sendMsg(obj);
        if (Register.getNtwrkServer() == null) {
            Register.getController().getAllPlayersId().get(c.getId()).moveDevelopmentcards();
        }
    }

    /**
     * Move robber to another terrain tile
     *
     * @param pos BitSet ID of new terrain tile
     */
    public void moveRobber(BitSet pos) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Ort", new Interpreter().tileToAxial(pos));
        lvl1.put("Räuber versetzen", lvl2);
        c.sendMsg(lvl1);
    }


    /**
     * Move robber to another terrain tile and pick an opponent
     *
     * @param pos      BitSet ID of new terrain tile
     * @param opponent int ID of one adjacent opponent
     */
    public void moveRobber(BitSet pos, int opponent) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Ort", new Interpreter().tileToAxial(pos));
        lvl2.put("Ziel", opponent);
        lvl1.put("Räuber versetzen", lvl2);
        c.sendMsg(lvl1);
    }

//    Messages for development cards:

    /**
     * Move knight to another terrain tile
     *
     * @param pos BitSet ID of new terrain tile
     */
    public void moveKnight(BitSet pos) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Ort", new Interpreter().tileToAxial(pos));
        lvl1.put("Ritter ausspielen", lvl2);
        c.sendMsg(lvl1);
    }
    /**
     * Move knight to another terrain tile and pick an opponent
     *
     * @param pos      BitSet ID of new terrain tile
     * @param opponent int ID of one adjacent opponent
     */
    public void moveKnight(BitSet pos, int opponent) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Ort", new Interpreter().tileToAxial(pos));
        lvl2.put("Ziel", opponent);
        lvl1.put("Ritter ausspielen", lvl2);
        c.sendMsg(lvl1);
    }

    /**
     * Play a monopol card with the selected resource
     *
     * @param resource selected resource
     */
    public void sendMonopol(String resource){
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        switch(resource){
            case "Lumber":
                lvl2.put("Rohstoff","Holz");
                break;
            case "Brick":
                lvl2.put("Rohstoff","Lehm");
                break;
            case "Grain":
                lvl2.put("Rohstoff","Getreide");
                break;
            case "Wool":
                lvl2.put("Rohstoff","Wolle");
                break;
            case "Ore":
                lvl2.put("Rohstoff","Erz");
                break;
        }
        lvl1.put("Monopol",lvl2);
        c.sendMsg(lvl1);
    }

    /**
     * Play year of plenty card
     *
     * @param res desired resources (ArrayList<Resource>)
     */
    public void yearOfPlenty(ArrayList<Resource> res) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Rohstoffe", new JsonLib().countRes(res));
        lvl1.put("Erfindung", lvl2);
        c.sendMsg(lvl1);
    }

    /**
     * Play road building card with two roads
     *
     * @param a first road (BitSet)
     * @param b second road (BitSet)
     */
    public void roadBuildingCard(BitSet a, BitSet b) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Straße 2", new Interpreter().edgeToAxial(b));
        lvl2.put("Straße 1", new Interpreter().edgeToAxial(a));
        lvl1.put("Straßenbaukarte ausspielen", lvl2);
        c.sendMsg(lvl1);
    }

    /**
     * Play road building card with just one road JSONObject
     *
     * @param a street (BitSet)
     */
    public void roadBuildingCard(BitSet a) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Straße 1", new Interpreter().edgeToAxial(a));
        lvl1.put("Straßenbaukarte ausspielen", lvl2);
        c.sendMsg(lvl1);
    }
    /**
     * Hand in resources JSONObject
     *
     * @param list Resource enum list of resources
     */
    public void handInResources(ArrayList<Resource> list) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Abgeben", (new JsonLib()).countRes(list));
        lvl1.put("Karten abgeben", lvl2);
        c.sendMsg(lvl1);
    }

    /**
     * Helper method returns JSONObject with a offer- and request-list of resources
     *
     * @param offer   Resource enum list of resources player wants to offer
     * @param request Resource enum list of resources player wants in exchange
     * @return JSONObject includes offer and request
     */
    private JSONObject offerAndRequest(ArrayList<Resource> offer, ArrayList<Resource> request) {
        JSONObject obj = new JSONObject();
        obj.put("Angebot", (new JsonLib()).countRes(offer));
        obj.put("Nachfrage", (new JsonLib()).countRes(request));
        return obj;
    }

    /**
     * Send trade request to harbors JSONObject
     *
     * @param offer   Resource enum list of resources player wants to offer
     * @param request Resource enum list of resources player wants in exchange
     */
    public void tradeSea(ArrayList<Resource> offer, ArrayList<Resource> request) {
        JSONObject lvl1 = new JSONObject();
        lvl1.put("Seehandel", offerAndRequest(offer, request));
        c.sendMsg(lvl1);
    }

    /**
     * Send trade request to fellow players JSONObject
     *
     * @param offer   Resource enum list of resources player wants to offer
     * @param request Resource enum list of resources player wants in exchange
     */
    public void offerTrade(ArrayList<Resource> offer, ArrayList<Resource> request) {
        JSONObject lvl1 = new JSONObject();
        lvl1.put("Handel anbieten", offerAndRequest(offer, request));
        c.sendMsg(lvl1);
    }

    /**
     * Accept a certain trade request JSONObject
     *
     * @param tradeId accepted trade offer (int)
     */
    public void acceptTrade(int tradeId, boolean accept) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Annehmen",accept);
        lvl2.put("Handel id", tradeId);
        lvl1.put("Handel annehmen", lvl2);
        c.sendMsg(lvl1);
    }

    /**
     * Perform trade JSONObject
     *
     * @param tradeId  certain trade id (int)
     * @param playerId certain player (int)
     */
    public void performTrade(int tradeId, int playerId) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Handel id", tradeId);
        lvl2.put("Mitspieler", playerId);
        lvl1.put("Handel abschließen", lvl2);
        c.sendMsg(lvl1);
    }



    /**
     * Cancel certain trade JSONObject
     *
     * @param tradeId certain trade id (int)
     */
    public void cancelTrade(int tradeId) {
        JSONObject lvl1 = new JSONObject();
        JSONObject lvl2 = new JSONObject();
        lvl2.put("Handel id", tradeId);
        lvl1.put("Handel abbrechen", lvl2);
        c.sendMsg(lvl1);
    }
}
