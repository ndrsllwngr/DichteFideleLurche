package model.board;

import model.players.Player;

import java.util.BitSet;

public class Edge {
    private BitSet id = new BitSet(37);
    private Player player = null;

    public Edge(Sea a, Terrain b) {
        this.id.or(a.getId());
        this.id.or(b.getId());
    }

    public Edge(Tile a, Tile b) {
        this.id.or(a.getId());
        this.id.or(b.getId());
    }

    /**
     * Return the bitset-Id from this edge
     *
     * @return id
     */
    public BitSet getBitSetID() {
        return id;
    }

    /**
     * Return the player, who build a road on this edge
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set a road from a specific player on this edge
     *
     * @param player specific player
     */
    public void setPlayer(Player player) {
        if (this.player == null) {
            this.player = player;
        }
    }

}
