package model.board;

import model.Resource;

public class Field extends Terrain {

    /**
     * Returns resource of field
     *
     * @return resource grain
     */
    @Override
    public Resource harvest() {
        return Resource.GRAIN;
    }
}
