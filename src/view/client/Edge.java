package view.client;


import controller.Register;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;

public class Edge {
    private static final Logger LOGGER = LogManager.getLogger(Edge.class.getName());
    private Point corner1;
    private Point corner2;
    private Point corner3;
    private Point corner4;
    private String color;
    private int roads;
    private ArrayList<Edge> allEdges = new ArrayList<>();
    private ArrayList<Integer> edgeNumberPainted = new ArrayList<>();

    /**
     * contructor with no parameter
     */
    public Edge() {
    }

    /**
     * constructor with four corners
     *
     * @param rectangleCorner1 first corner
     * @param rectangleCorner2 secnd corner
     * @param rectangleCorner3 third corner
     * @param rectangleCorner4 fourth corner
     */
    public Edge(Point rectangleCorner1, Point rectangleCorner2, Point rectangleCorner3, Point rectangleCorner4) {
        this.corner1 = rectangleCorner1;
        this.corner2 = rectangleCorner2;
        this.corner3 = rectangleCorner3;
        this.corner4 = rectangleCorner4;
    }

    /**
     * returns first corner
     * @return corner1
     */
    public Point getCorner1() {
        return corner1;
    }

    /**
     * returns second corner
     * @return croner2
     */
    public Point getCorner2() {
        return corner2;
    }

    /**
     * returns third corner
     * @return corner3
     */
    public Point getCorner3() {
        return corner3;
    }

    /**
     * returns fourth corner
     * @return coner4
     */
    public Point getCorner4() {
        return corner4;
    }

    /**
     * returns allEdges
     * @return arraylist with edges
     */
    public ArrayList<Edge> getAllEdges() {
        return allEdges;
    }

    /**
     * saves all Edges in array list
     * @param allEdges
     */
    public void setAllEdges(ArrayList<Edge> allEdges) {
        this.allEdges = allEdges;
        //logger
        String all = "\nEdges-Objekte : " + this.allEdges;
    }

    /**
     * returns correct edge
     * @param point x and y axis
     * @param controller
     * @return correct edge
     */
    public Edge getTheRightEdge(Point point, view.client.Controller controller) {
        Edge edge = new Edge();
        for (int i = 0; i < controller.getCanvasBoard().getAllEdges().size(); i++) {

            if ((point.getX() > controller.getCanvasBoard().getAllEdges().get(i).getCorner3().getX() &&
                    point.getY() > controller.getCanvasBoard().getAllEdges().get(i).getCorner2().getY() &&
                    point.getX() < controller.getCanvasBoard().getAllEdges().get(i).getCorner1().getX() &&
                    point.getY() < controller.getCanvasBoard().getAllEdges().get(i).getCorner4().getY())    //Edge 0
                    ||
                    (point.getX() > controller.getCanvasBoard().getAllEdges().get(i).getCorner2().getX() &&
                    point.getY() > controller.getCanvasBoard().getAllEdges().get(i).getCorner3().getY() &&
                    point.getX() < controller.getCanvasBoard().getAllEdges().get(i).getCorner4().getX() &&
                            point.getY() < controller.getCanvasBoard().getAllEdges().get(i).getCorner1().getY())    //Edge 3
                    ||
                    (point.getX() > controller.getCanvasBoard().getAllEdges().get(i).getCorner3().getX() &&
                            point.getY() > controller.getCanvasBoard().getAllEdges().get(i).getCorner4().getY() &&
                            point.getX() < controller.getCanvasBoard().getAllEdges().get(i).getCorner1().getX() &&
                            point.getY() < controller.getCanvasBoard().getAllEdges().get(i).getCorner2().getY())    //Edge 1
                    ||
                    (point.getX() > controller.getCanvasBoard().getAllEdges().get(i).getCorner2().getX() &&
                    point.getY() > controller.getCanvasBoard().getAllEdges().get(i).getCorner1().getY() &&
                    point.getX() < controller.getCanvasBoard().getAllEdges().get(i).getCorner4().getX() &&
                            point.getY() < controller.getCanvasBoard().getAllEdges().get(i).getCorner3().getY()) //Edge 4
                    //below: -not tested, works so far ~ ~
                    ||
                    (point.getX() < controller.getCanvasBoard().getAllEdges().get(i).getCorner1().getX() + 20 &&
                            point.getY() < controller.getCanvasBoard().getAllEdges().get(i).getCorner1().getY() - 10 &&
                    point.getX() > controller.getCanvasBoard().getAllEdges().get(i).getCorner3().getX() - 20 &&
                            point.getY() > controller.getCanvasBoard().getAllEdges().get(i).getCorner3().getY() + 10)   //Edge 2
                    //+15+15-15-15
                    //+20-10-20+10
                    ||
                    (point.getX() < controller.getCanvasBoard().getAllEdges().get(i).getCorner4().getX() + 20 &&
                            point.getY() < controller.getCanvasBoard().getAllEdges().get(i).getCorner4().getY() - 10 &&
                            point.getX() > controller.getCanvasBoard().getAllEdges().get(i).getCorner2().getX() - 20 &&
                            point.getY() > controller.getCanvasBoard().getAllEdges().get(i).getCorner2().getY() + 10)   //Edge 5
                    ) {
                edge = controller.getCanvasBoard().getAllEdges().get(i);
                return edge;
            }
        }
        return null;
    }

