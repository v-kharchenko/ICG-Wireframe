package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.BSpline;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Matrix4x4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface ModelFactory {

    /**
     * Creates a geometry based on the spline. The spline gets rotated around its X axis.
     * @param spline the base spline
     * @param rotationCount how many points will be generated per one spline point
     * @param alongLayerCount how many layers of points along the model will be connected with edges
     * @param acrossLayerCount how many layers of points across the model will be connected with edges
     * @return geometry of a spline rotated around its X axis. If the spline is empty, this will return <code>none</code>.
     * @throws IllegalArgumentException thrown if
     * <ul>
     *     <li><code> alongLayerCount > rotationCount </code></li>
     *     <li><code> spline.getSplinePoints().size() < acrossLayerCount </code></li>
     *     <li><code> alongLayerCount <= 0 </code></li>
     * </ul>
     */
    static SplineModel createRotatedSplineModel(BSpline spline, int rotationCount, int alongLayerCount, int acrossLayerCount) throws IllegalArgumentException {
        // If the spline is empty, return no geometry
        if (spline.getSplinePoints().isEmpty())
            return null;

        if (rotationCount < alongLayerCount) {
            throw new IllegalArgumentException("alongLayerCount can't be greater than rotationCount");
        }
        if (spline.getSplinePoints().size() < acrossLayerCount) {
            throw new IllegalArgumentException("acrossLayerCount " + acrossLayerCount + " is greater than spline's point count " + spline.getSplinePoints().size());
        }
        if (alongLayerCount <= 0) {
            throw new IllegalArgumentException("alongLayerCount " + alongLayerCount + " can't be zero");
        }

        // Create vertices and edges
        List<Vector4> vertexList = new ArrayList<>();

        // Each point gets rotated around X as many times as specified in the parameters
        for (var point: spline.getSplinePoints()) {
            for (int i = 0; i < rotationCount; i++){
                Matrix4x4 rotationMatrix = Matrix4x4.getRotationMatrix(new Vector4(1, 0, 0, 1), 360 * (double)i / rotationCount);
                vertexList.add(rotationMatrix.multiply(new Vector4(point.x, point.y, 0, 1), true));
            }
        }

        // Connect vertices on across-layers
        List<Integer> edgeList = new ArrayList<>(getSplineAcrossEdges(rotationCount, acrossLayerCount, spline.getSplinePoints().size()));

        // Connect vertices on along-layers
        for (int splineIndex = 0; splineIndex < spline.getSplinePoints().size() - 1; splineIndex++) {
            for (int layer = 0; layer < alongLayerCount; layer++) {
                edgeList.add(splineIndex * rotationCount + layer * rotationCount/alongLayerCount);
                edgeList.add((splineIndex + 1) * rotationCount + layer * rotationCount/alongLayerCount);
            }
        }

        SplineModel splineModel = new SplineModel(vertexList, edgeList);
        splineModel.setRotationCount(rotationCount);
        splineModel.setAlongLayerCount(alongLayerCount);
        splineModel.setAlongLayerCount(acrossLayerCount);

        return splineModel;
    }

    static List<Integer> getSplineAcrossEdges(int rotationCount, int acrossLayerCount, int splineSize) {
        List<Integer> edgeList = new ArrayList<>();

        if (acrossLayerCount >= 1) {
            for (int vertexIndex = 0; vertexIndex < rotationCount; vertexIndex++) {
                edgeList.add(vertexIndex);
                if ((vertexIndex + 1) % rotationCount == 0)
                    edgeList.add(vertexIndex - rotationCount + 1);
                else
                    edgeList.add(vertexIndex + 1);
            }
        }
        if (acrossLayerCount >= 2) {
            int betweenStepSpace = (splineSize - acrossLayerCount) / (acrossLayerCount - 1);
            int extraBetweenSpace = (splineSize - acrossLayerCount) % (acrossLayerCount - 1);

            int extraCount = 0;
            for (int layer = 1; layer < acrossLayerCount; layer++) {
                if (extraCount < extraBetweenSpace) {
                    extraCount++;
                }

                for (int vertexIndex = (betweenStepSpace * layer + extraCount + layer) * rotationCount; vertexIndex < (betweenStepSpace * layer + extraCount + layer) * rotationCount + rotationCount; vertexIndex++) {
                    edgeList.add(vertexIndex);
                    if ((vertexIndex + 1) % rotationCount == 0)
                        edgeList.add(vertexIndex - rotationCount + 1);
                    else
                        edgeList.add(vertexIndex + 1);
                }
            }
        }

        return edgeList;
    }

    /**
     * Creates a geometry of a simple 2x2x2 cube.
     * @return cube geometry
     */
    static Geometry createCube() {
        // Create vertices and edges
        List<Vector4> vertexList = new ArrayList<>();
        List<Integer> edgeList = new ArrayList<>();

        // Create vertices
        for (double i = -1; i <= 1; i += 2) {
            for (double j = -1; j <= 1; j += 2) {
                for (double k = -1; k <= 1; k += 2) {
                    vertexList.add(new Vector4(i, j, k, 1));
                }
            }
        }

        // TODO: Can't it be more elegant?
        // Connect vertices with edges
        edgeList.add(0);
        edgeList.add(1);

        edgeList.add(0);
        edgeList.add(2);

        edgeList.add(0);
        edgeList.add(4);

        edgeList.add(1);
        edgeList.add(3);

        edgeList.add(1);
        edgeList.add(5);

        edgeList.add(2);
        edgeList.add(3);

        edgeList.add(2);
        edgeList.add(6);

        edgeList.add(3);
        edgeList.add(7);

        edgeList.add(4);
        edgeList.add(5);

        edgeList.add(4);
        edgeList.add(6);

        edgeList.add(5);
        edgeList.add(7);

        edgeList.add(6);
        edgeList.add(7);

        return new Geometry(vertexList, edgeList);
    }
}
