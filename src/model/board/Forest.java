package model.board;

import model.Resource;

public class Forest extends Terrain {

    /**
     * Returns resource of forest
     *
     * @return resource lumber
     */
    @Override
    public Resource harvest() {
        return Resource.LUMBER;
    }
}
