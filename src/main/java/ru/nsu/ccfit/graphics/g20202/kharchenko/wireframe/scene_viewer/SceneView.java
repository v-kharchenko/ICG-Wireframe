package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.scene_viewer;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Matrix4x4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Vector4;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.Geometry;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.node.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class SceneView extends JPanel {
    // Colors
    Color edgeColor = new Color(236, 241, 186);
    // Color edgeColor = new Color(185, 255, 244);

    // Nodes
    private SceneNode scene;
    private CameraNode camera;
    private ModelNode focusNode;

    // Mouse actions
    private Point rotateScreenOrigin;
    private double rotationSpeed = 3;
    private double zoomSpeed = 0.1;

    public SceneView(SceneNode scene, CameraNode camera) {
        this.scene = scene;
        this.camera = camera;
        this.focusNode = scene.getModelNode();

        // Create mouse adapter
        SceneView sceneView = this;
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                sceneView.mousePressed(e);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                sceneView.mouseDragged(e);
            }
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                sceneView.mouseWheelMoved(e);
            }
        };

        // Add mouse adapter as listener of this object
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        this.addMouseWheelListener(mouseAdapter);

        this.repaint();
    }

    // Painting
    /**
     * Paints all of this component's contents.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        // Determine camera's viewport aspect ratio
        if (this.getWidth() < this.getHeight()) {
            camera.setViewPortWidth(1.5);
            camera.setViewPortHeight(1.5 * getHeight() / this.getWidth());
        } else {
            camera.setViewPortHeight(1.5);
            camera.setViewPortWidth(1.5 * getWidth() / this.getHeight());
        }

        // Create buffered image to display on this pane
        BufferedImage bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Paint background
        Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
        imageGraphics.setColor(new Color(24, 39, 42));
        imageGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        // Paint all nodes onto buffered image
        this.paintNode(bufferedImage, scene);

        // Draw buffered image
        g2d.drawImage(bufferedImage, (this.getWidth() - bufferedImage.getWidth())/ 2, (this.getHeight() - bufferedImage.getHeight())/ 2, this);

        // Draw border
        g2d.setColor(Color.WHITE);
        g2d.drawRect((this.getWidth() - bufferedImage.getWidth())/2,  (this.getHeight() - bufferedImage.getHeight())/2, bufferedImage.getWidth()-1, bufferedImage.getHeight()-1);
    }

    /**
     * Recursively paints all nodes in hierarchical order on the buffered image.
     * @param bufferedImage image which will be drawn on the pane
     * @param node current node to be painted
     */
    private void paintNode(BufferedImage bufferedImage, Node node) {
        if (node instanceof ModelNode modelNode) {
            // Matrix transformations
            Matrix4x4 modelNodeGlobalTransform = modelNode.getGlobalTransform();
            Matrix4x4 modelScaleTransform = modelNode.getBoundBoxMatrix();
            Matrix4x4 cameraGlobalTransform = camera.getGlobalTransform();
            Matrix4x4 cameraViewportTransform = camera.getViewportTransform();

            // Get final projection matrix
            Matrix4x4 projectionMatrix = cameraViewportTransform.multiply(
                    cameraGlobalTransform.multiply(
                            modelNodeGlobalTransform.multiply(
                                    modelScaleTransform)));

            // Get model
            Geometry geometry = modelNode.getModel();

            // Get graphics
            Graphics2D g =  (Graphics2D) bufferedImage.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Paint axes
            Vector4 center = new Vector4(0, 0, 0, 1);
            Vector4 viewPortCenter = projectionMatrix.multiply(center, true);
            Point screenPointCenter = getScreenPoint(viewPortCenter);

            // Helper function for painting axes
            BiFunction<Vector4, Color, Void> paintAxis = (Vector4 vector, Color color) -> {
                g.setColor(color);
                Vector4 viewPortAxis = projectionMatrix.multiply(vector, true);
                Point screenPointAxis = getScreenPoint(viewPortAxis);
                g.drawLine(screenPointCenter.x,
                        screenPointCenter.y,
                        screenPointAxis.x,
                        screenPointAxis.y);
                return null;
            };

            paintAxis.apply(new Vector4(1, 0, 0, 1), Color.RED);
            paintAxis.apply(new Vector4(0, 1, 0, 1), Color.GREEN);
            paintAxis.apply(new Vector4(0, 0, 1, 1), Color.BLUE);

            // Get viewport vertices
            List<Vector4> viewPortVertices = new ArrayList<>();
            for (var vertex: geometry.getVertexList()) {
                viewPortVertices.add(projectionMatrix.multiply(vertex, true));
            }

            // Paint vertices
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2));
            //paintVertices(g, viewPortVertices);

            // Paint edges
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1));
            paintEdges(g, viewPortVertices, geometry.getEdgeList());
        }

        for (var childNode: node.getChildNodes()) {
            paintNode(bufferedImage, childNode);
        }
    }

    private void paintEdges(Graphics2D g, List<Vector4> viewPortVertices, List<Integer> edgeList) {
        for (int i = 0; i < edgeList.size() / 2; i++) {
            Vector4 viewPortPoint1 = viewPortVertices.get(edgeList.get(2 * i));
            Vector4 viewPortPoint2 = viewPortVertices.get(edgeList.get(2 * i + 1));

            // Clip vertex
            if (viewPortPoint1.z <= 0 || viewPortPoint1.z > 1 || viewPortPoint2.z <= 0 || viewPortPoint2.z > 1)
                continue;

            // Get edge ends on screen
            Point screenPoint1 = getScreenPoint(viewPortPoint1);
            Point screenPoint2 = getScreenPoint(viewPortPoint2);

            // Set dynamic color
            Color color = getColorByDistance(edgeColor, Math.min(viewPortPoint1.z, viewPortPoint2.z));

            g.setColor(color);
            g.setStroke(new BasicStroke((int) (5 * (1 - Math.min(viewPortPoint1.z, viewPortPoint2.z))), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

            // Draw line
            g.drawLine(screenPoint1.x, screenPoint1.y, screenPoint2.x, screenPoint2.y);
        }
    }

    private Color getColorByDistance(Color color, double distance) {
        distance = 1 - distance;

        int rgb = color.getRGB();

        int red = Math.min(Math.max((int)(((rgb >> 16) & 0xFF) * distance), 0), 255);
        int green = Math.min(Math.max((int)(((rgb >> 8) & 0xFF) * distance), 0), 255);
        int blue = Math.min(Math.max((int)((rgb & 0xFF) * distance), 0), 255);

        int newRgb = (0xFF << 24) | (red << 16) | (green << 8) | blue;

        return new Color(newRgb);
    }

    private void paintVertices(Graphics2D g, List<Vector4> viewPortVertices) {
        for (var viewPortPoint: viewPortVertices) {
            if (viewPortPoint.z <= 0 || viewPortPoint.z > 1)
                continue;

            Point screenPoint = getScreenPoint(viewPortPoint);

            // TODO: Remove magic numbers
            g.drawOval(screenPoint.x - (int) (20 * (1 - viewPortPoint.z)), screenPoint.y - (int) (20 * (1 - viewPortPoint.z)), (int) (40 * (1 - viewPortPoint.z)), (int) (40 * (1 - viewPortPoint.z)));
        }
    }

    /**
     * Translate point from camera viewport's coordinates to screen coordinates.
     * @param viewPortPoint - point on the viewport
     * @return screen point coordinates
     */
    private Point getScreenPoint(Vector4 viewPortPoint) {
        int x = (int)((viewPortPoint.x) * this.getWidth() / camera.getViewPortWidth()) + this.getWidth()/2;
        int y = (int)((viewPortPoint.y) * this.getHeight() / camera.getViewPortHeight()) + this.getHeight()/2;

        return new Point(x, y);
    }

    // Mouse actions
    /**
     * When the mouse is pressed, the origin of mouse motion is saved.
     * @param e mouse event
     */
    public void mousePressed(MouseEvent e) {
        rotateScreenOrigin = e.getPoint();
    }

    /**
     * When the mouse gets dragged across the screen, the model rotates in that direction against an axis perpendicular to the motion.
     * @param e mouse event
     */
    public void mouseDragged(MouseEvent e) {
        // If origin of rotation wasn't set, set it and return
        if (rotateScreenOrigin == null) {
            rotateScreenOrigin = e.getPoint();
            return;
        }

        // Get mouse movement vector
        Point screenAxis = new Point(e.getX() - rotateScreenOrigin.x, e.getY() - rotateScreenOrigin.y);

        // Set new origin point
        rotateScreenOrigin = e.getPoint();

        // If vector is zero, return
        if (screenAxis.x == 0 && screenAxis.y == 0) {
            return;
        }

        // Set vector to rotate the model around and rotate it
        Vector4 focusNodeAxis = new Vector4(screenAxis.y, -screenAxis.x, 0, 1);
        focusNode.rotate(focusNodeAxis, rotationSpeed);

        this.repaint();
    }

    /**
     * When the mouse wheel is moved, the camera changes its near clipping plane distance.
     * @param e mouse event
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        double offset = e.getPreciseWheelRotation() * zoomSpeed;

        if (camera.getNearClippingPlane() <= 0 && offset >= 0)
            return;

        // Change near clipping plane distance
        camera.setNearClippingPlane(camera.getNearClippingPlane() - offset);
        camera.setFarClippingPlane(camera.getFarClippingPlane() - offset);

        // Move camera to account for the clipping plane movement
        camera.translate(0, 0, offset);

        this.repaint();
    }

    public void setScene(SceneNode scene, CameraNode camera) {
        this.scene = scene;
        this.camera = camera;
        this.focusNode = scene.getModelNode();
    }
}
