package model.players;

import controller.Register;
import model.Resource;
import model.board.*;
import model.cards.*;
import network.client.Client;
import network.client.ClientWriter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Ai extends Player {

    private static final Logger LOGGER = LogManager.getLogger(Ai.class.getName());
    private ArrayList<Resource> resourcesTerrains = new ArrayList<>();
    private ArrayList<Resource> neededResources = new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.LUMBER, Resource.GRAIN, Resource.WOOL, Resource.ORE));
    private Client c;
    private int moveAction = 0;
    private boolean cardPlayed = false;
    private ClientWriter clientWriter;

    public Ai() {

    }

    public Ai(int id, String name, PColor color) {
        LOGGER.traceEntry(this.toString());
        this.setId(id);
        this.setName(name);
        this.setColor(color);
        printPlayer();
    }

    public void setClient(final Client c) {
        this.c = c;
        this.clientWriter = new ClientWriter(this.c);
    }

    /**
     * Returns the best place to build a settlmenet
     *
     * @param corners place
     * @return a corner
     */
    private Corner cornersNextToTheBestSpecificTerrain(ArrayList<Corner> corners) {
        ArrayList<Corner> tmpCorners0 = new ArrayList<>();
        for (Corner corner : corners) {
            if (getBuildableSettlements().contains(corner)) {
                tmpCorners0.add(corner);
            }
        }
        ArrayList<Corner> tmpCorners1 = cornersNextToTerrains(tmpCorners0);
        ArrayList<Corner> tmpCorners2 = cornersNextToDifferentTerrains(tmpCorners1);
        ArrayList<Corner> tmpCorners3 = cornersNextToNewTerrains(tmpCorners2);
        ArrayList<Corner> tmpCorners4 = cornersNextToNumbertoken(tmpCorners3);
        if (tmpCorners4.size() > 0) {
            int tmp = ThreadLocalRandom.current().nextInt(0, tmpCorners4.size());
            Corner tmpCorner = tmpCorners4.get(tmp);
            return tmpCorner;
        }
        return null;
    }

    /**
     * Returns a list of corners next to the best number of needed resources
     *
     * @param corners list of corners next to min. one needed resource
     * @return list of corners
     */
    private ArrayList<Corner> cornersNextToNeededResources(ArrayList<Corner> corners) {
        ArrayList<Corner> tmpCorners = new ArrayList<>();
        for (int i = 3; i > 1; i--) {
            ArrayList<Corner> cornersNeededResources = cornersNextXNeededResources(i);
            if (cornersNeededResources.size() > 0) {
                for (Corner corner : corners) {
                    if (cornersNeededResources.contains(corner)) {
                        tmpCorners.add(corner);
                    }
                }
                if (tmpCorners.size() > 0) {
                    return tmpCorners;
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list with all corners from a specific list which are next to the best nummber of terrains
     *
     * @param corners specific list
     * @return list with corners next to the best number of terrains
     */
    private ArrayList<Corner> cornersNextToTerrains(ArrayList<Corner> corners) {
        ArrayList<Corner> tmpCorners = new ArrayList<>();
        for (int i = 3; i > 1; i--) {
            ArrayList<Corner> cornersTerrain = cornersNextXTerrain(i);
            if (cornersTerrain.size() > 0) {
                for (Corner corner : corners) {
                    if (cornersTerrain.contains(corner)) {
                        tmpCorners.add(corner);
                    }
                }
                if (tmpCorners.size() > 0) {
                    return tmpCorners;
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list with all corners from a specific list which are next to the best numbertoken
     *
     * @param corners specific list
     * @return list with corners next to the best numbertoken
     */
    private ArrayList<Corner> cornersNextToNumbertoken(ArrayList<Corner> corners) {
        ArrayList<Corner> tmpCorners = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            ArrayList<Corner> cornersNumbertoken = cornersNextXOrY(i);
            if (cornersNumbertoken.size() > 0) {
                for (Corner corner : corners) {
                    if (cornersNumbertoken.contains(corner)) {
                        tmpCorners.add(corner);
                    }
                }
                if (tmpCorners.size() > 0) {
                    return tmpCorners;
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list with all corners form a specific list which are next to different terrains
     *
     * @param corners specific list
     * @return list with corners next to different terrains
     */
    private ArrayList<Corner> cornersNextToDifferentTerrains(ArrayList<Corner> corners) {
        ArrayList<Corner> tmpCorners = new ArrayList<>();
        for (Corner corner : corners) {
            boolean different = true;
            for (Tile tile1 : getBoard().getTileneighborsOfCorner(corner)) {
                for (Tile tile2 : getBoard().getTileneighborsOfCorner(corner)) {
                    if (!(tile1 instanceof Sea || tile2 instanceof Sea)) {
                        if (((Terrain) tile1).harvest() == ((Terrain) tile2).harvest() && tile1 != tile2) {
                            different = false;
                        }
                    }
                }
            }
            if (different) {
                tmpCorners.add(corner);
            }
        }
        if (tmpCorners.size() > 0) {
            return tmpCorners;
        } else {
            for (Corner corner : corners) {
                boolean different = false;
                for (Tile tile1 : getBoard().getTileneighborsOfCorner(corner)) {
                    for (Tile tile2 : getBoard().getTileneighborsOfCorner(corner)) {
                        if (!(tile1 instanceof Sea || tile2 instanceof Sea)) {
                            if (((Terrain) tile1).harvest() != ((Terrain) tile2).harvest() && tile1 != tile2) {
                                different = true;
                            }
                        }
                    }
                }
                if (different) {
                    tmpCorners.add(corner);
                }
            }
            if (tmpCorners.size() > 0) {
                return tmpCorners;
            }
        }
        return corners;
    }

    /**
     * Returns a list with all corners from a specific list which are next to the best nummber of new terrains
     *
     * @param corners specific list
     * @return list with corners next to new terrains
     */
    private ArrayList<Corner> cornersNextToNewTerrains(ArrayList<Corner> corners) {
        ArrayList<Corner> tmpCorners = new ArrayList<>();
        for (int i = 3; i >= 2; i--) {
            ArrayList<Corner> cornersNewTerrains = cornersNextXNewTerrains(i);
            if (cornersNewTerrains.size() > 0) {
                for (Corner corner : corners) {
                    if (cornersNewTerrains.contains(corner)) {
                        tmpCorners.add(corner);
                    }
                }
                if (tmpCorners.size() > 0) {
                    return tmpCorners;
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list with all corners which are next to x terrains which are not in the resourcesTerrains-list
     *
     * @param x number of new terrains
     * @return list of corners
     */
    private ArrayList<Corner> cornersNextXNewTerrains(int x) {
        ArrayList<Corner> corners = new ArrayList<>();
        for (Corner corner : getBoard().getAllCorners().values()) {
            int count = 0;
            for (Tile tile : getBoard().getTileneighborsOfCorner(corner)) {
                if (tile instanceof Terrain) {
                    if (!(resourcesTerrains.contains(((Terrain) tile).harvest()))) {
                        count++;
                    }
                }
            }
            if (count >= x) {
                corners.add(corner);
            }
        }
        return corners;
    }

    /**
     * Returns a list with all corners which are next to a specific Terrain-Typ
     *
     * @param typ typ of Terrain("Forest", "Hill", "Mountain", "Field", "Pasture")
     * @return list of corners
     */
    private ArrayList<Corner> cornersNextSpecificTerrain(String typ) {
        ArrayList<Corner> corners = new ArrayList<>();
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Tile tile : getBoard().getAllTiles().values()) {
            switch (typ) {
                case "Forest":
                    if (tile instanceof Forest) {
                        tiles.add(tile);
                    }
                    break;
                case "Hill":
                    if (tile instanceof Hill) {
                        tiles.add(tile);
                    }
                    break;
                case "Mountain":
                    if (tile instanceof Mountain) {
                        tiles.add(tile);
                    }
                    break;
                case "Field":
                    if (tile instanceof Field) {
                        tiles.add(tile);
                    }
                    break;
                case "Pasture":
                    if (tile instanceof Pasture) {
                        tiles.add(tile);
                    }
                    break;
            }

        }
        for (Tile tile : tiles) {
            for (Corner corner : getBoard().getCornerneighborsOfTile(tile)) {
                if (!(corners.contains(corner))) {
                    corners.add(corner);
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list with all corners which are next to a tile with numbertoken X or Y (X = 7-Z und Y = 7+Z)
     *
     * @param z difference between the number of the numbertoken and 7 (Z = 1,2,3,4,5)
     * @return list of corners
     */
    private ArrayList<Corner> cornersNextXOrY(int z) {
        if (z <= 5 || z >= 1) {
            ArrayList<Corner> corners = new ArrayList<>();
            ArrayList<Tile> tiles = new ArrayList<>();
            for (Tile tile : getBoard().getAllTiles().values()) {
                if (tile instanceof Terrain) {
                    if (((Terrain) tile).getNumberToken() == 7 - z || ((Terrain) tile).getNumberToken() == 7 + z) {
                        tiles.add(tile);
                    }
                }
            }
            for (Tile tile : tiles) {
                for (Corner corner : getBoard().getCornerneighborsOfTile(tile)) {
                    if (!(corners.contains(corner))) {
                        corners.add(corner);
                    }
                }
            }
            return corners;
        }
        return null;
    }

    /**
     * Returns a list with all corners which are next to x needed resources
     *
     * @param x number of needed resources (2 or 3)
     * @return list of corners
     */
    private ArrayList<Corner> cornersNextXNeededResources(int x) {
        ArrayList<Corner> corners = new ArrayList<>();
        for (Corner corner : getBoard().getAllCorners().values()) {
            ArrayList<Tile> tileNeighbors = getBoard().getTileneighborsOfCorner(corner);
            int count = 0;
            for (Tile tile : tileNeighbors) {
                if (tile instanceof Terrain && !(tile instanceof Desert)) {
                    if (neededResources.contains(((Terrain) tile).harvest())) {
                        count++;
                    }
                }
            }
            if (count >= x) {
                corners.add(corner);
            }
        }
        return corners;
    }

    /**
     * Returns a list with all corners which are next to x terrains
     *
     * @param x number of neighbor-terrains (2 or 3)
     * @return list of corners
     */
    private ArrayList<Corner> cornersNextXTerrain(int x) {
        ArrayList<Corner> corners = new ArrayList<>();
        for (Corner corner : getBoard().getAllCorners().values()) {
            ArrayList<Tile> tileNeighbors = getBoard().getTileneighborsOfCorner(corner);
            int count = 0;
            for (Tile tile : tileNeighbors) {
                if (tile instanceof Terrain && !(tile instanceof Desert)) {
                    count++;
                }
            }
            if (count >= x) {
                corners.add(corner);
            }
        }
        return corners;
    }

    /**
     * Returns the best place to build a settlement
     *
     * @return a corner
     */
    public Corner settlement() {

        ArrayList<Corner> cornersBuildable = getBuildableSettlements();

        if (cornersBuildable.size() > 0) {
            int tmp;
            Corner tmpCorner = null;
            ArrayList<Corner> cornersForest = cornersNextSpecificTerrain("Forest");
            ArrayList<Corner> cornersHill = cornersNextSpecificTerrain("Hill");
            ArrayList<Corner> cornersField = cornersNextSpecificTerrain("Field");
            ArrayList<Corner> cornersPasture = cornersNextSpecificTerrain("Pasture");
            ArrayList<Corner> cornersMountain = cornersNextSpecificTerrain("Mountain");
            int start = 0;
            switch (start) {
                case 0:
                    if (cornersForest.size() > 0 && !(resourcesTerrains.contains(Resource.LUMBER))) {
                        tmpCorner = cornersNextToTheBestSpecificTerrain(cornersForest);
                        if (tmpCorner != null) {
                            break;
                        }
                    }
                case 1:
                    if (cornersHill.size() > 0 && !(resourcesTerrains.contains(Resource.BRICK))) {
                        tmpCorner = cornersNextToTheBestSpecificTerrain(cornersHill);
                        if (tmpCorner != null) {
                            break;
                        }
                    }
                case 2:
                    if (cornersField.size() > 0 && !(resourcesTerrains.contains(Resource.GRAIN))) {
                        tmpCorner = cornersNextToTheBestSpecificTerrain(cornersField);
                        if (tmpCorner != null) {
                            break;
                        }
                    }
                case 3:
                    if (cornersPasture.size() > 0 && !(resourcesTerrains.contains(Resource.WOOL))) {
                        tmpCorner = cornersNextToTheBestSpecificTerrain(cornersPasture);
                        if (tmpCorner != null) {
                            break;
                        }
                    }
                case 4:
                    if (cornersMountain.size() > 0 && !(resourcesTerrains.contains(Resource.ORE))) {
                        tmpCorner = cornersNextToTheBestSpecificTerrain(cornersMountain);
                        if (tmpCorner != null) {
                            break;
                        }
                    }
                case 5:
                    ArrayList<Corner> tmpCorners1 = cornersNextToTerrains(cornersBuildable);
                    ArrayList<Corner> tmpCorners2 = cornersNextToDifferentTerrains(tmpCorners1);
                    ArrayList<Corner> tmpCorners3 = cornersNextToNumbertoken(tmpCorners2);
                    tmp = ThreadLocalRandom.current().nextInt(0, tmpCorners3.size());
                    tmpCorner = tmpCorners3.get(tmp);

            }
            for (Tile tile : getBoard().getTileneighborsOfCorner(tmpCorner)) {
                if (tile instanceof Terrain) {
                    if (!(tile instanceof Desert)) {
                        resourcesTerrains.add(((Terrain) tile).harvest());
                        neededResources.remove(((Terrain) tile).harvest());
                    }
                }
            }

            return tmpCorner;
        }
        return null;
    }

    /**
     * Build settlement in setting up
     *
     */
    public void buildStartSettlement() {
        LOGGER.info("bau Startsiedlung von " + getName());
        slowDown();
        Corner tmpCorner = settlement();
        if (tmpCorner != null) {
            LOGGER.info(tmpCorner);
            clientWriter.tryToBuild(tmpCorner.getBitSetID(), "Settlement");
        }
    }

    /**
     * Build random road in setting up
     *
     */
    public void buildStartRoad() {
        LOGGER.info("bau Startstraße von " + getName());
        slowDown();
        ArrayList<Edge> edges = getBuildableRoads();
        if (edges.size() > 0) {
            int tmp = ThreadLocalRandom.current().nextInt(0, edges.size());
            clientWriter.tryToBuild(edges.get(tmp).getBitSetID(), "Street");
        }
    }

    /**
     * Build random settlement
     */
    public void buildSettlement() {
        if (getSettlements() >= 1) {
            if (getBrick() >= 1 && getGrain() >= 1 && getWool() >= 1 && getLumber() >= 1) {
                ArrayList<Corner> buildableSettlements = getBuildableSettlements();
                if (buildableSettlements.size() > 0) {
                    Corner tmpCorner = settlement();
                    clientWriter.tryToBuild(tmpCorner.getBitSetID(), "Settlement");
                }
            }
        }
    }

    /**
     * Build random city
     */
    public void buildCity() {
        if (getCities() >= 1) {
            if (getGrain() >= 2 && getOre() >= 3) {
                ArrayList<Corner> buildableCities = getBuildableCities();
                if (buildableCities.size() > 0) {
                    int tmp = ThreadLocalRandom.current().nextInt(0, buildableCities.size());
                    if (buildableCities.get(tmp).getPlayer() == this) {
                        if (!(buildableCities.get(tmp).getIsCity())) {
                            clientWriter.tryToBuild(buildableCities.get(tmp).getBitSetID(), "City");
                        }
                    }
                }
            }
        }
    }

    /**
     * Build a random road
     *
     * @return true, if ai has build a road
     */
    public boolean buildRoad() {
        if (getRoads() >= 1) {
            if (getBrick() >= 1 && getLumber() >= 1) {
                ArrayList<Edge> buildableRoads = getBuildableRoads();
                if (buildableRoads.size() > 0) {
                    if (neededResources.size() == 0) {
                        int max = 0;
                        for (Edge edge : buildableRoads) {
                            if (tryToGetLongestStreet(edge) > max) {
                                max = tryToGetLongestStreet(edge);
                            }
                        }
                        ArrayList<Edge> maxRoads = new ArrayList<>();
                        for (Edge edge : buildableRoads) {
                            if (tryToGetLongestStreet(edge) == max) {
                                maxRoads.add(edge);
                            }
                        }
                        if (maxRoads.size() > 0) {
                            int tmp = ThreadLocalRandom.current().nextInt(0, maxRoads.size());
                            clientWriter.tryToBuild(maxRoads.get(tmp).getBitSetID(), "Street");
                            return true;
                        }
                    } else {
                        ArrayList<Corner> corners = new ArrayList<>();
                        for (Corner corner : getBoard().getAllCorners().values()) {
                            if (corner.getPlayer() == null) {
                                if (distanceRule(corner.getBitSetID())) {
                                    for (Tile tile : getBoard().getTileneighborsOfCorner(corner)) {
                                        if (tile instanceof Terrain && !(tile instanceof Desert)) {
                                            for (Resource resource : neededResources) {
                                                if (((Terrain) tile).harvest() == resource) {
                                                    if (!(corners.contains(corner))) {
                                                        corners.add(corner);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        ArrayList<Edge> shortestWay = new ArrayList<>();
                        for (Corner corner : corners) {
                            ArrayList<Edge> tmp = shortesWay(corner);
                            if (shortestWay.size() == 0) {
                                shortestWay = tmp;
                            } else {
                                if (tmp.size() < shortestWay.size()) {
                                    shortestWay = tmp;
                                }
                            }
                        }
                        LOGGER.info(shortestWay);
                        for (Edge edge : shortestWay) {
                            if (buildableRoads.contains(edge)) {
                                clientWriter.tryToBuild(edge.getBitSetID(), "Street");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns a list with edges which construct the shortest way from a edge to a corner
     *
     * @param startEdge start edge
     * @param startCorner ende corner
     * @param way         calculate way
     * @return list with edge
     */
    private ArrayList<Edge> shortestWayEdge(Edge startEdge, Corner startCorner, ArrayList<Edge> way) {
        ArrayList<Edge> neighbors = getBoard().getEdgeneighborsOfCorner(startCorner);
        for (Edge edge : neighbors) {
            if (edge == startEdge) {
                way.add(edge);
                return way;
            }
        }
        ArrayList<Edge> shortestWay = new ArrayList<>();
        for (int i = 0; i < neighbors.size(); i++) {
            if (!(way.contains(neighbors.get(i)))) {
                if (neighbors.get(i).getPlayer() == null) {
                    ArrayList<Corner> cornersNeighbors = getBoard().getCornerneighborsOfEdge(neighbors.get(i));
                    Corner tmpCorner = startCorner;
                    for (Corner corner : cornersNeighbors) {
                        if (corner != startCorner) {
                            tmpCorner = corner;
                        }
                    }
                    if (tmpCorner.getPlayer() == this || tmpCorner.getPlayer() == null) {
                        ArrayList<Edge> newWay = new ArrayList<>();
                        for (Edge tmpEdge : way) {
                            newWay.add(tmpEdge);
                        }
                        newWay.add(neighbors.get(i));
                        ArrayList tmpShortestWay = shortestWayEdge(startEdge, tmpCorner, newWay);
                        if (tmpShortestWay != null) {
                            if (shortestWay.size() == 0) {
                                shortestWay = tmpShortestWay;
                            } else {
                                if (tmpShortestWay.size() < shortestWay.size()) {
                                    shortestWay = tmpShortestWay;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (shortestWay.size() > 0) {
            return shortestWay;
        } else {
            return null;
        }
    }

    /**
     * Returns a list with all roads used to build the shortest way to a corner
     *
     * @param corner tile corner
     * @return edge
     */
    private ArrayList<Edge> shortesWay(Corner corner) {
        ArrayList<Edge> edges = new ArrayList<>();
        for (Edge edge : getBuildableRoads()) {
            ArrayList tmp = shortestWayEdge(edge, corner, new ArrayList<>());
            if (tmp != null) {
                if (edges.size() == 0) {
                    edges = tmp;
                } else {
                    if (tmp.size() < edges.size()) {
                        edges = tmp;
                    }
                }
            }
        }
        return edges;
    }

    /**
     * Returns length of a part of a street starting from a specific edge in a specific direction when you build a new edge
     *
     * @param edge             specific edge
     * @param corner           specify the direction
     * @param edgesOfPlayer    list with edges of the specific player, which have not been counted yet
     * @param edgesOfPlayertmp list with edges of the specific player, which have not been counted yet in the specific direction
     * @param tryEdge          new edge
     * @return length of a part of a street
     */
    private int tryToGetStreetLengthPart(Edge edge, Corner corner, ArrayList<Edge> edgesOfPlayer, ArrayList<Edge> edgesOfPlayertmp, Edge tryEdge) {
        edgesOfPlayer.remove(edge);
        edgesOfPlayertmp.remove(edge);
        if (((corner.getPlayer() != this && corner.getPlayer() != null) || edge.getPlayer() == null && edge != tryEdge)) {
            return 0;
        } else {
            ArrayList<Edge> edges = getBoard().getEdgeneighborsOfCorner(corner);
            for (int i = 0; i < edges.size(); ) {
                Edge edgeneighbor = edges.get(i);
                if (edgeneighbor.getPlayer() != this && edgeneighbor != tryEdge || edgeneighbor == edge || !edgesOfPlayertmp.contains(edgeneighbor)) {
                    edges.remove(edgeneighbor);
                } else {
                    i++;
                }
            }
            int countPart;
            if (edges.size() == 2) {
                Corner corner0;
                if (getBoard().getCornerneighborsOfEdge(edges.get(0)).get(0) == corner) {
                    corner0 = getBoard().getCornerneighborsOfEdge(edges.get(0)).get(1);
                } else {
                    corner0 = getBoard().getCornerneighborsOfEdge(edges.get(0)).get(0);
                }
                Corner corner1;
                if (getBoard().getCornerneighborsOfEdge(edges.get(1)).get(0) == corner) {
                    corner1 = getBoard().getCornerneighborsOfEdge(edges.get(1)).get(1);
                } else {
                    corner1 = getBoard().getCornerneighborsOfEdge(edges.get(1)).get(0);
                }
                ArrayList<Edge> edgesOfPlayertmp0 = new ArrayList<>();
                ArrayList<Edge> edgesOfPlayertmp1 = new ArrayList<>();
                for (int i = 0; i < edgesOfPlayertmp.size(); i++) {
                    edgesOfPlayertmp0.add(edgesOfPlayertmp.get(i));
                    edgesOfPlayertmp1.add(edgesOfPlayertmp.get(i));
                }
                countPart = 1 + Math.max(tryToGetStreetLengthPart(edges.get(0), corner0, edgesOfPlayer, edgesOfPlayertmp0, tryEdge), tryToGetStreetLengthPart(edges.get(1), corner1, edgesOfPlayer, edgesOfPlayertmp1, tryEdge));
            } else if (edges.size() == 1) {
                Corner corner0;
                if (getBoard().getCornerneighborsOfEdge(edges.get(0)).get(0) == corner) {
                    corner0 = getBoard().getCornerneighborsOfEdge(edges.get(0)).get(1);
                } else {
                    corner0 = getBoard().getCornerneighborsOfEdge(edges.get(0)).get(0);
                }
                countPart = 1 + tryToGetStreetLengthPart(edges.get(0), corner0, edgesOfPlayer, edgesOfPlayertmp, tryEdge);
            } else {
                countPart = 1;
            }
            return countPart;
        }
    }

    /**
     * Returns the length of the street starting from a specific edge when you build a new edge
     *
     * @param edge    specific edge
     * @param tryEdge new edge
     * @return length of street
     */
    private int tryToGetStreetLength(Edge edge, Edge tryEdge) {
        ArrayList<Edge> edgesOfPlayer = new ArrayList<>();
        for (Edge edgePlayer : getBoard().getAllEdges().values()) {
            if (edgePlayer.getPlayer() == edge.getPlayer()) {
                edgesOfPlayer.add(edgePlayer);
            }
        }
        edgesOfPlayer.add(tryEdge);
        int counter = -1;

        int countLeft = tryToGetStreetLengthPart(edge, getBoard().getCornerneighborsOfEdge(edge).get(0), edgesOfPlayer, edgesOfPlayer, tryEdge);
        int countRight = tryToGetStreetLengthPart(edge, getBoard().getCornerneighborsOfEdge(edge).get(1), edgesOfPlayer, edgesOfPlayer, tryEdge);

        counter = counter + countLeft + countRight;
        return counter;
    }

    /**
     * Returns the length of the longest street from a specific player when you build a street on a specific edge
     *
     * @param tryEdge specific edge
     * @return length of longest street
     */
    private int tryToGetLongestStreet(Edge tryEdge) {
        int tmp = 0;
        for (Edge edge : getBoard().getAllEdges().values()) {
            if (edge.getPlayer() != null) {
                if (edge.getPlayer().getId() == getId()) {
                    tmp = Math.max(tmp, tryToGetStreetLength(edge, tryEdge));
                }
            }
        }
        int streetLength = tryToGetStreetLength(tryEdge, tryEdge);
        if (streetLength > tmp) {
            tmp = streetLength;
        }
        return tmp;
    }

    /**
     * AI makes a move
     *
     */
    public void makeMove() {
        slowDown();
        moveActionPlus();
        boolean needsResources = false;
        switch (moveAction) {
            case 1:
                LOGGER.info(getName() + ": Case 1 betreten");
                if (neededResources.size() > 0) {
                    LOGGER.info("neededResources: " + neededResources.size());
                    boolean tmp = needsResources();
                    if (tmp) {
                        break;
                    } else {
                        needsResources = true;
                    }
                }
                moveActionPlus();
                LOGGER.info(getName() + ": Case 1 verlassen");
            case 2:
                LOGGER.info(getName() + ": Case 2 betreten");
                if (!needsResources) {
                    if (getBuildableCities().size() > 0) {
                        if (testEnoughBuildings(Test.CITY)) {
                            if (testRessources(Test.CITY)) {
                                moveActionMinus();
                                buildCity();
                                break;
                            }
                        }
                    }
                }
                moveActionPlus();
                LOGGER.info(getName() + ": Case 2 verlassen");
            case 3:
                LOGGER.info(getName() + ": Case 3 betreten");
                if (!needsResources) {
                    if (getBuildableSettlements().size() > 0) {
                        if (testEnoughBuildings(Test.SETTLEMENT)) {
                            if (testRessources(Test.SETTLEMENT)) {
                                moveActionMinus();
                                buildSettlement();
                                break;
                            }
                        }
                    }
                }
                moveActionPlus();
                LOGGER.info(getName() + ": Case 3 verlassen");
            case 4:
                LOGGER.info(getName() + ": Case 4 betreten");
                if (!needsResources) {
                    if (!(getLongestRoad()) || getBuildableSettlements().size() > 0) {
                        if (testEnoughBuildings(Test.STREET)) {
                            if (testRessources(Test.STREET)) {
                                moveActionMinus();
                                buildRoad();
                                break;
                            }
                        }
                    }
                }
                moveActionPlus();
                LOGGER.info(getName() + ": Case 4 verlassen");
            case 5:
                LOGGER.info(getName() + ": Case 5 betreten");
                if (!needsResources) {
                    if (getDevelopmentCardsMove().size() <= 0) {
                        if (testCards()) {
                            if (Register.getNtwrkServer() == null) {
                                if (c.getCounterDevCard() < 25) {
                                    clientWriter.buyDevelopmentCardButton();
                                    LOGGER.info("Ai buyed a developmentcard");
                                    break;
                                }
                            } else {
                                if (Register.getController().getManagement().getDevelopmentCardFactory().getSizeDevelopmentCards() > 0) {
                                    clientWriter.buyDevelopmentCardButton();
                                    LOGGER.info("Ai buyed a developmentcard");
                                    break;
                                }
                            }
                        }
                    }
                }
                moveActionPlus();
                LOGGER.info(getName() + ": Case 5 verlassen");
            case 6:
                LOGGER.info(getName() + ": Case 6 betreten");
                if (!needsResources) {
                    if (cardPlayed == false) {
                        for (DevelopmentCard card : getDevelopmentCards()) {
                            if (card instanceof KnightCard) {
                                BitSet bs = changeRobberPosition();
                                int id = robCard(bs);
                                if (id >= 0) {
                                    clientWriter.moveKnight(bs, id);
                                    cardPlayed = true;
                                    LOGGER.info(this.getName() + " played KnightCard, new position: " + bs + ", player: " + getAllPlayersId().get(id).getName());
                                    break;
                                } else {
                                    clientWriter.moveKnight(bs);
                                    cardPlayed = true;
                                    LOGGER.info(this.getName() + " played KnightCard, new position: " + bs);
                                    break;
                                }
                            } else if (card instanceof YearOfPlentyCard) {
                                Resource resource1 = yearOfPlanty1();
                                Resource resource2 = yearOfPlanty2(resource1);
                                ArrayList<Resource> resources = new ArrayList<>();
                                resources.add(resource1);
                                resources.add(resource2);
                                clientWriter.yearOfPlenty(resources);
                                cardPlayed = true;
                                LOGGER.info(this.getName() + " played YearOfPlentyCard, resource1: " + resource1 + ", resource2: " + resource2);
                                break;
                            } else if (card instanceof RoadBuildingCard) {
                                if (getRoads() > 0) {
                                    BitSet road1 = roadBuilding1();
                                    if (road1.nextSetBit(0) != 1000) {
                                        if (getBuildableRoadsRoadBuildingCard(road1).size() > 0) {
                                            BitSet road2 = roadBuilding2(road1);
                                            if (road2.nextSetBit(0) != 1000) {
                                                clientWriter.roadBuildingCard(road1, road2);
                                                cardPlayed = true;
                                                LOGGER.info(this.getName() + " played RoadBuidlingCard, road1: " + road1 + ", road2: " + road2);
                                                break;
                                            }
                                        } else {
                                            clientWriter.roadBuildingCard(road1);
                                            cardPlayed = true;
                                            LOGGER.info(this.getName() + " played RoadBuidlingCard, road1: " + road1);
                                            break;
                                        }
                                    }
                                }
                            } else if (card instanceof MonopolyCard) {
                                String resource = monopoly();
                                clientWriter.sendMonopol(resource);
                                cardPlayed = true;
                                LOGGER.info(this.getName() + " played MonopolyCard, resource1: " + resource);
                                break;
                            }

                        }
                    }
                }
                moveActionPlus();
                LOGGER.info(getName() + ": Case 6 verlassen");
            case 0:
                LOGGER.info(getName() + ": Case 0 betreten");
                slowDown();
                cardPlayed = false;
                clientWriter.endTurn();
                LOGGER.info(getName() + ": Case 0 verlassen");

        }

    }

    /**
     * Add one to moveAction (for the next action in makemove)
     *
     */
    private void moveActionPlus() {
        moveAction = (moveAction + 1) % 7;
    }

    private void moveActionMinus() {
        moveAction = (moveAction - 1) % 7;
    }

    /**
     * Make a move to get a settlement next to a needed resource
     *
     * @return true, if ai has something done
     */
    private boolean needsResources() {
        boolean noPlace = true;
        if (getBuildableSettlements().size() > 0) {
            ArrayList<Corner> possiblePlaces = new ArrayList<>();
            for (Corner corner : getBuildableSettlements()) {
                for (Tile tile : getBoard().getTileneighborsOfCorner(corner)) {
                    if (tile instanceof Terrain) {
                        if (!(tile instanceof Desert)) {
                            for (Resource resource : neededResources) {
                                if (((Terrain) tile).harvest() == resource) {
                                    possiblePlaces.add(corner);
                                }
                            }
                        }
                    }
                }
            }
            if (possiblePlaces.size() > 0) {
                ArrayList<Corner> tmpCorners = cornersNextToNeededResources(possiblePlaces);
                int tmp = ThreadLocalRandom.current().nextInt(0, tmpCorners.size());
                Corner tmpCorner = tmpCorners.get(tmp);
                if (testEnoughBuildings(Test.SETTLEMENT)) {
                    if (testRessources(Test.SETTLEMENT)) {
                        for (Tile tmpTile : getBoard().getTileneighborsOfCorner(tmpCorner)) {
                            if (tmpTile instanceof Terrain) {
                                if (!(tmpTile instanceof Desert)) {
                                    resourcesTerrains.add(((Terrain) tmpTile).harvest());
                                    neededResources.remove(((Terrain) tmpTile).harvest());
                                }
                            }
                        }
                        clientWriter.tryToBuild(tmpCorner.getBitSetID(), "Settlement");
                        return true;
                    } else {
                        return tradeForSettlement();
                    }
                } else {
                    if (testEnoughBuildings(Test.CITY)) {
                        if (testRessources(Test.CITY)) {
                            buildCity();
                            return true;
                        } else {
                            return tradeForCity();
                        }
                    }
                }
                return false;
            }
        }
        if (noPlace) {
            if (testEnoughBuildings(Test.STREET)) {
                if (testRessources(Test.STREET)) {
                    boolean buildARoad = buildRoad();
                    if (buildARoad) {
                        return true;
                    }
                } else {
                    return tradeForStreet();
                }
            }
        }
        return false;
    }

    /**
     * Try to trade to get the resources for a settlement
     *
     * @return true, if ai has trade
     */
    private boolean tradeForSettlement() {
        slowDown();
        boolean canNothing = false;
        Resource resource;
        if (getSpecificResource(Resource.ORE) >= 4) {
            resource = Resource.ORE;
        } else if (getSpecificResource(Resource.GRAIN) >= 5) {
            resource = Resource.GRAIN;
        } else if (getSpecificResource(Resource.WOOL) >= 5) {
            resource = Resource.WOOL;
        } else if (getSpecificResource(Resource.LUMBER) >= 5) {
            resource = Resource.LUMBER;
        } else if (getSpecificResource(Resource.BRICK) >= 5) {
            resource = Resource.BRICK;
        } else {
            canNothing = true;
            resource = null;
        }
        if (!canNothing) {
            ArrayList<Resource> resources = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                resources.add(resource);
            }
            if (getBrick() < 1) {
                clientWriter.tradeSea(resources, new ArrayList<>(Arrays.asList(Resource.BRICK)));
                return true;
            }
            if (getGrain() < 1) {
                clientWriter.tradeSea(resources, new ArrayList<>(Arrays.asList(Resource.GRAIN)));
                return true;
            }
            if (getWool() < 1) {
                clientWriter.tradeSea(resources, new ArrayList<>(Arrays.asList(Resource.WOOL)));
                return true;
            }
            if (getLumber() < 1) {
                clientWriter.tradeSea(resources, new ArrayList<>(Arrays.asList(Resource.LUMBER)));
                return true;
            }
        }
        return false;
    }

    /**
     * Try to trade to get the resources for a street
     *
     * @return true, if ai has trade
     */
    private boolean tradeForStreet() {
        slowDown();
        boolean canNothing = false;
        Resource resource;
        if (getSpecificResource(Resource.ORE) >= 4) {
            resource = Resource.ORE;
        } else if (getSpecificResource(Resource.GRAIN) >= 4) {
            resource = Resource.GRAIN;
        } else if (getSpecificResource(Resource.LUMBER) >= 5) {
            resource = Resource.LUMBER;
        } else if (getSpecificResource(Resource.WOOL) >= 4) {
            resource = Resource.WOOL;
        } else if (getSpecificResource(Resource.BRICK) >= 5) {
            resource = Resource.BRICK;
        } else {
            canNothing = true;
            resource = null;
        }
        if (!canNothing) {
            ArrayList<Resource> resources = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                resources.add(resource);
            }
            if (getBrick() < 1) {
                clientWriter.tradeSea(resources, new ArrayList<>(Arrays.asList(Resource.BRICK)));
                return true;
            }
            if (getLumber() < 1) {
                clientWriter.tradeSea(resources, new ArrayList<>(Arrays.asList(Resource.LUMBER)));
                return true;
            }
        }
        return false;
    }

    /**
     * Try to trade to get the resources for a city
     *
     * @return true, if ai has trade
     */
    private boolean tradeForCity() {
        slowDown();
        boolean canNothing = false;
        Resource resource;
        if (getSpecificResource(Resource.ORE) >= 7) {
            resource = Resource.ORE;
        } else if (getSpecificResource(Resource.GRAIN) >= 6) {
            resource = Resource.GRAIN;
        } else if (getSpecificResource(Resource.LUMBER) >= 4) {
            resource = Resource.LUMBER;
        } else if (getSpecificResource(Resource.WOOL) >= 4) {
            resource = Resource.WOOL;
        } else if (getSpecificResource(Resource.BRICK) >= 4) {
            resource = Resource.BRICK;
        } else {
            canNothing = true;
            resource = null;
        }
        if (!canNothing) {
            ArrayList<Resource> resources = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                resources.add(resource);
            }
            if (getGrain() < 2) {
                clientWriter.tradeSea(resources, new ArrayList<>(Arrays.asList(Resource.GRAIN)));
                return true;
            }
            if (getOre() < 3) {
                clientWriter.tradeSea(resources, new ArrayList<>(Arrays.asList(Resource.ORE)));
                return true;
            }
        }
        return false;
    }

    /**
     * Submit the cards with the largest number when a 7 is diced
     */
    public void startRobbing() {
        slowDown();
        ArrayList<Resource> resources = new ArrayList<>();
        int rob = getManagement().getRobber().cardNumberToRob(this);
        HashMap<Resource, Integer> numbers = new HashMap<>();
        numbers.put(Resource.BRICK, getSpecificResource(Resource.BRICK));
        numbers.put(Resource.ORE, getSpecificResource(Resource.ORE));
        numbers.put(Resource.WOOL, getSpecificResource(Resource.WOOL));
        numbers.put(Resource.GRAIN, getSpecificResource(Resource.GRAIN));
        numbers.put(Resource.LUMBER, getSpecificResource(Resource.LUMBER));
        LOGGER.info("Ai: Ressourcen 1, Brick: " + numbers.get(Resource.BRICK) + ", Ore: " + numbers.get(Resource.ORE) + ", Wool: " + numbers.get(Resource.WOOL) + ", Grain: " + numbers.get(Resource.GRAIN) + ", Lumber: " + numbers.get(Resource.LUMBER));

        while (resources.size() < rob) {
            resources.add(largestResourceNumber(numbers));
            LOGGER.info("Ai: Ressourcen 1, Brick: " + numbers.get(Resource.BRICK) + ", Ore: " + numbers.get(Resource.ORE) + ", Wool: " + numbers.get(Resource.WOOL) + ", Grain: " + numbers.get(Resource.GRAIN) + ", Lumber: " + numbers.get(Resource.LUMBER));

        }
        clientWriter.handInResources(resources);
    }

    /**
     * Change the position of the robber and rob a card from an other player
     *
     */
    public void robber() {
        slowDown();
        BitSet tmpBs = changeRobberPosition();
        LOGGER.info("neue Räuber-Position: " + tmpBs);
        int tmpId = robCard(tmpBs);
        if (tmpId == -1) {
            clientWriter.moveRobber(tmpBs);
        } else {
            clientWriter.moveRobber(tmpBs, tmpId);
        }
    }

    /**
     * Calculate new position of the robber
     *
     * @return new position of the robber
     */
    private BitSet changeRobberPosition() {
        ArrayList<Terrain> terrains = new ArrayList<>();
        for (Tile tile : getBoard().getAllTiles().values()) {
            if (tile instanceof Terrain && !tile.getId().equals(Register.getController().getManagement().getRobber().getPosition())) {
                boolean tmp = true;
                for (Corner corner : getBoard().getCornerneighborsOfTile(tile)) {
                    if (corner.getPlayer() == this) {
                        tmp = false;
                    }
                }
                if (tmp) {
                    terrains.add((Terrain) tile);
                }
            }
        }
        if (terrains.size() > 0) {
            int maxRobablePlayer = 0;
            for (Terrain terrain : terrains) {
                ArrayList<Player> robablePlayer = getManagement().getRobber().getRobablePlayer(terrain.getId());
                int tmpRobablePlayer = robablePlayer.size();
                for (Player player : robablePlayer) {
                    if (player == this) {
                        tmpRobablePlayer--;
                    }
                }
                if (tmpRobablePlayer > maxRobablePlayer) {
                    maxRobablePlayer = tmpRobablePlayer;
                }
            }
            ArrayList<Terrain> robTerrains = new ArrayList<>();
            for (Terrain terrain : terrains) {
                ArrayList<Player> robablePlayer = getManagement().getRobber().getRobablePlayer(terrain.getId());
                int tmpRobablePlayer = robablePlayer.size();
                for (Player player : robablePlayer) {
                    if (player == this) {
                        tmpRobablePlayer--;
                    }
                }
                if (tmpRobablePlayer == maxRobablePlayer) {
                    robTerrains.add(terrain);
                }
            }
            if (robTerrains.size() > 0) {
                int tmp = ThreadLocalRandom.current().nextInt(0, robTerrains.size());
                return robTerrains.get(tmp).getId();
            } else {
                int tmp = ThreadLocalRandom.current().nextInt(0, terrains.size());
                return terrains.get(tmp).getId();
            }
        }
        int tmp = 0;
        while (getBoard().getAllTiles().get(getBoard().makeIdTile(tmp)) instanceof Sea || getManagement().getRobber().getPosition().equals(getBoard().makeIdTile(tmp))) {
            tmp = ThreadLocalRandom.current().nextInt(0, 37);
        }
        return terrains.get(tmp).getId();
    }

    /**
     * Calculat a player from whom you want to rob a card
     *
     * @return returns the player you want to rob a card
     * @position position where the robber is
     */
    public int robCard(BitSet position) {
        ArrayList<Player> robablePlayers = getManagement().getRobber().getRobablePlayer(position);
        if (robablePlayers.size() > 0) {
            Player tmpPlayer = robablePlayers.get(0);
            for (Player player : robablePlayers) {
                if (player.getResource() > tmpPlayer.getResource()) {
                    tmpPlayer = player;
                }
            }
            LOGGER.info("Beraubter Spieler: " + tmpPlayer.getName() + ", " + tmpPlayer.getColor());
            return tmpPlayer.getId();
        }
        return -1;
    }

    /**
     * Return the resource with the largest number of cards and subtract one from the number of cards from this resource
     *
     * @param numbers numbers of cards (sequence: BRICK, ORE, WOOL, GRAIN, LUMBER)
     * @return resource
     */
    public Resource largestResourceNumber(HashMap<Resource, Integer> numbers) {
        ArrayList<Resource> resources = new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.ORE, Resource.WOOL, Resource.GRAIN, Resource.LUMBER));
        Resource tmpResource = resources.get(0);
        for (int i = 0; i < resources.size(); i++) {
            if (numbers.get(resources.get(i)) > numbers.get(tmpResource)) {
                tmpResource = resources.get(i);
                LOGGER.info("Ai: Ressourcen, " + resources.get(i) + ": " + getSpecificResource(resources.get(i)));

            }
        }
        LOGGER.info("Ai: Ressourcen, 2: " + tmpResource + ": " + numbers.get(tmpResource));
        numbers.put(tmpResource, numbers.get(tmpResource) - 1);
        LOGGER.info("Ai: Ressourcen, 3: " + tmpResource + ": " + numbers.get(tmpResource));

        return tmpResource;
    }

    /**
     * Returns a resource for the monopoly-card
     *
     * @return resource as a string
     */
    public String monopoly() {
        Resource tmpResource = Resource.LUMBER;
        HashMap<Resource, Integer> resources = new HashMap<>();
        resources.put(Resource.BRICK, getBrick());
        resources.put(Resource.GRAIN, getGrain());
        resources.put(Resource.LUMBER, getLumber());
        resources.put(Resource.ORE, getOre());
        resources.put(Resource.WOOL, getWool());
        for (Resource resource : resources.keySet()) {
            if (resources.get(resource) < resources.get(tmpResource)) {
                tmpResource = resource;
            }
        }
        switch (tmpResource) {
            case LUMBER:
                return "Lumber";
            case BRICK:
                return "Brick";
            case GRAIN:
                return "Grain";
            case ORE:
                return "Ore";
            case WOOL:
                return "Wool";
        }
        return "Lumber";
    }

    /**
     * Returns the first resource for the year-of-planty-card
     *
     * @return resource
     */
    private Resource yearOfPlanty1() {
        Resource tmpResource = Resource.LUMBER;
        HashMap<Resource, Integer> resources = new HashMap<>();
        resources.put(Resource.BRICK, getBrick());
        resources.put(Resource.GRAIN, getGrain());
        resources.put(Resource.LUMBER, getLumber());
        resources.put(Resource.ORE, getOre());
        resources.put(Resource.WOOL, getWool());
        for (Resource resource : resources.keySet()) {
            if (resources.get(resource) < resources.get(tmpResource)) {
                tmpResource = resource;
            }
        }
        return tmpResource;
    }

    /**
     * Returns the second resource for the year-of-planty-card
     *
     * @param resourceGet first resource from the year-of-planty-card
     * @return resource as
     */
    private Resource yearOfPlanty2(Resource resourceGet) {
        Resource tmpResource = Resource.LUMBER;
        HashMap<Resource, Integer> resources = new HashMap<>();
        resources.put(Resource.BRICK, getBrick());
        resources.put(Resource.GRAIN, getGrain());
        resources.put(Resource.LUMBER, getLumber());
        resources.put(Resource.ORE, getOre());
        resources.put(Resource.WOOL, getWool());
        resources.put(resourceGet, resources.get(resourceGet) + 1);
        for (Resource resource : resources.keySet()) {
            if (resources.get(resource) < resources.get(tmpResource)) {
                tmpResource = resource;
            }
        }
        return tmpResource;
    }

    /**
     * Returns the first road for the road-building-card
     *
     * @return road
     */
    private BitSet roadBuilding1() {
        ArrayList<Edge> buildableRoads = getBuildableRoads();
        if (buildableRoads.size() > 0) {
            if (neededResources.size() == 0) {
                int max = 0;
                for (Edge edge : buildableRoads) {
                    if (tryToGetLongestStreet(edge) > max) {
                        max = tryToGetLongestStreet(edge);
                    }
                }
                ArrayList<Edge> maxRoads = new ArrayList<>();
                for (Edge edge : buildableRoads) {
                    if (tryToGetLongestStreet(edge) == max) {
                        maxRoads.add(edge);
                    }
                }
                int tmp = ThreadLocalRandom.current().nextInt(0, maxRoads.size());
                return maxRoads.get(tmp).getBitSetID();
            } else {
                ArrayList<Corner> corners = new ArrayList<>();
                for (Corner corner : getBoard().getAllCorners().values()) {
                    if (corner.getPlayer() == null) {
                        if (distanceRule(corner.getBitSetID())) {
                            for (Tile tile : getBoard().getTileneighborsOfCorner(corner)) {
                                if (tile instanceof Terrain && !(tile instanceof Desert)) {
                                    for (Resource resource : neededResources) {
                                        if (((Terrain) tile).harvest() == resource) {
                                            if (!(corners.contains(corner))) {
                                                corners.add(corner);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ArrayList<Edge> shortestWay = new ArrayList<>();
                for (Corner corner : corners) {
                    ArrayList<Edge> tmp = shortesWay(corner);
                    if (shortestWay.size() == 0) {
                        shortestWay = tmp;
                    } else {
                        if (tmp.size() < shortestWay.size()) {
                            shortestWay = tmp;
                        }
                    }
                }
                for (Edge edge : shortestWay) {
                    if (buildableRoads.contains(edge)) {
                        return edge.getBitSetID();
                    }
                }
            }
        }
        BitSet bs = new BitSet();
        bs.set(1000);
        return bs;
    }

    /**
     * Returns the second road for the road-building-card
     *
     * @param bitSet first road from the road-building-card
     * @return road
     */
    private BitSet roadBuilding2(BitSet bitSet) {
        ArrayList<Edge> edges = getBuildableRoadsRoadBuildingCard(bitSet);
        if (edges.size() > 0) {
            ArrayList<Edge> tmpEdges = new ArrayList<>();
            for (Edge edge : getBoard().getEdgeneighborOfEdge(getBoard().getAllEdges().get(bitSet))) {
                if (edge.getPlayer() == null && edges.contains(edge)) {
                    tmpEdges.add(edge);
                }
            }
            if (tmpEdges.size() > 0) {
                int tmp = ThreadLocalRandom.current().nextInt(0, tmpEdges.size());
                return tmpEdges.get(tmp).getBitSetID();
            } else {
                int tmp = ThreadLocalRandom.current().nextInt(0, edges.size());
                return edges.get(tmp).getBitSetID();
            }
        }
        BitSet bs = new BitSet();
        bs.set(1000);
        return bs;
    }

    /**
     * To slow down the AI
     *
     */
    private void slowDown() {
        if (!c.getCmdLine()) {
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                LOGGER.catching(Level.ERROR, e);
            }
        }
    }
}
