package model;

import model.board.*;
import model.players.Player;
import view.client.EventTypes;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Observable;

public class Robber extends Observable {

    private BitSet position;
    private Board board;
    private Management management;

    public Robber(Board board, Management management) {
        this.management = management;
        this.board = board;
        for (Tile tile : board.getAllTiles().values()) {
            if (tile instanceof Desert) {
                this.position = tile.getId();
                ((Desert) tile).setRobberIsActive(true);
            }
        }
    }

    public Robber(Board board, Management management, BitSet position) {
        this.management = management;
        this.board = board;
        this.position = position;
        ((Terrain) board.getAllTiles().get(position)).setRobberIsActive(true);
    }

    // die Reihenfolge ist: startRobbing(), changePosition() und dann robCard()

    /**
     * Submit the half of the cards from a player, if he has more than 7 cards
     *
     * @param player player who has to give away resource cards
     * @param resources resources to submit
     */
    public void startRobbing(Player player, ArrayList<Resource> resources) {
        if (resources.size() == cardNumberToRob(player)) {
            for (Resource resource : resources) {
                player.setSpecificResource(resource, -1);
                management.setSpecificResource(resource, 1);
            }
        }
    }

    /**
     * Returns the number of cards to rob
     *
     * @param player specific player
     * @return number to rob
     */
    public int cardNumberToRob(Player player) {
        if (player.getResource() > 7) {
            return Math.floorDiv(player.getResource(), 2);
        } else {
            return 0;
        }
    }

    /**
     * Puts the robber on a new position
     *
     * @param position new position
     */
    public boolean changePosition(BitSet position) {
        if (!(position.equals(this.position))) {
            ((Terrain) board.getAllTiles().get(this.position)).setRobberIsActive(false);
            this.position = position;
            ((Terrain) board.getAllTiles().get(this.position)).setRobberIsActive(true);
            setChanged();
            notifyObservers(EventTypes.ROBBER);
            return true;
        } else {
            return false;
        }
    }

    /**
     * player who is the robber get a card of an other player
     *
     * @param robber player who rob a card from an other player
     * @param player player who is robbed
     * @
     */
    public Resource robCard(Player robber, Player player) {
        if (getRobablePlayer(position).contains(player)) {
            ArrayList<Resource> resources = player.getAllResourceList();
            if (resources.size() > 0) {
                Resource resource = resources.get((int) (Math.random() * resources.size()));
                player.setSpecificResource(resource, -1);
                robber.setSpecificResource(resource, 1);
                return resource;
            }
        }
        //TODO wenn keine Ressourcen vorhanden, dann kann man nicht klauen
        return null;
    }

    /**
     * Get all Players who have a settlement or a city on the tile where the robber is
     *
     * @return list of players
     */
    public ArrayList<Player> getRobablePlayer(BitSet pos) {
        ArrayList<Player> players = new ArrayList<>();
        for (Corner corner : board.getCornerneighborsOfTile(board.getAllTiles().get(pos))) {
            if (corner.getPlayer() != null && !(players.contains(corner.getPlayer()))) {
                players.add(corner.getPlayer());
            }
        }
        return players;
    }

    /**
     * Return the position from the robber
     *
     * @return position
     */
    public BitSet getPosition() {
        return position;
    }


}
