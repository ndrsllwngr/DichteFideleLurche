package model.board;

import model.players.Player;

import java.util.BitSet;

public class Corner {
    private boolean isCity = false;
    private PortType portType = null;
    private BitSet id = new BitSet(37);
    private Player player = null;

    public Corner(Tile a, Tile b, Tile c) {
        this.id.or(a.getId());
        this.id.or(b.getId());
        this.id.or(c.getId());
    }

    /**
     * Return the bitset-Id from this corner
     *
     * @return id
     */
    public BitSet getBitSetID() {
        return id;
    }

    /**
     * Return the player, who build a settlement/city on this corner
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set a settlement from a specific player on this corner
     *
     * @param player specific player
     */
    public void setPlayer(Player player) {
        if (this.player == null) {
            this.player = player;
        }
    }

    /**
     * Return the type of port from this corner
     *
     * @return porttype
     */
    public PortType getPortType() {
        return portType;
    }

    /**
     * Set a porttype
     *
     * @param portType type of port
     */
    public void setPortType(PortType portType) {
        if (this.portType == null) {
            this.portType = portType;
        }
    }

    /**
     * Return true, if there is a city on this corner; return false if there is no city on this corner
     *
     * @return true or false
     */
    public boolean getIsCity() {
        return isCity;
    }

    /**
     * Build a city on this corner
     */
    public void setIsCity() {
        if (player != null && !getIsCity()) {
            isCity = true;
        }
    }
}
