package model.board;

import model.Resource;

public class Hill extends Terrain {

    /**
     * Returns resource of hill
     *
     * @return resource brick
     */
    @Override
    public Resource harvest() {
        return Resource.BRICK;
    }
}

