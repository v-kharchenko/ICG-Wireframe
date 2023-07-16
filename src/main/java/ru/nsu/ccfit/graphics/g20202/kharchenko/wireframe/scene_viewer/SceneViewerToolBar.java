package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.scene_viewer;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class SceneViewerToolBar extends JToolBar {

    public SceneViewerToolBar(HashMap<String, ActionListener> actions) {
        super();
        this.setFloatable(false);

        // Button that opens scene
        JButton openButton = new JButton("Open scene");
        this.add(openButton);
        openButton.addActionListener(actions.get("Open"));

        // Button that saves scene
        JButton saveButton = new JButton("Save scene");
        this.add(saveButton);
        saveButton.addActionListener(actions.get("Save"));

        // Button that launches b-spline editor
        JButton editorButton = new JButton("BSpline editor");
        this.add(editorButton);
        editorButton.addActionListener(actions.get("BSpline editor"));

        // Button that normalizes view
        JButton normalizeViewButton = new JButton("Normalize view");
        this.add(normalizeViewButton);
        normalizeViewButton.addActionListener(actions.get("Normalize view"));
    }

}
