package network.server;

import controller.Register;
import model.players.Player;
import model.players.Status;
import network.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


class ClientThreadSequence {
    private static final Logger LOGGER = LogManager.getLogger(ClientThreadSequence.class.getName());
    private static ArrayList<Integer> originalSequence = new ArrayList<>();
    private static HashMap<Integer, ClientThread> clients = new HashMap<>();
    private static Level level = Level.SETUP;
    private static boolean toggle = true;
    private static boolean devCard;
    private ClientThread clientThread;


    ClientThreadSequence(ClientThread clientThread) {
        this.clientThread = clientThread;
    }

    /**
     * Reset sequence, clients and set sequence level to setup
     */
    static void reset() {
        toggle = true;
        originalSequence.clear();
        clients.clear();
        level = Level.SETUP;
    }

    static Level getLevel() {
        return level;
    }

    static void setLevel(Level l) {
        level = l;
    }

    public static boolean isDevCard() {
        return devCard;
    }

    public static void setDevCard(boolean devCard) {
        ClientThreadSequence.devCard = devCard;
    }

    /**
     * Add client to player sequence
     *
     * @param clientID player id
     * @param clientThread clientThread
     */
    public void add(Integer clientID, ClientThread clientThread) {
        originalSequence.add(clientID);
        clients.put(clientID, clientThread);
    }

    /**
     * Sequence of players
     * saved at ArrayList originalSequence [ID of players, first player -> last player]
     *
     */
    public void setSequence() {
        originalSequence = new ArrayList<>();
        clients = new HashMap<>();
        ArrayList<Integer> result = new ArrayList<>();
        HashMap<Integer, Integer> temp = new HashMap<>();

        for (ClientThread clientThread : Register.getNtwrkServer().getThreads().values()) {
            int[] dices = Register.getController().getManagement().rollDices();
            JSONObject event = new JSONObject();
            event.put("Spieler", clientThread.getClientID());
            event.put("Wurf", new JSONArray(dices));
            JSONObject dice = new JSONObject();
            dice.put("Würfelwurf", event);
            clientThread.msgAll(dice);

            if (!result.contains(dices[0] + dices[1])) {
                result.add(dices[0] + dices[1]);
                temp.put(clientThread.getClientID(), dices[0] + dices[1]);
            } else {
                setSequence();
                return;
            }
        }
        ArrayList<Integer> keys = new ArrayList<>(temp.keySet());
        ArrayList<Integer> values = new ArrayList<>(temp.values());
        Collections.sort(keys);
        Collections.sort(values);
        LinkedHashMap<Integer, Integer> sortedResult = new LinkedHashMap<>();

        Iterator<Integer> valueIt = values.iterator();
        int i = 0;
        while (valueIt.hasNext()) {
            Integer val = values.get(i);
            i++;
            Iterator<Integer> keyIt = keys.iterator();
            int j = 0;
            while (keyIt.hasNext()) {
                Integer key = keys.get(j);
                Integer compare = temp.get(key);
                j++;
                if (compare.equals(val)) {
                    sortedResult.put(key, val);
                    break;
                }
            }
            if (i == keys.size()) {
                break;
            }
        }
        originalSequence = new ArrayList<>(keys.size());
        ArrayList<Integer> temp1 = new ArrayList<>();
        for (Integer id : sortedResult.keySet()) {
            temp1.add(id);
        }
        Collections.reverse(temp1);
        originalSequence = temp1;
        LOGGER.info("Player sequence " + originalSequence);
        nextPlayer();
    }

