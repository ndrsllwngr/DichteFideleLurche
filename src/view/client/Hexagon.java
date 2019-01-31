package view.client;

import java.util.ArrayList;

public class Hexagon {

    private ArrayList<Hexagon> allHex = new ArrayList<>();
    private ArrayList<Point> allPoints = new ArrayList<>();

    /**
     * constructor
     */
    public Hexagon() {
    }

    /**
     * contructor with an arr list of all points
     *
     * @param allPoints
     */
    public Hexagon(ArrayList<Point> allPoints) {
        this.allPoints = allPoints;
    }

    /**
     * returns arraylistwith all points
     * @return
     */
    public ArrayList<Point> getAllPoints() {
        return allPoints;
    }

    /**
     * sets an array list with all hexagons
     * @param allHex
     */
    public void setAllHex(ArrayList<Hexagon> allHex) {
        this.allHex = allHex;
    }

}
