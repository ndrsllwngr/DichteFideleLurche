package network.client;

import controller.Register;
import model.players.Ai;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Client.class.getName());
    private final boolean ai;
    private int id;
    private boolean ready;
    private InetSocketAddress address = null;
    private Socket socket = null;
    private BufferedReader in = null;
    private OutputStreamWriter out = null;
    private int counterDevCard =0;
    private Ai thisAi;
    private ClientListener clientListener;
    private boolean cmdLine = false;

    /**
     * Client constructor
     *
     * @param hostName   host name
     * @param portNumber port number
     */
    public Client(String hostName, int portNumber, boolean ai) {
        address = new InetSocketAddress(hostName, portNumber);
        clientListener = new ClientListener(this);
        this.ai = ai;
        ready = true;
        Register.setController(new controller.Controller());
        Register.setNtwrkClient(this);
        connect();
        if (socket != null) {
            new Thread(this).start();
        } else {
            Thread.currentThread().interrupt();
        }
    }


    public Client(String[] args) {
        ArrayList<String> tmp = new ArrayList<>();
        for (String s : args) {
            tmp.add(s);
        }
        String[] ipAndPort;
        String name = "DFL (KI)";
        String color = "Weiß";
        switch (tmp.size()) {
            case 1:
                ipAndPort = tmp.get(0).split(":");
                break;
            case 2:
                color = tmp.get(0);
                ipAndPort = tmp.get(1).split(":");
                break;
            case 3:
                name = tmp.get(0);
                color = tmp.get(1);
                ipAndPort = tmp.get(2).split(":");
                break;
            default:
                ipAndPort = tmp.get(0).split(":");
                LOGGER.info("Case not covered!");
        }
        this.cmdLine = true;
        address = new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
        clientListener = new ClientListener(this);
        Register.setController(new controller.Controller());
        Register.setNtwrkClient(this);
        this.ai = true;
        clientListener.setCmdLineColor(color);
        clientListener.setCmdLineName(name);
        ready = true;
        connect();
        if (socket != null) {
            new Thread(this).start();
        } else {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Client constructor
     *
     * @param hostName   host name
     * @param portNumber port number
     */
    public Client(String hostName, int portNumber) {
        address = new InetSocketAddress(hostName, portNumber);
        clientListener = new ClientListener(this);
        this.ai = true;
        ready = true;
        connect();
        if (socket != null) {
            new Thread(this).start();
        } else {
            Thread.currentThread().interrupt();
        }
    }

    public boolean getCmdLine() {
        return cmdLine;
    }

    /**
     * Initialize OutputStreamWriter and start up BufferedReader
     *
     */
    @Override
    public void run() {
        if (socket != null) {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                out = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            } catch (IOException ioe) {
                LOGGER.catching(Level.ERROR, ioe);
            }
            while (!Thread.interrupted()) {
                receiveMsg();
            }

        }
    }

    /**
     * Connect to server via Hostname and port
     *
     */
    public void connect() {
        try {
            socket = new Socket(address.getHostName(), address.getPort());
            LOGGER.info(socket);
        } catch (IOException ioe) {
            LOGGER.catching(Level.ERROR, ioe);
            socket = null;
        }
        if (socket == null) {
            new ClientListener(this).writeToConsole(ClientListener.warn + "Server not found");
            LOGGER.info("Server not found");
            return;
        }
        new ClientListener(this).writeToConsole(ClientListener.info + "Connected to " + address.getHostName() + ", " + address.getPort());
        LOGGER.info("Connected to " + address.getHostName() + ", " + address.getPort());
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
        } catch (Exception e) {
            LOGGER.catching(Level.ERROR, e);
        }
    }

    /**
     * Send chat message to server
     *
     * @param msg text
     */
    public synchronized void sendMsg(String msg) {
            JSONObject innerObj = new JSONObject();
            innerObj.put("Nachricht", msg);
            JSONObject outerObj = new JSONObject();
            outerObj.put("Chatnachricht senden", innerObj);
            if (out != null) {
                try {
                    out.write(outerObj.toString() + "\n");
                    out.flush();
                    LOGGER.info(outerObj.toString());
                } catch (IOException ioe) {
                    LOGGER.catching(Level.ERROR, ioe);
                }
            }
    }

    /**
     * Send json object to server
     *
     * @param obj json object
     */
    public synchronized void sendMsg(Object obj) {
            String msg = obj.toString();
            if (out != null) {
                try {
                    out.write(msg + "\n");
                    out.flush();
                    LOGGER.info(msg);
                } catch (IOException ioe) {
                    LOGGER.catching(Level.ERROR, ioe);
                }
            }
    }
    /**
     * Read incoming messages from server
     *
     */
    public synchronized void receiveMsg() {
        try {
            String msg;
            if (getAi()) {
                if (ready && (msg = in.readLine()) != null) {
                    toggleReady();
                    LOGGER.info(msg);
                    if (!cmdLine) {
                        clientListener.handleMsg(msg);
                        try {
                            Thread.sleep(85);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        clientListener.handleCMDLine(msg);
                    }
                }
            } else {
                if (ready && (msg = in.readLine()) != null) {
                    toggleReady();
                    LOGGER.info(msg);
                    if (!cmdLine) {
                        clientListener.handleMsg(msg);
                        try {
                            Thread.sleep(85);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        clientListener.handleCMDLine(msg);
                    }
                }
            }
        } catch (IOException ioe) {
            LOGGER.warn("Lost connection.");
            LOGGER.catching(Level.ERROR, ioe);
            disconnect();
            Thread.currentThread().interrupt();
        }

    }

    /**
     * Check if client is an AI
     *
     * @return boolean value (true if client is AI)
     */
    public boolean getAi() {
        return ai;
    }

    /**
     * Toggle if AI is ready or Client has to wait
     */
    public void toggleReady() {
        ready = !ready;
    }

    /**
     * Development card counter to disable the button developmentcard button
     *
     * @return counterdevCard
     */
    public int getCounterDevCard() {
        return counterDevCard;
    }

    public void setCounterDevCard(int counterDevCard) {
        this.counterDevCard = counterDevCard;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ai getThisAi() {
        return thisAi;
    }

    public void setThisAi(Ai thisAi) {
        this.thisAi = thisAi;
    }
}

