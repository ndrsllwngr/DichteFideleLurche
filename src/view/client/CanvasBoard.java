package view.client;

import controller.Register;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import model.board.*;
import model.players.Player;
import model.players.Status;
import network.client.ClientWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Observable;
import java.util.Observer;

public class CanvasBoard implements Observer {

    private static final Logger LOGGER = LogManager.getLogger(CanvasBoard.class.getName());
    private static ArrayList<Hexagon> allHex = new ArrayList<>();
    private static ArrayList<Point> allPointsOfHex = new ArrayList<>();
    Image nt2 = new Image("/Numbertoken/2.png");
    ImagePattern two = new ImagePattern(nt2);
    Image nt3 = new Image("/Numbertoken/3.png");
    ImagePattern three = new ImagePattern(nt3);
    Image nt4 = new Image("/Numbertoken/4.png");
    ImagePattern four = new ImagePattern(nt4);
    Image nt5 = new Image("/Numbertoken/5.png");
    ImagePattern five = new ImagePattern(nt5);
    Image nt6 = new Image("/Numbertoken/6.png");
    ImagePattern six = new ImagePattern(nt6);
    Image nt8 = new Image("/Numbertoken/8.png");
    ImagePattern eight = new ImagePattern(nt8);
    Image nt9 = new Image("/Numbertoken/9.png");
    ImagePattern nine = new ImagePattern(nt9);
    Image nt10 = new Image("/Numbertoken/10.png");
    ImagePattern ten = new ImagePattern(nt10);
    Image nt11 = new Image("/Numbertoken/11.png");
    ImagePattern eleven = new ImagePattern(nt11);
    Image nt12 = new Image("/Numbertoken/12.png");
    ImagePattern twelve = new ImagePattern(nt12);
    Image robberImg = new Image("/robber_b_w.png");
    ImagePattern robber = new ImagePattern(robberImg);
    //Cities and Settlements:
    Image blueCityImg = new Image("/BuildingBlocks/City_blue3.png");
    Image redCityImg = new Image("/BuildingBlocks/City_red3.png");
    Image orangeCityImg = new Image("/BuildingBlocks/City_orange3.png");
    Image whiteCityImg = new Image("/BuildingBlocks/City_white2.png");
    Image neutralCity = new Image("/BuildingBlocks/City_neutral2.png");
    ImagePattern city = new ImagePattern(neutralCity);
    ImagePattern blueCity = new ImagePattern(blueCityImg);
    ImagePattern redCity = new ImagePattern(redCityImg);
    ImagePattern orangeCity = new ImagePattern(orangeCityImg);
    ImagePattern whiteCity = new ImagePattern(whiteCityImg);
    Image blueSettlement = new Image("/BuildingBlocks/Settlement_blue3.png");
    Image redSettlement = new Image("/BuildingBlocks/Settlement_red3.png");
    Image orangeSettlement = new Image("/BuildingBlocks/Settlement_orange3.png");
    Image whiteSettlement = new Image("/BuildingBlocks/Settlement_white2.png");
    Image neutralSettlement = new Image("/BuildingBlocks/Settlement_neutral2.png");
    ImagePattern settlement = new ImagePattern(neutralSettlement);
    ImagePattern blueSettle = new ImagePattern(blueSettlement);
    ImagePattern redSettle = new ImagePattern(redSettlement);
    ImagePattern orangeSettle = new ImagePattern(orangeSettlement);
    ImagePattern whiteSettle = new ImagePattern(whiteSettlement);
    Image leftBotImg = new Image("/Jetty/left_bottom.png");
    Image leftCenImg = new Image("/Jetty/left_center.png");
    Image leftTopImg = new Image("/Jetty/left_top.png");
    Image rightBotImg = new Image("/Jetty/right_bottom.png");
    Image rightCenImg = new Image("/Jetty/right_center.png");
    Image rightTopImg = new Image("/Jetty/right_top.png");
    ImagePattern leftBot = new ImagePattern(leftBotImg);
    ImagePattern leftCen = new ImagePattern(leftCenImg);
    ImagePattern leftTop = new ImagePattern(leftTopImg);
    ImagePattern rightBot = new ImagePattern(rightBotImg);
    ImagePattern rightCen = new ImagePattern(rightCenImg);
    ImagePattern rightTop = new ImagePattern(rightTopImg);

    private ArrayList<Edge> allEdges = new ArrayList<>();
    private BitSet robberPos = new BitSet();
    private int index = 0;
    private int robberIndex;
    private Edge edge;
    private Hexagon hexagon;
    private ArrayList<Double> possibleStreetArea = new ArrayList<>();
    private ArrayList<Point> allCenters = new ArrayList<>();
    private ArrayList<Integer> allCentersIndex = new ArrayList<>();
    private BitSet newRobberPos = new BitSet();
    private ArrayList<String> otherPlayers = new ArrayList<>();
    private ArrayList<String> otherPlayersColor = new ArrayList<>();

    /**
     * contructor canvasBoard
     */
    public CanvasBoard() {
        Register.setCanvasBoard(this);
        Register.addObserver();
    }


    /**
     * draws HexagonBoard row after row
     *
     * @param r radius of a Hex
     */
    public void drawHexagonBoardLayer1(int r) {
        Hexagon hexagon = new Hexagon();
        // row one ( water )
        for (int i = 1; i < 5; i++) {
            double distance = i * Math.sqrt(3) * r + 0.5 * Math.sqrt(3) * r;
            double[] x = calcHexX(distance, r);
            double[] y = calcHexy(0, r);
            Tile tile = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));
            drawBridge(tile, x, y);
            Register.getViewClientController().gc1.setFill(setTileImg(index));
            Register.getViewClientController().gc1.setStroke(Color.RED);

