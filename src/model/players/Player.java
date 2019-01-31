package model.players;

import controller.Register;
import model.Management;
import model.Resource;
import model.board.*;
import model.cards.DevelopmentCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.client.EventTypes;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Observable;

public abstract class Player extends Observable {
    private static final Logger LOGGER = LogManager.getLogger(Player.class.getName());
    ArrayList<BitSet> roadsList = new ArrayList<>();
    ArrayList<BitSet> settlementsList = new ArrayList<>();
    ArrayList<BitSet> citiesList = new ArrayList<>();
    private int id;
    private String name = null;
    private PColor color = PColor.NULL;
    private int brick = 0;
    private int lumber = 0;
    private int ore = 0;
    private int grain = 0;
    private int wool = 0;
    private int resource = 0; // amount of all resources
    private int diceValue;
    private ArrayList<DevelopmentCard> developmentCards = new ArrayList<>(); // amount of cards which can be played
    private ArrayList<DevelopmentCard> developmentCardsMove = new ArrayList<>(); // card which can not be played, just one
    private int victoryPoints = 0; // which are be shown
    private boolean longestRoad = false;
    private boolean largestArmy = false;
    private int knight = 0; // amount played knight
    private int developmentDev = 0; // amount of development cards on hand
    private int knightsDev = 0; // amount of knight cards on hand
    private int monopolyDev = 0; // amount of monopoly cards on hand
    private int victoryPointDev = 0; // amount of victoryPoint cards on hand
    private int roadBuildingDev = 0; // amount of roadBuilding cards on hand
    private int yearOfPlentyDev = 0; // amount of yearOfPlenty cards on hand
    private int roads = 15;
    private int cities = 4;
    private int settlements = 5;
    private ArrayList<PortType> harborTyps = new ArrayList<>();
    private boolean active;
    private Board board;
    private HashMap<Integer, Player> allPlayersId = new HashMap<>();
    private Management management;
    private Status status = null;

