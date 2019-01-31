package network;

import controller.Register;
import model.Resource;
import model.board.Tile;
import model.players.PColor;
import model.players.Status;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.BitSet;
import java.util.HashMap;

public class Interpreter {
    private static final Logger LOGGER = LogManager.getLogger(Interpreter.class.getName());
    private static HashMap<BitSet, Character> charBitSet = new HashMap<>();
    private static HashMap<String, String> translations = new HashMap<>();
    private static HashMap<String, String> translationsH = new HashMap<>();
    private static HashMap<String, Status> conversions = new HashMap<>();
    private static HashMap<PColor, String> colors = new HashMap<>();
    private static HashMap<Resource, String> resourceStringHashMap = new HashMap<>();

    /**
     * Default constructor
     */
    public Interpreter() {

    }

    /**
     * Make BitSet with one int value
     *
     * @param i int value
     * @return BitSet
     */
    public BitSet makeBitSet(int i) {
        BitSet id = new BitSet();
        id.flip(i);
        return id;
    }

    /**
     * Make edge BitSet
     *
     * @param a BitSet of tile a
     * @param b BitSet of tile b
     * @return BitSet of edge between tile a and b
     */
    public BitSet makeEdgeBitSet(BitSet a, BitSet b) {
        BitSet aNew = (BitSet) a.clone();
        BitSet bNew = (BitSet) b.clone();
        aNew.or(bNew);
        return aNew;
    }

    /**
     * Make corner BitSet
     *
     * @param a BitSet of tile a
     * @param b BitSet of tile b
     * @param c BitSet of tile c
     * @return BitSet of the corner in between tile a, b and c
     */
    public BitSet makeCornerBitSet(BitSet a, BitSet b, BitSet c) {
        BitSet aAndB = makeEdgeBitSet(a, b);
        BitSet cNew = (BitSet) c.clone();
        aAndB.or(cNew);
        return aAndB;
    }

    /**
     * Convert Char to related BitSet of model structure (TILE)
     *
     * @param c Char
     * @return BitSet ID
     */
    public synchronized BitSet charToBitSet(Character c) {
        LOGGER.debug(c);
        for (BitSet tmp : charBitSet.keySet()) {
            if (charBitSet.get(tmp).equals(c)) {
                LOGGER.debug("" + c + " -> " + tmp);
                return tmp;
            }
        }
        return new BitSet();
    }

    /**
     * Convert two Chars to related BitSet of model structure (EDGE)
     *
     * @param a Char
     * @param b Char
     * @return BitSet a+b
     */
    public BitSet twoCharsToBitSet(Character a, Character b) {
        BitSet aBs = (BitSet) charToBitSet(a).clone();
        BitSet bBs = (BitSet) charToBitSet(b).clone();
        aBs.or(bBs);
        LOGGER.debug("" + a + b + " -> " + aBs);
        return aBs;
    }

    /**
     * Convert three Chars to related BitSet of model structure (CORNER)
     *
     * @param a Char
     * @param b Char
     * @param c Char
     * @return BitSet a+b+c
     */
    public BitSet threeCharsToBitSet(Character a, Character b, Character c) {
        BitSet abBitSet = (BitSet) twoCharsToBitSet(a, b).clone();
        LOGGER.debug("abBitSet: " + abBitSet);
        try {
            BitSet cBitSet = (BitSet) charToBitSet(c).clone();
            LOGGER.debug("cBitSet: " + cBitSet);
            abBitSet.or(cBitSet);
        } catch (NullPointerException e) {
            LOGGER.catching(Level.ERROR, e);
        }
        LOGGER.debug("" + a + b + c + " -> " + abBitSet);
        return abBitSet;
    }

    /**
     * Convert BitSet to related Char of network logic (TILE)
     *
     * @param bs BitSet ID
     * @return Char
     */
    public Character bitSetToChar(BitSet bs) {
        return charBitSet.get(bs);
    }

    /**
     * Convert two BitSets to related Chars of network logic (EDGE)
     *
     * @param a BitSet
     * @param b BitSet
     * @return String a+b
     */
    public String twoBitSetsToString(BitSet a, BitSet b) {
        Character aChar = bitSetToChar(a);
        Character bChar = bitSetToChar(b);
        String s = "" + aChar + bChar;
        return s;
    }

    /**
     * Convert three BitSets to releated Chars of network logic (CORNER)
     *
     * @param a BitSet
     * @param b BitSet
     * @param c BitSet
     * @return String a+b+c
     */
    public String threeBitSetsToString(BitSet a, BitSet b, BitSet c) {
        String abString = twoBitSetsToString(a, b);
        Character cChar = bitSetToChar(c);
        String s = abString + cChar;
        return s;
    }

