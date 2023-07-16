package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Vector4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.ModelFactory;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.CameraNode;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.ModelNode;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.SceneNode;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.scene_viewer.SceneViewerFrame;

import javax.swing.*;


/**
 * 3D application which provides the ability to create, view, save and load simple wireframe objects.
 */
public class WireframeApp {
    static ModelNode modelNode;
    static CameraNode camera;

    public static void main(String[] args) {
        WireframeApp.run();
    }

    /**
     * Creates a basic scene and passes it to UI.
     */
    private static void run() {
        // Create scene
        SceneNode scene = createScene();

        // Create scene viewer window
        SceneViewerFrame sceneViewer = new SceneViewerFrame(scene);
        sceneViewer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Creates a basic scene, consisting of one camera and one central model.
     * @return created scene
     */
    private static SceneNode createScene() {
        SceneNode scene = new SceneNode(null);

        // Create cube model
        modelNode = new ModelNode(scene);
        scene.addNode(modelNode);
        scene.setModelNode(modelNode);
        modelNode.setModel(ModelFactory.createCube());

        // Create camera
        camera = scene.createCameraNode();
        camera.translate(0, 0, -5);

        return scene;
    }
}