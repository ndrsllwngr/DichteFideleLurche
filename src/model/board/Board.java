package model.board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public abstract class Board {
    private static final Logger LOGGER = LogManager.getLogger(Board.class.getName());
    private Tile[][] plainBoard = new Tile[7][7];
    private Map<BitSet, Tile> allTiles = new HashMap<>();
    private Map<BitSet, Edge> allEdges = new HashMap<>();
    private Map<BitSet, Corner> allCorners = new HashMap<>();

    /**
     * Initialize lists with all edges, corners and tiles and set Ids and neighbors
     *
     */
    void initLists() {
        // Set IDs and neighbors
        int tmp = 0;
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (getBoard()[y][x] != null) {
                    getBoard()[y][x].setId(tmp);
                    tmp = tmp + 1;
                }
            }
        }
        // Create neighbor map
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (getBoard()[y][x] != null) {
                    getBoard()[y][x].setNeighbor(this);
                }
            }
        }
        // Create tile map
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (getBoard()[y][x] != null) {
                    getAllTiles().put(getBoard()[y][x].getId(), getBoard()[y][x]);
                }
            }
        }
        // Create edge map
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (getBoard()[y][x] != null) {
                    // Tile ID
                    BitSet tmpID = getBoard()[y][x].getId();
                    // Neighbors of tile
                    Map<BitSet, Tile> tmpList = getBoard()[y][x].getNeighbors();
                    if (getBoard()[y][x] instanceof Sea) {
                        for (Tile aTmpList : tmpList.values()) {
                            if (aTmpList instanceof Terrain) {
                                //BitSet aTmpID = aTmpList.getId();
                                BitSet aTmpID = new BitSet(37);
                                aTmpID.or(aTmpList.getId());
                                aTmpID.or(tmpID);
                                if (getAllEdges().containsKey(aTmpID)) {
                                    getBoard()[y][x].setEdge(getAllEdges().get(aTmpID));
                                } else {
                                    Edge tmpEdge = new Edge(getBoard()[y][x], aTmpList);
                                    getAllEdges().put(tmpEdge.getBitSetID(), tmpEdge);
                                    getBoard()[y][x].setEdge(getAllEdges().get(tmpEdge.getBitSetID()));
                                }
                            }
                        }
                    } else {
                        for (Tile aTmpList : tmpList.values()) {
                            BitSet aTmpID = new BitSet(37);
                            aTmpID.or(aTmpList.getId());
                            aTmpID.or(tmpID);
                            if (getAllEdges().containsKey(aTmpID)) {
                                getBoard()[y][x].setEdge(getAllEdges().get(aTmpID));
                            } else {
                                Edge tmpEdge = new Edge(getBoard()[y][x], aTmpList);
                                getAllEdges().put(tmpEdge.getBitSetID(), tmpEdge);
                                getBoard()[y][x].setEdge(getAllEdges().get(tmpEdge.getBitSetID()));
                            }
                        }
                    }
                }
            }
        }
        // Create corner map
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (getBoard()[y][x] != null) {
                    // Tile ID
                    BitSet tmpID = getBoard()[y][x].getId();
                    // Neighbors of tile
                    Map<BitSet, Tile> tmpList = getBoard()[y][x].getNeighbors();
                    for (Tile aTmpList : tmpList.values()) {
                        Map<BitSet, Tile> compareTmpList = aTmpList.getNeighbors();
                        for (Tile bTmpList : tmpList.values()) {
                            for (Tile cTmpList : compareTmpList.values()) {
                                if (bTmpList == cTmpList) {
                                    BitSet aTmpID = new BitSet(37);
                                    aTmpID.or(aTmpList.getId());
                                    aTmpID.or(tmpID);
                                    aTmpID.or(bTmpList.getId());
                                    if (getAllCorners().containsKey(aTmpID)) {
                                        getBoard()[y][x].setCorner(getAllCorners().get(aTmpID));
                                    } else {
                                        Corner tmpCorner = new Corner(getBoard()[y][x], aTmpList, bTmpList);
                                        getAllCorners().put(tmpCorner.getBitSetID(), tmpCorner);
                                        getBoard()[y][x].setCorner(getAllCorners().get(tmpCorner.getBitSetID()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the specific tile with the coordinates x and y
     *
     * @param x x-coordinate (-3 <= x <= 3)
     * @param y y-coordinate (-3 <= y <= 3)
     * @return specific tile
     */
    public Tile getSpecificTile(int x, int y) {
        int newX = 3 + x;
        int newY = 3 + y;
        if (newX >= 0 && newX <= 6 && newY >= 0 && newY <= 6) {
            return plainBoard[newY][newX];
        }
        return null;
    }

    /**
     * Returns an array with the coordinates of a specific tile
     *
     * @param tile specific tile
     * @return x-coordinat at the first position and the y-cooridnate at the second position of the array
     */
    public int[] getSpecificTileCo(Tile tile) {
        int xCo, yCo;
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (plainBoard[y][x] != null) {
                    if (plainBoard[y][x].getId().equals(tile.getId())) {
                        xCo = x - 3;
                        yCo = y - 3;
                        return new int[]{xCo, yCo};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get a map of all neighbors of a specific tile
     *
     * @param tile specific tile
     * @return map of all neighbors
     */
    public Map<BitSet, Tile> getNeighbors(Tile tile) {
        final int xCo = this.getSpecificTileCo(tile)[0];
        final int yCo = this.getSpecificTileCo(tile)[1];
        Map<BitSet, Tile> neighborTiles = new HashMap<>();
        // NORTH WEST
        if (yCo - 1 >= -3 && yCo - 1 <= 3 && this.getSpecificTile(xCo, yCo - 1) != null) {
            Tile tmp1 = this.getSpecificTile(xCo, yCo - 1);
            neighborTiles.put(tmp1.getId(), tmp1);
        }
        // NORTH EAST
        if (xCo + 1 >= -3 && xCo + 1 <= 3 && yCo - 1 >= -3 && yCo - 1 <= 3 && this.getSpecificTile(xCo + 1, yCo - 1) != null) {
            Tile tmp2 = this.getSpecificTile(xCo + 1, yCo - 1);
            neighborTiles.put(tmp2.getId(), tmp2);
        }
        // EAST
        if (xCo + 1 >= -3 && xCo + 1 <= 3 && this.getSpecificTile(xCo + 1, yCo) != null) {
            Tile tmp3 = this.getSpecificTile(xCo + 1, yCo);
            neighborTiles.put(tmp3.getId(), tmp3);
        }
        // SOUTH EAST
        if (yCo + 1 >= -3 && yCo + 1 <= 3 && this.getSpecificTile(xCo, yCo + 1) != null) {
            Tile tmp4 = this.getSpecificTile(xCo, yCo + 1);
            neighborTiles.put(tmp4.getId(), tmp4);
        }
        // SOUTH WEST
        if (xCo - 1 >= -3 && xCo - 1 <= 3 && yCo + 1 >= -3 && yCo + 1 <= 3 && this.getSpecificTile(xCo - 1, yCo + 1) != null) {
            Tile tmp5 = this.getSpecificTile(xCo - 1, yCo + 1);
            neighborTiles.put(tmp5.getId(), tmp5);
        }
        // WEST
        if (xCo - 1 >= -3 && xCo - 1 <= 3 && this.getSpecificTile(xCo - 1, yCo) != null) {
            Tile tmp6 = this.getSpecificTile(xCo - 1, yCo);
            neighborTiles.put(tmp6.getId(), tmp6);
        }
        return neighborTiles;
    }

    /**
     * Makes a Bitset-Id for a tile with integer-Id
     *
     * @param i integer-Id
     * @return Bitset-Id
     */
    public BitSet makeIdTile(int i) {
        BitSet id = new BitSet();
        id.flip(i);
        return id;
    }

    /**
     * Makes a Bitset-Id for a edge with tile-neighbors with specific integer-Id
     *
     * @param i integer-Id from first tile
     * @param j integer-Id from second tile
     * @return Bitset-Id
     */
    public BitSet makeIdEdge(int i, int j) {
        BitSet id = new BitSet();
        id.flip(i);
        id.flip(j);
        return id;
    }

    /**
     * Makes a Bitset-Id for a corner with tile-neighbors with specific integer-Id
     *
     * @param i integer-Id from first tile
     * @param j integer-Id from second tile
     * @param k integer-Id form third tile
     * @return Bitset-Id
     */
    public BitSet makeIdCorner(int i, int j, int k) {
        BitSet id = new BitSet();
        id.flip(i);
        id.flip(j);
        id.flip(k);
        return id;
    }

    // Print help methods

    /**
     * Print integer-Ids from all tiles
     */
    public void printBoardIDs() {
        String all = "\n";
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (getBoard()[y][x] == null) {
                    all = all + String.format("%6s", "null");
                } else {
                    all = all + String.format("%6s", getBoard()[y][x].getId());
                }
            }
            all = all + "\n";
        }
        LOGGER.info(all);
    }

    /**
     * Print coordinates from all tiles
     */
    public void printBoardSpecialCo() {
        String all = "\n";
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (getBoard()[y][x] == null) {
                    all = all + "(     )";
                } else {
                    all = all + String.format("(%2d,%2d)", this.getSpecificTileCo(getBoard()[y][x])[0], this.getSpecificTileCo(getBoard()[y][x])[1]);
                }
            }
            all = all + "\n";
        }
        LOGGER.info(all);
    }

    /**
     * Print all neighbors from all tiles
     */
    public void printTileNeighbors() {
        String all = "\n";
        for (int y = 0; y < this.getBoardYLength(); y++) {
            all = all + "Row: " + y + "\n";
            for (int x = 0; x < this.getBoardXLength(); x++) {
                all = all + String.format("%30s : ", getBoard()[y][x]);
                if (getBoard()[y][x] == null) {
                    all = all + "\n";
                } else {
                    all = all + getBoard()[y][x].getNeighbors().values();
                    all = all + "\n";
                }
            }
        }
        LOGGER.info(all);
    }

    /**
     * Print all tiles/corners, which are ports, with their specific port
     */
    public void printPorts() {
        String all = "\n";
        for (Tile tile : getAllTiles().values()) {
            if (tile instanceof Sea) {
                if (((Sea) tile).getPort() != null) {
                    all = all + String.format("%12s : ", tile.getId()) + ((Sea) tile).getPort() + "\n";
                }
            }
        }
        all = all + "\n";
        for (Corner corner : getAllCorners().values()) {
            if (corner.getPortType() != null) {
                all = all + String.format("%12s : ", corner.getBitSetID()) + corner.getPortType() + "\n";
            }
        }
        LOGGER.info(all);
    }

    /**
     * Print numbertokens from all tiles
     */
    public void printNumberTokens() {
        String all = "\n";
        for (int y = 0; y < this.getBoardYLength(); y++) {
            for (int x = 0; x < this.getBoardXLength(); x++) {
                if (getBoard()[y][x] == null || getBoard()[y][x] instanceof Sea) {
                    all = all + String.format("%6s", "null");
                } else {
                    all = all + String.format("%6s", ((Terrain) getBoard()[y][x]).getNumberToken());
                }
            }
            all = all + "\n";
        }
        LOGGER.info(all);
    }

    // Neighborly calculations

    /**
     * Returns a list of all corners, which are neighbors from a specific corner
     *
     * @param corner specific corner
     * @return list of corner-neighbors
     */
    public ArrayList<Corner> getCornerneighborsOfCorner(Corner corner) {
        ArrayList<Corner> corners = new ArrayList<>();
        for (Edge edge : getEdgeneighborsOfCorner(corner)) {
            for (Corner cornertmp : getCornerneighborsOfEdge(edge)) {
                if (corner != cornertmp) {
                    corners.add(cornertmp);
                }
            }
        }
        return corners;
    }

    /**
     * Returns a list of all edges, which are neighbors from a specific edge
     *
     * @param edge specific edge
     * @return list of edge-neighbors
     */
    public ArrayList<Edge> getEdgeneighborOfEdge(Edge edge) {
        ArrayList<Edge> edges = new ArrayList<>();
        for (Corner corner : getCornerneighborsOfEdge(edge)) {
            for (Edge edgetmp : getEdgeneighborsOfCorner(corner)) {
                if (edge != edgetmp) {
                    edges.add(edgetmp);
                }
            }
        }
        return edges;
    }

    /**
     * Returns a list of all tiles, which are neighbors from a specific corner
     *
     * @param corner specific corner
     * @return list of tile-neighbors
     */
    public ArrayList<Tile> getTileneighborsOfCorner(Corner corner) {
        ArrayList<Tile> tiles = new ArrayList<>();
        int i = corner.getBitSetID().nextSetBit(0);
        int j = corner.getBitSetID().nextSetBit(i + 1);
        int k = corner.getBitSetID().nextSetBit(j + 1);
        tiles.add(allTiles.get(makeIdTile(i)));
        tiles.add(allTiles.get(makeIdTile(j)));
        tiles.add(allTiles.get(makeIdTile(k)));
        return tiles;
    }

    /**
     * Returns a list of all corners, which are neighbors from a specific tile
     *
     * @param tile specific tile
     * @return list of corner-neighbors
     */
    public ArrayList<Corner> getCornerneighborsOfTile(Tile tile) {
        ArrayList<Corner> corners = new ArrayList<>();
        int i = tile.getId().nextSetBit(0);
        for (Corner corner : allCorners.values()) {
            if (corner.getBitSetID().get(i)) {
                corners.add(corner);
            }
        }
        return corners;
    }

    /**
     * Returns a list of all corners, which are neighbors from a specific edge
     *
     * @param edge specific edge
     * @return list of corner-neighbors
     */
    public ArrayList<Corner> getCornerneighborsOfEdge(Edge edge) {
        ArrayList<Corner> corners = new ArrayList<>();
        int i = edge.getBitSetID().nextSetBit(0);
        int j = edge.getBitSetID().nextSetBit(i + 1);
        for (Corner corner : allCorners.values()) {
            if (corner.getBitSetID().get(i) && corner.getBitSetID().get(j)) {
                corners.add(corner);
            }
        }
        return corners;
    }

    /**
     * Returns a list of all edges, which are neighbors from a specific corner
     *
     * @param corner specific corner
     * @return list of edge-neighbors
     */
    public ArrayList<Edge> getEdgeneighborsOfCorner(Corner corner) {
        ArrayList<Edge> edges = new ArrayList<>();
        int i = corner.getBitSetID().nextSetBit(0);
        int j = corner.getBitSetID().nextSetBit(i + 1);
        int k = corner.getBitSetID().nextSetBit(j + 1);
        if (allEdges.containsKey(makeIdEdge(i, j))) {
            edges.add(allEdges.get(makeIdEdge(i, j)));
        }
        if (allEdges.containsKey(makeIdEdge(i, k))) {
            edges.add(allEdges.get(makeIdEdge(i, k)));
        }
        if (allEdges.containsKey(makeIdEdge(j, k))) {
            edges.add(allEdges.get(makeIdEdge(j, k)));
        }
        return edges;
    }

    /**
     * Returns a BitSet of the terrain-tile, which is next to a specific sea-tile with harbor and next to the corners with harbor
     *
     * @param bitSet bitSet from a specific sea-tile
     * @return bitSet from the terrain-tile
     */
    public BitSet getTerrainneighborOfSeaharbor(BitSet bitSet) {
        ArrayList<Corner> corners = getCornerneighborsOfTile(allTiles.get(bitSet));
        ArrayList<Corner> cornersTmp = getCornerneighborsOfTile(allTiles.get(bitSet));
        for (Corner corner : corners) {
            if (corner.getPortType() == null) {
                cornersTmp.remove(corner);
            }
        }
        BitSet terrain = new BitSet();
        terrain.or(cornersTmp.get(0).getBitSetID());
        terrain.and(cornersTmp.get(1).getBitSetID());
        terrain.xor(bitSet);
        return terrain;

    }

    /**
     * Returns length of a part of a street starting from a specific edge in a specific direction
     *
     * @param edge             specific edge
     * @param corner           specify the direction
     * @param edgesOfPlayer    list with edges of the specific player, which have not been counted yet
     * @param edgesOfPlayertmp list with edges of the specific player, which have not been counted yet in the specific direction
     * @return length of a part of a street
     */
    public int getStreetLengthPart(Edge edge, Corner corner, ArrayList<Edge> edgesOfPlayer, ArrayList<Edge> edgesOfPlayertmp) {
        edgesOfPlayer.remove(edge);
        edgesOfPlayertmp.remove(edge);
        if (edge.getPlayer() == null) {
            return 0;
        } else if (corner.getPlayer() != edge.getPlayer() && corner.getPlayer() != null) {
            return 1;
        } else {
            ArrayList<Edge> edges = getEdgeneighborsOfCorner(corner);
            for (int i = 0; i < edges.size(); ) {
                Edge edgeneighbor = edges.get(i);
                if (edgeneighbor.getPlayer() != edge.getPlayer() || edgeneighbor == edge || !edgesOfPlayertmp.contains(edgeneighbor)) {
                    edges.remove(edgeneighbor);
                } else {
                    i++;
                }
            }
            int countPart;
            if (edges.size() == 2) {
                Corner corner0;
                if (getCornerneighborsOfEdge(edges.get(0)).get(0) == corner) {
                    corner0 = getCornerneighborsOfEdge(edges.get(0)).get(1);
                } else {
                    corner0 = getCornerneighborsOfEdge(edges.get(0)).get(0);
                }
                Corner corner1;
                if (getCornerneighborsOfEdge(edges.get(1)).get(0) == corner) {
                    corner1 = getCornerneighborsOfEdge(edges.get(1)).get(1);
                } else {
                    corner1 = getCornerneighborsOfEdge(edges.get(1)).get(0);
                }
                ArrayList<Edge> edgesOfPlayertmp0 = new ArrayList<>();
                ArrayList<Edge> edgesOfPlayertmp1 = new ArrayList<>();
                for (int i = 0; i < edgesOfPlayertmp.size(); i++) {
                    edgesOfPlayertmp0.add(edgesOfPlayertmp.get(i));
                    edgesOfPlayertmp1.add(edgesOfPlayertmp.get(i));
                }
                countPart = 1 + Math.max(getStreetLengthPart(edges.get(0), corner0, edgesOfPlayer, edgesOfPlayertmp0), getStreetLengthPart(edges.get(1), corner1, edgesOfPlayer, edgesOfPlayertmp1));
            } else if (edges.size() == 1) {
                Corner corner0;
                if (getCornerneighborsOfEdge(edges.get(0)).get(0) == corner) {
                    corner0 = getCornerneighborsOfEdge(edges.get(0)).get(1);
                } else {
                    corner0 = getCornerneighborsOfEdge(edges.get(0)).get(0);
                }
                countPart = 1 + getStreetLengthPart(edges.get(0), corner0, edgesOfPlayer, edgesOfPlayertmp);
            } else {
                countPart = 1;
            }
            return countPart;
        }
    }

    /**
     * Returns the length of the street starting from a specific edge
     *
     * @param edge specific edge
     * @return length of street
     */
    public int getStreetLength(Edge edge) {
        ArrayList<Edge> edgesOfPlayer = new ArrayList<>();
        for (Edge edgePlayer : allEdges.values()) {
            if (edgePlayer.getPlayer() == edge.getPlayer()) {
                edgesOfPlayer.add(edgePlayer);
            }
        }
        int counter = -1;

        int countLeft = getStreetLengthPart(edge, getCornerneighborsOfEdge(edge).get(0), edgesOfPlayer, edgesOfPlayer);
        int countRight = getStreetLengthPart(edge, getCornerneighborsOfEdge(edge).get(1), edgesOfPlayer, edgesOfPlayer);

        counter = counter + countLeft + countRight;
        return counter;
    }

    /**
     * Returns the length of the longest street from a specific player
     *
     * @param playerID specific player
     * @return length of longest street
     */
    //public int getLongestStreet(Player player) {
    public int getLongestStreet(int playerID) {
        int tmp = 0;
        for (Edge edge : allEdges.values()) {
            if (edge.getPlayer() != null) {
                if (edge.getPlayer().getId() == playerID) {
                    tmp = Math.max(tmp, getStreetLength(edge));
                }
            }
        }
        return tmp;
    }

    /**
     * Return the array-board
     *
     * @return board
     */
    public Tile[][] getBoard() {
        return plainBoard;
    }

    /**
     * Return the x-length of the board
     *
     * @return x-length
     */
    public int getBoardXLength() {
        return plainBoard[0].length;
    }

    /**
     * Return the y-length of the board
     *
     * @return y-length
     */
    public int getBoardYLength() {
        return plainBoard.length;
    }

    /**
     * Return a map of all tiles
     *
     * @return map of all tiles
     */
    public Map<BitSet, Tile> getAllTiles() {
        return allTiles;
    }

    /**
     * Return a map of all edges
     *
     * @return map of all edges
     */
    public Map<BitSet, Edge> getAllEdges() {
        return allEdges;
    }

    /**
     * Return a map of all corners
     *
     * @return map of all corners
     */
    public Map<BitSet, Corner> getAllCorners() {
        return allCorners;
    }


}