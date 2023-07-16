package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.scene_viewer;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.file.ModelParser;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.SceneNode;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Provides UI for choosing file to save the scene to, or to load the scene from.
 */
public class FileChooser {
    // Parent frame for file dialogs
    private JFrame parentFrame;

    public FileChooser(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Shows the file dialog for opening files. Upon opening the file, the file is passed to
     * ModelParser and returns the complete SceneNode.
     * @return scene node created from file
     */
    public SceneNode showOpenDialog() {
        FileDialog fileDialog = new FileDialog(parentFrame, "Open scene", FileDialog.LOAD);
        fileDialog.setFile("*.icg");
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            return ModelParser.fileToScene(fileDialog.getFiles()[0]);
        }
        return null;
    }

    /**
     * Shows the file dialog for saving files. When the file is chosen, calls ModelParser to write the scene info to file.
     */
    public void showSaveDialog(SceneNode scene) {
        FileDialog fileDialog = new FileDialog(parentFrame, "Save scene", FileDialog.SAVE);
        fileDialog.setFile("*.icg");
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            File file = fileDialog.getFiles()[0];

            // Add extension if needed
            if (!file.getName().endsWith(".icg"))
                file = new File(file.getPath() + ".icg");

            boolean saved = ModelParser.sceneToFile(scene, file);
            if (!saved) {
                JOptionPane.showMessageDialog(parentFrame, "Failed to save file!");
            }
        }
    }
}
