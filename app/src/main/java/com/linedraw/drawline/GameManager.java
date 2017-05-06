package com.linedraw.drawline;

import android.graphics.Point;
import android.util.Log;

import com.linedraw.drawline.utils.Fraction;
import com.linedraw.drawline.utils.YIntercept;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by byron.
 */
public class GameManager {

    private static final String TAG = GameManager.class.getSimpleName();

    public interface Listener {
        void onGameCompleted();
    }

    public enum GameMode {
        TWOPOINTS, YSLOPE, POINTSLOPE
    }

    private Point[] points = new Point[2];
    private Fraction slope;
    private YIntercept yIntercept;
    private ArrayList<Point> pointsInLine;

    private boolean[] pointsVisible = new boolean[points.length];
    private boolean lineDrawn;
    private boolean slopeInputCorrect;
    private boolean yInterceptInputCorrect;

    private GameMode gameMode;
    private Listener listener;

    public GameManager(GameMode gameMode, Listener listener) {
        this.gameMode = gameMode;
        this.listener = listener;
    }

    public void generateGame() {
        switch (gameMode) {
            case TWOPOINTS: {
                yIntercept = new YIntercept(generateRandomCoordinate(null), 1);
                ArrayList<Integer> filter = new ArrayList<>();
                filter.add(0);
                slope = new Fraction(generateRandomCoordinate(filter),
                        generateRandomCoordinate(filter));

                points[0] = generatePointFromEquation(slope, yIntercept.toDouble(), null);
                points[1] = generatePointFromEquation(slope, yIntercept.toDouble(), points[0]);
                break; }
            case YSLOPE: {
                yIntercept = new YIntercept(generateRandomCoordinate(null), 1);
                ArrayList<Integer> filter = new ArrayList<>();
                filter.add(0);
                slope = new Fraction(generateRandomCoordinate(filter),
                        generateRandomCoordinate(filter));

                points[0] = new Point(0, yIntercept.toInt());
                pointsVisible[0] = true;
                pointsInLine = getAllVisiblePoints(slope, yIntercept.toDouble());
                pointsInLine.remove(points[0]);
                break; }
            case POINTSLOPE: {
                yIntercept = new YIntercept(generateRandomCoordinate(null), 1);
                ArrayList<Integer> filter = new ArrayList<>();
                filter.add(0);
                slope = new Fraction(generateRandomCoordinate(filter),
                        generateRandomCoordinate(filter));

                pointsInLine = getAllVisiblePoints(slope, yIntercept.toDouble());
                Random random = new Random();
                points[0] = pointsInLine.get(random.nextInt(pointsInLine.size() - 1));
                pointsVisible[0] = true;
                pointsInLine.remove(points[0]);
                break; }
        }
    }

    public void setPoints(Point pointA, Point pointB) {
        points[0] = pointA;
        points[1] = pointB;
        slope = new Fraction(points[1].y - points[0].y, points[1].x - points[0].x);
        yIntercept = new YIntercept(slope, pointA.x, pointA.y);
    }

    public void setYInterceptAndSlope(int yInt, Fraction slope) {
        this.yIntercept = new YIntercept(yInt, 1);
        this.slope = slope;

        points[0] = new Point(0, yIntercept.toInt());
        pointsVisible[0] = true;
        pointsInLine = getAllVisiblePoints(slope, yIntercept.toDouble());
        for (int i = 0 ; i < pointsInLine.size() ; i++) {
            Log.d(TAG, String.format("point[%s]: (%s,%s)", i, pointsInLine.get(i).x, pointsInLine.get(i).y));
        }
    }

    public void setPointAndSlope(Point point, Fraction slope) {
        points[0] = point;
        this.slope = slope;
        yIntercept = calculateYIntercept(point, slope);

        pointsVisible[0] = true;
        pointsInLine = getAllVisiblePoints(slope, yIntercept.toDouble());
        pointsInLine.remove(points[0]);
//        Random random = new Random();
//        points[0] = pointsInLine.get(random.nextInt(pointsInLine.size() - 1));
    }

    public YIntercept calculateYIntercept(Point point, Fraction slope) {
        // b = y - mx
        int mxnum = slope.getSimplifiedNum() * point.x;
        int ynum = point.y * slope.getSimplifiedDen();
        int bnum = ynum - mxnum;
        return new YIntercept(bnum, slope.getSimplifiedDen());
    }

    public Point generatePointFromEquation(Fraction slope, double yIntercept, Point point) {
        int x;
        double y;
        while (true) {
            // y = mx + b;
            x = generateRandomCoordinate(null);
            y = (slope.toDouble() * x) + yIntercept;

            if (x <= 10 && x >= -10 && y <= 10 && y >= -10 && y % 1 == 0) {
                if (point == null) {
                    break;
                }
                if (point.x != x && point.y != y) {
                    break;
                }
            }
        }
        return new Point(x, (int) y);
    }