    public void notifyObserverCanvasboard() {
        setChanged();
        notifyObservers(EventTypes.VICTORYPOINTS);
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    void printPlayer() {
        LOGGER.debug(this.toString()
                + "\n" + String.format("%1$-10s%2$-10d", "id", this.getId()) + String.format("%1$-10s%2$-10s", "color", this.getColor()) + String.format("%1$-10s%2$-10s", "name", this.getName()) + String.format("%1$-10s%2$-10s", "status", this.getStatus())
                + "\n" + String.format("%1$-10s%2$-10d", "vp", this.getVictoryPoints()) + String.format("%1$-10s%2$-10b", "road", this.getLongestRoad()) + String.format("%1$-10s%2$-10b", "army", this.getLargestArmy())
                + "\n" + String.format("%1$-10s%2$-10d", "dev", this.getDevelopmentDev()) + String.format("%1$-10s%2$-10d", "dice", this.getDiceValue())
                + "\n" + String.format("%1$-10s%2$-10d", "res", this.getResource()) + String.format("%1$-10s%2$-10d", "lumber", this.getLumber()) + String.format("%1$-10s%2$-10d", "brick", this.getBrick()) + String.format("%1$-10s%2$-10d", "grain", this.getGrain()) + String.format("%1$-10s%2$-10d", "wool", this.getWool()) + String.format("%1$-10s%2$-10d", "ore", this.getOre())
                + "\n" + String.format("%1$-10s", "dev") + this.getDevelopmentCards()
                + "\n" + String.format("%1$-10s", "dev BLOCK") + this.getDevelopmentCardsMove()
                + "\n" + String.format("%1$-10s", "Harbors") + this.getHarborTyps()
                + "\n" + String.format("%1$-15s", "Roads") + String.format("%1$-10s%2$-10d", "left", this.getRoads()) + String.format("%1$-10s", "built") + roadsList
                + "\n" + String.format("%1$-15s", "Settlements") + String.format("%1$-10s%2$-10d", "left", this.getSettlements()) + String.format("%1$-10s", "built") + settlementsList
                + "\n" + String.format("%1$-15s", "Cities") + String.format("%1$-10s%2$-10d", "left", this.getCities()) + String.format("%1$-10s", "built") + citiesList
        );
        if (management != null) {
            management.printManagement();
        }
    }

    /**
     * Return the status
     *
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set a status
     *
     * @param status
     */
    public void setStatus(Status status) {
        this.status = status;
        printPlayer();
    }

    /**
     * Return the id
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set a id
     *
     * @param id set player id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return the name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set a name
     *
     * @param name of player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the color
     *
     * @return color
     */
    public PColor getColor() {
        return color;
    }

    /**
     * Set a color
     *
     * @param color of player
     */
    public void setColor(PColor color) {
        this.color = color;
    }

    /**
     * Return number of brickcards
     *
     * @return number of brickcards
     */
    public int getBrick() {
        return brick;
    }

    /**
     * Set the number of brickcards
     *
     * @param brick new number of brickcards
     */
    public void setBrick(int brick) {
        LOGGER.debug(this.toString() + " [ id: " + this.getId() + " ] old: " + this.brick + ", new: " + brick);
        this.brick = brick;
        updateResource();
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    /**
     * Return number of lumbercards
     *
     * @return number of lumbercards
     */
    public int getLumber() {
        return lumber;
    }

    /**
     * Set the number of lumbercards
     *
     * @param lumber new number of lumbercards
     */
    public void setLumber(int lumber) {
        LOGGER.debug(this.toString() + " [ id: " + this.getId() + " ] old: " + this.lumber + ", new: " + lumber);
        this.lumber = lumber;
        updateResource();
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    /**
     * Return number of orecards
     *
     * @return number of orecards
     */
    public int getOre() {
        return ore;
    }

    /**
     * Set the number of orecards
     *
     * @param ore new number of orecards
     */
    public void setOre(int ore) {
        LOGGER.debug(this.toString() + " [ id: " + this.getId() + " ] old: " + this.ore + ", new: " + ore);
        this.ore = ore;
        updateResource();
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    /**
     * Return number of graincards
     *
     * @return number of graincards
     */
    public int getGrain() {
        return grain;
    }

    /**
     * Set the number of graincards
     *
     * @param grain new number of graincards
     */
    public void setGrain(int grain) {
        LOGGER.debug(this.toString() + " [ id: " + this.getId() + " ] old: " + this.grain + ", new: " + grain);
        this.grain = grain;
        updateResource();
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    /**
     * Return number of woolcards
     *
     * @return number of woolcards
     */
    public int getWool() {
        return wool;
    }

    /**
     * Set the number of woolcards
     *
     * @param wool new number of woolcards
     */
    public void setWool(int wool) {
        LOGGER.debug(this.toString() + " [ id: " + this.getId() + " ] old: " + this.wool + ", new: " + wool);
        this.wool = wool;
        updateResource();
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    /**
     * Return number of resourcecards
     *
     * @return number of resourcecards
     */
    public int getResource() {
        return resource;
    }

    /**
     * Set the number of resourcecards
     *
     * @param resource new number of resourcecards
     */
    public void setResource(int resource) {
        this.resource = resource;
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    /**
     * Getter diceValue
     *
     * @return dice value
     */
    public int getDiceValue() {
        return diceValue;
    }

    /**
     * Setter diceValue
     *
     * @param diceValue set the dice value
     */
    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

    /**
     * Return the list with all the playable developmentcards
     *
     * @return playable developmentcards
     */
    public ArrayList<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * Return the list of developmentcards buying in this move and which are not playable
     *
     * @return not playable developmentcards
     */
    public ArrayList<DevelopmentCard> getDevelopmentCardsMove() {
        return developmentCardsMove;
    }

    /**
     * Return number of victory points
     *
     * @return number of victory points
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Set number of vicory points
     *
     * @param victoryPoints new number of victory points
     *                      TODO: mangement.finish!
     */
    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
        setChanged();
        notifyObservers(EventTypes.VICTORYPOINTS);
    }

    /**
     * Return true, if player has the longest road; return false, if player hasn't the longset road
     *
     * @return true or false
     */
    public boolean getLongestRoad() {
        return longestRoad;
    }

    /**
     * Set longest road true, if player has the longest road; set longest road false, if player hasn't the longest road
     *
     * @param longestRoad true or false
     */
    public void setLongestRoad(boolean longestRoad) {
        this.longestRoad = longestRoad;
        setChanged();
        notifyObservers(EventTypes.LONGESTROAD);
    }

    /**
     * Return true, if player has the largest army; return false, if player hasn't the largest army
     *
     * @return true or false
     */
    public boolean getLargestArmy() {
        return largestArmy;
    }

    /**
     * Set largest army true, if player has the largest army; set largest army false, if player hasn't the largest army
     *
     * @param largestArmy true of false
     */
    public void setLargestArmy(boolean largestArmy) {
        this.largestArmy = largestArmy;
        setChanged();
        notifyObservers(EventTypes.LARGESTARMY);
    }

    /**
     * Return the number of monopoly cards
     *
     * @return number of monopoly cards
     */
    public int getMonopolyDev() {
        return monopolyDev;
    }

    /**
     * Set the number of monopoly cards
     *
     * @param monopolyDev new number
     */
    public void setMonopolyDev(int monopolyDev) {
        this.monopolyDev = monopolyDev;
        Register.getController().notifyObserverDevCards();
    }

    /**
     * Return the number of victorypoint cards
     *
     * @return number of victorypoint cards
     */
    public int getVictoryPointDev() {
        return victoryPointDev;
    }

    /**
     * Set the number of victorypoint cards
     *
     * @param victoryPointDev new number
     */
    public void setVictoryPointDev(int victoryPointDev) {
        this.victoryPointDev = victoryPointDev;
        Register.getController().notifyObserverDevCards();
    }

    /**
     * Return the number of road-building cards
     *
     * @return number of raod-building cards
     */
    public int getRoadBuildingDev() {
        return roadBuildingDev;
    }

    /**
     * Set the number of road-building cards
     *
     * @param roadBuildingDev new number
     */
    public void setRoadBuildingDev(int roadBuildingDev) {
        this.roadBuildingDev = roadBuildingDev;
        Register.getController().notifyObserverDevCards();
    }

    /**
     * Return the number of year-of-plenty cards
     *
     * @return number of year-of-plenty cards
     */
    public int getYearOfPlentyDev() {
        return yearOfPlentyDev;
    }

    /**
     * Set the number of year-of-plenty cards
     *
     * @param yearOfPlentyDev new number of year-of-plenty cards
     */
    public void setYearOfPlentyDev(int yearOfPlentyDev) {
        this.yearOfPlentyDev = yearOfPlentyDev;
        Register.getController().notifyObserverDevCards();
    }

    /**
     * Return number of development cards
     *
     * @return number of development cards
     */
    public int getDevelopmentDev() {
        return developmentDev;
    }

    /**
     * Set the number of developmentcards
     *
     * @param developmentDev new number
     */
    public void setDevelopmentDev(int developmentDev) {
        this.developmentDev = developmentDev;
        Register.getController().notifyObserverDevCards();
    }

    /**
     * Return the number of disclosed knightcards
     *
     * @return number of disclosed knightcards
     */
    public int getKnightsDev() {
        return knightsDev;
    }

    /**
     * Set the number of disclosed knightcards
     *
     * @param knightsDev new number of disclosed knightcards
     */
    public void setKnightsDev(int knightsDev) {
        LOGGER.info(this.knightsDev + ", " + knightsDev + " old and new value of knightDev Card. Knight card which is on the hand of ClientName: " + name);
        this.knightsDev = knightsDev;
        Register.getController().notifyObserverDevCards();
    }


    public int getKnight() {
        return knight;
    }

    public void setKnight(int knight) {
        this.knight = knight;
        setChanged();
        notifyObservers(EventTypes.KNIGHTS);
    }


    /**
     * Return number of playable roads
     *
     * @return number of playable roads
     */
    public int getRoads() {
        return roads;
    }

    /**
     * Return number of playable cities
     *
     * @return number of playable cities
     */
    public int getCities() {
        return cities;
    }

    /**
     * Return number of playable settlements
     *
     * @return number of playable settlements
     */
    public int getSettlements() {
        return settlements;
    }

    /**
     * Return the list with all typs of harbor at witch the player sit
     *
     * @return list of harbortyps
     */
    public ArrayList<PortType> getHarborTyps() {
        return harborTyps;
    }

    /**
     * Return true, if the player is the active player; return false, if the player is not the active player
     *
     * @return true of false
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Set true, if player is the active player; set false if the player is the active player
     *
     * @param active true or false
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Return the board
     *
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Set the board
     *
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Hand over a list with all players
     *
     * @param allPlayersId list with all players
     */
    public void setAllPlayers(HashMap<Integer, Player> allPlayersId) {
        this.allPlayersId = allPlayersId;
    }

    /**
     * Return a list with all players
     *
     * @return list with all players
     */
    public HashMap<Integer, Player> getAllPlayersId() {
        return allPlayersId;
    }

    /**
     * Return the management
     *
     * @return management
     */
    public Management getManagement() {
        return management;
    }

    /**
     * Set the management
     *
     * @param management
     */
    public void setManagement(Management management) {
        this.management = management;
    }

    /**
     * update number of resource
     */
    public void updateResource() {
        this.resource = brick + lumber + wool + ore + grain;
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    /**
     * Get a list with all resources
     *
     * @return list with all resources
     */
    public ArrayList<Resource> getAllResourceList() {
        ArrayList<Resource> resources = new ArrayList<>();
        for (int i = 0; i < getBrick(); i++) {
            resources.add(Resource.BRICK);
        }
        for (int i = 0; i < getWool(); i++) {
            resources.add(Resource.WOOL);
        }
        for (int i = 0; i < getGrain(); i++) {
            resources.add(Resource.GRAIN);
        }
        for (int i = 0; i < getOre(); i++) {
            resources.add(Resource.ORE);
        }
        for (int i = 0; i < getLumber(); i++) {
            resources.add(Resource.LUMBER);
        }
        return resources;
    }

    /**
     * Set the number of a specific resource
     *
     * @param resource specific resource
     * @param i        number to add
     */
    public void setSpecificResource(Resource resource, int i) {
        switch (resource) {
            case LUMBER:
                setLumber(getLumber() + i);
                break;
            case BRICK:
                setBrick(getBrick() + i);
                break;
            case ORE:
                setOre(getOre() + i);
                break;
            case WOOL:
                setWool(getWool() + i);
                break;
            case GRAIN:
                setGrain(getGrain() + i);
                break;
        }
        updateResource();
        setChanged();
        notifyObservers(EventTypes.RESOURCES);
    }

    /**
     * returns the nummer of a spcific resource
     *
     * @param resource specific resource
     * @return nummber of a specific resource
     */
    public int getSpecificResource(Resource resource) {
        switch (resource) {
            case LUMBER:
                return getLumber();
            case BRICK:
                return getBrick();
            case ORE:
                return getOre();
            case WOOL:
                return getWool();
            case GRAIN:
                return getGrain();
        }
        return 0;
    }

    /**
     * Checks the "distance Rule"
     *
     * @param bitSet specific Corner
     * @return rule respected
     */
    public boolean distanceRule(BitSet bitSet) {
        for (Corner corner : board.getCornerneighborsOfCorner(board.getAllCorners().get(bitSet))) {
            if (corner.getPlayer() != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if an adjacent road from a corner or edge is from this player
     *
     * @param bitSet specific corner or edge
     * @return true if an adjacent road is from this player
     */
    public boolean nextToStreet(BitSet bitSet) {
        if (bitSet.cardinality() == 2) {
            for (Edge edge : board.getEdgeneighborOfEdge(board.getAllEdges().get(bitSet))) {
                if (edge.getPlayer() == this) {
                    BitSet bitSettmp = new BitSet();
                    bitSettmp.or(edge.getBitSetID());
                    bitSettmp.or(bitSet);
                    if (board.getAllCorners().get(bitSettmp).getPlayer() == this || board.getAllCorners().get(bitSettmp).getPlayer() == null) {
                        return true;
                    }
                }
            }
        } else if (bitSet.cardinality() == 3) {
            for (Edge edge : board.getEdgeneighborsOfCorner(board.getAllCorners().get(bitSet))) {
                if (edge.getPlayer() == this) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Returns true if an adjacent road from a corner or edge is from this player when you build a new road
     *
     * @param bitSet    specific corner or edge
     * @param bitSetTry new road
     * @return true if an adjacent road is from this player
     */
    public boolean nextToStreetRoadBuildingCard(BitSet bitSet, BitSet bitSetTry) {
        if (bitSet.cardinality() == 2) {
            for (Edge edge : board.getEdgeneighborOfEdge(board.getAllEdges().get(bitSet))) {
                if (edge.getPlayer() == this || edge == board.getAllEdges().get(bitSetTry)) {
                    BitSet bitSettmp = new BitSet();
                    bitSettmp.or(edge.getBitSetID());
                    bitSettmp.or(bitSet);
                    if (board.getAllCorners().get(bitSettmp).getPlayer() == this || board.getAllCorners().get(bitSettmp).getPlayer() == null) {
                        return true;
                    }
                }
            }
        } else if (bitSet.cardinality() == 3) {
            for (Edge edge : board.getEdgeneighborsOfCorner(board.getAllCorners().get(bitSet))) {
                if (edge.getPlayer() == this) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Returns true if an adjacent settlement/city from a edge is from this player
     *
     * @param bitSet specific edge
     * @return true if an adjacent settlement/city if from this player
     */
    public boolean nextToSettlementCity(BitSet bitSet) {
        for (Corner corner : board.getCornerneighborsOfEdge(board.getAllEdges().get(bitSet))) {
            if (corner.getPlayer() == this) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of edges where you can build a road in the setting up
     *
     * @return list of edges
     */
    public ArrayList<Edge> getBuildableStartRoads() {
        ArrayList<Edge> edges = new ArrayList<>();
        BitSet settlement = null;
        for (Corner corner : board.getAllCorners().values()) {
            boolean tmp = true;
            if (corner.getPlayer() == this) {
                for (Edge edge : board.getEdgeneighborsOfCorner(corner)) {
                    if (edge.getPlayer() == this) {
                        tmp = false;
                    }
                }
                if (tmp) {
                    settlement = corner.getBitSetID();
                }
            }
        }
        for (Edge edge : board.getAllEdges().values()) {
            if (edge.getPlayer() == null) {
                if (board.getCornerneighborsOfEdge(edge).contains(board.getAllCorners().get(settlement))) {
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    /**
     * Returns a list of corners where you can build a settlement in the setting up
     *
     * @return list of corners
     */
    public ArrayList<Corner> getBuildableStartSettlements() {
        ArrayList<Corner> corners = new ArrayList<>();
        for (Corner corner : board.getAllCorners().values()) {
            if (corner.getPlayer() == null) {
                if (distanceRule(corner.getBitSetID())) {
                    corners.add(corner);
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list of all edges where you can build a road in the normal move (not in the setting up)
     *
     * @return list of edges
     */
    public ArrayList<Edge> getBuildableRoadsMove() {
        ArrayList<Edge> edges = new ArrayList<>();
        for (Edge edge : board.getAllEdges().values()) {
            if (edge.getPlayer() == null) {
                if (nextToStreet(edge.getBitSetID()) || nextToSettlementCity((edge.getBitSetID()))) {
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    /**
     * Returns a list of all corners where you can build a settlement in the normal move (not in the setting up)
     *
     * @return list of corners
     */
    public ArrayList<Corner> getBuildableSettlementsMove() {
        ArrayList<Corner> corners = new ArrayList<>();
        for (Corner corner : board.getAllCorners().values()) {
            if (corner.getPlayer() == null) {
                if (distanceRule(corner.getBitSetID())) {
                    if (nextToStreet(corner.getBitSetID())) {
                        corners.add(corner);
                    }
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list of all edges where you can build a road
     *
     * @return list of edges
     */
    public ArrayList<Edge> getBuildableRoads() {
        if (status == Status.BUILD_STREET) {
            return getBuildableStartRoads();
        } else {
            return getBuildableRoadsMove();
        }
    }

    /**
     * Returns a list of all corners where you can build a settlment
     *
     * @return list of corners
     */
    public ArrayList<Corner> getBuildableSettlements() {
        if (status == Status.BUILD_SETTLEMENT) {
            LOGGER.info("start settlement " + getBuildableStartSettlements().size());
            return getBuildableStartSettlements();
        } else {
            LOGGER.info("move settlement " + getBuildableSettlementsMove().size());
            return getBuildableSettlementsMove();

        }
    }

    /**
     * Returns a list of all corners where you can build a city
     *
     * @return list of corners
     */
    public ArrayList<Corner> getBuildableCities() {
        ArrayList<Corner> corners = new ArrayList<>();
        for (Corner corner : board.getAllCorners().values()) {
            if (corner.getPlayer() == this) {
                if (!corner.getIsCity()) {
                    corners.add(corner);
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list of all edges where you can build your second road when you play a road-bulding card
     *
     * @param bitSet first road
     * @return list of edges
     */
    public ArrayList<Edge> getBuildableRoadsRoadBuildingCard(BitSet bitSet) {
        ArrayList<Edge> edges = new ArrayList<>();
        for (Edge edge : board.getAllEdges().values()) {
            if (edge.getPlayer() == null) {
                if (nextToStreetRoadBuildingCard(edge.getBitSetID(), bitSet) || nextToSettlementCity((edge.getBitSetID()))) {
                    if (edge != board.getAllEdges().get(bitSet)) {
                        edges.add(edge);
                    }

                }
            }
        }
        return edges;
    }

    /**
     * build the settlement
     *
     * @param bitSet place to build
     */
    public void buildSettlementFORCE(BitSet bitSet) {
        LOGGER.info(this.getName() + ": " + bitSet);
        board.getAllCorners().get(bitSet).setPlayer(this);
        settlements--;
        settlementsList.add(bitSet);
        if (board.getAllCorners().get(bitSet).getPortType() != null && !(harborTyps.contains(board.getAllCorners().get(bitSet).getPortType()))) {
            harborTyps.add(board.getAllCorners().get(bitSet).getPortType());
        }
        setChanged();
        notifyObservers(EventTypes.SETTLEMENT);
    }

    /**
     * build the city
     *
     * @param bitSet place to build
     */
    public void buildCityFORCE(BitSet bitSet) {
        LOGGER.info(this.getName() + ": " + bitSet);
        board.getAllCorners().get(bitSet).setPlayer(this);
        board.getAllCorners().get(bitSet).setIsCity();
        settlements++;
        settlementsList.remove(bitSet);
        cities--;
        citiesList.add(bitSet);
        setChanged();
        notifyObservers(EventTypes.CITY);
    }

    /**
     * build the street
     *
     * @param bitSet place to build
     */
    public void buildStreetFORCE(BitSet bitSet) {
        LOGGER.info(this.getName() + ": " + bitSet);
        board.getAllEdges().get(bitSet).setPlayer(this);
        roads--;
        roadsList.add(bitSet);
        setChanged();
        notifyObservers(EventTypes.ROAD);
    }

    /**
     * build settlement in setting up
     *
     * @param bitSet specific corner
     * @return returns a list of resources which you get when you build the second settlement
     */
    public ArrayList<Resource> buildStartSettlement(BitSet bitSet) {
        ArrayList<Resource> resources = new ArrayList<>();
        if (getBuildableStartSettlements().contains(board.getAllCorners().get(bitSet))) {
            board.getAllCorners().get(bitSet).setPlayer(this);
            settlements--;
            settlementsList.add(bitSet);
            victoryPoints++;
            if (board.getAllCorners().get(bitSet).getPortType() != null && !(harborTyps.contains(board.getAllCorners().get(bitSet).getPortType()))) {
                harborTyps.add(board.getAllCorners().get(bitSet).getPortType());
            }
            // harvest
            if (settlements == 3) {
                for (Tile tile : board.getTileneighborsOfCorner(board.getAllCorners().get(bitSet))) {
                    if (tile instanceof Terrain) {
                        Resource resource = management.getOneResourceCard(board.getAllCorners().get(bitSet), tile);
                        if (resource != null) {
                            resources.add(resource);
                        }
                    }
                }
            }
        }
        return resources;
    }

    /**
     * build road in setting up
     *
     * @param bitSet specific edge
     */
    public void buildStartRoad(BitSet bitSet) {
        if (getBuildableStartRoads().contains(board.getAllEdges().get(bitSet))) {
            board.getAllEdges().get(bitSet).setPlayer(this);
            roads--;
            roadsList.add(bitSet);
        }
    }

    /**
     * build road when playing RoadBuildingCard
     *
     * @param bitSet specific edge
     */
    public void buildRoadBuildingCardRoad(BitSet bitSet) {
        if (getBuildableRoads().contains(board.getAllEdges().get(bitSet))) {
            if (roads > 0) {
                board.getAllEdges().get(bitSet).setPlayer(this);
                roads--;
                roadsList.add(bitSet);
            }
        }
    }

    /**
     * Build settlement
     *
     * @param bitSet specific corner
     */
    public void buildSettlement(BitSet bitSet) {
        LOGGER.info(bitSet);
        if (testBuildings(Test.SETTLEMENT, bitSet)) {
            board.getAllCorners().get(bitSet).setPlayer(this);
            settlements--;
            settlementsList.add(bitSet);
            setSpecificResource(Resource.BRICK, -1);
            management.setSpecificResource(Resource.BRICK, 1);
            setSpecificResource(Resource.GRAIN, -1);
            management.setSpecificResource(Resource.GRAIN, 1);
            setSpecificResource(Resource.WOOL, -1);
            management.setSpecificResource(Resource.WOOL, 1);
            setSpecificResource(Resource.LUMBER, -1);
            management.setSpecificResource(Resource.LUMBER, 1);
            victoryPoints++;
            if (board.getAllCorners().get(bitSet).getPortType() != null && !(harborTyps.contains(board.getAllCorners().get(bitSet).getPortType()))) {
                harborTyps.add(board.getAllCorners().get(bitSet).getPortType());
            }
            LOGGER.info("SUCCESS");
        }
    }

    /**
     * Build city
     *
     * @param bitSet specific corner
     */
    public void buildCity(BitSet bitSet) {
        if (testBuildings(Test.CITY, bitSet)) {
            board.getAllCorners().get(bitSet).setIsCity();
            settlements++;
            settlementsList.remove(bitSet);
            cities--;
            citiesList.add(bitSet);
            setSpecificResource(Resource.GRAIN, -2);
            management.setSpecificResource(Resource.GRAIN, 2);
            setSpecificResource(Resource.ORE, -3);
            management.setSpecificResource(Resource.ORE, 3);
            victoryPoints++;
            LOGGER.info(bitSet);
        }
    }

    /**
     * Build Road
     *
     * @param bitSet specific edge
     */
    public void buildRoad(BitSet bitSet) {
        if (testBuildings(Test.STREET, bitSet)) {
            board.getAllEdges().get(bitSet).setPlayer(this);
            roads--;
            roadsList.add(bitSet);
            setSpecificResource(Resource.BRICK, -1);
            management.setSpecificResource(Resource.BRICK, 1);
            setSpecificResource(Resource.LUMBER, -1);
            management.setSpecificResource(Resource.LUMBER, 1);
            LOGGER.info(bitSet);
        }
    }

    /**
     * buy a development card, if its possible
     */
    public void buyDevelopmentCard(Player player) {

        if (testCards() && player.developmentCardsMove.size() == 0) {
            DevelopmentCard card = management.getDevelopmentCardFactory().takeDevelopCard();
            player.developmentCardsMove.add(0, card);
            setSpecificResource(Resource.WOOL, -1);
            management.setSpecificResource(Resource.WOOL, 1);
            setSpecificResource(Resource.GRAIN, -1);
            management.setSpecificResource(Resource.GRAIN, 1);
            setSpecificResource(Resource.ORE, -1);
            management.setSpecificResource(Resource.ORE, 1);
        }
    }

    /**
     * if development card can be bought
     *
     * @return true/false
     */
    public boolean testCards() {
        if (management.getDevelopmentCardFactory().getSizeDevelopmentCards() > 0) {
            if (wool >= 1 && grain >= 1 && ore >= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * test if enough resources for development card
     *
     * @return
     */
    public boolean testEnoughRessourcesDevCard() {
        return wool >= 1 && grain >= 1 && ore >= 1;
    }

    /**
     * Move the cards, which were buying in this move to the cards you can play in the next move
     */
    public void moveDevelopmentcards() {
        if (developmentCardsMove.size() > 0) {
            developmentCards.addAll(developmentCardsMove);
            developmentCardsMove.clear();
        }
        LOGGER.info(developmentCards + " Karten können gespielt werden");

    }

    /**
     * Test if you have enough resources to build a specific building/developmentcard
     *
     * @param building specific building
     * @return true if you have enough resources
     */
    public boolean testRessources(Test building) {
        switch (building) {
            case STREET:
                if (!(status == Status.BUILD_SETTLEMENT || status == Status.BUILD_STREET)) {
                    if (brick >= 1 && lumber >= 1) {
                        return true;
                    }
                } else {
                    return true;
                }
                break;
            case SETTLEMENT:
                if (!(status == Status.BUILD_SETTLEMENT || status == Status.BUILD_STREET)) {
                    if (brick >= 1 && grain >= 1 && wool >= 1 && lumber >= 1) {
                        return true;
                    }
                } else {
                    return true;
                }
                break;
            case CITY:
                if (grain >= 2 && ore >= 3) {
                    return true;
                }
                break;
            case DEVELOPMENT_CARD:
                if (wool >= 1 && grain >= 1 && ore >= 1) {
                    return true;
                }
                break;

        }
        return false;
    }

    /**
     * Test if you have enough buildings to build a specific building
     *
     * @param building specific building
     * @return true if you have enough buildings
     */
    public boolean testEnoughBuildings(Test building) {
        switch (building) {
            case STREET:
                if (!(status == Status.BUILD_SETTLEMENT || status == Status.BUILD_STREET)) {
                    if (roads >= 1) {
                        return true;
                    }
                } else {
                    return true;
                }
                break;
            case SETTLEMENT:
                if (!(status == Status.BUILD_SETTLEMENT || status == Status.BUILD_STREET)) {
                    if (settlements >= 1) {
                        return true;
                    }
                } else {
                    return true;
                }
                break;
            case CITY:
                if (cities >= 1) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Test if you can build a specific building on a specific place
     *
     * @param building specific building
     * @param bitSet   specific place
     * @return true if you have enough resources
     */
    public boolean testPlace(Test building, BitSet bitSet) {
        switch (building) {
            case STREET:
                if (getBuildableStartRoads().contains(board.getAllEdges().get(bitSet))) {
                    return true;
                }
                break;
            case SETTLEMENT:
                if (getBuildableSettlements().contains(getBoard().getAllCorners().get(bitSet))) {
                    return true;
                }
                break;
            case CITY:
                if (getBuildableCities().contains(board.getAllCorners().get(bitSet))) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Test if there are enough development cards
     *
     * @return
     */
    public boolean testEnoughDevelopmentCards() {
        return management.getDevelopmentCardFactory().getSizeDevelopmentCards() > 0;
    }

    /**
     * Test if you can build a specific building
     *
     * @param building specific building
     * @param bitSet   specific place
     * @return true if you can build
     */
    public boolean testBuildings(Test building, BitSet bitSet) {
        switch (building) {
            case STREET:
                if (!(status == Status.BUILD_SETTLEMENT || status == Status.BUILD_STREET)) {
                    if (roads >= 1 && brick >= 1 && lumber >= 1) {
                        if (getBuildableRoads().contains(board.getAllEdges().get(bitSet))) {
                            return true;
                        }
                    }
                } else {
                    if (getBuildableStartRoads().contains(board.getAllEdges().get(bitSet))) {
                        return true;
                    }
                }
                break;
            case SETTLEMENT:
                if (!(status == Status.BUILD_SETTLEMENT || status == Status.BUILD_STREET)) {
                    if (settlements >= 1 && brick >= 1 && grain >= 1 && wool >= 1 && lumber >= 1) {
                        if (getBuildableSettlements().contains(board.getAllCorners().get(bitSet))) {
                            return true;
                        }
                    }
                } else {
                    if (getBuildableStartSettlements().contains(board.getAllCorners().get(bitSet))) {
                        return true;
                    }
                }
                break;
            case CITY:
                if (cities >= 1 && grain >= 2 && ore >= 3) {
                    if (getBuildableCities().contains(board.getAllCorners().get(bitSet))) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    /**
     * Test if there are enough buildings to play the road-building card
     *
     * @return ture if you have enough buildings
     */
    public boolean testEnoughBuildingsRoadBuildingCard() {
        return roads >= 2;
    }

    public boolean testPlaceRoadBuildingCard(BitSet bitSet1, BitSet bitSet2) {
        if (getBuildableRoads().contains(board.getAllEdges().get(bitSet1))) {
            if (getBuildableRoadsRoadBuildingCard(bitSet1).contains(board.getAllEdges().get(bitSet2))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cheat for resources
     *
     * @param x nummber of resources you want to get
     */
    public void cheatPLUSres(int x) {
        brick += x;
        grain += x;
        wool += x;
        ore += x;
        lumber += x;
    }

    /**
     * Cheat for development cards
     */
    public void cheatDevCard() {
        grain += 1;
        wool += 1;
        ore += 1;
    }

}

