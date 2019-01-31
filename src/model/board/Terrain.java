package model.board;

import model.Resource;

public abstract class Terrain extends Tile {

    private int numbertoken = 0;
    private boolean robberIsActive = false;

    /**
     * Set a numbertoken
     *
     * @param numbertokenToken numbertoken on tile
     */
    public void setNumbertoken(int numbertokenToken) {
        if (this.numbertoken == 0) {
            this.numbertoken = numbertokenToken;
        }
    }

    /**
     * Return the numbertoken
     *
     * @return numbertoken
     */
    public int getNumberToken() {
        return numbertoken;
    }

    /**
     * Return true, if robber sit on this terrain; return false, if robber sit not on this terrain
     *
     * @return true of false
     */
    public boolean getRobberIsActive() {
        return robberIsActive;
    }

    /**
     * Set true, if robber sit on this terrain; set false, if robber sit not on this terrain
     *
     * @param robberIsActive true of false
     */
    public void setRobberIsActive(boolean robberIsActive) {
        this.robberIsActive = robberIsActive;

    }

    /**
     * Returns resource of this Terrain
     *
     * @return resource
     */
    public abstract Resource harvest();
}