    public ArrayList<Point> getAllVisiblePoints(Fraction slope, double yIntercept) {
        ArrayList<Point> pointList = new ArrayList<>();
        for (int x = -10 ; x <= 10 ; x++) {
            double y = (slope.toDouble() * (double) x) + yIntercept;
            StringBuilder builder = new StringBuilder();
            builder.append("(" + slope.toDouble() + " * ");
            builder.append(String.valueOf((double) x) + ") + ");
            builder.append(String.valueOf(yIntercept));
            builder.append(" = " + y);
            Log.d(TAG, builder.toString());
            if (x <= 10 && x >= -10 && y <= 10 && y >= -10) {
                if (y % 1 == 0) {
                    pointList.add(new Point(x, (int) y));
                }
            }
        }
        return pointList;
    }

    public Point generatePoint(Point refPoint) {
        Point point = new Point();
        int x = generateRandomCoordinate(null);
        int y = generateRandomCoordinate(null);

        if (refPoint != null) {
            while (x == refPoint.x) {
                x = generateRandomCoordinate(null);
            }
            while (y == refPoint.y) {
                y = generateRandomCoordinate(null);
            }
        }

        point.x = x;
        point.y = y;

        return point;
    }

    public static int generateRandomCoordinate(ArrayList<Integer> filter) {
        Random random = new Random();
        int next = random.nextInt(10 - (-10)) + (-10);
        while (filter != null && filter.contains(next)) {
            next = random.nextInt(10 - (-10)) + (-10);
        }
        return next;
    }

    public Point[] getPoints() {
        return points;
    }

    public YIntercept getyIntercept() {
        return yIntercept;
    }

    public Fraction getSlope() {
        return slope;
    }

    public boolean isPointVisible(int index) {
        return index < pointsVisible.length && pointsVisible[index];
    }

    public boolean areAllPointsVisible() {
        return pointsVisible[0] & pointsVisible[1];
    }

    public void setPointVisible(int pointIndex, boolean visible) {
        if (pointIndex >= points.length) {
            return;
        }
        pointsVisible[pointIndex] = visible;
        checkGameComplete();
    }

    public void setSlopeInputCorrect(boolean slopeInputCorrect) {
        this.slopeInputCorrect = slopeInputCorrect;
        checkGameComplete();
    }

    public void setyInterceptInputCorrect(boolean yInterceptInputCorrect) {
        this.yInterceptInputCorrect = yInterceptInputCorrect;
        checkGameComplete();
    }

    public void setLineDrawn(boolean lineDrawn) {
        this.lineDrawn = lineDrawn;
        checkGameComplete();
    }

    public int checkPoint(float xTouch, float yTouch, float touchRadius,
                              int width, int height, int dimension) {
        switch (gameMode) {
            case TWOPOINTS: {
                for (int i = 0 ; i < pointsVisible.length ; i++) {
                    if (pointsVisible[i]) {
                        continue;
                    }
                    if (isTouchValid(points[i], xTouch, yTouch,
                            width, height, dimension, touchRadius)) {
                        pointsVisible[i] = true;
                        return i;
                    }
                }
                break;
            }
            case YSLOPE:
            case POINTSLOPE:
                for (int i = 0 ; i < pointsInLine.size() ; i++) {
                    if (isTouchValid(pointsInLine.get(i), xTouch, yTouch,
                            width, height, dimension, touchRadius)) {
                        pointsVisible[1] = true;
                        points[1] = pointsInLine.get(i);
                        return i;
                    }
                }
                break;
        }
        return -1;
    }

    private void checkGameComplete() {
        if (pointsVisible[0] && pointsVisible[1] &&
                yInterceptInputCorrect && slopeInputCorrect && lineDrawn) {
            if (listener != null) {
                listener.onGameCompleted();
            }
        }
    }

    private boolean isTouchValid(Point point, float xTouch, float yTouch,
                                 int width, int height, int dimension, float radius) {
        if (point == null) {
            return false;
        }
        float x = interpX(width, dimension, point.x);
        float y = interpY(height, dimension, point.y);

        float touchRadius = (float) Math.sqrt(Math.pow(xTouch - x, 2) + Math.pow(yTouch - y, 2));

        return (touchRadius < radius);
    }

    private float interpX(double width, int dimension, double x) {
        return (float) ((x + dimension)
                / (dimension * 2) * width);
    }

    private float interpY(double height, int dimension, double y) {
        return (float) ((y + dimension)
                / (dimension * 2) * -height + height);
    }
}
