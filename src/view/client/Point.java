package view.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;

public class Point {
    private static final Logger LOGGER = LogManager.getLogger(Point.class.getName());
    private double x;
    private double y;
    private ArrayList<Integer> occupiedCorners=new ArrayList<Integer>();
    private int pointNumber;

//    public ArrayList<Point> allPointsOnACircle(Point middlePoint) {
//        ArrayList<Point> allPoints = new ArrayList<Point>();
//        for (double i = 0.0; i<360.0; i++){
//           Point pointOnCircle = new Point(middlePoint.getX()*(1+Math.cos(i)), middlePoint.getY()*(1+Math.sin(i)));
//           allPoints.add(pointOnCircle);
//        }return allPoints;
//    }

    /**
     * contructor
     *
     * @param x x axis
     * @param y y axis
     */
    public Point(double x, double y) {
        this.x = Math.floor(x * 100) / 100;
        this.y = Math.floor(y * 100) / 100;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * gets adjacent hexagons for building settlements and cities
     * @param coordinates points
     * @param controller
     * @return Bitset (hex)
     */
    public BitSet getAdjacentHexagons(Point coordinates, view.client.Controller controller) {
        String all = "\n";
        ArrayList<Integer> adjacentHexagons = new ArrayList<>();
        double xToFind = Math.round(coordinates.getX());
        double yToFind = Math.round(coordinates.getY());
        for (int i = 0; i < controller.getCanvasBoard().getAllHex().size(); i++) {
            for (int j = 0; j < 6; j++) {
                double xInList = Math.round(controller.getCanvasBoard().getAllHex().get(i).getAllPoints().get(j).getX());
                double yInList = Math.round(controller.getCanvasBoard().getAllHex().get(i).getAllPoints().get(j).getY());
                if (xToFind == xInList && yToFind == yInList) {
                    adjacentHexagons.add(i);
                }
                all = all + String.format("%30s %7s%7s\n", "Searching for", xToFind, yToFind)
                        + String.format("%30s %7s%7s\n", "Coordinates found in list", xInList, yInList)
                        + String.format("%30s %14s", "IDs of adjacent hexagons", adjacentHexagons) + "\n";
            }
        }
        LOGGER.debug(all);
        BitSet tmp = new BitSet();
        if (adjacentHexagons.size() == 3) {
            tmp = controller.getCanvasBoard().getBoard().makeIdCorner(adjacentHexagons.get(0),
                    adjacentHexagons.get(1), adjacentHexagons.get(2));
            LOGGER.info(tmp);
        }
        return tmp;
    }

    /**
     * gets the correct point for settlement/citie
     * @param xUpperCornerLeft
     * @param xLowerCornerRight
     * @param yUpperCornerLeft
     * @param yLowerCornerRight
     * @param controller
     * @return
     */
    public Point getTheRightPoint(double xUpperCornerLeft, double xLowerCornerRight,
                                  double yUpperCornerLeft, double yLowerCornerRight, view.client.Controller controller) {
        String all = "";
        for (int i = 0; i < controller.getCanvasBoard().getAllHex().size(); i++) {
            for (int j = 0; j < 6; j++) {
                double x=controller.getCanvasBoard().getAllHex().get(i).getAllPoints().get(j).getX();
                double y=controller.getCanvasBoard().getAllHex().get(i).getAllPoints().get(j).getY();
                if (x>xUpperCornerLeft&&x<xLowerCornerRight&&y>yUpperCornerLeft&&y<yLowerCornerRight){
                    double mx=x;
                    double my=y;
                    all = all + String.format("Calculated Point in List %7s%7s", mx, my);
                    LOGGER.debug(all);
                    Point mPoint = new Point(mx, my);
                    return mPoint;
                }
            }
        }all = all + "Corner doesn't exist";
        LOGGER.debug(all);
        return null;
    }
}