    /**
     * Handle sequence for the next player. Set player active or inactive
     *
     */
    public void nextPlayer() {
        HashMap<Integer, Player> players = Register.getController().getAllPlayersId();
        try {
            Player me = players.get(clientThread.getClientID());
            int indexOfMe;
            switch (level) {
                case FIRST_ROUND:
                    if (players.get(originalSequence.get(0)).getStatus() == Status.WAIT_FOR_GAME_START) {
                        players.get(originalSequence.get(0)).setStatus(Status.BUILD_SETTLEMENT);
                        players.get(originalSequence.get(0)).setActive(true);
                        for (int id : players.keySet()) {
                            if (id != originalSequence.get(0)) {
                                setPlayerWait(id);
                            }
                        }
                        new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(originalSequence.get(0))).statusUpdate();
                    } else {
                        indexOfMe = originalSequence.indexOf(me.getId());
                        if (indexOfMe < originalSequence.size() - 1) {
                            players.get(originalSequence.get(indexOfMe + 1)).setStatus(Status.BUILD_SETTLEMENT);
                            players.get(originalSequence.get(indexOfMe + 1)).setActive(true);
                            setPlayerWait(me.getId());
                            new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(originalSequence.get(indexOfMe + 1))).statusUpdate();
                        } else {
                            level = Level.SECOND_ROUND;
                            nextPlayer();

                        }
                    }
                    break;
                case SECOND_ROUND:
                    if (toggle) {
                        toggle = false;
                        players.get(getReversedSequence().get(0)).setStatus(Status.BUILD_SETTLEMENT);
                        players.get(getReversedSequence().get(0)).setActive(true);
                        new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(getReversedSequence().get(0))).statusUpdate();
                    } else {
                        indexOfMe = getReversedSequence().indexOf(me.getId());
                        if (indexOfMe < getReversedSequence().size() - 1) {
                            players.get(getReversedSequence().get(indexOfMe + 1)).setStatus(Status.BUILD_SETTLEMENT);
                            players.get(getReversedSequence().get(indexOfMe + 1)).setActive(true);
                            setPlayerWait(me.getId());
                            new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(getReversedSequence().get(indexOfMe + 1))).statusUpdate();
                        } else {
                            level = Level.OTHER_ROUNDS;
                            nextPlayer();
                        }
                    }
                    break;
                case OTHER_ROUNDS: // TODO 2 roll the dice
                    if (!toggle) {
                        toggle = true;
                        players.get(originalSequence.get(0)).setStatus(Status.ROLL_DICE);
                        for (Player p : players.values()) {
                            p.setActive(false);
                        }
                        devCard = true;

                        players.get(originalSequence.get(0)).setActive(true);
                        new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(originalSequence.get(0))).statusUpdate();
                    } else {
                        indexOfMe = originalSequence.indexOf(me.getId());
                        if (indexOfMe < originalSequence.size() - 1) {
                            players.get(originalSequence.get(indexOfMe + 1)).setStatus(Status.ROLL_DICE);
                            for (Player p : players.values()) {
                                p.setActive(false);
                            }
                            players.get(originalSequence.get(indexOfMe + 1)).setActive(true);
                            devCard = true;
                            new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(originalSequence.get(indexOfMe + 1))).statusUpdate();
                        } else {
                            toggle = false;
                            devCard = true;
                            nextPlayer();
                        }
                    }
                    break;
                default:
                    LOGGER.warn("Case not covered!");
            }
        } catch (NullPointerException e) {
            LOGGER.catching(org.apache.logging.log4j.Level.ERROR, e);
        }
    }

    /**
     * Set player to status wait
     *
     * @param id player id
     */
    public void setPlayerWait(int id) {
        HashMap<Integer, Player> players = Register.getController().getAllPlayersId();
        players.get(id).setStatus(Status.WAIT);
        players.get(id).setActive(false);
        new ClientThreadEngine(Register.getNtwrkServer().getThreads().get(id)).statusUpdate();
    }

    /**
     * Reverse the sequence list
     *
     * @return reversedSequence ArrayList
     */
    private ArrayList<Integer> getReversedSequence() {
        ArrayList<Integer> reversedSequence = new ArrayList<>(originalSequence);
        Collections.reverse(reversedSequence);
        return reversedSequence;
    }

    public ArrayList<Integer> getOriginal() {
        return originalSequence;
    }


}