    /**
     * Translate network term to model term (DE->EN)
     *
     * @param s german term
     * @return english term
     */
    public String ntwrkTranslate(String s) {
        return translations.get(s);
    }

    public String ntwrkTranslateH(String s) {
        return translationsH.get(s);
    }

    /**
     * Translate model term to network term (EN->DE)
     *
     * @param s english term
     * @return german term
     */
    public String modelTranslate(String s) {
        for (String tmp : translations.keySet()) {
            if (translations.get(tmp).equals(s)) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * Translate model term to network term (EN->DE)
     *
     * @param s english term
     * @return german term
     */
    public String modelTranslateH(String s) {
        for (String tmp : translationsH.keySet()) {
            if (translationsH.get(tmp).equals(s)) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * Convert single corner BitSet to three BitSets, convert to network logic
     *
     * @param corner BitSet with three Bits
     * @return converted corner string
     */
    public String oneCornerBitSetToString(BitSet corner) {
        int p1 = corner.nextSetBit(0);
        int p2 = corner.nextSetBit(p1 + 1);
        int p3 = corner.nextSetBit(p2 + 1);
        return threeBitSetsToString(makeBitSet(p1), makeBitSet(p2), makeBitSet(p3));
    }

    /**
     * Convert single edge BitSet to two BitSets, convert to network logic
     *
     * @param edge BitSet with two Bits
     * @return converted edge string
     */
    public String oneEdgeBitSetToString(BitSet edge) {
        int p1 = edge.nextSetBit(0);
        int p2 = edge.nextSetBit(p1 + 1);
        return twoBitSetsToString(makeBitSet(p1), makeBitSet(p2));
    }

    /**
     * Convert network term to enum
     *
     * @param s string
     * @return model.players.Status enum
     */
    public Status ntwrkConversion(String s) {
        if (conversions.containsKey(s)) {
            return conversions.get(s);
        } else {
            return Status.BOOBS;
        }
    }

    /**
     * Convert enum to network term
     *
     * @param e model.players.Status enum
     * @return string
     */
    public String modelConversion(model.players.Status e) {
        for (String tmp : conversions.keySet()) {
            if (conversions.get(tmp) == e) {
                return tmp;
            }
        }
        return "";
    }

    /**
     * Convert string to BitSet (Ntwrk to Model)
     *
     * @param s string (2 or 3 chars)
     * @return BitSet (location)
     */
    public BitSet stringToBitSet(String s) {
        if (s.length() == 2) {
            return twoCharsToBitSet(s.charAt(0), s.charAt(1));
        } else {
            return threeCharsToBitSet(s.charAt(0), s.charAt(1), s.charAt(2));
        }
    }

    /**
     * Convert BitSet to String (Model to Ntwrk)
     *
     * @param bs bitset (cardinality 2 or 3)
     * @return string (location)
     */
    public String bitSetToString(BitSet bs) {
        if (bs.cardinality() == 2) {
            return oneEdgeBitSetToString(bs);
        } else {
            return oneCornerBitSetToString(bs);
        }
    }

    public BitSet testBoard(int x, int y) {
        int[][] test = new int[7][7];
        test[0] = new int[]{-1, -1, -1, 0, 1, 2, 3};
        test[1] = new int[]{-1, -1, 4, 5, 6, 7, 8};
        test[2] = new int[]{-1, 9, 10, 11, 12, 13, 14};
        test[3] = new int[]{15, 16, 17, 18, 19, 20, 21};
        test[4] = new int[]{22, 23, 24, 25, 26, 27, -1};
        test[5] = new int[]{28, 29, 30, 31, 32, -1, -1};
        test[6] = new int[]{33, 34, 35, 36, -1, -1, -1};
        return makeBitSet(test[y + 3][x + 3]);
    }

    /**
     * Convert network protocol 1.0 coordinates to model BitSet
     *
     * @param jsonObject x-, y-coordinates of one tile
     * @return coordinates converted to model BitSet of one tile
     */
    public BitSet axialToTile(JSONObject jsonObject) {
        int x = jsonObject.getInt("x");
        int y = jsonObject.getInt("y");
        return testBoard(x + y, y * (-1));
    }

    /**
     * Convert network protocol axial coordinates to model BitSet
     *
     * @param jsonArray x-,y-coordinates of two or three tiles
     * @return coordinates converted to model BitSet of one edge or corner
     */
    public BitSet axialToEdgeOrCorner(JSONArray jsonArray) {
        BitSet a = (BitSet) axialToTile(jsonArray.getJSONObject(0)).clone();
        BitSet b = (BitSet) axialToTile(jsonArray.getJSONObject(1)).clone();
        a.or(b);
        if (jsonArray.length() == 3) {
            BitSet c = (BitSet) axialToTile(jsonArray.getJSONObject(2)).clone();
            a.or(c);
        }
        LOGGER.info(jsonArray + ". " + a);
        return a;
    }

    /**
     * Convert BitSet of one model tile to network protocol axial coordinates
     *
     * @param bs BitSet of one tile
     * @return JSONObject with network protocol axial coordinates of certain tile
     */
    public JSONObject tileToAxial(BitSet bs) {
        Tile tile = Register.getController().getBoard().getAllTiles().get(bs);
        int[] co = Register.getController().getBoard().getSpecificTileCo(tile);
        int x = co[0];
        int y = co[1];
        JSONObject oneTile = new JSONObject();
        oneTile.put("x", x + y);
        oneTile.put("y", y * (-1));
        return oneTile;
    }

    /**
     * Convert BitSet of one model edge to network protocol axial coordinates
     *
     * @param edge BitSet of one edge
     * @return JSONArray with network protocol axial coordinates of certain edge
     */
    public JSONArray edgeToAxial(BitSet edge) {
        int p1 = edge.nextSetBit(0);
        int p2 = edge.nextSetBit(p1 + 1);
        JSONArray oneEdge = new JSONArray();
        oneEdge.put(tileToAxial(makeBitSet(p1)));
        oneEdge.put(tileToAxial(makeBitSet(p2)));
        return oneEdge;
    }

    /**
     * Convert BitSet of one model corner to network protocol axial coordinates
     *
     * @param corner BitSet of one corner
     * @return JSONArray with network protocol axial coordinates of certain corner
     */
    public JSONArray cornerToAxial(BitSet corner) {
        int p1 = corner.nextSetBit(0);
        int p2 = corner.nextSetBit(p1 + 1);
        int p3 = corner.nextSetBit(p2 + 1);
        BitSet edge = makeBitSet(p1);
        edge.or(makeBitSet(p2));
        JSONArray oneCorner = edgeToAxial(edge);
        oneCorner.put(tileToAxial(makeBitSet(p3)));
        return oneCorner;
    }

    /**
     * Convert BitSet of one model corner or edge to network protocol axial coordinates
     *
     * @param bs BitSet of one corner or edge
     * @return JSONArray with network protocol axial coordinates of certain corner or edge
     */
    public JSONArray bitSetToAxial(BitSet bs) {
        if (bs.cardinality() == 2) {
            return edgeToAxial(bs);
        } else {
            return cornerToAxial(bs);
        }
    }

    /**
     * Convert string color to PColor
     *
     * @param color string color
     * @return color key
     */
    public PColor stringToPColor(String color) {
        for (PColor tmp : colors.keySet()) {
            if (colors.get(tmp).equals(color)) {
                return tmp;
            }
        }
        return PColor.NULL;
    }

    public String resourceToString(Resource res) {
        return resourceStringHashMap.get(res);
    }

    /**
     * Convert string resource to resource
     *
     * @param res string resource
     * @return resource key
     */
    public Resource stringToResource(String res) {
        for (Resource tmp : resourceStringHashMap.keySet()) {
            if (resourceStringHashMap.get(tmp).equals(res)) {
                return tmp;
            }
        }
        return Resource.NULL;
    }

    public String pColorToString(PColor pColor) {
        return colors.get(pColor);
    }

    /**
     * Initialize HashMap of colors. PColor, string
     *
     */
    public void initPColors() {
        colors.put(PColor.RED, "Rot");
        colors.put(PColor.ORANGE, "Orange");
        colors.put(PColor.BLUE, "Blau");
        colors.put(PColor.WHITE, "Weiß");
        colors.put(PColor.GREEN, "Grün");
        colors.put(PColor.BROWN, "Braun");
    }

    /**
     * Initialize  HashMap of model structure ids and network logic chars
     *
     */
    public void initCharBitSet() {
        // 1. row
        charBitSet.put(makeBitSet(0), 'a');
        charBitSet.put(makeBitSet(1), 'b');
        charBitSet.put(makeBitSet(2), 'c');
        charBitSet.put(makeBitSet(3), 'd');
        // 2. row
        charBitSet.put(makeBitSet(4), 'e');
        charBitSet.put(makeBitSet(5), 'A');
        charBitSet.put(makeBitSet(6), 'B');
        charBitSet.put(makeBitSet(7), 'C');
        charBitSet.put(makeBitSet(8), 'f');
        // 3. row
        charBitSet.put(makeBitSet(9), 'g');
        charBitSet.put(makeBitSet(10), 'D');
        charBitSet.put(makeBitSet(11), 'E');
        charBitSet.put(makeBitSet(12), 'F');
        charBitSet.put(makeBitSet(13), 'G');
        charBitSet.put(makeBitSet(14), 'h');
        // 4. row
        charBitSet.put(makeBitSet(15), 'i');
        charBitSet.put(makeBitSet(16), 'H');
        charBitSet.put(makeBitSet(17), 'I');
        charBitSet.put(makeBitSet(18), 'J');
        charBitSet.put(makeBitSet(19), 'K');
        charBitSet.put(makeBitSet(20), 'L');
        charBitSet.put(makeBitSet(21), 'j');
        // 5. row
        charBitSet.put(makeBitSet(22), 'k');
        charBitSet.put(makeBitSet(23), 'M');
        charBitSet.put(makeBitSet(24), 'N');
        charBitSet.put(makeBitSet(25), 'O');
        charBitSet.put(makeBitSet(26), 'P');
        charBitSet.put(makeBitSet(27), 'l');
        // 6. row
        charBitSet.put(makeBitSet(28), 'm');
        charBitSet.put(makeBitSet(29), 'Q');
        charBitSet.put(makeBitSet(30), 'R');
        charBitSet.put(makeBitSet(31), 'S');
        charBitSet.put(makeBitSet(32), 'n');
        // 7. row
        charBitSet.put(makeBitSet(33), 'o');
        charBitSet.put(makeBitSet(34), 'p');
        charBitSet.put(makeBitSet(35), 'q');
        charBitSet.put(makeBitSet(36), 'r');
    }

    /**
     * Initialize HashMap of network terms and model terms (DE-EN)
     *
     */
    public void initTranslations() {
        // Tile type
        translations.put("Ackerland", "Field");
        translations.put("Hügelland", "Hill");
        translations.put("Weideland", "Pasture");
        translations.put("Wald", "Forest");
        translations.put("Gebirge", "Mountain");
        translations.put("Wüste", "Desert");
        translations.put("Meer", "Sea");
        // Resource type
        translations.put("Holz", "LUMBER");
        translations.put("Lehm", "BRICK");
        translations.put("Wolle", "WOOL");
        translations.put("Getreide", "GRAIN");
        translations.put("Erz", "ORE");
    }

    /**
     * Initialize HashMap of network terms and model terms (DE-EN)
     *
     */
    public void initHarbor() {
        translationsH.put("Holz Hafen", "LUMBER");
        translationsH.put("Lehm Hafen", "BRICK");
        translationsH.put("Wolle Hafen", "WOOL");
        translationsH.put("Erz Hafen", "ORE");
        translationsH.put("Getreide Hafen", "GRAIN");
        translationsH.put("Hafen", "GENERIC");
    }

    /**
     * Initialize HashMap of network terms and model enums (DE-ENUM)
     *
     */
    public void initConversions() {
        // Status updates
        conversions.put("Spiel starten", Status.START_GAME);
        conversions.put("Wartet auf Spielbeginn", Status.WAIT_FOR_GAME_START);
        conversions.put("Dorf bauen", Status.BUILD_SETTLEMENT);
        conversions.put("Straße bauen", Status.BUILD_STREET);
        conversions.put("Würfeln", Status.ROLL_DICE);
        conversions.put("Karten wegen Räuber abgeben", Status.HAND_IN_CARDS_BECAUSE_OF_ROBBER);
        conversions.put("Räuber versetzen", Status.MOVE_ROBBER);
        conversions.put("Handeln oder Bauen", Status.TRADE_OR_BUILD);
        conversions.put("Bauen", Status.BUILD);
        conversions.put("Warten", Status.WAIT);
        conversions.put("Verbindung verloren", Status.CONNECTION_LOST);
        conversions.put("Boobs", Status.BOOBS);
    }

    /**
     * Initialize HashMap of network terms and model terms (class - DE)
     *
     */
    public void initResourceStringHashMap() {
        resourceStringHashMap.put(Resource.BRICK, "Lehm");
        resourceStringHashMap.put(Resource.WOOL, "Wolle");
        resourceStringHashMap.put(Resource.LUMBER, "Holz");
        resourceStringHashMap.put(Resource.GRAIN, "Getreide");
        resourceStringHashMap.put(Resource.ORE, "Erz");
    }
}
