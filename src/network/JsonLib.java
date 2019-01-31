package network;


import controller.Register;
import model.Management;
import model.Resource;
import model.board.PortType;
import model.players.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonLib {

    private static final Logger LOGGER = LogManager.getLogger(JsonLib.class.getName());

    public JsonLib() {

    }

    /**
     * Create JSONObject according to a list of resource enums
     *
     * @param list resource ArrayList
     * @return JSONObject resource
     */
    public JSONObject countRes(ArrayList<Resource> list) {
        int brick = 0;
        int lumber = 0;
        int grain = 0;
        int ore = 0;
        int wool = 0;
        for (int i = 0; i < list.size(); i++) {
            switch (list.get(i)) {
                case BRICK:
                    brick++;
                    break;
                case LUMBER:
                    lumber++;
                    break;
                case GRAIN:
                    grain++;
                    break;
                case ORE:
                    ore++;
                    break;
                case WOOL:
                    wool++;
                    break;
            }
        }
        JSONObject res = new JSONObject();
        if (brick > 0) {
            res.put("Lehm", brick);
        }
        if (lumber > 0) {
            res.put("Holz", lumber);
        }
        if (grain > 0) {
            res.put("Getreide", grain);
        }
        if (ore > 0) {
            res.put("Erz", ore);
        }
        if (wool > 0) {
            res.put("Wolle", wool);
        }
        return res;
    }

    /**
     * Handle resource JSONObject
     *
     * @param res resources JSONObject
     * @param op  MathOperation (add, subtract, set)
     * @param id  player id
     */
    public void changePlayerRes(JSONObject res, MathOp op, int id) {
        Player p = Register.getController().getAllPlayersId().get(id);
        if (p != null) {
            if (res.has("Holz") || res.has("Lehm") || res.has("Getreide")
                    || res.has("Erz") || res.has("Wolle")) {
                for (String type : res.keySet()) {
                    switch (type) {
                        case "Holz":
                            int oldLumber = p.getLumber();
                            p.setLumber(calc(op, oldLumber, res.getInt(type)));
                            break;
                        case "Lehm":
                            int oldBrick = p.getBrick();
                            p.setBrick(calc(op, oldBrick, res.getInt(type)));
                            break;
                        case "Wolle":
                            int oldWool = p.getWool();
                            p.setWool(calc(op, oldWool, res.getInt(type)));
                            break;
                        case "Getreide":
                            int oldGrain = p.getGrain();
                            p.setGrain(calc(op, oldGrain, res.getInt(type)));
                            break;
                        case "Erz":
                            int oldOre = p.getOre();
                            p.setOre(calc(op, oldOre, res.getInt(type)));

                            break;
                        default:
                            LOGGER.warn("Case not covered!");
                    }
                }
                p.updateResource();
            } else {
                try {
                    if (res.has("Unbekannt") && Register.getController().getAllPlayersId().
                            containsKey(id)) {
                        Register.getController().getAllPlayersId().get(id).
                                setResource(res.getInt("Unbekannt"));
                    }
                } catch (NullPointerException e) {
                    LOGGER.catching(org.apache.logging.log4j.Level.ERROR, e);
                }
            }
        }
    }

    /**
     * Update resources in player management for own client
     *
     * @param res JSONObject resource , math operation
     * @param op math operation
     */
    public void changeMangamentRes(JSONObject res, MathOp op) {
        Management m = Register.getController().getManagement();
        if (res.has("Holz") || res.has("Lehm") || res.has("Getreide")
                || res.has("Erz") || res.has("Wolle")) {
            for (String type : res.keySet()) {
                switch (type) {
                    case "Lehm":
                        int oldBrick = m.getBrick();
                        m.setBrick(calc(op, oldBrick, res.getInt(type)));
                        break;
                    case "Holz":
                        int oldLumber = m.getLumber();
                        m.setLumber(calc(op, oldLumber, res.getInt(type)));
                        break;
                    case "Wolle":
                        int oldWool = m.getWool();
                        m.setWool(calc(op, oldWool, res.getInt(type)));
                        break;
                    case "Getreide":
                        int oldGrain = m.getGrain();
                        m.setGrain(calc(op, oldGrain, res.getInt(type)));
                        break;
                    case "Erz":
                        int oldOre = m.getOre();
                        m.setOre(calc(op, oldOre, res.getInt(type)));
                        break;
                    default:
                        LOGGER.warn("Case not covered!");
                }
            }
        }
    }

    /**
     * Update unknown resource amount to player (plus)
     *
     * @param res JSONObject resource
     * @param id  player id
     */
    public void unknownResPLUS(JSONObject res, int id) {
        Player p = Register.getController().getAllPlayersId().get(id);
        if (res.has("Holz") || res.has("Lehm") || res.has("Getreide")
                || res.has("Erz") || res.has("Wolle")) {
            for (String type : res.keySet()) {
                switch (type) {
                    case "Holz":
                    case "Lehm":
                    case "Wolle":
                    case "Getreide":
                    case "Erz":
                        int old = p.getResource();
                        p.setResource(old + res.getInt(type));
                        break;
                    default:
                        LOGGER.warn("Case not covered!");
                }
            }
        }
        if (res.has("Unbekannt")) {
            int tmp = p.getResource();
            p.setResource(tmp + res.getInt("Unbekannt"));
        }
    }

    /**
     * Update unknown resource amount to player (minus)
     *
     * @param res JSONObject resource
     * @param id player id
     */
    public void unknownResMINUS(JSONObject res, int id) {
        Player p = Register.getController().getAllPlayersId().get(id);
        if (res.has("Holz") || res.has("Lehm") || res.has("Getreide")
                || res.has("Erz") || res.has("Wolle")) {
            for (String type : res.keySet()) {
                switch (type) {
                    case "Holz":
                    case "Lehm":
                    case "Wolle":
                    case "Getreide":
                    case "Erz":
                        int old = p.getResource();
                        p.setResource(old - res.getInt(type));
                        break;
                    default:
                        LOGGER.warn("Case not covered!");
                }
            }
        }
        if (res.has("Unbekannt")) {
            int tmp = p.getResource();
            p.setResource(tmp - res.getInt("Unbekannt"));
        }
    }

    /**
     * Handle development card JSONObject
     *
     * @param dev developmentCard JSONObject
     * @param op  MathOperation (add, set)
     * @param id  player id
     */
    public void dev(JSONObject dev, MathOp op, int id) {
        Player p = Register.getController().getAllPlayersId().get(id);
        if (dev.has("Entwicklungskarte")) {
            String type = dev.getString("Entwicklungskarte");
            switch (type) {
                case "Ritter":
                    int oldKnight = p.getKnightsDev();
                    p.setKnightsDev(oldKnight + 1);
                    break;
                case "Straßenbau":
                    int oldRoad = p.getRoadBuildingDev();
                    p.setRoadBuildingDev(oldRoad + 1);
                    break;
                case "Monopol":
                    int oldMonopol = p.getMonopolyDev();
                    p.setMonopolyDev(oldMonopol + 1);
                    break;
                case "Erfindung":
                    int oldYearOf = p.getYearOfPlentyDev();
                    p.setYearOfPlentyDev(oldYearOf + 1);
                    break;
                case "Siegpunkt":
                    int oldVp = p.getVictoryPointDev();
                    p.setVictoryPointDev(oldVp + 1);
                    break;
                default:
                    LOGGER.info("Case not covered!");
            }
            p.setDevelopmentDev(p.getYearOfPlentyDev() + p.getVictoryPointDev() + p.getMonopolyDev() + p.getRoadBuildingDev() + p.getKnightsDev());
        } else if (dev.has("Ritter") || dev.has("Straßenbau") || dev.has("Monopol")
                || dev.has("Erfindung") || dev.has("Siegpunkt")) {
            for (String type : dev.keySet()) {
                switch (type) {
                    case "Ritter":
                        int oldKnight = p.getKnightsDev();
                        p.setKnightsDev(calc(op, oldKnight, dev.getInt(type)));
                        break;
                    case "Straßenbau":
                        int oldRoad = p.getRoadBuildingDev();
                        p.setRoadBuildingDev(calc(op, oldRoad, dev.getInt(type)));
                        break;
                    case "Monopol":
                        int oldMonopol = p.getMonopolyDev();
                        p.setMonopolyDev(calc(op, oldMonopol, dev.getInt(type)));
                        break;
                    case "Erfindung":
                        int oldYearOf = p.getYearOfPlentyDev();
                        p.setYearOfPlentyDev(calc(op, oldYearOf, dev.getInt(type)));
                        break;
                    case "Siegpunkt":
                        int oldVp = p.getVictoryPointDev();
                        p.setVictoryPointDev(calc(op, oldVp, dev.getInt(type)));
                        break;
                    default:
                        LOGGER.info("Case not covered!");
                }
            }
            p.setDevelopmentDev(p.getYearOfPlentyDev() + p.getVictoryPointDev() + p.getMonopolyDev() + p.getRoadBuildingDev() + p.getKnightsDev());
        } else {
            try {
                if (dev.has("Unbekannt") && Register.getController().getAllPlayersId().containsKey(id)) {
                    Register.getController().getAllPlayersId().get(id).
                            setDevelopmentDev(dev.getInt("Unbekannt"));

                }
            } catch (NullPointerException e) {
                LOGGER.catching(org.apache.logging.log4j.Level.ERROR, e);
            }
        }
    }

    /**
     * Calculate two int values according to MathOp enum
     *
     * @param op MathOperation (add, subtract, set) enum
     * @param o  old int value
     * @param n  new int value
     * @return calculated int value
     */
    private int calc(MathOp op, int o, int n) {
        return op.apply(o, n);
    }

    public ArrayList<Resource> countResJSONObj(JSONObject res) {
        ArrayList<Resource> resourceArrayList = new ArrayList<>();
        if (res.has("Holz") || res.has("Lehm") || res.has("Getreide")
                || res.has("Erz") || res.has("Wolle")) {
            for (String type : res.keySet()) {
                switch (type) {
                    case "Holz":
                        for (int i = 0; i < res.getInt("Holz"); i++) {
                            resourceArrayList.add(Resource.LUMBER);
                        }
                        break;
                    case "Lehm":
                        for (int i = 0; i < res.getInt("Lehm"); i++) {
                            resourceArrayList.add(Resource.BRICK);
                        }
                        break;
                    case "Wolle":
                        for (int i = 0; i < res.getInt("Wolle"); i++) {
                            resourceArrayList.add(Resource.WOOL);
                        }
                        break;
                    case "Getreide":
                        for (int i = 0; i < res.getInt("Getreide"); i++) {
                            resourceArrayList.add(Resource.GRAIN);
                        }
                        break;
                    case "Erz":
                        for (int i = 0; i < res.getInt("Erz"); i++) {
                            resourceArrayList.add(Resource.ORE);
                        }
                        break;
                    default:
                        LOGGER.warn("Case not covered!");
                }
            }
        }
        return resourceArrayList;
    }

    /**
     * Check if player has enough resources
     *
     * @param res resource ArrayList
     * @param id player id
     * @return boolean
     */
    public boolean checkIfPlayerHasEnoughRes(ArrayList<Resource> res, int id) {
        Player p = Register.getController().getAllPlayersId().get(id);
        int brick = 0;
        int lumber = 0;
        int grain = 0;
        int ore = 0;
        int wool = 0;
        for (int i = 0; i < res.size(); i++) {
            switch (res.get(i)) {
                case BRICK:
                    brick++;
                    if (brick > p.getBrick()) {
                        return false;
                    }
                    break;
                case LUMBER:
                    lumber++;
                    if (lumber > p.getLumber()) {
                        return false;
                    }
                    break;
                case GRAIN:
                    grain++;
                    if (grain > p.getGrain()) {
                        return false;
                    }
                    break;
                case ORE:
                    ore++;
                    if (ore > p.getOre()) {
                        return false;
                    }
                    break;
                case WOOL:
                    wool++;
                    if (wool > p.getWool()) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * Check if the management has enough resources left
     *
     * @param res resource ArrayList
     * @return boolean
     */
    public boolean checkIfManagementHasEnoughRes(ArrayList<Resource> res) {
        Management m = Register.getController().getManagement();
        int brick = 0;
        int lumber = 0;
        int grain = 0;
        int ore = 0;
        int wool = 0;
        for (int i = 0; i < res.size(); i++) {
            switch (res.get(i)) {
                case LUMBER:
                    lumber++;
                    if (lumber > m.getLumber()) {
                        return false;
                    }
                    break;
                case BRICK:
                    brick++;
                    if (brick > m.getBrick()) {
                        return false;
                    }
                    break;
                case GRAIN:
                    grain++;
                    if (grain > m.getGrain()) {
                        return false;
                    }
                    break;
                case ORE:
                    ore++;
                    if (ore > m.getOre()) {
                        return false;
                    }
                    break;
                case WOOL:
                    wool++;
                    if (wool > m.getWool()) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * Handle sea trade with different kinds of ratio and update player resources
     *
     * @param playerID selected player
     * @param offer resources to offer
     * @param request resources requested
     */
    public void makeSeaTrade(int playerID, ArrayList<Resource> offer, ArrayList<Resource> request){
        Player p = Register.getController().getAllPlayersId().get(playerID);
        switch (checkRatio(offer, request)){
            case FOUR_ONE:
                changePlayerRes(countRes(offer),MathOp.SUBTRACT,p.getId());
                changeMangamentRes(countRes(offer),MathOp.ADD);
                changePlayerRes(countRes(request),MathOp.ADD,p.getId());
                changeMangamentRes(countRes(request),MathOp.SUBTRACT);
                break;
            case THREE_ONE:
                changePlayerRes(countRes(offer),MathOp.SUBTRACT,p.getId());
                changeMangamentRes(countRes(offer),MathOp.ADD);
                changePlayerRes(countRes(request),MathOp.ADD,p.getId());
                changeMangamentRes(countRes(request),MathOp.SUBTRACT);
                break;
            case TWO_ONE:
                changePlayerRes(countRes(offer),MathOp.SUBTRACT,p.getId());
                changeMangamentRes(countRes(offer),MathOp.ADD);
                changePlayerRes(countRes(request),MathOp.ADD,p.getId());
                changeMangamentRes(countRes(request),MathOp.SUBTRACT);
                break;
            case NULL:
                LOGGER.info("Case not covered! "+checkRatio(offer, request));
        }
    }

    /**
     * Check the players harbor
     *
     * @param playerID selected player
     * @param res resource
     * @param ratio trade ratiop
     * @return boolean
     */
    public boolean checkHarbor(int playerID, Resource res, Ratio ratio){
        Player p = Register.getController().getAllPlayersId().get(playerID);
        ArrayList<PortType> harbors = p.getHarborTyps();
        switch (ratio) {
            case FOUR_ONE:
                return true;
            case THREE_ONE:
                return harbors.contains(PortType.GENERIC);
            case TWO_ONE:
                switch (res) {
                    case ORE:
                        return harbors.contains(PortType.ORE);
                    case WOOL:
                        return harbors.contains(PortType.WOOL);
                    case GRAIN:
                        return harbors.contains(PortType.GRAIN);
                    case BRICK:
                        return harbors.contains(PortType.BRICK);
                    case LUMBER:
                        return harbors.contains(PortType.LUMBER);
                    default:
                        LOGGER.info("Case not covered!");
                        return false;
                }
            case NULL:
            default:
                LOGGER.info("Case not covered! " + ratio);
                return false;
        }
    }

    /**
     * Convert trade offer to Enum ratio (FOUR_ONE, THREE_ONE, TWO_ONE)
     *
     * @param offer resource ArrayList
     * @param request resource ArrayList
     * @return ratio Enum
     */
    public Ratio checkRatio(ArrayList<Resource> offer, ArrayList<Resource> request){
        int x = offer.size();
        int y = request.size();
        if(x > 3 && (x % 4) == 0 && (x/4) == y){
            return Ratio.FOUR_ONE;
        }
        if(x > 2 && (x % 3) == 0 && (x/3) == y){
            return Ratio.THREE_ONE;
        }
        if(x > 1 && (x % 2) == 0 && (x/2) == y){
            return Ratio.TWO_ONE;
        }
        return Ratio.NULL;
    }

    /**
     * Check if trade resources are from the same kind
     *
     * @param list resource ArrayList
     * @return boolean
     */
    public boolean checkAllOfOneKind(ArrayList<Resource> list){
        ArrayList<Resource> tmp = new ArrayList<>(list);
        if (!list.isEmpty() && list.size() > 1) {
            Resource first = tmp.get(0);
            tmp.remove(0);
            for (Resource res : tmp) {
                if(first != res){
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * Create a new ArrayList with resources
     *
     * @param res resource Resource
     * @param count size of ArrayList
     * @return ArrayList
     */
    public ArrayList<Resource> createResList(Resource res, int count) {
        ArrayList<Resource> resourceArrayList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            resourceArrayList.add(res);
        }
        return resourceArrayList;
    }

    /**
     * Check if domestic trade is valid
     *
     * @param offer resource ArrayList
     * @param request resource ArrayLisr
     * @return boolean
     */
    public boolean checkDomesticTrade(ArrayList<Resource> offer, ArrayList<Resource> request) {
        // NO give away
        if (offer.isEmpty() || request.isEmpty()) {
            LOGGER.debug(offer + ", " + request);
            return false;
        }
        // NO trade of same res but different amounts
        if (checkAllOfOneKind(offer) && checkAllOfOneKind(request) && offer.get(0) == request.get(0)) {
            LOGGER.debug(offer + ", " + request);
            return false;
        }
        // NO trade of res x vs x + y
        if (checkAllOfOneKind(offer) && request.contains(offer.get(0))) {
            LOGGER.debug(offer + ", " + request);
            return false;
        }
        LOGGER.debug(offer + ", " + request);
        return true;
    }
}
