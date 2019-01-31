package model.board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.BitSet;

public class CustomisedBoard extends Board {
    private static final Logger LOGGER = LogManager.getLogger(CustomisedBoard.class.getName());

    public CustomisedBoard() {

    }

    /**
     * Set a new terrain with numbertoken
     *
     * @param bitSet      place
     * @param typ         kind of terrain ("desert", "field", "forest", "hill", "mountain", "pasture", "sea")
     * @param numbertoken numbertoken for each tile
     */
    public void setTile(BitSet bitSet, String typ, int numbertoken) {
        switch (typ) {
            case "Desert":
                getAllTiles().put(bitSet, new Desert());
                ((Terrain) getAllTiles().get(bitSet)).setNumbertoken(numbertoken);
                break;
            case "Field":
                getAllTiles().put(bitSet, new Field());
                ((Terrain) getAllTiles().get(bitSet)).setNumbertoken(numbertoken);
                break;
            case "Forest":
                getAllTiles().put(bitSet, new Forest());
                ((Terrain) getAllTiles().get(bitSet)).setNumbertoken(numbertoken);
                break;
            case "Hill":
                getAllTiles().put(bitSet, new Hill());
                ((Terrain) getAllTiles().get(bitSet)).setNumbertoken(numbertoken);
                break;
            case "Mountain":
                getAllTiles().put(bitSet, new Mountain());
                ((Terrain) getAllTiles().get(bitSet)).setNumbertoken(numbertoken);
                break;
            case "Pasture":
                getAllTiles().put(bitSet, new Pasture());
                ((Terrain) getAllTiles().get(bitSet)).setNumbertoken(numbertoken);
                break;
            case "Sea":
                getAllTiles().put(bitSet, new Sea());
        }
    }

    /**
     * Initialize ArrayBoard and all lists, neighbors and ids
     * use after calling the method setTile() for all tiles
     *
     */
    public void initBoard() {
        getBoard()[0] = new Tile[]{null, null, null, getAllTiles().get(makeIdTile(0)), getAllTiles().get(makeIdTile(1)), getAllTiles().get(makeIdTile(2)), getAllTiles().get(makeIdTile(3))};
        getBoard()[1] = new Tile[]{null, null, getAllTiles().get(makeIdTile(4)), getAllTiles().get(makeIdTile(5)), getAllTiles().get(makeIdTile(6)), getAllTiles().get(makeIdTile(7)), getAllTiles().get(makeIdTile(8))};
        getBoard()[2] = new Tile[]{null, getAllTiles().get(makeIdTile(9)), getAllTiles().get(makeIdTile(10)), getAllTiles().get(makeIdTile(11)), getAllTiles().get(makeIdTile(12)), getAllTiles().get(makeIdTile(13)), getAllTiles().get(makeIdTile(14))};
        getBoard()[3] = new Tile[]{getAllTiles().get(makeIdTile(15)), getAllTiles().get(makeIdTile(16)), getAllTiles().get(makeIdTile(17)), getAllTiles().get(makeIdTile(18)), getAllTiles().get(makeIdTile(19)), getAllTiles().get(makeIdTile(20)), getAllTiles().get(makeIdTile(21))};
        getBoard()[4] = new Tile[]{getAllTiles().get(makeIdTile(22)), getAllTiles().get(makeIdTile(23)), getAllTiles().get(makeIdTile(24)), getAllTiles().get(makeIdTile(25)), getAllTiles().get(makeIdTile(26)), getAllTiles().get(makeIdTile(27)), null};
        getBoard()[5] = new Tile[]{getAllTiles().get(makeIdTile(28)), getAllTiles().get(makeIdTile(29)), getAllTiles().get(makeIdTile(30)), getAllTiles().get(makeIdTile(31)), getAllTiles().get(makeIdTile(32)), null, null};
        getBoard()[6] = new Tile[]{getAllTiles().get(makeIdTile(33)), getAllTiles().get(makeIdTile(34)), getAllTiles().get(makeIdTile(35)), getAllTiles().get(makeIdTile(36)), null, null, null};
        initLists();
    }

    /**
     * Set harbor
     * use after calling the method initBoard()
     *
     * @param sea     sea, where the harbor is set
     * @param terrain terrain next to the harbor
     * @param typ     harbortyp ("generic", "brick", "lumber", "ore", "grain", "wool")
     */
    public void setHarbor(BitSet sea, BitSet terrain, String typ) {
        BitSet bitSet = new BitSet();
        bitSet.or(sea);
        bitSet.or(terrain);
        LOGGER.info(bitSet);
        Corner corner0 = getCornerneighborsOfEdge(getAllEdges().get(bitSet)).get(0);
        Corner corner1 = getCornerneighborsOfEdge(getAllEdges().get(bitSet)).get(1);
        switch (typ) {
            case "GENERIC":
                ((Sea) getAllTiles().get(sea)).setPort(PortType.GENERIC);
                corner0.setPortType(PortType.GENERIC);
                corner1.setPortType(PortType.GENERIC);
                break;
            case "BRICK":
                ((Sea) getAllTiles().get(sea)).setPort(PortType.BRICK);
                corner0.setPortType(PortType.BRICK);
                corner1.setPortType(PortType.BRICK);
                break;
            case "LUMBER":
                ((Sea) getAllTiles().get(sea)).setPort(PortType.LUMBER);
                corner0.setPortType(PortType.LUMBER);
                corner1.setPortType(PortType.LUMBER);
                break;
            case "ORE":
                ((Sea) getAllTiles().get(sea)).setPort(PortType.ORE);
                corner0.setPortType(PortType.ORE);
                corner1.setPortType(PortType.ORE);
                break;
            case "GRAIN":
                ((Sea) getAllTiles().get(sea)).setPort(PortType.GRAIN);
                corner0.setPortType(PortType.GRAIN);
                corner1.setPortType(PortType.GRAIN);
                break;
            case "WOOL":
                ((Sea) getAllTiles().get(sea)).setPort(PortType.WOOL);
                corner0.setPortType(PortType.WOOL);
                corner1.setPortType(PortType.WOOL);
                break;

        }
    }

}