    /**
     * returns adjacent hexagons
     * @param edge
     * @return
     */
    public BitSet getAdjacentHexagonsForEdges(Edge edge) {
        ArrayList<Point> pointsInEdge = new ArrayList<>();
        ArrayList<Point> tmp = new ArrayList<>();
        BitSet adjacentHexagons1 = new BitSet();
        BitSet adjacentHexagons2 = new BitSet();
        for (int i = 0; i < Register.getViewClientController().getCanvasBoard().getAllHex().size(); i++) {
            for (int j = 0; j < 6; j++) {
                Point pointInList = Register.getViewClientController().getCanvasBoard().getAllHex().get(i).getAllPoints().get(j);
                /* Edge 0 = unten rechts "/"
                *  Edge 1 = unten links "\"
                *  Edge 2 = links "|" usw (gg Uhrzeigersinn)
                * */
                if (//Edge 0 /
                        (pointInList.getX() > edge.getCorner3().getX() &&
                                pointInList.getY() > edge.getCorner2().getY() &&
                                pointInList.getX() < edge.getCorner1().getX() &&
                                pointInList.getY() < edge.getCorner4().getY())
                                ||  //Edge 3 /
                                (pointInList.getX() > edge.getCorner2().getX() &&
                                        pointInList.getY() > edge.getCorner3().getY() &&
                                        pointInList.getX() < edge.getCorner4().getX() &&
                                        pointInList.getY() < edge.getCorner1().getY())
                                ||  //Edge 1 \
                                (pointInList.getX() > edge.getCorner3().getX() &&
                                        pointInList.getY() > edge.getCorner4().getY() &&
                                        pointInList.getX() < edge.getCorner1().getX() &&
                                        pointInList.getY() < edge.getCorner2().getY())
                                ||  //Edge 4 \
                                (pointInList.getX() > edge.getCorner2().getX() &&
                                        pointInList.getY() > edge.getCorner1().getY() &&
                                        pointInList.getX() < edge.getCorner4().getX() &&
                                        pointInList.getY() < edge.getCorner3().getY())
                                || //Edge 2 |
                                (pointInList.getX() > edge.getCorner3().getX() - 4 &&
                                pointInList.getY() > edge.getCorner3().getY() - 4 &&
                                pointInList.getX() < edge.getCorner1().getX() + 4 &&
                                pointInList.getY() < edge.getCorner1().getY() + 4)
                                //-4-4+4+4
                                //-15 0 +15 0
                                || //Edge 5 |
                                (pointInList.getX() > edge.getCorner2().getX() - 4 &&
                                        pointInList.getY() > edge.getCorner2().getY() - 4 &&
                                        pointInList.getX() < edge.getCorner4().getX() + 4 &&
                                        pointInList.getY() < edge.getCorner4().getY() + 4)
                        ) {
                    pointsInEdge.add(pointInList);
                    //                pointsInEdge.get(i);
                }
            }
        }
        adjacentHexagons1 = pointsInEdge.get(0).getAdjacentHexagons(pointsInEdge.get(0), Register.getViewClientController());
        adjacentHexagons2 = pointsInEdge.get(1).getAdjacentHexagons(pointsInEdge.get(1), Register.getViewClientController());
        adjacentHexagons1.and(adjacentHexagons2);
        return adjacentHexagons1;
    }


    /**
     * get all painted edges
     *
     * @return
     */
    public ArrayList<Integer> getEdgeNumberPainted() {
        return edgeNumberPainted;
    }

    /**
     * get array list with edge numbers
     * @param edge
     * @param controller
     * @return
     */
    public Integer getEdgeNumber(Edge edge, view.client.Controller controller) {
        ArrayList<Edge> allEdges = controller.getCanvasBoard().getAllEdges();
        int edgeNumber;
        for (int i = 0; i < allEdges.size(); i++) {
            if (edge == allEdges.get(i)) {
                edgeNumber = i;
            } else {
            }
        }
        return null;
    }
}