package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.scene_viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SceneViewerMenuBar extends JMenuBar {

    private final HashMap<String, ActionListener> actions;

    SceneViewerMenuBar(HashMap<String, ActionListener> actions) {
        super();

        this.actions = actions;

        // File menu
        this.add(this.getFileMenu());

        // Edit menu
        this.add(this.getEditMenu());

        // View menu
        this.add(this.getViewMenu());

        // About menu
        this.add(this.getHelpMenu());
    }

    private JMenu getFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save");
        fileMenu.add(saveItem);

        return fileMenu;
    }

    private JMenu getEditMenu() {
        JMenu editMenu = new JMenu("Edit");

        JMenuItem splineEditorItem = new JMenuItem("B-Spline Editor");
        splineEditorItem.addActionListener(actions.get("BSpline editor"));
        editMenu.add(splineEditorItem);

        return editMenu;
    }

    private JMenu getViewMenu() {
        JMenu viewMenu = new JMenu("View");

        JMenuItem normalizeViewItem = new JMenuItem("Normalize view");
        normalizeViewItem.addActionListener(actions.get("Normalize view"));
        viewMenu.add(normalizeViewItem);

        return viewMenu;
    }

    @Override
    public JMenu getHelpMenu() {
        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutMessage());
        helpMenu.add(aboutItem);

        return helpMenu;
    }

    private void showAboutMessage() {
        JTextArea aboutText = new JTextArea();

        aboutText.setEditable(false);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);

        aboutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        try {
            InputStreamReader aboutFile = new InputStreamReader(new FileInputStream("src/main/resources/about.txt"), StandardCharsets.UTF_8);
            aboutText.read(aboutFile, "txt");
        } catch (IOException e) {
            aboutText.setText("Failed to load about.txt");
        }

        JScrollPane aboutTextScroll = new JScrollPane(aboutText);
        aboutTextScroll.setPreferredSize(new Dimension(400, 200));

        JOptionPane.showMessageDialog(this.getParent(), aboutTextScroll, "About", JOptionPane.INFORMATION_MESSAGE);
    }

}
