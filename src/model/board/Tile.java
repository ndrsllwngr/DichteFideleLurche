package model.board;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    private BitSet id = new BitSet(37);
    private Map<BitSet, Tile> neighbors = new HashMap<>();
    private Map<BitSet, Edge> edges = new HashMap<>();
    private Map<BitSet, Corner> corners = new HashMap<>();

    /**
     * Return id
     *
     * @return id
     */
    public BitSet getId() {
        return id;
    }

    /**
     * Set id
     *
     * @param id new id
     */
    public void setId(int id) {
        this.id.flip(id);
    }

    /**
     * Set the neighbors of the tile
     *
     * @param board default board or random board
     */
    public void setNeighbor(Board board) {
        this.neighbors = board.getNeighbors(this);
    }

    /**
     * Return all neighbors of the tile
     *
     * @return map of neighbors
     */
    public Map<BitSet, Tile> getNeighbors() {
        return neighbors;
    }

    /**
     * Add a specific edge to the list of edges
     *
     * @param edge specific edge
     */
    public void setEdge(Edge edge) {
        this.edges.put(edge.getBitSetID(), edge);
    }

    /**
     * Add a specific corner to the list of corners
     *
     * @param corner specific corner
     */
    public void setCorner(Corner corner) {
        this.corners.put(corner.getBitSetID(), corner);
    }

}
