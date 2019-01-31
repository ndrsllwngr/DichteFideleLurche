package view.client;

import java.util.ArrayList;

public class Corner {

    private Hexagon tile1;
    private Hexagon tile2;
    private Hexagon tile3;
    private int settlements, cities;
    private String color;
    private ArrayList<Corner> allSettlements = new ArrayList<>();
    private ArrayList<Corner> allCities = new ArrayList<>();

    /**
     * contructor which saves 3 Hexagons and a color
     *
     * @param tile1 first tile
     * @param tile2 second tile
     * @param tile3 third tile
     * @param color color
     */
    public Corner(Hexagon tile1, Hexagon tile2, Hexagon tile3, String color) {
        this.tile1 = tile1;
        this.tile2 = tile2;
        this.tile3 = tile3;
        this.color = color;
    }


    public ArrayList<Corner> getAllSettlements() {
        return allSettlements;
    }

    public void setAllSettlements(ArrayList<Corner> allSettlements) {
        this.allSettlements = allSettlements;
    }

    public ArrayList<Corner> getAllCities() {
        return allCities;
    }

    public void setAllCities(ArrayList<Corner> allCities) {
        this.allCities = allCities;
    }

}
