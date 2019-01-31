package model;

import model.board.*;
import model.cards.DevelopmentCard;
import model.cards.DevelopmentCardFactory;
import model.cards.VictoryPointCard;
import model.players.Player;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class
Management {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Management.class.getName());
    private int brick = 19;
    private int lumber = 19;
    private int ore = 19;
    private int grain = 19;
    private int wool = 19;
    private DevelopmentCardFactory developmentCardFactory = new DevelopmentCardFactory(this);
    private int dice = 0;
    private Robber robber;
    private Board board;
    private HashMap<Integer, Player> allPlayersId = new HashMap<>();

    public Management() {

    }

    /**
     * Print the management infos
     *
     */
    public void printManagement() {
        String tmp = "";
        for (Player p : allPlayersId.values()) {
            tmp = tmp + "\n" + String.format("%1$-10s", p.getName()) + String.format("%1$-10s%2$-10d", "sum", p.getResource()) + String.format("%1$-10s%2$-10d", "lumber", p.getLumber()) + String.format("%1$-10s%2$-10d", "brick", p.getBrick()) + String.format("%1$-10s%2$-10d", "grain", p.getGrain()) + String.format("%1$-10s%2$-10d", "wool", p.getWool()) + String.format("%1$-10s%2$-10d", "ore", p.getOre());
        }
        LOGGER.debug(this.toString() + tmp
                + "\n" + String.format("%1$-10s", "[ M ]") + String.format("%1$-10s%2$-10d", "sum", this.getLumber() + this.getBrick() + this.getGrain() + this.getWool() + this.getOre()) + String.format("%1$-10s%2$-10d", "lumber", this.getLumber()) + String.format("%1$-10s%2$-10d", "brick", this.getBrick()) + String.format("%1$-10s%2$-10d", "grain", this.getGrain()) + String.format("%1$-10s%2$-10d", "wool", this.getWool()) + String.format("%1$-10s%2$-10d", "ore", this.getOre()));
    }

    /**
     * Roll the dice
     *
     * @return sum of dice
     */
    public int[] rollDices() {
        //origin= min_Value; bound max_Value; +1 to include 6 (alternative: bound: 7)
        int a = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        int b = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        int[] dices = new int[]{a, b};
        dice = a + b;
        setDice(dice);
        LOGGER.info("\n Dice [ " + a + ", " + b + " ]\n Resources [ BRICK " + brick + ", LUMBER " + lumber + ", ORE " + ore + ", GRAIN " + grain + ", WOOL " + wool + " ]");
        return dices;
    }

    /**
     * Distribute the resource cards after the dice
     *
     * @param dice diced number
     * @return returns a hashmap with all resources which were harvest for each player
     */
    public HashMap<Player, ArrayList<Resource>> bigHarvest(int dice) {
        HashMap<Player, ArrayList<Resource>> ressourceMap = new HashMap<>();
        ArrayList<Tile> tiles = new ArrayList<>();
        ArrayList<Corner> corners0 = new ArrayList<>();
        ArrayList<Corner> corners1 = new ArrayList<>();
        ArrayList<Corner> corners2 = new ArrayList<>();
        int tmp = 0;
        for (Tile tile : board.getAllTiles().values()) {
            if (tile instanceof Terrain) {
                if (((Terrain) tile).getNumberToken() == dice && !((Terrain) tile).getRobberIsActive()) {
                    tiles.add(tile);
                    tmp++;
                    for (Corner corner : board.getCornerneighborsOfTile(tile)) {
                        if (corner.getPlayer() != null) {
                            if (tmp == 1) {
                                corners1.add(corner);
                                corners0.add(corner);
                            } else {
                                corners2.add(corner);
                                corners0.add(corner);
                            }
                        }
                    }
                }
            }
        }
        for (Corner corner : corners0) {
            ressourceMap.put(corner.getPlayer(), new ArrayList<>());
        }
        if (tiles.size() == 2 && ((Terrain) tiles.get(0)).harvest() != ((Terrain) tiles.get(1)).harvest()) {
            if (enoughRessources(tiles.get(0), corners1)) {
                for (Corner corner : corners1) {
                    ArrayList<Resource> resources = distributRessource(corner, tiles.get(0));
                    for (Resource resource : resources) {
                        LOGGER.info("corner: " + corner.getBitSetID() + ", resource: " + resource
                                + ", management: brick: " + brick + ", lumber: " + lumber
                                + ", grain: " + grain + ", wool: " + wool + ", ore: " + ore);
                        ressourceMap.get(corner.getPlayer()).add(resource);
                    }
                }
            }
            if (enoughRessources(tiles.get(1), corners2)) {
                for (Corner corner : corners2) {
                    ArrayList<Resource> resources = distributRessource(corner, tiles.get(1));
                    for (Resource resource : resources) {
                        LOGGER.info("corner: " + corner.getBitSetID() + ", resource: " + resource
                                + ", management: brick: " + brick + ", lumber: " + lumber
                                + ", grain: " + grain + ", wool: " + wool + ", ore: " + ore);
                        ressourceMap.get(corner.getPlayer()).add(resource);
                    }
                }
            }
        } else if (tiles.size() > 0) {
            if (enoughRessources(tiles.get(0), corners0)) {
                for (Corner corner : corners0) {
                    ArrayList<Resource> resources = distributRessource(corner, tiles.get(0));
                    for (Resource resource : resources) {
                        LOGGER.info("corner: " + corner.getBitSetID() + ", resource: " + resource
                                + ", management: brick: " + brick + ", lumber: " + lumber
                                + ", grain: " + grain + ", wool: " + wool + ", ore: " + ore);
                        ressourceMap.get(corner.getPlayer()).add(resource);
                    }
                }
            }
        }
        return ressourceMap;
    }

    /**
     * Checks, if there are enough resource cards to distribute
     *
     * @param tile    tile with specific ressource
     * @param corners list with all cities and settlements, which get resources
     * @return if there are enough resource cards to distribute
     */
    public boolean enoughRessources(Tile tile, ArrayList<Corner> corners) {
        // checks, if only one player gets resource
        int tmp = 0;
        for (Corner corner : corners) {
            for (Corner cornertmp : corners) {
                if (corner.getPlayer() != cornertmp.getPlayer()) {
                    tmp++;
                }
            }
        }
        if (tmp < 1) {
            return true;
        }
        // checks, if number of needed resources is not more than number of available resource cards
        return neededRessources(corners) <= getExistingRessource(((Terrain) tile).harvest());
    }

    /**
     * Returns the number of needed ressources
     *
     * @param corners list with all cities and settlements, which get resources
     * @return number of needed ressources
     */
    public int neededRessources(ArrayList<Corner> corners) {
        int count = 0;
        for (Corner corner : corners) {
            if (corner.getIsCity()) {
                count = count + 2;
            } else {
                count++;
            }
        }
        return count;
    }

    /**
     * Distribute the required number of resource cards depending on whether it is a city or a settlement
     *
     * @param corner corner with settlement or city
     * @param tile   tile with specific resource
     * @return returns a list with all the resource cards
     */
    public ArrayList<Resource> distributRessource(Corner corner, Tile tile) {
        ArrayList<Resource> resources = new ArrayList<>();
        if (corner.getIsCity()) {
            for (int i = 0; i < Math.min(getExistingRessource(((Terrain) tile).harvest()), 2); i++) {
                Resource resource = getOneResourceCard(corner, tile);
                if (resource != null) {
                    resources.add(resource);
                }
            }
        } else {
            for (int i = 0; i < Math.min(getExistingRessource(((Terrain) tile).harvest()), 1); i++) {
                Resource resource = getOneResourceCard(corner, tile);
                if (resource != null) {
                    resources.add(resource);
                }
            }
        }
        return resources;
    }

    /**
     * Gives the player one card of a certain resource and takes away the corresponding card from the management
     *
     * @param corner corner with specific player
     * @param tile   tile with specific resource
     * @return returns the certain resource
     */
    public Resource getOneResourceCard(Corner corner, Tile tile) {
        if (tile instanceof Terrain) {
            if (!(tile instanceof Desert)) {
                Resource resource = ((Terrain) tile).harvest();
                corner.getPlayer().setSpecificResource(resource, 1);
                setSpecificResource(resource, -1);
                return resource;
            }
        }
        return null;
    }

    /**
     * For a resource, returns the number of resource cards still present in the management
     *
     * @param resource resource which is checked
     * @return number of resource cards of a certain resource
     */
    public int getExistingRessource(Resource resource) {
        switch (resource) {
            case BRICK:
                return brick;
            case LUMBER:
                return lumber;
            case ORE:
                return ore;
            case GRAIN:
                return grain;
            case WOOL:
                return wool;
        }
        return 0;
    }

    /**
     * Set the number of a specific resource
     *
     * @param resource specific resource
     * @param i        number to add
     */
    public void setSpecificResource(Resource resource, int i) {
        switch (resource) {
            case LUMBER:
                setLumber(getLumber() + i);
                break;
            case BRICK:
                setBrick(getBrick() + i);
                break;
            case ORE:
                setOre(getOre() + i);
                break;
            case WOOL:
                setWool(getWool() + i);
                break;
            case GRAIN:
                setGrain(getGrain() + i);
                break;
        }
    }

    /**
     * Check longest road
     *
     * @return returns the id of a player if the player get the longset road, returns 0 if nothing has changed, returns -1 if nobody has the longest road now
     */
    public int longestRoad() {
        HashMap<Player, Integer> roads = new HashMap<>();
        ArrayList<Integer> roads2 = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
        int max = 0;
        for (Player player : allPlayersId.values()) {
            roads2.add(board.getLongestStreet(player.getId()));
            roads.put(player, board.getLongestStreet(player.getId()));
            LOGGER.info(player.getName() + " longest road: " + board.getLongestStreet(player.getId()));
            if (board.getLongestStreet(player.getId()) > max) {
                max = board.getLongestStreet(player.getId());
            }
        }
        if (max >= 5) {
            for (Player player : roads.keySet()) {
                if (roads.get(player) == max) {
                    players.add(player);
                }
            }
            if (players.size() == 1) {
                for (Player player : allPlayersId.values()) {
                    if (player.getLongestRoad() && !players.contains(player)) {
                        player.setLongestRoad(false);
                        player.setVictoryPoints(player.getVictoryPoints() - 2);
                        LOGGER.info("player lost longest road: " + player.getName());
                    }
                }
                if (!(players.get(0).getLongestRoad())) {
                    players.get(0).setLongestRoad(true);
                    players.get(0).setVictoryPoints(players.get(0).getVictoryPoints() + 2);
                    LOGGER.info("player get longest road: " + players.get(0).getName());
                    return players.get(0).getId();
                }
            } else {
                for (Player player : allPlayersId.values()) {
//                    //if (player.getLongestRoad() == true && !allPlayersId.containsValue(player)) {
                    if (player.getLongestRoad() == true && !players.contains(player)) {
                        player.setLongestRoad(false);
                        player.setVictoryPoints(player.getVictoryPoints() - 2);
                        LOGGER.info("nobody has longest road anymore, player lost longest road: " + player.getName());
                        return -1;
                    }
                }
            }
        } else {
            for (Player player : allPlayersId.values()) {
                if (player.getLongestRoad() == true) {
                    player.setLongestRoad(false);
                    player.setVictoryPoints(player.getVictoryPoints() - 2);
                    LOGGER.info("nobody has longest road anymore, player lost longest road: " + player.getName());
                    return -1;
                }
            }
        }
        LOGGER.info("longest road: nothing changed");
        return 0;
    }

    /**
     * Check largest army
     *
     * @return returns the id of the player who has the largest army or returns 0 if nothing has changed
     */
    public int largestArmy() {
        HashMap<Player, Integer> knights = new HashMap<>();
        ArrayList<Integer> knights2 = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
        int max = 0;
        for (Player player : allPlayersId.values()) {
            LOGGER.info(player.getName() + ": " + knights.get(player));
            knights2.add(player.getKnight());
            knights.put(player, player.getKnight());
            if (player.getKnight() > max) {
                max = player.getKnight();
            }
        }
        if (max >= 3) {
            for (Player player : knights.keySet()) {
                if (knights.get(player) == max) {
                    players.add(player);
                    LOGGER.info("Max: " + max + ", " + player.getName() + ": " + knights.get(player));
                }
            }
            if (players.size() == 1) {
                for (Player player : allPlayersId.values()) {
                    if (player.getLargestArmy() && !players.contains(player)) {
                        player.setLargestArmy(false);
                        player.setVictoryPoints(player.getVictoryPoints() - 2);
                    }

                }
                if (!players.get(0).getLargestArmy()) {
                    players.get(0).setLargestArmy(true);
                    players.get(0).setVictoryPoints(players.get(0).getVictoryPoints() + 2);
                    return players.get(0).getId();
                }
            }
        }
        return 0;
    }

    /**
     * Checks, if the game is finish
     *
     * @return player who has won
     */
    public int finish() {

        for (Player player : allPlayersId.values()) {
            int victoryPointCards = 0;
            for (DevelopmentCard card : player.getDevelopmentCards()) {
                if (card instanceof VictoryPointCard) {
                    victoryPointCards++;
                }
            }
            for (DevelopmentCard card : player.getDevelopmentCardsMove()) {
                if (card instanceof VictoryPointCard) {
                    victoryPointCards++;
                }
            }
            LOGGER.info("offene Siegpunkte: " + player.getVictoryPoints() + ", Siegpunktkarten: " + victoryPointCards);
            if (player.getVictoryPoints() + victoryPointCards >= 10 && player.getActive()) {
                return player.getId();
            }
        }
        return 0;
    }

    /**
     * Return number of brickcards
     *
     * @return number of brickcards
     */
    public int getBrick() {
        return brick;
    }

    /**
     * Set the number of brickcards
     *
     * @param brick new number of brickcards
     */
    public void setBrick(int brick) {
        LOGGER.info("vorher Brick: " + brick);
        this.brick = brick;
        LOGGER.info("nacher Brick: " + brick);
    }

    /**
     * Return number of lumbercards
     *
     * @return number of lumbercards
     */
    public int getLumber() {
        return lumber;
    }

    /**
     * Set the number of lumbercards
     *
     * @param lumber new number of lumbercards
     */
    public void setLumber(int lumber) {
        LOGGER.info("vorher Lumber: " + lumber);
        this.lumber = lumber;
        LOGGER.info("nacher Lumber: " + lumber);

    }

    /**
     * Return number of orecards
     *
     * @return number of orecards
     */
    public int getOre() {
        return ore;
    }

    /**
     * Set the number of orecards
     *
     * @param ore new number of orecards
     */
    public void setOre(int ore) {
        LOGGER.info("vorher Ore: " + ore);
        this.ore = ore;
        LOGGER.info("nacher Ore: " + ore);

    }

    /**
     * Return number of graincards
     *
     * @return number of graincars
     */
    public int getGrain() {
        return grain;
    }

    /**
     * Set the number of graincards
     *
     * @param grain new number of graincards
     */
    public void setGrain(int grain) {
        LOGGER.info("vorher Grain: " + grain);
        this.grain = grain;
        LOGGER.info("nacher Grain: " + grain);

    }

    /**
     * Return number of woolcards
     *
     * @return number of woolcars
     */
    public int getWool() {
        return wool;
    }

    /**
     * Set the number of woolcards
     *
     * @param wool new number of woolcards
     */
    public void setWool(int wool) {
        LOGGER.info("vorher Wool: " + wool);
        this.wool = wool;
        LOGGER.info("nacher Wool: " + wool);

    }

    /**
     * Get the Develompnet cardfactory
     *
     * @return development cardfactory
     */
    public DevelopmentCardFactory getDevelopmentCardFactory() {
        return developmentCardFactory;
    }

    /**
     * Get the dice
     */
    public int getDice() {
        return dice;
    }

    /**
     * Set the dice
     *
     * @param dice new dice
     */
    public void setDice(int dice) {
        this.dice = dice;
    }

    /**
     * Get the robber
     *
     * @return robber
     */
    public Robber getRobber() {
        return robber;
    }

    /**
     * Set the robber
     *
     * @param board board with all tiles
     */
    public void setRobber(Board board) {
        robber = new Robber(board, this);
    }

    /**
     * Set the robber
     *
     * @param board board with all tiles
     * @param pos   new position
     */
    public void setRobber(Board board, BitSet pos) {
        robber = new Robber(board, this, pos);
    }

    /**
     * Get all players
     *
     * @return allPlayersId
     */
    public HashMap<Integer, Player> getAllPlayersId() {
        return allPlayersId;
    }

    /**
     * Hand over all players
     *
     * @param allPlayersId list with all players
     */
    public void setAllPlayersId(HashMap<Integer, Player> allPlayersId) {
        this.allPlayersId = allPlayersId;
    }

    /**
     * Set the board
     *
     * @param board game board
     */
    public void setBoard(Board board) {
        this.board = board;
    }


}
