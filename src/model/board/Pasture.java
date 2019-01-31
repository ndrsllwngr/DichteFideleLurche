package model.board;

import model.Resource;

public class Pasture extends Terrain {

    /**
     * Returns resource of pasture
     *
     * @return resource wool
     */
    @Override
    public Resource harvest() {
        return Resource.WOOL;
    }
}
