package model.board;

import model.Resource;

public class Desert extends Terrain {

    /**
     * Returns resource of desert
     *
     * @return null
     */
    @Override
    public Resource harvest() {
        return null;
    }
}
