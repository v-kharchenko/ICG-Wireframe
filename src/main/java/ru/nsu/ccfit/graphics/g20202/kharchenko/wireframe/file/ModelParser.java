package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.file;

import lombok.Getter;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.BSpline;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Matrix4x4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Vector4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.Geometry;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.SplineModel;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.CameraNode;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.ModelNode;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.SceneNode;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to save scenes to files and to load scenes from files.
 */
public interface ModelParser {

    enum ModelType {
        NONE_TYPE,
        SPLINE_TYPE
    }

    /**
     * Reads the info from file and creates a scene accordingly.
     * @param file the file to read the scene from
     * @return scene node parsed from file
     */
    static SceneNode fileToScene(File file) {
        try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
            SceneNode scene = new SceneNode(null);
            // Read scene transformation
            scene.setLocalTransform(streamToMatrix(input));

            // Read camera transformation
            CameraNode camera = scene.createCameraNode();
            camera.setLocalTransform(streamToMatrix(input));

            // Read camera parameters
            camera.setNearClippingPlane(input.readDouble());
            camera.setFarClippingPlane(input.readDouble());
            camera.setViewPortWidth(input.readDouble());
            camera.setViewPortHeight(input.readDouble());

            // Read model node transformation
            ModelNode modelNode = new ModelNode(scene);
            modelNode.setLocalTransform(streamToMatrix(input));

            // Read spline
            BSpline spline = new BSpline();
            spline.setSplinePointsPerSegment(input.readInt());
            int keyPointListSize = input.readInt();
            for (int i = 0; i < keyPointListSize; i++) {
                spline.addKeyPoint(new Point2D.Double(input.readDouble(), input.readDouble()));
            }
            scene.setSpline(spline);

            // Read additional type parameters
            int modelType = input.readInt();
            if (modelType == ModelType.SPLINE_TYPE.ordinal()) {
                modelNode.setModel(streamToSplineGeometry(input));
            } else {
                modelNode.setModel(streamToGeometry(input));
            }

            // Read model node's geometry
            scene.addNode(modelNode);
            scene.setModelNode(modelNode);

            return scene;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes the scene to file.
     * @param scene scene node to write into file
     * @param file the file to write the scene to
     * @return true if the scene was saved successfully, false otherwise
     */
    static boolean sceneToFile(SceneNode scene, File file) {

        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(file))) {
            // Write scene transformation
            matrixToStream(scene.getLocalTransform(), output);

            // Write camera transformation
            CameraNode camera = scene.getCameraList().get(0);
            matrixToStream(camera.getLocalTransform(), output);

            // Write camera parameters
            output.writeDouble(camera.getNearClippingPlane());
            output.writeDouble(camera.getFarClippingPlane());
            output.writeDouble(camera.getViewPortWidth());
            output.writeDouble(camera.getViewPortHeight());

            // Write model node transformation
            ModelNode modelNode = scene.getModelNode();
            matrixToStream(modelNode.getLocalTransform(), output);

            // Write spline
            BSpline spline = scene.getSpline();
            List<Point2D.Double> keyPointList = spline.getKeyPoints();
            int splinePointsPerSegment = spline.getSplinePointsPerSegment();
            output.writeInt(splinePointsPerSegment);
            output.writeInt(keyPointList.size());
            for (var keyPoint: keyPointList) {
                output.writeDouble(keyPoint.x);
                output.writeDouble(keyPoint.y);
            }

            // Write additional type parameters
            Geometry model = modelNode.getModel();
            if (model instanceof SplineModel splineModel) {
                output.writeInt(ModelType.SPLINE_TYPE.ordinal());
                output.writeInt(splineModel.getRotationCount());
                output.writeInt(splineModel.getAcrossLayerCount());
                output.writeInt(splineModel.getAlongLayerCount());
            }
            else {
                output.writeInt(ModelType.NONE_TYPE.ordinal());
            }

            // Write model node's geometry
            geometryToStream(modelNode.getModel(), output);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Reads geometry from input stream.
     * @param input stream to read from
     * @return parsed geometry
     * @throws IOException if an error occurred while reading from stream
     */
    static Geometry streamToGeometry(DataInputStream input) throws IOException {
        List<Vector4> vertexList = new ArrayList<>();
        List<Integer> edgeList = new ArrayList<>();

        // Read number of vertices
        int vertexCount = input.readInt();

        // Read vertices
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            Vector4 vertex = new Vector4();
            vertex.x = input.readDouble();
            vertex.y = input.readDouble();
            vertex.z = input.readDouble();
            vertex.w = input.readDouble();
            vertexList.add(vertex);
        }

        // Read number of edges
        int edgeVertexCount = input.readInt();

        // Read vertices
        for (int vertexIndex = 0; vertexIndex < edgeVertexCount; vertexIndex++) {
            edgeList.add(input.readInt());
        }

        return new Geometry(vertexList, edgeList);
    }