            Register.getViewClientController().gc1.fillPolygon(x, y, 6);
            Tile tmp = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));

            for (int j = 0; j < 6; j++) {
                allPointsOfHex.add(new Point(x[j], y[j]));
            }
            allHex.add(new Hexagon(allPointsOfHex));
            allPointsOfHex = new ArrayList<>();

        }

        //row two
        for (int i = 1; i < 6; i++) {
            double distance = i * Math.sqrt(3) * r;
            double[] x = calcHexX(distance, r);
            double[] y = calcHexy(1.5 * r, r);
            Tile tile = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));
            drawBridge(tile, x, y);
            Register.getViewClientController().gc1.setFill(setTileImg(index));
            Register.getViewClientController().gc1.fillPolygon(x, y, 6);
            for (int j = 0; j < 6; j++) {
                allPointsOfHex.add(new Point(x[j], y[j]));
            }
            allHex.add(new Hexagon(allPointsOfHex));
            allPointsOfHex = new ArrayList<>();
            setNumberTokens(index - 1);
        }

        //row three
        for (int i = 0; i < 6; i++) {
            double distance = i * Math.sqrt(3) * r + 0.5 * Math.sqrt(3) * r;
            Register.getViewClientController().gc1.setStroke(Color.BLACK);
            double[] x = calcHexX(distance, r);
            double[] y = calcHexy(3 * r, r);
            Tile tile = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));
            drawBridge(tile, x, y);
            Register.getViewClientController().gc1.setFill(setTileImg(index));
            Register.getViewClientController().gc1.fillPolygon(x, y, 6);
            for (int j = 0; j < 6; j++) {
                allPointsOfHex.add(new Point(x[j], y[j]));
            }
            allHex.add(new Hexagon(allPointsOfHex));
            allPointsOfHex = new ArrayList<>();
            setNumberTokens(index - 1);
        }

        //row four ( center )
        for (int i = 0; i < 7; i++) {
            double distance = i * Math.sqrt(3) * r;
            Register.getViewClientController().gc1.setStroke(Color.BLACK);
            double[] x = calcHexX(distance, r);
            double[] y = calcHexy(4.5 * r, r);
            Tile tile = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));
            drawBridge(tile, x, y);
            Register.getViewClientController().gc1.setFill(setTileImg(index));
            Register.getViewClientController().gc1.fillPolygon(x, y, 6);
            for (int j = 0; j < 6; j++) {
                allPointsOfHex.add(new Point(x[j], y[j]));
            }
            allHex.add(new Hexagon(allPointsOfHex));
            allPointsOfHex = new ArrayList<>();
            setNumberTokens(index - 1);
        }

        //row five
        for (int i = 0; i < 6; i++) {
            double distance = i * Math.sqrt(3) * r + 0.5 * Math.sqrt(3) * r;
            Register.getViewClientController().gc1.setStroke(Color.BLACK);
            double[] x = calcHexX(distance, r);
            double[] y = calcHexy(6 * r, r);
            Tile tile = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));
            drawBridge(tile, x, y);
            Register.getViewClientController().gc1.setFill(setTileImg(index));
            Register.getViewClientController().gc1.fillPolygon(x, y, 6);
            for (int j = 0; j < 6; j++) {
                allPointsOfHex.add(new Point(x[j], y[j]));
            }
            allHex.add(new Hexagon(allPointsOfHex));
            allPointsOfHex = new ArrayList<>();
            setNumberTokens(index - 1);
        }

        //row six
        for (int i = 1; i < 6; i++) {
            double distance = i * Math.sqrt(3) * r;
            Register.getViewClientController().gc1.setStroke(Color.BLACK);
            double[] x = calcHexX(distance, r);
            double[] y = calcHexy(7.5 * r, r);
            Tile tile = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));
            drawBridge(tile, x, y);
            Register.getViewClientController().gc1.setFill(setTileImg(index));
            Register.getViewClientController().gc1.fillPolygon(x, y, 6);
            for (int j = 0; j < 6; j++) {
                allPointsOfHex.add(new Point(x[j], y[j]));
            }
            allHex.add(new Hexagon(allPointsOfHex));
            allPointsOfHex = new ArrayList<>();
            setNumberTokens(index - 1);
        }

        //last row(water)
        for (int i = 1; i < 5; i++) {
            double distance = i * Math.sqrt(3) * r + 0.5 * Math.sqrt(3) * r;
            double[] x = calcHexX(distance, r);
            double[] y = calcHexy(9 * r, r);
            Tile tile = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));
            drawBridge(tile, x, y);
            Register.getViewClientController().gc1.setFill(setTileImg(index));
            Register.getViewClientController().gc1.fillPolygon(x, y, 6);
            for (int j = 0; j < 6; j++) {
                allPointsOfHex.add(new Point(x[j], y[j]));
            }
            allHex.add(new Hexagon(allPointsOfHex));
            allPointsOfHex = new ArrayList<>();
        }
        hexagon.setAllHex(allHex);
        String all = "\n37 Hex-Obj : " + allHex;
        saveEdgesAsRectangles();
        convertToEdge(Register.getController().getBoard().makeIdEdge(10, 17));
    }

    /**
     * draws jetties in a correct way
     *
     * @param tile Tile
     * @param x x coordinates of the polygon's points
     * @param y y coordinates of the polygon's points
     */
    public void drawBridge(Tile tile, double[] x, double[] y) {
        if (tile instanceof Sea) {
            if (((Sea) tile).getPort() != null) {
                Tile neighbor = getBoard().getAllTiles().get(getBoard().getTerrainneighborOfSeaharbor(tile.getId()));
                if (getBoard().getSpecificTileCo(tile)[0] == getBoard().getSpecificTileCo(neighbor)[0]) {
                    if (getBoard().getSpecificTileCo(tile)[1] < 0) {
                        Register.getViewClientController().gc1.setFill(rightBot);
                    } else {
                        Register.getViewClientController().gc1.setFill(leftTop);
                    }
                } else if (getBoard().getSpecificTileCo(tile)[1] == getBoard().getSpecificTileCo(neighbor)[1]) {
                    if (getBoard().getSpecificTileCo(tile)[0] < 0) {
                        Register.getViewClientController().gc1.setFill(rightCen);
                    } else {
                        Register.getViewClientController().gc1.setFill(leftCen);
                    }
                } else if (getBoard().getSpecificTileCo(tile)[0] != getBoard().getSpecificTileCo(neighbor)[0] && getBoard().getSpecificTileCo(tile)[1] != getBoard().getSpecificTileCo(neighbor)[1]) {
                    if (getBoard().getSpecificTileCo(tile)[1] < 0) {
                        Register.getViewClientController().gc1.setFill(leftBot);
                    } else {
                        Register.getViewClientController().gc1.setFill(rightTop);
                    }
                }
                Register.getViewClientController().gc1.fillPolygon(x, y, 6);
            }
        }
    }


    /**
     * save edges as rectangles(4 corners) in Edge.allEdges
     */
    public void saveEdgesAsRectangles() {
        edge = new Edge();
        hexagon = new Hexagon();
        for (int i = 0; i < allHex.size(); i++) {
            for (int j = 0; j <= 5; j++) {

                double x1, x2, x3, x4;
                double y1, y2, y3, y4;

                if (j == 0) {

                    x1 = allHex.get(i).getAllPoints().get(j).getX() + 1;
                    y1 = allHex.get(i).getAllPoints().get(j).getY() + 1;

                    x2 = allHex.get(i).getAllPoints().get(j).getX() - 1;
                    y2 = allHex.get(i).getAllPoints().get(j).getY() - 1;

                    x3 = allHex.get(i).getAllPoints().get(j + 1).getX() - 1;
                    y3 = allHex.get(i).getAllPoints().get(j + 1).getY() - 1;

                    x4 = allHex.get(i).getAllPoints().get(j + 1).getX() + 1;
                    y4 = allHex.get(i).getAllPoints().get(j + 1).getY() + 1;

                    allEdges.add(new Edge(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4)));


                } else if (j == 1) {

                    x1 = allHex.get(i).getAllPoints().get(j).getX() + 1;
                    y1 = allHex.get(i).getAllPoints().get(j).getY() - 1;

                    x2 = allHex.get(i).getAllPoints().get(j).getX() - 1;
                    y2 = allHex.get(i).getAllPoints().get(j).getY() + 1;

                    x3 = allHex.get(i).getAllPoints().get(j + 1).getX() - 1;
                    y3 = allHex.get(i).getAllPoints().get(j + 1).getY() + 1;

                    x4 = allHex.get(i).getAllPoints().get(j + 1).getX() + 1;
                    y4 = allHex.get(i).getAllPoints().get(j + 1).getY() - 1;

                    allEdges.add(new Edge(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4)));


                } else if (j == 2) {

                    x1 = allHex.get(i).getAllPoints().get(j).getX() + 1;
                    y1 = allHex.get(i).getAllPoints().get(j).getY();

                    x2 = allHex.get(i).getAllPoints().get(j).getX() - 1;
                    y2 = allHex.get(i).getAllPoints().get(j).getY();

                    x3 = allHex.get(i).getAllPoints().get(j + 1).getX() - 1;
                    y3 = allHex.get(i).getAllPoints().get(j + 1).getY();

                    x4 = allHex.get(i).getAllPoints().get(j + 1).getX() + 1;
                    y4 = allHex.get(i).getAllPoints().get(j + 1).getY();


                    allEdges.add(new Edge(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4)));


                } else if (j == 3) {

                    x1 = allHex.get(i).getAllPoints().get(j).getX() + 1;
                    y1 = allHex.get(i).getAllPoints().get(j).getY() + 1;

                    x2 = allHex.get(i).getAllPoints().get(j).getX() - 1;
                    y2 = allHex.get(i).getAllPoints().get(j).getY() - 1;

                    x3 = allHex.get(i).getAllPoints().get(j + 1).getX() - 1;
                    y3 = allHex.get(i).getAllPoints().get(j + 1).getY() - 1;

                    x4 = allHex.get(i).getAllPoints().get(j + 1).getX() + 1;
                    y4 = allHex.get(i).getAllPoints().get(j + 1).getY() + 1;


                    allEdges.add(new Edge(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4)));


                } else if (j == 4) {
                    x1 = allHex.get(i).getAllPoints().get(j).getX() + 1;
                    y1 = allHex.get(i).getAllPoints().get(j).getY() - 1;

                    x2 = allHex.get(i).getAllPoints().get(j).getX() - 1;
                    y2 = allHex.get(i).getAllPoints().get(j).getY() + 1;

                    x3 = allHex.get(i).getAllPoints().get(j + 1).getX() - 1;
                    y3 = allHex.get(i).getAllPoints().get(j + 1).getY() + 1;

                    x4 = allHex.get(i).getAllPoints().get(j + 1).getX() + 1;
                    y4 = allHex.get(i).getAllPoints().get(j + 1).getY() - 1;


                    allEdges.add(new Edge(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4)));

                } else if (j == 5) {
                    x1 = allHex.get(i).getAllPoints().get(j).getX() + 1;
                    y1 = allHex.get(i).getAllPoints().get(j).getY();

                    x2 = allHex.get(i).getAllPoints().get(j).getX() - 1;
                    y2 = allHex.get(i).getAllPoints().get(j).getY();

                    x3 = allHex.get(i).getAllPoints().get(0).getX() - 1;
                    y3 = allHex.get(i).getAllPoints().get(0).getY();

                    x4 = allHex.get(i).getAllPoints().get(0).getX() + 1;
                    y4 = allHex.get(i).getAllPoints().get(0).getY();


                    allEdges.add(new Edge(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4)));
                }
            }
        }
        edge.setAllEdges(allEdges);
    }

    /**
     * if clicked correctly, street will be build/drawn
     */
    public void drawStreets() {
        javafx.application.Platform.runLater(() -> {
                    Register.getViewClientController().gc2road.clearRect(0, 0, Register.getViewClientController().gc2road.getCanvas().getWidth(),
                            Register.getViewClientController().gc2road.getCanvas().getHeight());
                    double x1, x2, x3, x4;
                    double y1, y2, y3, y4;
                    for (model.board.Edge edge : Register.getController().getBoard().getAllEdges().values()) {
                        if (edge.getPlayer() != null) {
                            switch (edge.getPlayer().getColor()) {
                                case BLUE:
                                    Register.getViewClientController().gc2road.setStroke(Color.BLUE);
                                    break;
                                case ORANGE:
                                    Register.getViewClientController().gc2road.setStroke(Color.ORANGE);
                                    break;
                                case WHITE:
                                    Register.getViewClientController().gc2road.setStroke(Color.WHITE);
                                    break;
                                case RED:
                                    Register.getViewClientController().gc2road.setStroke(Color.RED);
                                    break;
                                case NULL:
                                    Register.getViewClientController().gc2road.setStroke(Color.BLACK);
                                    break;
                            }
                            Edge e = convertToEdge(edge.getBitSetID());
                            x1 = e.getCorner1().getX();
                            y1 = e.getCorner1().getY();
                            x2 = e.getCorner2().getX();
                            y2 = e.getCorner2().getY();
                            x3 = e.getCorner3().getX();
                            y3 = e.getCorner3().getY();
                            x4 = e.getCorner4().getX();
                            y4 = e.getCorner4().getY();
                            Register.getViewClientController().gc2road.setLineWidth(4);
                            Register.getViewClientController().gc2road.strokeLine(x1, y1, x2, y2);
                            Register.getViewClientController().gc2road.strokeLine(x2, y2, x3, y3);
                            Register.getViewClientController().gc2road.strokeLine(x3, y3, x4, y4);
                            Register.getViewClientController().gc2road.strokeLine(x4, y4, x1, y1);
                        }
                    }


                }
        );
    }

    /**
     * converts bitset to point object
     */
    public Point convertToPoint(BitSet id) {
        int p1 = id.nextSetBit(0);
        int p2 = id.nextSetBit(p1 + 1);
        int p3 = id.nextSetBit(p2 + 1);
        Hexagon hexagon1 = getAllHex().get(p1);
        Hexagon hexagon2 = getAllHex().get(p2);
        Hexagon hexagon3 = getAllHex().get(p3);
        ArrayList<Point> point1 = hexagon1.getAllPoints();
        ArrayList<Point> point2 = hexagon2.getAllPoints();
        ArrayList<Point> point3 = hexagon3.getAllPoints();
        Point tmp;
        for (int i = 0; i < 6; i++) {
            tmp = point1.get(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 6; k++) {
                    if (point1.get(i).getX() == point2.get(j).getX() && point1.get(i).getY() == point2.get(j).getY() && point1.get(i).getX() == point3.get(k).getX() && point1.get(i).getY() == point3.get(k).getY()) {
                        return tmp;
                    }
                }
            }
        }
        return null;
    }

    /**
     * converts bitset to edge
     * @param id
     * @return
     */
    public Edge convertToEdge(BitSet id) {
        int p1 = id.nextSetBit(0);
        int p2 = id.nextSetBit(p1 + 1);
        Hexagon hexagon1 = getAllHex().get(p1);
        Hexagon hexagon2 = getAllHex().get(p2);
        ArrayList<Point> point1 = hexagon1.getAllPoints();
        ArrayList<Point> point2 = hexagon2.getAllPoints();
        Edge tmp = new Edge();
        Point mx;
        Point my;
        ArrayList<Point> list = new ArrayList<Point>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (point1.get(i).getX() == point2.get(j).getX() && point1.get(i).getY() == point2.get(j).getY()) {
                    Point p = new Point(point1.get(i).getX(), point1.get(i).getY());
                    list.add(p);
                }
            }
        }
        mx = list.get(0);
        my = list.get(1);
        if (Math.round(mx.getX()) == Math.round(my.getX())) {
            double x = mx.getX();
            double y = (mx.getY() + my.getY()) / 2;
            Point pm = new Point(x, y);
            tmp = tmp.getTheRightEdge(pm, Register.getViewClientController());
            return tmp;
        } else {
            double x = (mx.getX() + my.getX()) / 2;
            double y = (mx.getY() + my.getY()) / 2;
            Point pm = new Point(x, y);
            tmp = tmp.getTheRightEdge(pm, Register.getViewClientController());
            return tmp;
        }
    }

    /**
     * settlements will be drawn when correctly clicked
     */
    public void drawSettlements() {
        javafx.application.Platform.runLater(() -> {
            Register.getViewClientController().gc2settlement.clearRect(0, 0, Register.getViewClientController().gc2settlement.getCanvas().getWidth(),
                    Register.getViewClientController().gc2settlement.getCanvas().getHeight());
            for (model.board.Corner corner : Register.getController().getBoard().getAllCorners().values()) {
                if (corner.getPlayer() != null && !corner.getIsCity()) {
                    switch (corner.getPlayer().getColor()) {
                        case BLUE:
                            Register.getViewClientController().gc2settlement.setFill(blueSettle);
                            break;
                        case ORANGE:
                            Register.getViewClientController().gc2settlement.setFill(orangeSettle);
                            break;
                        case WHITE:
                            Register.getViewClientController().gc2settlement.setFill(whiteSettle);
                            break;
                        case RED:
                            Register.getViewClientController().gc2settlement.setFill(redSettle);
                            break;
                        case NULL:
                            Register.getViewClientController().gc2settlement.setFill(Color.BLACK);
                            break;
                    }
                    Point point = convertToPoint(corner.getBitSetID());
                    Register.getViewClientController().gc2settlement.setLineWidth(1);
                    Register.getViewClientController().gc2settlement.fillRect(point.getX() - 12, point.getY() - 12, 24, 24);
                }

            }
        });
    }

    /**
     * cities will be drawn when correctly clicked
     */
    public void drawCities() {
        javafx.application.Platform.runLater(() -> {
            Register.getViewClientController().gc2city.clearRect(0, 0, Register.getViewClientController().gc2city.getCanvas().getWidth(),
                    Register.getViewClientController().gc2city.getCanvas().getHeight());
            for (model.board.Corner corner : Register.getController().getBoard().getAllCorners().values()) {
                if (corner.getPlayer() != null && corner.getIsCity()) {
                    switch (corner.getPlayer().getColor()) {
                        case BLUE:
                            Register.getViewClientController().gc2city.setFill(blueCity);
                            break;
                        case ORANGE:
                            Register.getViewClientController().gc2city.setFill(orangeCity);
                            break;
                        case WHITE:
                            Register.getViewClientController().gc2city.setFill(whiteCity);
                            break;
                        case RED:
                            Register.getViewClientController().gc2city.setFill(redCity);
                            break;
                        case NULL:
                            Register.getViewClientController().gc2city.setFill(Color.BLACK);
                            break;
                    }
                    Point point = convertToPoint(corner.getBitSetID());
                    Register.getViewClientController().gc2city.setLineWidth(1);
                    Register.getViewClientController().gc2city.fillRect(point.getX() - 17, point.getY() - 21, 34, 34);
                }
            }
        });
    }

    /**
     * shows possible positions for settlements
     */
    public void showPossibleSettlements() {
        javafx.application.Platform.runLater(() -> {
            LOGGER.info("buildable settlements empty /not empty?" + Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableSettlements());
            for (model.board.Corner corner : Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableSettlements()) {
                Point point = convertToPoint(corner.getBitSetID());
                LOGGER.info("jumps into the method;possible settlement" + point);
                Register.getViewClientController().gc4.setFill(settlement);
                Register.getViewClientController().gc4.fillOval(point.getX() - 12, point.getY() - 12, 24, 24);
                LOGGER.info(" possible settlement drawing methods have been activated");
            }
        });
    }

    /**
     * shows possible positions for cities
     */
    public void showPossibleCities() {
        javafx.application.Platform.runLater(() -> {
            for (model.board.Corner corner : Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableCities()) {
                Point point = convertToPoint(corner.getBitSetID());
                LOGGER.info("jumps into the method;possible city = " + point);
                double x = point.getX();
                double y = point.getY();
                LOGGER.info("the possible city coordinates : " + x + " , " + y);
                Register.getViewClientController().gc4.setLineWidth(2);
                LOGGER.info("line width is set");
                Register.getViewClientController().gc4.setStroke(Color.LIGHTGREY);
                Register.getViewClientController().gc4.setFill(city);
                LOGGER.info("color is set");
                Register.getViewClientController().gc4.fillRect(x - 17, y - 21, 34, 34);
                LOGGER.info(" possible city drawing methods have been activated");
            }
        });
    }


    /**
     * possible spots for streets will be shown [squares]
     */
    public void showPossibleStreets2() {
        javafx.application.Platform.runLater(() -> {
            for (model.board.Edge edge : Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableRoads()) {
                Edge edgeView = convertToEdge(edge.getBitSetID());
                LOGGER.info("possible streets at: " + edgeView);
                double x1 = edgeView.getCorner1().getX();
                double y1 = edgeView.getCorner1().getY();
                double x3 = edgeView.getCorner2().getX();
                double y3 = edgeView.getCorner2().getY();
                double x2 = edgeView.getCorner3().getX();
                double y2 = edgeView.getCorner3().getY();
                double x4 = edgeView.getCorner4().getX();
                double y4 = edgeView.getCorner4().getY();

                //for intesection
                double zx = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
                double zy = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4);
                double n = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
                double x = zx / n;
                double y = zy / n;
                Register.getViewClientController().gc4.setLineWidth(2);
                Register.getViewClientController().gc4.setStroke(Color.LIGHTGREY);
                Register.getViewClientController().gc4.strokeRect(x - 8, y - 8, 16, 16);
                setPossibleStreetArea(x - 8, y - 8, x + 8, y + 8);
            }
        });
    }

    /**
     * draws two roads which were chosen
     * from road building card
     *
     * @param a Bitset
     */
    public void roadBuildingPainter(BitSet a) {
        javafx.application.Platform.runLater(() -> {
            for (model.board.Edge edge : Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getBuildableRoadsRoadBuildingCard(a)) {
                Edge edgeView = convertToEdge(edge.getBitSetID());
                LOGGER.info("possible streets at: " + edgeView);
                double x1 = edgeView.getCorner1().getX();
                double y1 = edgeView.getCorner1().getY();
                double x3 = edgeView.getCorner2().getX();
                double y3 = edgeView.getCorner2().getY();
                double x2 = edgeView.getCorner3().getX();
                double y2 = edgeView.getCorner3().getY();
                double x4 = edgeView.getCorner4().getX();
                double y4 = edgeView.getCorner4().getY();
                //for intesection
                double zx = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
                double zy = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4);
                double n = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
                double x = zx / n;
                double y = zy / n;
                Register.getViewClientController().gc4.setLineWidth(2);
                Register.getViewClientController().gc4.setStroke(Color.LIGHTGREY);
                Register.getViewClientController().gc4.strokeRect(x - 8, y - 8, 16, 16);
                setPossibleStreetArea(x - 8, y - 8, x + 8, y + 8);
            }
        });
    }

    /**
     * for development card road building card
     * shows possible streets two times
     */
    public void roadBuildingCardHelpMethod() {
        Player p = Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId());
        if (p.getRoads() > 1) {
            showPossibleStreets2();
            Register.getViewClientController().roadBuildingCardListener(true, new BitSet());
        } else {
            showPossibleStreets2();
            Register.getViewClientController().roadBuildingCardListener(false, new BitSet());
        }
    }

    /**
     * Saves area of possible street
     * @param xStart start in x axis
     * @param yStart start in y axis
     * @param xEnd end in x axis
     * @param yEnd end in y axis
     */
    public void setPossibleStreetArea(double xStart, double yStart, double xEnd, double yEnd) {
        possibleStreetArea.add(xStart);
        possibleStreetArea.add(yStart);
        possibleStreetArea.add(xEnd);
        possibleStreetArea.add(yEnd);
    }

    /**
     * get 4 points of possible street area
     * @return possible streetArea
     */
    public ArrayList<Double> getPossibleStreetArea() {
        return possibleStreetArea;
    }

    /**
     * fills hexagonTiles with pictures
     * @param index of tile
     * @return null nothing
     */
    public ImagePattern setTileImg(int index) {
        //seaTiles
        Image sea = new Image("/tiles2/sea.png");
        Image gen = new Image("/tiles2/sea_gen.png");
        Image wool = new Image("/tiles2/sea_wool.png");
        Image brick = new Image("/tiles2/sea_brick.png");
        Image grain = new Image("/tiles2/sea_grain.png");
        Image lumber = new Image("/tiles2/sea_lumber.png");
        Image ore = new Image("/tiles2/sea_ore.png");
        //landtilesk
        Image desert = new Image("/tiles2/desert.png");
        Image forest = new Image("/tiles2/forest.png");
        Image hill = new Image("/tiles2/hill.png");
        Image pasture = new Image("/tiles2/pasture.png");
        Image field = new Image("/tiles2/field.png");
        Image mountain = new Image("/tiles2/mountain.png");

        Tile tmp = Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index));
        if (tmp instanceof Sea) {
            if (((Sea) tmp).getPort() == PortType.GENERIC) {
                this.index++;
                return new ImagePattern(gen);
            } else if (((Sea) tmp).getPort() == PortType.WOOL) {
                this.index++;
                return new ImagePattern(wool);
            } else if (((Sea) tmp).getPort() == PortType.BRICK) {
                this.index++;
                return new ImagePattern(brick);
            } else if (((Sea) tmp).getPort() == PortType.GRAIN) {
                this.index++;
                return new ImagePattern(grain);
            } else if (((Sea) tmp).getPort() == PortType.LUMBER) {
                this.index++;
                return new ImagePattern(lumber);
            } else if (((Sea) tmp).getPort() == PortType.ORE) {
                this.index++;
                return new ImagePattern(ore);
            }
            this.index++;
            return new ImagePattern(sea);
        } else if (tmp instanceof Desert) {
            this.index++;
            return new ImagePattern(desert);
        } else if (tmp instanceof Forest) {
            this.index++;
            return new ImagePattern(forest);
        } else if (tmp instanceof Hill) {
            this.index++;
            return new ImagePattern(hill);
        } else if (tmp instanceof Pasture) {
            this.index++;
            return new ImagePattern(pasture);
        } else if (tmp instanceof Field) {
            this.index++;
            return new ImagePattern(field);
        } else if (tmp instanceof Mountain) {
            this.index++;
            return new ImagePattern(mountain);
        }
        return null;
    }

    /**
     * Set number tokens default or random
     * @param index index of tile
     */
    public void setNumberTokens(int index) {
        javafx.application.Platform.runLater(() -> {
            if (!(getBoard().getAllTiles().get(getBoard().makeIdTile(index)) instanceof Sea) && !(getBoard().getAllTiles().get(getBoard().makeIdTile(index)) instanceof Desert)) {
                String numberToken = String.valueOf(((Terrain) Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index))).getNumberToken());
                //for later img instead of text
                int numberTok = ((Terrain) Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index))).getNumberToken();
                double y4 = allHex.get(index).getAllPoints().get(4).getY();
                double y1 = allHex.get(index).getAllPoints().get(1).getY();
                double x1 = allHex.get(index).getAllPoints().get(1).getX();
                double yCenter = ((y1 - y4) / 2) + y4;

                Register.getViewClientController().gc1.setFill(Color.WHEAT);

                switch (numberTok) {
                    case 2:
                        Register.getViewClientController().gc1.setFill(two);
                        break;
                    case 3:
                        Register.getViewClientController().gc1.setFill(three);
                        break;
                    case 4:
                        Register.getViewClientController().gc1.setFill(four);
                        break;
                    case 5:
                        Register.getViewClientController().gc1.setFill(five);
                        break;
                    case 6:
                        Register.getViewClientController().gc1.setFill(six);
                        break;
                    case 8:
                        Register.getViewClientController().gc1.setFill(eight);
                        break;
                    case 9:
                        Register.getViewClientController().gc1.setFill(nine);
                        break;
                    case 10:
                        Register.getViewClientController().gc1.setFill(ten);
                        break;
                    case 11:
                        Register.getViewClientController().gc1.setFill(eleven);
                        break;
                    default:
                        Register.getViewClientController().gc1.setFill(twelve);
                }
                Register.getViewClientController().gc1.fillOval(x1 - 14, yCenter - 13, 29, 29);
            }
        });

    }


    /**
     * clears gc3 with the previous robber position
     * draws the robber at the new position
     */
    public void setRobber() {
        javafx.application.Platform.runLater(() -> {
            Register.getViewClientController().gc3.clearRect(0, 0, Register.getViewClientController().gc3.getCanvas().getWidth(),
                    Register.getViewClientController().gc3.getCanvas().getHeight());
            BitSet position = Register.getController().getManagement().getRobber().getPosition();
            robberIndex = position.nextSetBit(0);
            Image rob = new Image("/robber_colored.png");
            ImagePattern robPat = new ImagePattern(rob);
            double y4 = allHex.get(robberIndex).getAllPoints().get(4).getY();
            double y1 = allHex.get(robberIndex).getAllPoints().get(1).getY();
            double x1 = allHex.get(robberIndex).getAllPoints().get(1).getX();
            double yCenter = ((y1 - y4) / 2) + y4;
            Register.getViewClientController().gc3.setFill(robPat);
            Register.getViewClientController().gc3.fillOval(x1 - 20, yCenter - 25, 47, 47);
            LOGGER.info("Robber is drawn at new position: " + robberIndex);
        });
    }

    /**
     * when seven is diced, possibilities for the new robber position are offered to the user
     * the new position is then sent to server
     */
    public void changeRobberPosition() {
        javafx.application.Platform.runLater(() -> {
            showPossibleRobberPosition();
            sendNewRobberPosition();
        });
    }

    /**
     * registers the users mouse click on the new robber position if it is within the designated area
     * converts the coordinates of the click into a BitSet
     * sends the new position to the server
     */
    public void sendNewRobberPosition() {
        javafx.application.Platform.runLater(() -> Register.getViewClientController().layer4.setOnMouseClicked(event -> {
            boolean b = false;
            double x = event.getX();
            double y = event.getY();
            BitSet position;
            LOGGER.info("User clicked at ( " + x + ", " + y + " )");
            for (int i = 0; i < Register.getViewClientController().getCanvasBoard().getAllCenters().size(); i++) {
                if (x > Register.getViewClientController().getCanvasBoard().getAllCenters().get(i).getX() - 20 &&
                        y > Register.getViewClientController().getCanvasBoard().getAllCenters().get(i).getY() - 20 &&
                        x < Register.getViewClientController().getCanvasBoard().getAllCenters().get(i).getX() + 20 &&
                        y < Register.getViewClientController().getCanvasBoard().getAllCenters().get(i).getY() + 20) {
                    Register.getAudioClips().getClick().play();
                    LOGGER.info("coordinates of the mouse click is within the listener");
                    position = Register.getController().getBoard().makeIdTile(getAllCentersIndex().get(i));
                    if (!(position.equals(Register.getController().getManagement().getRobber().getPosition()))) {
                        LOGGER.info("\nOld Position of robber " + Register.getController().getManagement().getRobber().getPosition() + "\nNew position of robber" + position);
                        setNewRobberPos(position);
                        Register.getViewClientController().gc4.clearRect(0, 0, Register.getViewClientController().gc4.getCanvas().getWidth(),
                                Register.getViewClientController().gc4.getCanvas().getHeight());
                        Register.getViewClientController().gc4.setGlobalAlpha(1.0);
                        b = true;
                    }

                }
            }
            if (b) {
                ArrayList<Player> player = Register.getController().getManagement().getRobber().getRobablePlayer(newRobberPos);
                player.remove(Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()));
                if (player.size() != 0) {
                    if (player.size() == 1) {
                        if (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getStatus() == Status.MOVE_ROBBER) {
                            new ClientWriter().moveRobber(Register.getViewClientController().getCanvasBoard().getNewRobberPos(), player.get(0).getId());
                            LOGGER.info("Designated tile has 1 settlement");

                        } else {
                            new ClientWriter().moveKnight(Register.getViewClientController().getCanvasBoard().getNewRobberPos(), player.get(0).getId());
                            LOGGER.info("Designated tile has 1 settlement. KnightCard is played");
                        }
                    } else {
                        setOtherPlayers(player);
                        Register.getViewClientController().openRobFromOthers();
                        LOGGER.info("Designated tile has settlements");
                    }

                } else {
                    if (Register.getController().getAllPlayersId().get(Register.getNtwrkClient().getId()).getStatus() == Status.MOVE_ROBBER) {
                        new ClientWriter().moveRobber(newRobberPos);
                        LOGGER.info("Designated tile has no settlements");
                    } else {
                        new ClientWriter().moveKnight(newRobberPos);
                        LOGGER.info("Designated tile has no settlements. KnightCard is played");
                        Register.getViewClientController().layer4.setOnMouseClicked(null);
                        Register.getViewClientController().gc4.clearRect(0, 0, Register.getViewClientController().gc4.getCanvas().getWidth(), Register.getViewClientController().gc4.getCanvas().getHeight());
                        Register.getViewClientController().gc4.setGlobalAlpha(1.0);
                    }
                }
            }
        }));
    }

    public ArrayList<String> getOtherPlayersColor() {
        return otherPlayersColor;
    }

    /**
     * initializes the ArrayList otherPlayers and otherPlayersColor with the names / colors of all players that have a settlement on the specific tile
     * which is the new robber position
     */
    public void setOtherPlayers(ArrayList<Player> player) {
        otherPlayers.clear();
        otherPlayersColor.clear();
        for (Player tmp : player) {
            if (Register.getNtwrkClient().getId() != tmp.getId()) {
                otherPlayers.add(tmp.getName());
                switch (tmp.getColor()) {
                    case BLUE:
                        otherPlayersColor.add(tmp.getName() + " » Blue");
                        break;
                    case BROWN:
                        otherPlayersColor.add(tmp.getName() + " » Brown");
                        break;
                    case GREEN:
                        otherPlayersColor.add(tmp.getName() + " » Green");
                        break;
                    case ORANGE:
                        otherPlayersColor.add(tmp.getName() + " » Orange");
                        break;
                    case RED:
                        otherPlayersColor.add(tmp.getName() + " » Red");
                        break;
                    case WHITE:
                        otherPlayersColor.add(tmp.getName() + " » White");
                        break;
                    case NULL:
                        otherPlayersColor.add(tmp.getName() + " » ??");
                        break;
                }
            }
            LOGGER.info("Names of the players at the designated tile: " + otherPlayers);
        }
    }

    public BitSet getNewRobberPos() {
        return newRobberPos;
    }

    public void setNewRobberPos(BitSet newRobberPos) {
        this.newRobberPos = newRobberPos;
    }


    /**
     * shows possible areas where robber can move
     */
    public void showPossibleRobberPosition() {
        javafx.application.Platform.runLater(() -> {
            for (index = 1; index <= 36; index++) {
                if (Register.getController().getBoard().getAllTiles().get(getBoard().makeIdTile(index)) instanceof Sea) {
                } else if (robberIndex == index) {
                } else {
                    double y4 = allHex.get(index).getAllPoints().get(4).getY();
                    double y1 = allHex.get(index).getAllPoints().get(1).getY();
                    double x1 = allHex.get(index).getAllPoints().get(1).getX();
                    double yCenter = ((y1 - y4) / 2) + y4;
                    Register.getViewClientController().gc4.setFill(robber);
                    Register.getViewClientController().gc4.setGlobalAlpha(0.7);
                    Register.getViewClientController().gc4.fillOval(x1 - 20, yCenter - 25, 47, 47);
                    setAllCentersIndex(index);
                    setAllCenters(new Point(x1, yCenter));
                }
            }

        });
        LOGGER.info("all robber positions have been set" + getAllCenters() + "all robber positions at indexes have been set" + getAllCentersIndex());
    }

    public ArrayList<Point> getAllCenters() {
        return allCenters;
    }

    public void setAllCenters(Point point) {
        allCenters.add(point);
    }

    public ArrayList<Integer> getAllCentersIndex() {
        return allCentersIndex;
    }

    public void setAllCentersIndex(Integer integer) {
        allCentersIndex.add(integer);
    }

    public int getRobberPosition() {
        return robberIndex;
    }

    /**
     * calculates Hexagons in x axis
     * @param x x axis
     * @param r radius
     * @return array of x axis'
     */
    public double[] calcHexX(double x, int r) {
        double width = Register.getViewClientController().gc1.getCanvas().getWidth();
        final double translateX = 3.5 * (Math.sqrt(3) * r);
        double[] corners = new double[6];
        for (int i = 0; i < 6; i++) {
            corners[i] = x + (r + (r * Math.cos((1 + i * 2) * Math.PI / 6))) + (0.5 * width) - translateX - 5;
        }
        return corners;
    }

    /**
     * calculates Hexagon in y axis
     * @param y y axis
     * @param r radius
     * @return array of y axis'
     */
    public double[] calcHexy(double y, int r) {
        // half of the size of canvas
        double height = 0.5 * Register.getViewClientController().gc1.getCanvas().getHeight();
        final double translateY = 0.5 * (7 * 1.5 * r + (0.5 * r));

        double[] corners = new double[6];
        for (int i = 0; i < 6; i++) {
            corners[i] = y + (r + (r * Math.sin((1 + i * 2) * Math.PI / 6))) + height - translateY;
        }
        return corners;
    }


    public Board getBoard() {
        return Register.getController().getBoard();
    }

    public ArrayList<Hexagon> getAllHex() {
        return allHex;
    }

    public ArrayList<Edge> getAllEdges() {
        return allEdges;
    }

    /**
     * updates gui when something happens (buildings, amount of resources, victorypoints,...)
     * @param o
     * @param arg
     */
    @Override
    public void update(final Observable o, final Object arg) {
        final EventTypes event = (EventTypes) arg;
        switch (event) {
            case CANVASBOARD:
                drawHexagonBoardLayer1((int) (Register.getViewClientController().gc1.getCanvas().getHeight() / 11));
                break;
            case DEVELOPMENTCARDS:
                Register.getViewClientController().bindPlayerInfoDevCards();
                Register.getViewClientController().addDevCardToStack();
                break;
            case ROAD:
                drawStreets();
                Register.getViewClientController().bindPlayerInfoCertainLR();
                break;
            case SETTLEMENT:
                drawSettlements();
                Register.getViewClientController().bindPlayerInfoCertainLR();
                break;
            case CITY:
                drawSettlements();
                drawCities();
                break;
            case ROBBER:
                setRobber();
                break;
            case VICTORYPOINTS:
                Register.getViewClientController().bindPlayerInfoVP();
                break;
            case RESOURCES:
                Register.getViewClientController().bindPlayerInfoRC();
                Register.getViewClientController().bindResourceAmount();
                break;
            case LONGESTROAD:
                Register.getViewClientController().bindPayerInfoLongestRoad();
                Register.getViewClientController().bindPlayerInfoCertainLR();
                break;
            case LARGESTARMY:
                Register.getViewClientController().bindPlayerInfoLargestArmy();
                break;
            case KNIGHTS:
                Register.getViewClientController().bindPlayerKnightsCount();
                break;
            case TRADE:
                break;
            default:
                LOGGER.info(event);
        }
    }
}

