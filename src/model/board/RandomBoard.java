package model.board;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBoard extends Board {

    private static final Logger LOGGER = LogManager.getLogger(RandomBoard.class.getName());
    private List<Terrain> tmpTerrain = new ArrayList<>(19);
    private ArrayList<Integer> numberTokens = new ArrayList<>(37);
    private Map<BitSet, Tile> outerTerrains = new HashMap<>();
    private Tile tmpTile;

    public RandomBoard(boolean harbors) {
        createAllTerrainTiles();
        // Initialize random board (see and terrain tiles)
        getBoard()[0] = new Tile[]{null, null, null, new Sea(), new Sea(), new Sea(), new Sea()};
        getBoard()[1] = new Tile[]{null, null, new Sea(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), new Sea()};
        getBoard()[2] = new Tile[]{null, new Sea(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), new Sea()};
        getBoard()[3] = new Tile[]{new Sea(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), new Sea()};
        getBoard()[4] = new Tile[]{new Sea(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), new Sea(), null};
        getBoard()[5] = new Tile[]{new Sea(), getRandomLandscape(), getRandomLandscape(), getRandomLandscape(), new Sea(), null, null};
        getBoard()[6] = new Tile[]{new Sea(), new Sea(), new Sea(), new Sea(), null, null, null};
        initLists();
        setNumberTokensRandom();
        if (harbors) {
            setHarborsRandom();
        } else {
            setPortsRandom();
        }
    }

    /**
     * Creates all terrain tiles in a list
     */
    private void createAllTerrainTiles() {
        // 4 Forest, Field, Pasture
        for (int i = 0; i < 4; i++) {
            tmpTerrain.add(new Forest());
            tmpTerrain.add(new Field());
            tmpTerrain.add(new Pasture());
        }
        // 3 Mountain, Hill
        for (int i = 0; i < 3; i++) {
            tmpTerrain.add(new Mountain());
            tmpTerrain.add(new Hill());
        }
        // 1 Desert
        tmpTerrain.add(new Desert());
    }

    /**
     * Returns a random landscape
     *
     * @return landscape
     */
    private Terrain getRandomLandscape() {
        int index = ThreadLocalRandom.current().nextInt(0, tmpTerrain.size());
        Terrain terrain = tmpTerrain.get(index);
        tmpTerrain.remove(index);
        return terrain;
    }

    /**
     * set all numbertokens random
     */
    private void setNumberTokensRandom() {
        // 6 edges
        ArrayList<Tile> tmpList = new ArrayList<>();
        for (Tile tile : getAllTiles().values()) {
            if (tile instanceof Terrain) {
                ArrayList<Tile> tmpNeighbors = new ArrayList<>();
                tmpNeighbors.addAll(tile.getNeighbors().values());
                for (int i = 0; i < tmpNeighbors.size(); i++) {
                    if (!(tmpNeighbors.get(i) instanceof Sea)) {
                        tmpNeighbors.remove(i);
                    }
                }
                int counter = 0;
                for (int i = 0; i < tmpNeighbors.size(); i++) {
                    if (tmpNeighbors.get(i) instanceof Sea) {
                        counter++;
                    }
                }
                if (counter == 3) {
                    tmpList.add(tile);
                }
            }
        }
        // remove all sea tiles
        for (Tile tile : getAllTiles().values()) {
            if (!(tile instanceof Sea)) {
                outerTerrains.put(tile.getId(), tile);
            }
        }
        int index = ThreadLocalRandom.current().nextInt(0, tmpList.size());
        // random startScreen edge
        tmpTile = tmpList.get(index);
        // remove startTile
        outerTerrains.remove(tmpTile.getId());
        // remove inner tiles
        outerTerrains.remove(makeIdTile(11));
        outerTerrains.remove(makeIdTile(12));
        outerTerrains.remove(makeIdTile(17));
        outerTerrains.remove(makeIdTile(18));
        outerTerrains.remove(makeIdTile(19));
        outerTerrains.remove(makeIdTile(24));
        outerTerrains.remove(makeIdTile(25));
        // alphabetical order of number tokens
        numberTokens = new ArrayList<>(18);
        numberTokens.addAll(Arrays.asList(5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11));
        if (!(tmpTile instanceof Desert)) {
            ((Terrain) tmpTile).setNumbertoken(numberTokens.get(0));
            numberTokens.remove(0);
        }
        outerTerrains.remove(tmpTile.getId());
        String pos = "";
        if (tmpTile.getId().equals(makeIdTile(5)) || tmpTile.getId().equals(makeIdTile(16)) || tmpTile.getId().equals(makeIdTile(29))) {
            pos = "WEST";
        } else {
            pos = "EAST";
        }
//        System.out.println(outerTerrains);
        String all = "\n outerTerrains :\n" + outerTerrains;
        LOGGER.info(all);
        counterClock(tmpTile, pos);
        outerTerrains.clear();
        outerTerrains.put(getAllTiles().get(makeIdTile(11)).getId(), getAllTiles().get(makeIdTile(11)));
        outerTerrains.put(getAllTiles().get(makeIdTile(17)).getId(), getAllTiles().get(makeIdTile(17)));
        outerTerrains.put(getAllTiles().get(makeIdTile(24)).getId(), getAllTiles().get(makeIdTile(24)));
        outerTerrains.put(getAllTiles().get(makeIdTile(25)).getId(), getAllTiles().get(makeIdTile(25)));
        outerTerrains.put(getAllTiles().get(makeIdTile(19)).getId(), getAllTiles().get(makeIdTile(19)));
        outerTerrains.put(getAllTiles().get(makeIdTile(12)).getId(), getAllTiles().get(makeIdTile(12)));
        if (tmpTile.getId().equals(makeIdTile(10)) || tmpTile.getId().equals(makeIdTile(13)) || tmpTile.getId().equals(makeIdTile(6))) {
            pos = "WEST";
        } else {
            pos = "EAST";
        }
        counterClock(tmpTile, pos);
        outerTerrains.clear();
        outerTerrains.put(getAllTiles().get(makeIdTile(18)).getId(), getAllTiles().get(makeIdTile(18)));
        counterClock(tmpTile, pos);
    }

    /**
     * Set all ports random
     */
    private void setHarborsRandom() {
        ArrayList<PortType> portTypes = new ArrayList<>(Arrays.asList(PortType.BRICK, PortType.GRAIN, PortType.LUMBER, PortType.ORE, PortType.WOOL, PortType.GENERIC, PortType.GENERIC, PortType.GENERIC, PortType.GENERIC));
        ArrayList<Integer> seatiles = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 8, 14, 21, 27, 32, 36, 35, 34, 33, 28, 22, 15, 9, 4));
        int start = ThreadLocalRandom.current().nextInt(0, 2);
        if (start == 0) {
            setHarbors(seatiles, portTypes, 0);
        } else {
            setHarbors(seatiles, portTypes, 1);
        }
    }

    /**
     * Helpmethod for setHarborsRandom
     *
     * @param seatiles  list with sea tiles
     * @param portTypes list with portTypes
     * @param index     0, if start at the first tile; 1, if start at the second tile
     */
    private void setHarbors(ArrayList<Integer> seatiles, ArrayList<PortType> portTypes, int index) {
        for (int i = 0; i < seatiles.size(); i++) {
            if (i % 2 == index) {
                int port = ThreadLocalRandom.current().nextInt(0, portTypes.size());
                ((Sea) getAllTiles().get(makeIdTile(seatiles.get(i)))).setPort(portTypes.get(port));
                ArrayList<Corner> corners = new ArrayList<>();
                for (Corner corner : getCornerneighborsOfTile(getAllTiles().get(makeIdTile(seatiles.get(i))))) {
                    corners.add(corner);
                }
                if (corners.size() == 2) {
                    for (Corner corner : corners) {
                        corner.setPortType(portTypes.get(port));
                    }
                } else {
                    BitSet tmp1 = new BitSet();
                    BitSet tmp2 = new BitSet();
                    BitSet tmp3 = new BitSet();
                    tmp1.or(corners.get(0).getBitSetID());
                    tmp1.and(corners.get(1).getBitSetID());
                    tmp2.or(corners.get(0).getBitSetID());
                    tmp2.and(corners.get(2).getBitSetID());
                    tmp3.or(corners.get(1).getBitSetID());
                    tmp3.and(corners.get(2).getBitSetID());
                    tmp1.or(tmp2);
                    tmp1.or(tmp3);
                    Corner delete = new Corner(new Sea(), new Sea(), new Sea());
                    for (Corner corner : corners) {
                        if (corner.getBitSetID().equals(tmp1)) {
                            corner.setPortType(portTypes.get(port));
                            delete = corner;
                        }
                    }
                    corners.remove(delete);
                    int tmp = ThreadLocalRandom.current().nextInt(0, 2);
                    corners.get(tmp).setPortType(portTypes.get(port));
                }
                portTypes.remove(port);
            }
        }
    }

    /**
     * set all porttiles/ports random
     */
    private void setPortsRandom() {
        int index;
        ArrayList<ArrayList<PortType>> allPortTiles = new ArrayList<>(6);
        ArrayList<PortType> portTileA = new ArrayList<>(3);
        ArrayList<PortType> portTileB = new ArrayList<>(3);
        ArrayList<PortType> portTileC = new ArrayList<>(3);
        ArrayList<PortType> portTileD = new ArrayList<>(3);
        ArrayList<PortType> portTileE = new ArrayList<>(3);
        ArrayList<PortType> portTileF = new ArrayList<>(3);
        portTileA.addAll(Arrays.asList(PortType.GENERIC, null, PortType.GRAIN));
        portTileB.addAll(Arrays.asList(null, PortType.ORE, null));
        portTileC.addAll(Arrays.asList(PortType.GENERIC, null, PortType.WOOL));
        portTileD.addAll(Arrays.asList(null, PortType.GENERIC, null));
        portTileE.addAll(Arrays.asList(PortType.GENERIC, null, PortType.BRICK));
        portTileF.addAll(Arrays.asList(null, PortType.LUMBER, null));
        allPortTiles.add(portTileA);
        allPortTiles.add(portTileB);
        allPortTiles.add(portTileC);
        allPortTiles.add(portTileD);
        allPortTiles.add(portTileE);
        allPortTiles.add(portTileF);
        index = ThreadLocalRandom.current().nextInt(0, allPortTiles.size());
        setPorts(0, 1, 2, allPortTiles.get(index));
        allPortTiles.remove(index);
        index = ThreadLocalRandom.current().nextInt(0, allPortTiles.size());
        setPorts(3, 8, 14, allPortTiles.get(index));
        allPortTiles.remove(index);
        index = ThreadLocalRandom.current().nextInt(0, allPortTiles.size());
        setPorts(21, 27, 32, allPortTiles.get(index));
        allPortTiles.remove(index);
        index = ThreadLocalRandom.current().nextInt(0, allPortTiles.size());
        setPorts(36, 35, 34, allPortTiles.get(index));
        allPortTiles.remove(index);
        index = ThreadLocalRandom.current().nextInt(0, allPortTiles.size());
        setPorts(33, 28, 22, allPortTiles.get(index));
        allPortTiles.remove(index);
        index = 0;
        setPorts(15, 9, 4, allPortTiles.get(index));
        allPortTiles.remove(index);
    }

    /**
     * Set all ports at the specific porttile
     *
     * @param a        left tile of the porttile
     * @param b        middle tile of the porttile
     * @param c        right tile of the porttile
     * @param portTile specific porttile
     */
    private void setPorts(int a, int b, int c, ArrayList<PortType> portTile) {
        if (portTile.get(0) == null) {
            ((Sea) (getAllTiles().get(makeIdTile(b)))).setPort(portTile.get(1));
            if ((getAllTiles().get(makeIdTile(b))).getNeighbors().containsKey(makeIdTile(6))) {
                getAllCorners().get(makeIdCorner(1, 5, 6)).setPortType(portTile.get(1));
                getAllCorners().get(makeIdCorner(1, 2, 6)).setPortType(portTile.get(1));
            } else if ((getAllTiles().get(makeIdTile(b))).getNeighbors().containsKey(makeIdTile(13))) {
                getAllCorners().get(makeIdCorner(7, 8, 13)).setPortType(portTile.get(1));
                getAllCorners().get(makeIdCorner(8, 13, 14)).setPortType(portTile.get(1));
            } else if ((getAllTiles().get(makeIdTile(b))).getNeighbors().containsKey(makeIdTile(26))) {
                getAllCorners().get(makeIdCorner(20, 26, 27)).setPortType(portTile.get(1));
                getAllCorners().get(makeIdCorner(26, 27, 32)).setPortType(portTile.get(1));
            } else if ((getAllTiles().get(makeIdTile(b))).getNeighbors().containsKey(makeIdTile(30))) {
                getAllCorners().get(makeIdCorner(30, 31, 35)).setPortType(portTile.get(1));
                getAllCorners().get(makeIdCorner(30, 34, 35)).setPortType(portTile.get(1));
            } else if ((getAllTiles().get(makeIdTile(b))).getNeighbors().containsKey(makeIdTile(23))) {
                getAllCorners().get(makeIdCorner(23, 28, 29)).setPortType(portTile.get(1));
                getAllCorners().get(makeIdCorner(22, 23, 28)).setPortType(portTile.get(1));
            } else if ((getAllTiles().get(makeIdTile(b))).getNeighbors().containsKey(makeIdTile(10))) {
                getAllCorners().get(makeIdCorner(4, 9, 10)).setPortType(portTile.get(1));
                getAllCorners().get(makeIdCorner(9, 10, 16)).setPortType(portTile.get(1));
            }
        } else {
            ((Sea) (getAllTiles().get(makeIdTile(a)))).setPort(portTile.get(0));
            ((Sea) (getAllTiles().get(makeIdTile(c)))).setPort(portTile.get(2));
            if (a == 0) {
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(0, 5))).get(0).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(0, 5))).get(1).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(2, 6))).get(0).setPortType(portTile.get(2));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(2, 6))).get(1).setPortType(portTile.get(2));
            } else if (a == 3) {
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(3, 7))).get(0).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(3, 7))).get(1).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(14, 13))).get(0).setPortType(portTile.get(2));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(14, 13))).get(1).setPortType(portTile.get(2));
            } else if (a == 21) {
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(21, 20))).get(0).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(21, 20))).get(1).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(26, 32))).get(0).setPortType(portTile.get(2));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(26, 32))).get(1).setPortType(portTile.get(2));
            } else if (a == 36) {
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(31, 36))).get(0).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(31, 36))).get(1).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(30, 34))).get(0).setPortType(portTile.get(2));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(30, 34))).get(1).setPortType(portTile.get(2));
            } else if (a == 33) {
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(29, 33))).get(0).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(29, 33))).get(1).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(22, 23))).get(0).setPortType(portTile.get(2));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(22, 23))).get(1).setPortType(portTile.get(2));
            }
            if (a == 15) {
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(16, 15))).get(0).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(16, 15))).get(1).setPortType(portTile.get(0));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(4, 10))).get(0).setPortType(portTile.get(2));
                getCornerneighborsOfEdge(getAllEdges().get(makeIdEdge(4, 10))).get(1).setPortType(portTile.get(2));
            }
        }
    }

    /**
     * Set numbertokens counter-clock from a specific tile
     *
     * @param tmp specific tile
     * @param pos position of the specific tile (either "WEST" or "EAST")
     */
    private void counterClock(Tile tmp, String pos) {
        // get board coordinates
        int xCo = getSpecificTileCo(tmp)[0];
        int yCo = getSpecificTileCo(tmp)[1];

        if (outerTerrains.size() != 0) {
            if (pos.equals("WEST")) {
                // WEST
                if (xCo - 1 >= -3 && xCo - 1 <= 3 && this.getSpecificTile(xCo - 1, yCo) != null && outerTerrains.containsKey(getSpecificTile(xCo - 1, yCo).getId())) {
                    tmp = this.getSpecificTile(xCo - 1, yCo);
                }
                // SOUTH WEST
                else if (xCo - 1 >= -3 && xCo - 1 <= 3 && yCo + 1 >= -3 && yCo + 1 <= 3 && this.getSpecificTile(xCo - 1, yCo + 1) != null && outerTerrains.containsKey(getSpecificTile(xCo - 1, yCo + 1).getId())) {
                    tmp = this.getSpecificTile(xCo - 1, yCo + 1);
                }
                // SOUTH EAST
                else if (yCo + 1 >= -3 && yCo + 1 <= 3 && this.getSpecificTile(xCo, yCo + 1) != null && outerTerrains.containsKey(getSpecificTile(xCo, yCo + 1).getId())) {
                    tmp = this.getSpecificTile(xCo, yCo + 1);
                }
                // EAST
                else if (xCo + 1 >= -3 && xCo + 1 <= 3 && this.getSpecificTile(xCo + 1, yCo) != null && outerTerrains.containsKey(getSpecificTile(xCo + 1, yCo).getId())) {
                    tmp = this.getSpecificTile(xCo + 1, yCo);
                }
                // NORTH EAST
                else if (xCo + 1 >= -3 && xCo + 1 <= 3 && yCo - 1 >= -3 && yCo - 1 <= 3 && this.getSpecificTile(xCo + 1, yCo - 1) != null && outerTerrains.containsKey(getSpecificTile(xCo + 1, yCo - 1).getId())) {
                    tmp = this.getSpecificTile(xCo + 1, yCo - 1);
                }
                // NORTH WEST
                else if (yCo - 1 >= -3 && yCo - 1 <= 3 && this.getSpecificTile(xCo, yCo - 1) != null && outerTerrains.containsKey(getSpecificTile(xCo, yCo - 1).getId())) {
                    tmp = this.getSpecificTile(xCo, yCo - 1);
                }
            }
            if (pos.equals("EAST")) {
                // EAST
                if (xCo + 1 >= -3 && xCo + 1 <= 3 && this.getSpecificTile(xCo + 1, yCo) != null && outerTerrains.containsKey(getSpecificTile(xCo + 1, yCo).getId())) {
                    tmp = this.getSpecificTile(xCo + 1, yCo);
                }
                // NORTH EAST
                else if (xCo + 1 >= -3 && xCo + 1 <= 3 && yCo - 1 >= -3 && yCo - 1 <= 3 && this.getSpecificTile(xCo + 1, yCo - 1) != null && outerTerrains.containsKey(getSpecificTile(xCo + 1, yCo - 1).getId())) {
                    tmp = this.getSpecificTile(xCo + 1, yCo - 1);
                }
                // NORTH WEST
                else if (yCo - 1 >= -3 && yCo - 1 <= 3 && this.getSpecificTile(xCo, yCo - 1) != null && outerTerrains.containsKey(getSpecificTile(xCo, yCo - 1).getId())) {
                    tmp = this.getSpecificTile(xCo, yCo - 1);
                }
                // WEST
                else if (xCo - 1 >= -3 && xCo - 1 <= 3 && this.getSpecificTile(xCo - 1, yCo) != null && outerTerrains.containsKey(getSpecificTile(xCo - 1, yCo).getId())) {
                    tmp = this.getSpecificTile(xCo - 1, yCo);
                }
                // SOUTH WEST
                else if (xCo - 1 >= -3 && xCo - 1 <= 3 && yCo + 1 >= -3 && yCo + 1 <= 3 && this.getSpecificTile(xCo - 1, yCo + 1) != null && outerTerrains.containsKey(getSpecificTile(xCo - 1, yCo + 1).getId())) {
                    tmp = this.getSpecificTile(xCo - 1, yCo + 1);
                }
                // SOUTH EAST
                else if (yCo + 1 >= -3 && yCo + 1 <= 3 && this.getSpecificTile(xCo, yCo + 1) != null && outerTerrains.containsKey(getSpecificTile(xCo, yCo + 1).getId())) {
                    tmp = this.getSpecificTile(xCo, yCo + 1);
                }
            }
            try {
                if (!(tmp instanceof Desert)) {
                    ((Terrain) getAllTiles().get(tmp.getId())).setNumbertoken(numberTokens.get(0));
                    numberTokens.remove(0);
                }

            } catch (IndexOutOfBoundsException ex) {
                LOGGER.catching(Level.ERROR, ex);
            }
            outerTerrains.remove(tmp.getId());
            counterClock(tmp, pos);
        } else {
            tmpTile = tmp;
        }
    }
}


