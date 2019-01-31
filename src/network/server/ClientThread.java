package network.server;

import controller.Register;
import model.players.Status;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

class ClientThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(ClientThread.class.getName());
    private final HashMap<Integer, ClientThread> threads;
    private int clientID;
    private BufferedReader in = null;
    private OutputStreamWriter out = null;
    private Socket socket = null;
    private boolean ready = true;

    /**
     * ClientThread constructor
     *
     * @param socket  socket
     * @param threads HashMap of all other ClientThreads
     */
    ClientThread(Socket socket, HashMap<Integer, ClientThread> threads) {
        this.socket = socket;
        this.threads = threads;
    }

    /**
     * Initialize OutputStreamWrite and start up BufferedReader
     *
     */
    @Override
    public void run() {
        LOGGER.traceEntry();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            new ClientThreadEngine(this).welcome();
        } catch (IOException ioe) {
            LOGGER.catching(Level.ERROR, ioe);
        }

        while (!Thread.interrupted()) {
            try {
                String msg;
                if (in.read() != -1) {
                    if (ready && (msg = in.readLine()) != null) {
                        toggleReady();
                        String addBracket = "{" + msg;
                        String msgToString = " < " + clientID + "   " + addBracket;
                        LOGGER.info(msgToString);
                        Register.getViewStartServerController().appendConsoleOutputToTextArea(msgToString);
                        new ClientThreadLevelVerification(this, addBracket);
                    }
                } else {
                    LOGGER.warn("Lost connection.");
//                    LOGGER.catching(Level.ERROR, e);
                    Register.getController().getAllPlayersId().get(clientID).setStatus(Status.CONNECTION_LOST);
                    msgAllButThisClient(new ClientThreadEngine(this).jsonStatusUpdate(clientID));
                    JSONObject innerObj = new JSONObject();
                    innerObj.put("Nachricht", "Verbindung zu einem Spieler verloren");
                    innerObj.put("Sieger", -1);
                    JSONObject obj = new JSONObject();
                    obj.put("Spiel beendet", innerObj);
                    msgAllButThisClient(obj);
                    disconnect();
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                LOGGER.warn("Lost connection to " + clientID + ".");
                LOGGER.catching(Level.ERROR, e);
                Register.getController().getAllPlayersId().get(clientID).setStatus(Status.CONNECTION_LOST);
                msgAllButThisClient(new ClientThreadEngine(this).jsonStatusUpdate(clientID));
                JSONObject innerObj = new JSONObject();
                innerObj.put("Nachricht", "Verbindung zu einem Spieler verloren");
                innerObj.put("Sieger", -1);
                JSONObject obj = new JSONObject();
                obj.put("Spiel beendet", innerObj);
                msgAllButThisClient(obj);
                Register.getViewStartServerController().appendConsoleOutputToTextArea(e + "");
                LOGGER.warn("Disconnected");
                disconnect();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Disconnect from server, close socket
     *
     */
    public void disconnect() {
        LOGGER.traceEntry();
        try {
            out.close();
            in.close();
            socket.close();
            Register.getController().getAllPlayersId().remove(getClientID());
            threads.remove(getClientID());
        } catch (Exception e) {
            LOGGER.catching(Level.ERROR, e);
        }
    }

    /**
     * Send message (JSONObject) to client
     *
     * @param obj message (JSONObject)
     */
    synchronized void msgThisClient(Object obj) {
        try {
            String msg = obj.toString();
            if (out != null) {
                try {
                    threads.get(clientID).out.write(msg + "\n");
                    String msgToString = "   " + clientID + " > " + msg;
                    threads.get(clientID).out.flush();
                    Register.getViewStartServerController().appendConsoleOutputToTextArea(msgToString);
                    LOGGER.info(msgToString);
                } catch (IOException e) {
                    disconnect(); // TODO is this good?
                    LOGGER.catching(Level.ERROR, e);
                }
                out.flush();
            }
        } catch (IOException ioe) {
            LOGGER.catching(Level.ERROR, ioe);
        }
    }

    /**
     * Send message (JSONObject) to everyone
     *
     * @param obj message (JSONObject)
     */
    synchronized void msgAll(Object obj) {
        try {
            String msg = obj.toString();
            if (out != null) {
                try {
                    for (int key : threads.keySet()) {
                        threads.get(key).out.write(msg + "\n");
                        String msgToString = "   " + threads.get(key).getClientID() + " > " + msg;
                        threads.get(key).out.flush();
                        Register.getViewStartServerController().appendConsoleOutputToTextArea(msgToString);
                        LOGGER.info(msgToString);
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException ioe) {
                    LOGGER.catching(Level.ERROR, ioe);
                }
                out.flush();
            }
        } catch (IOException ioe) {
            LOGGER.catching(Level.ERROR, ioe);
        }
    }

    /**
     * Send message (JSONObject) to everyone but this client
     *
     * @param obj message (JSONObject)
     */
    synchronized void msgAllButThisClient(Object obj) {
        try {
            String msg = obj.toString();
            if (out != null) {
                try {
                    for (int key : threads.keySet()) {
                        if (key != clientID) {
                            threads.get(key).out.write(msg + "\n");
                            String msgToString = "   " + threads.get(key).getClientID() + " > " + msg;
                            threads.get(key).out.flush();
                            Register.getViewStartServerController().appendConsoleOutputToTextArea(msgToString);
                            LOGGER.info(msgToString);
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    LOGGER.catching(Level.ERROR, e);
                }
                out.flush();
            }
        } catch (IOException ioe) {
            LOGGER.catching(Level.ERROR, ioe);
        }

    }

    /**
     * Get client id
     *
     * @return client id (int)
     */
    int getClientID() {
        return clientID;
    }

    /**
     * Set client ID
     *
     * @param clientID client id (int9
     */
    void setClientID(int clientID) {
        this.clientID = clientID;
        LOGGER.info("clientID: " + this.clientID);
    }

    /**
     * Get all ClientThreads
     *
     * @return HashMap of all other ClientThreads
     */
    HashMap<Integer, ClientThread> getThreads() {
        return threads;
    }

    public void toggleReady() {
        ready = !ready;
    }
}