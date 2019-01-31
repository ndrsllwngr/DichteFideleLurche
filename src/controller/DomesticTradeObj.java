package controller;

import model.Resource;
import model.players.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class DomesticTradeObj {
    private static final Logger LOGGER = LogManager.getLogger(DomesticTradeObj.class.getName());
    private int tradeID;
    private int playerID;
    private ArrayList<Resource> offer = new ArrayList<>();
    private ArrayList<Resource> request = new ArrayList<>();
    private HashMap<Integer, Player> potentialPartners = new HashMap<>();
    private HashMap<Integer, Player> acceptedPartners = new HashMap<>();

    /**
     * Handle domestic trade
     *
     * @param tradeID  int -> trade id
     * @param playerID int -> player who wants to trade
     * @param offer    ArrayList resource
     * @param request  ArrayList resource
     */
    public DomesticTradeObj(int tradeID, int playerID, ArrayList<Resource> offer, ArrayList<Resource> request) {
        this.tradeID = tradeID;
        this.playerID = playerID;
        this.offer = offer;
        this.request = request;
        this.potentialPartners = new HashMap<>(Register.getController().getAllPlayersId());
        this.potentialPartners.remove(playerID);
        LOGGER.info(tradeID + ", " + playerID + ", " + offer + ", " + request + ", " + potentialPartners);
    }

    public int getTradeID() {
        return tradeID;
    }

    public void setTradeID(int tradeID) {
        this.tradeID = tradeID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public ArrayList<Resource> getOffer() {
        return offer;
    }

    public void setOffer(ArrayList<Resource> offer) {
        this.offer = offer;
    }

    public ArrayList<Resource> getRequest() {
        return request;
    }

    public void setRequest(ArrayList<Resource> request) {
        this.request = request;
    }

    public void removePartner(int partner) {
        potentialPartners.remove(partner);
    }

    public HashMap<Integer, Player> getPotentialPartners(){
        return potentialPartners;
    }

    public HashMap<Integer, Player> getAcceptedPartners() {
        return acceptedPartners;
    }

    public void setAcceptedPartners(HashMap<Integer, Player> acceptedPartners) {
        this.acceptedPartners = acceptedPartners;
    }

    public void makePartnerAccept(int id) {
        Player p  = getPotentialPartners().get(id);
        getAcceptedPartners().put(id,p);
        getPotentialPartners().remove(id);
    }
}
