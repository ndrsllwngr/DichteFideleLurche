package network.server;

import controller.Controller;
import controller.Register;
import model.Management;
import model.board.DefaultBoard;
import model.board.RandomBoard;
import network.client.Client;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Server.class.getName());
    private static ServerSocket s;
    private static HashMap<Integer, ClientThread> threads = new HashMap<>();
    private static boolean cheatMode;
    private boolean status = true;
    private int maxClientsCount = 0;
    private int uniqueID = 0;

    /**
     * Server constructor
     *
     * @param maxClientsCount max clients count (int)
     * @param map             default or random map (string)
     */
    public Server(int maxClientsCount, String map) {
        this.maxClientsCount = maxClientsCount;
        Register.setNtwrkServer(this);
        Register.setController(new Controller());
        Register.getController().resetAllPlayersId();
        ClientThreadSequence.reset();
        if (map.equals("Default")) {
            Register.getController().setBoard(new DefaultBoard());
        } else if (map.equals("Lumber")) {
            Register.getController().setBoard(new RandomBoard(true));
        } else if (map.equals("Random")) {
            Register.getController().setBoard(new RandomBoard(false));
        }
        Register.getController().setManagement(new Management());
        Register.getController().getManagement().setBoard(Register.getController().getBoard());
        Register.getController().getManagement().setRobber(Register.getController().getBoard());
        Register.getController().getManagement().setAllPlayersId((Register.getController().getAllPlayersId()));
        startUp();
        new Thread(this).start();
    }

    /**
     * Server constructor
     *
     * @param maxClientsCount max clients count (int)
     * @param map             default or random map (string)
     */
    public Server(int maxClientsCount, String map, int aiCount) {
        this.maxClientsCount = maxClientsCount;
        Register.setNtwrkServer(this);
        Register.setController(new Controller());
        Register.getController().resetAllPlayersId();
        ClientThreadSequence.reset();
        if (map.equals("Default")) {
            Register.getController().setBoard(new DefaultBoard());
        } else if (map.equals("Lumber")) {
            Register.getController().setBoard(new RandomBoard(true));
        } else if (map.equals("Random")) {
            Register.getController().setBoard(new RandomBoard(false));
        }
        Register.getController().setManagement(new Management());
        Register.getController().getManagement().setBoard(Register.getController().getBoard());
        Register.getController().getManagement().setRobber(Register.getController().getBoard());
        Register.getController().getManagement().setAllPlayersId((Register.getController().getAllPlayersId()));
        startUp();
        new Thread(this).start();
        for(int i = 0; i < aiCount; i++){
            new Client("localhost", 10003);
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isCheatMode() {
        return cheatMode;
    }

    public static void setCheatMode(boolean cheatMode) {
        Server.cheatMode = cheatMode;
    }

    /**
     * Start server socket
     *
     */
    private void startUp() {
        try {
            s = new ServerSocket(10003);
            String tmp = "Started server..."
                    + String.format("\n%1$-12s", "hostname: ") + InetAddress.getLocalHost().getHostName()
                    + String.format("\n%1$-12s", "ip: ") + InetAddress.getLocalHost().getHostAddress()
                    + String.format("\n%1$-12s", "port: ") + s.getLocalPort();
            Register.getViewStartServerController().appendConsoleOutputToTextArea(tmp + "\n");
            LOGGER.info(tmp);
        } catch (IOException ioe) {
            Register.getViewStartServerController().appendConsoleOutputToTextArea("Could not create " +
                    "startServer socket status port " + s.getLocalPort() + ". Quitting.");
            LOGGER.catching(Level.ERROR, ioe);
            System.exit(-1);
        }
    }

    /**
     * Create for each new connection another thread, until max clients count is reached
     *
     */
    @Override
    public void run() {
        Socket clientSocket;
        while (status) {
            try {
                clientSocket = s.accept();
//                for (int key : threads.keySet()) {
//                    JSONObject ping = new JSONObject();
//                    ping.put("Serverantwort", "Ping");
//                    threads.get(key).msgThisClient(ping); // TODO check if someone disconnected
//                }
                uniqueID++;
                LOGGER.info(threads.size());
                if (threads.size() < maxClientsCount) {
//                        clientSocket.setKeepAlive(true);
                    threads.put(uniqueID, new ClientThread(clientSocket, threads));
                    threads.get(uniqueID).setClientID(uniqueID);
                    threads.get(uniqueID).start();
                    LOGGER.info("Id: " + uniqueID + ", Socket: " + clientSocket);
                } else {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    JSONObject tmp = new JSONObject();
                    tmp.put("Serverantwort", "Try later.");
                    os.println(tmp);
                    LOGGER.info("Kicked: " + uniqueID + ", " + clientSocket);
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                LOGGER.catching(Level.ERROR, e);
            }
        }
    }

    /**
     * Set server status
     *
     * @param tmp new boolean value
     */
    public void setServerStatus(Boolean tmp) {
        status = tmp;
        if (!status) {
            LOGGER.info(threads.size());
            try {
                s.close();
                threads = new HashMap<>();
                LOGGER.info("Server stopped.");
            } catch (Exception ioe) {
                LOGGER.catching(Level.ERROR, ioe);
                System.exit(-1);
            }
        }
    }

    /**
     * Get max clients count
     *
     * @return mac clients count (int)
     */
    int getMaxClientsCount() {
        return maxClientsCount;
    }

    public HashMap<Integer, ClientThread> getThreads() {
        return threads;
    }

    public int getClientThreadID(int playerID) {
        for (int i : getThreads().keySet()) {
            if (getThreads().get(i).getClientID() == playerID) {
                return i;
            }
        }
        return -1;
    }
}

