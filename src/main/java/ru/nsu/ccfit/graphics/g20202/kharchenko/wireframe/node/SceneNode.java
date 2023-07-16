package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.BSpline;

import java.util.ArrayList;
import java.util.List;

/**
 * SceneNode is used as a root to the node hierarchy, which oversees its elements by type.
 */
public class SceneNode extends Node {
    private List<CameraNode> cameraList = new ArrayList<>();
    @Getter @Setter
    private ModelNode modelNode;
    @Getter @Setter
    private BSpline spline;

    public SceneNode(Node parentNode) {
        super(parentNode);
    }

    // Camera list

    /**
     * Creates a camera node as a direct child node and returns it.
     * @return camera node
     */
    public CameraNode createCameraNode() {
        CameraNode camera = new CameraNode(this);

        this.addNode(camera);

        cameraList.add(camera);

        return camera;
    }
    /**
     * Adds camera that the scene will oversee.
     * @param cameraNode - camera to be added to scene's camera list
     */

    public void addCameraNode(CameraNode cameraNode) {
        cameraList.add(cameraNode);
    }

    /**
     * Returns all the cameras that the scene oversees.
     * @return list of camera nodes
     */
    public List<CameraNode> getCameraList() {
        return cameraList;
    }
}
