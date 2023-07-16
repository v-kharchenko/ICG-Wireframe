package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Matrix4x4;

/**
 * This node contains additional information to describe a logical model of a camera.
 */
public class CameraNode extends Node {
    // Camera viewport in local units
    private double cameraViewPortWidth = 4;
    private double cameraViewPortHeight = 2;

    // Clipping planes
    private double nearClippingPlane = 3;
    private double farClippingPlane = 40;

    public CameraNode(Node parentNode) {
        super(parentNode);
    }

    /**
     * Returns a matrix that that is used to project points (set in camera's coordinates) onto the viewport plane.
     * The resulting vector4 should be divided by its last component w. The resulting components x and y will describe the resulting
     * projection point. The component z will be from (0, 1) if it was between the two clipping planes, where the larger z is, the further it was from the camera.
     * If z is not from (0, 1), then it is out of clipping bounds and should not be rendered.
     * @return the projection matrix
     */
    public Matrix4x4 getViewportTransform() {
        return new Matrix4x4(new double[][] {{nearClippingPlane, 0, 0, 0},
                                            {0, nearClippingPlane, 0, 0},
                                            {0, 0, farClippingPlane / (farClippingPlane - nearClippingPlane), farClippingPlane * nearClippingPlane / (farClippingPlane - nearClippingPlane)},
                                            {0, 0, 1.0, 0.0}});
    }

    // Camera settings
    public double getViewPortHeight() {
        return cameraViewPortHeight;
    }
    public double getViewPortWidth() {
        return cameraViewPortWidth;
    }
    public void setViewPortWidth(double width) {
        this.cameraViewPortWidth = width;
    }
    public void setViewPortHeight(double height) {
        this.cameraViewPortHeight = height;
    }

    public double getNearClippingPlane() {
        return nearClippingPlane;
    }
    public void setNearClippingPlane(double nearClippingPlane) {
        this.nearClippingPlane = nearClippingPlane;
    }
    public double getFarClippingPlane() {
        return farClippingPlane;
    }
    public void setFarClippingPlane(double farClippingPlane) {
        this.farClippingPlane = farClippingPlane;
    }
}