    static SplineModel streamToSplineGeometry(DataInputStream input) throws IOException {

        List<Vector4> vertexList = new ArrayList<>();
        List<Integer> edgeList = new ArrayList<>();

        int rotationCount = input.readInt();
        int acrossLayerCount = input.readInt();
        int alongLayerCount = input.readInt();

        // Read number of vertices
        int vertexCount = input.readInt();

        // Read vertices
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            Vector4 vertex = new Vector4();
            vertex.x = input.readDouble();
            vertex.y = input.readDouble();
            vertex.z = input.readDouble();
            vertex.w = input.readDouble();
            vertexList.add(vertex);
        }

        // Read number of edges
        int edgeVertexCount = input.readInt();

        // Read vertices
        for (int vertexIndex = 0; vertexIndex < edgeVertexCount; vertexIndex++) {
            edgeList.add(input.readInt());
        }

        SplineModel splineModel = new SplineModel(vertexList, edgeList);

        splineModel.setRotationCount(rotationCount);
        splineModel.setAlongLayerCount(alongLayerCount);
        splineModel.setAcrossLayerCount(acrossLayerCount);

        return splineModel;
    }

    /**
     * Writes geometry to output stream.
     * @param geometry the geometry to write
     * @param output the output stream to write to
     * @throws IOException if an error occurred while writing to stream
     */
    static void geometryToStream(Geometry geometry, DataOutputStream output) throws IOException {
        List<Vector4> vertexList = geometry.getVertexList();
        List<Integer> edgeList = geometry.getEdgeList();

        // Write number of vertices
        output.writeInt(vertexList.size());

        // Write vertices
        for (var vertex: vertexList) {
            output.writeDouble(vertex.x);
            output.writeDouble(vertex.y);
            output.writeDouble(vertex.z);
            output.writeDouble(vertex.w);
        }

        // Write number of edges
        output.writeInt(edgeList.size());

        // Write vertices
        for (var vertexIndex: edgeList) {
            output.writeInt(vertexIndex);
        }
    }


    /**
     * Write matrix to output stream.
     * @param matrix the matrix to write
     * @param output the stream to write to
     * @throws IOException if an error occurred while writing to output stream
     */
    static void matrixToStream(Matrix4x4 matrix, DataOutputStream output) throws IOException {
        double[][] matrixArray = matrix.matrix;

        for (double[] doubles : matrixArray) {
            for (int j = 0; j < matrixArray[0].length; j++) {
                output.writeDouble(doubles[j]);
            }
        }
    }

    /**
     * Reads matrix from input stream.
     * @param input stream to read from
     * @return parsed matrix
     * @throws IOException if an error occurred while reading from stream
     */
    static Matrix4x4 streamToMatrix(DataInputStream input) throws IOException {
        double[][] matrixArray = new double[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrixArray[i][j] = input.readDouble();
            }
        }

        return new Matrix4x4(matrixArray);
    }
}
