package controller;

import model.Management;
import model.board.Board;
import model.board.CustomisedBoard;
import model.players.Ai;
import model.players.Human;
import model.players.PColor;
import model.players.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.client.EventTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;

public class Controller extends Observable {

    private static final Logger LOGGER = LogManager.getLogger(Controller.class.getName());
    private Board board;
    private HashMap<Integer, Player> allPlayersId = new HashMap<>();
    private ArrayList<Player> sequence = new ArrayList<>();
    private Management management;

    public Controller() {
        LOGGER.info(this);
    }

    /**
     * Return the management
     *
     * @return management
     */
    public Management getManagement() {
        return management;
    }

    /**
     * Set the management
     *
     * @param management management of player
     */
    public void setManagement(Management management) {
        LOGGER.info(management);
        this.management = management;
    }

    /**
     * Notify observer for the canvasboard an the robber
     *
     */
    public void notifyObserverCanvasboard() {
        for (model.players.Player player : Register.getController().getAllPlayersId().values()) {
            player.setBoard(Register.getController().getBoard());
            player.notifyObserverCanvasboard();
        }
        setChanged();
        notifyObservers(EventTypes.CANVASBOARD);
        setChanged();
        notifyObservers(EventTypes.ROBBER);
    }

    /**
     * Notify observer for development cards
     *
     */
    public void notifyObserverDevCards() {
        setChanged();
        notifyObservers(EventTypes.DEVELOPMENTCARDS);
    }

    /**
     * Add a player to the game
     *
     * @param id    id of the player
     * @param name  name of the player
     * @param color color of the player
     * @param human ture, if player is a human; else, if player is a ai
     */
    public void addPlayer(int id, String name, PColor color, boolean human) {
        if (human) {
            allPlayersId.put(id, new Human(id, name, color));
        } else {
            allPlayersId.put(id, new Ai(id, name, color));
        }
    }

    /**
     * Establish the startScreen player by roll the dice
     *
     * @param allPlayersId HashMap Ineger
     * @return startScreen player
     */
    public Player startplayer(HashMap<Integer, Player> allPlayersId) {
        LOGGER.traceEntry();
        ArrayList<Integer> dices = new ArrayList<>();
        // ArrayList<Player> players = new ArrayList<>();
        HashMap<Integer, Player> players = new HashMap<>();
        for (Player player : allPlayersId.values()) {
            management.rollDices();
            dices.add(management.getDice());
        }
        int max = Collections.max(dices);
        for (int i = 0; i < dices.size(); i++) {
            if (dices.get(i) == max) {
                players.put(i, allPlayersId.get(i));
            }
        }
        if (players.size() == 1) {
            return players.get(0);
        }
        return startplayer(players);
    }

    /**
     * Return the sequence
     *
     * @return sequence list of players
     */
    public ArrayList<Player> getSequence() {
        return sequence;
    }

    /**
     * Set a player to the sequence
     *
     * @param player new player tp sequence
     */
    public void setSequence(Player player) {
        LOGGER.info(player.getId() + ", " + player.getName() + ", " + player.getColor() + ", " + player.getStatus());
        sequence.add(player);
    }

    /**
     * Return the board
     *
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Set board
     *
     * @param board Board
     */
    public void setBoard(Board board) {
        LOGGER.traceEntry();
        this.board = board;
        Register.getController().getBoard().printBoardIDs();
        Register.getController().getBoard().printNumberTokens();
        Register.getController().getBoard().printBoardSpecialCo();
        Register.getController().getBoard().printTileNeighbors();
        Register.getController().getBoard().printPorts();
    }

    /**
     * Getter allPlayersId
     *
     * @return allPlayersId
     */
    public HashMap<Integer, Player> getAllPlayersId() {
        return allPlayersId;
    }

    /**
     * Reset all players
     */
    public void resetAllPlayersId() {
        this.allPlayersId.clear();
    }

    /**
     * Initialize a customiesed board
     */
    public void initCustomisedBoard() {
        LOGGER.traceEntry();
        this.board = new CustomisedBoard();
    }
}
