package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Matrix4x4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Vector4;

import java.util.LinkedList;
import java.util.List;

/**
 * Describes a node in scene's hierarchy. Each node has a parent node and child nodes. Each node has a local transformation matrix,
 * which describes the coordinate system change in relation to its parent's node coordinate system.
 */
public class Node {
    private Node parentNode;
    private List<Node> childNodes = new LinkedList<>();
    private Matrix4x4 localTransformMatrix = new Matrix4x4();

    // Node hierarchy
    public Node(Node parentNode) {
        this.parentNode = parentNode;
    }
    public void addNode(Node node) {
        childNodes.add(node);
    }
    public Node getParentNode() { return parentNode; }
    public List<Node> getChildNodes() {
        return childNodes;
    }

    // Affine transformations
    public Matrix4x4 getLocalTransform() {
        return localTransformMatrix;
    }
    public Matrix4x4 getGlobalTransform() {
        if (parentNode == null) {
            return localTransformMatrix;
        }
        return parentNode.getGlobalTransform().multiply(localTransformMatrix);
    }
    public void translate(double dx, double dy, double dz) {
        localTransformMatrix = localTransformMatrix.translate(dx, dy, dz);
    }
    public void scale(double xScale, double yScale, double zScale) {
        localTransformMatrix = localTransformMatrix.scale(xScale, yScale, zScale);
    }
    public void rotate(Vector4 axis, double angle) {
        this.localTransformMatrix = localTransformMatrix.rotate(axis, angle);
    }
    public void setLocalTransform(Matrix4x4 matrix) {
        this.localTransformMatrix = matrix;
    }
}
