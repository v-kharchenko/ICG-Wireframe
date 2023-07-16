package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.scene_viewer;

import com.formdev.flatlaf.FlatDarkLaf;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.b_spline_editor.BSplineEditor;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Matrix4x4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.CameraNode;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.SceneNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

/**
 * Provides GUI for inspecting one 3D model, saving and loading scenes, changing model's topology.
 */
public class SceneViewerFrame extends JFrame {

    private HashMap<String, ActionListener> actions;
    private FileChooser fileChooser = new FileChooser(this);

    private SceneView sceneView;
    private SceneNode scene;

    public SceneViewerFrame(SceneNode sceneNode) {
        super("3D Scene Viewer");

        this.scene = sceneNode;

        // Setting up FlatLaf theme
        FlatDarkLaf.setup();
        SwingUtilities.updateComponentTreeUI(this);

        // Window settings
        this.setMinimumSize(new Dimension(640, 480));
        this.setLocation(400, 160);
        this.setVisible(true);

        // Create B-Spline editor
        BSplineEditor splineEditor = new BSplineEditor();
        splineEditor.setVisible(false);
        splineEditor.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scene.getModelNode().setModel(splineEditor.getSplineModel());
                repaint();
            }
        });
        splineEditor.addSplineModelChangedListener(m -> {
            scene.getModelNode().setModel(m);
            repaint();
            return null;
        });

        // Define actions
        actions = new HashMap<>() {{
            put("Normalize view", e -> {
                scene.getModelNode().setLocalTransform(new Matrix4x4());
                repaint();
            });
            put("BSpline editor", e -> {
                splineEditor.setVisible(true);
            });
            put("Save", e -> {
                scene.setSpline(splineEditor.getSpline());
                fileChooser.showSaveDialog(scene);
            });
            put("Open", e -> {
                SceneNode scene = fileChooser.showOpenDialog();
                if (scene != null) {
                    setScene(scene, scene.getCameraList().get(0));
                    splineEditor.setSpline(scene.getSpline());
                }
            });
        }};

        // Add scene view
        CameraNode camera = scene.getCameraList().get(0);
        sceneView = new SceneView(scene, camera);
        this.add(sceneView);

        // Add toolbar
        SceneViewerToolBar toolBar = new SceneViewerToolBar(actions);
        this.add(toolBar, BorderLayout.PAGE_START);

        // Add menu
        SceneViewerMenuBar menuBar = new SceneViewerMenuBar(actions);
        this.setJMenuBar(menuBar);

        this.setScene(scene, camera);

        this.pack();
    }

    public void setScene(SceneNode scene, CameraNode camera) {
        this.scene = scene;
        sceneView.setScene(scene, camera);
        this.repaint();
    }

}
