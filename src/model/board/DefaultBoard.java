package model.board;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultBoard extends Board {
    /**
     * Default board for beginner
     */
    public DefaultBoard() {
        // Initialize board (see and terrain)
        getBoard()[0] = new Tile[]{null, null, null, new Sea(), new Sea(), new Sea(), new Sea()};
        getBoard()[1] = new Tile[]{null, null, new Sea(), new Mountain(), new Pasture(), new Forest(), new Sea()};
        getBoard()[2] = new Tile[]{null, new Sea(), new Field(), new Hill(), new Pasture(), new Hill(), new Sea()};
        getBoard()[3] = new Tile[]{new Sea(), new Field(), new Forest(), new Desert(), new Forest(), new Mountain(), new Sea()};
        getBoard()[4] = new Tile[]{new Sea(), new Forest(), new Mountain(), new Field(), new Pasture(), new Sea(), null};
        getBoard()[5] = new Tile[]{new Sea(), new Hill(), new Field(), new Pasture(), new Sea(), null, null};
        getBoard()[6] = new Tile[]{new Sea(), new Sea(), new Sea(), new Sea(), null, null, null};

        initLists();
        // Set numberTokens and ports on the tiles
        ArrayList<Integer> numberTokens = new ArrayList<>(18);
        numberTokens.addAll(Arrays.asList(10, 2, 9, 12, 6, 4, 10, 9, 11, 3, 8, 8, 3, 4, 5, 5, 6, 11));
        ArrayList<PortType> portTiles = new ArrayList<>(18);
        portTiles.addAll(Arrays.asList(PortType.GENERIC, null, PortType.GRAIN, null, null,
                PortType.ORE, PortType.LUMBER, null, null, PortType.GENERIC, PortType.BRICK,
                null, null, PortType.WOOL, PortType.GENERIC, null, PortType.GENERIC, null));
        for (int i = 0; i < 37; i++) {
            for (Tile tile : getAllTiles().values()) {
                if (tile.getId().nextSetBit(0) == i) {
                    if (!(tile instanceof Sea || tile instanceof Desert)) {
                        ((Terrain) tile).setNumbertoken(numberTokens.get(0));
                        numberTokens.remove(0);
                    } else if (tile instanceof Sea) {
                        ((Sea) tile).setPort(portTiles.get(0));
                        portTiles.remove(0);
                    }
                }
            }
        }
        // Set harbors on the corner
        ArrayList<int[]> cornerIds = new ArrayList<>(18);
        cornerIds.addAll(Arrays.asList(new int[]{0, 1, 5}, new int[]{0, 4, 5}, new int[]{1, 2, 6}, new int[]{2, 6, 7},
                new int[]{7, 8, 13}, new int[]{8, 13, 14}, new int[]{4, 9, 10}, new int[]{9, 10, 16}, new int[]{14, 20, 21},
                new int[]{20, 21, 27}, new int[]{16, 22, 23}, new int[]{22, 23, 28}, new int[]{26, 27, 32},
                new int[]{26, 31, 32}, new int[]{28, 29, 33}, new int[]{29, 33, 34}, new int[]{30, 31, 35}, new int[]{30, 34, 35}));
        ArrayList<PortType> portCorners = new ArrayList<>(18);
        portCorners.addAll(Arrays.asList(PortType.GENERIC, PortType.GRAIN, PortType.ORE, PortType.LUMBER,
                PortType.GENERIC, PortType.BRICK, PortType.WOOL, PortType.GENERIC, PortType.GENERIC));
        int j = 0;
        for (int i = 0; i < 18; i++) {
            getAllCorners().get(makeIdCorner(cornerIds.get(i)[0], cornerIds.get(i)[1], cornerIds.get(i)[2])).setPortType(portCorners.get(j));
            if (i % 2 != 0) {
                j++;
            }
        }
    }
}
