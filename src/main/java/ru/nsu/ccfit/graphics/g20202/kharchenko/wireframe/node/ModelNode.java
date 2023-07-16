package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Matrix4x4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.Geometry;

/**
 * Describes a node with attached geometry (a.k.a. model). Geometry's vertices are set in this node's coordinate system.
 */
public class ModelNode extends Node {
    private Geometry model;

    public ModelNode(Node parentNode) {
        super(parentNode);
    }

    // Model
    public void setModel(Geometry model) {
        if (model != null)
            this.model = model;
    }
    public Geometry getModel() {
        return model;
    }

    /**
     * Creates matrix that is used to scale the model to fit a box with dimensions 1x1x1
     * @return scaling matrix
     */
    public Matrix4x4 getBoundBoxMatrix() {
        double xMax = model.getVertexList().get(0).x;
        double xMin = model.getVertexList().get(0).x;

        double yMax = model.getVertexList().get(0).y;
        double yMin = model.getVertexList().get(0).y;

        double zMax = model.getVertexList().get(0).z;
        double zMin = model.getVertexList().get(0).z;

        for (var vertex: model.getVertexList()) {
            xMax = Math.max(vertex.x, xMax);
            xMin = Math.min(vertex.x, xMin);

            yMax = Math.max(vertex.y, yMax);
            yMin = Math.min(vertex.y, yMin);

            zMax = Math.max(vertex.z, zMax);
            zMin = Math.min(vertex.z, zMin);
        }

        double scale = Math.max(Math.max(xMax - xMin, yMax - yMin), zMax - zMin);

        return Matrix4x4.getScaleMatrix(1/scale, 1/scale, 1/scale);
    }
}
