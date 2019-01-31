package model.board;

import model.Resource;

public class Mountain extends Terrain {

    /**
     * Returns resource of mountain
     *
     * @return resource ore
     */
    @Override
    public Resource harvest() {
        return Resource.ORE;
    }
}
