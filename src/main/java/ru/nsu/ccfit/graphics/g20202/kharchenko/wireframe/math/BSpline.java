package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math;

import lombok.Getter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes a 2D B-Spline by a list of key points and per-segment accuracy.
 */
public class BSpline {
    // Constants
    public final static int DEFAULT_SEGMENT_SIZE = 10;

    // Matrix used to calculate spline points
    private static final Matrix4x4 splineMatrix = new Matrix4x4(new double[][]{{-1, 3, -3, 1},
                                                                                {3, -6, 3, 0},
                                                                                {-3, 0, 3, 0},
                                                                                {1, 4, 1, 0}})
                                                                                            .multiply(1.0 / 6);

    private List<Point2D.Double> keyPointList = new ArrayList<>();
    private List<Point2D.Double> splinePointList = new ArrayList<>();
    @Getter
    private int splinePointsPerSegment = DEFAULT_SEGMENT_SIZE;

    // Key points that describe the spline
    public List<Point2D.Double> getKeyPoints() {
        return keyPointList;
    }
    public void addKeyPoint(Point2D.Double keyPoint) {
        keyPointList.add(keyPoint);

        evaluateSpline();
    }
    public void setKeyPoint(int index, double x, double y) {
        keyPointList.set(index, new Point2D.Double(x, y));

        evaluateSpline();
    }
    public void setKeyPointX(int dragPointIndex, double x) {
        keyPointList.get(dragPointIndex).x = x;

        evaluateSpline();
    }
    public void setKeyPointY(int dragPointIndex, double y) {
        keyPointList.get(dragPointIndex).y = y;

        evaluateSpline();
    }
    public void removeKeyPoint(int index) {
        keyPointList.remove(index);

        evaluateSpline();
    }

    // Spline
    /**
     * Recalculate all spline points.
     */
    public void evaluateSpline() {
        // Clear all spline points
        splinePointList.clear();

        if (keyPointList.size() < 4)
            return;

        // For each 4 neighbouring key points
        for (int i = 1; i < keyPointList.size() - 2; i++){

            // Get 4d vectors of components x and y
            Vector4 xComponents = new Vector4(keyPointList.get(i-1).x, keyPointList.get(i).x, keyPointList.get(i+1).x, keyPointList.get(i+2).x);
            Vector4 yComponents = new Vector4(keyPointList.get(i-1).y, keyPointList.get(i).y, keyPointList.get(i+1).y, keyPointList.get(i+2).y);

            // Get polynomial coefficients by multiplying the spline matrix by component vectors
            Vector4 xCoefficients = splineMatrix.multiply(xComponents, false);
            Vector4 yCoefficients = splineMatrix.multiply(yComponents, false);

            // Calculate 'splinePointsPerSegment' points
            double t;
            for (int j = 0; j < splinePointsPerSegment; j++) {
                t = (double) j /splinePointsPerSegment;

                // Coordinates of the new point
                double x = xCoefficients.x * t * t * t + xCoefficients.y * t * t + xCoefficients.z * t + xCoefficients.w;
                double y = yCoefficients.x * t * t * t + yCoefficients.y * t * t + yCoefficients.z * t + yCoefficients.w;

                splinePointList.add(new Point2D.Double(x, y));
            }
        }
    }

    public List<Point2D.Double> getSplinePoints() {
        return splinePointList;
    }

    public void setSplinePointsPerSegment(int splinePointsPerSegment) {
        this.splinePointsPerSegment = splinePointsPerSegment;
        evaluateSpline();
    }
}
