package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.b_spline_editor;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.BSpline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class BSplinePane extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {

    // Constants
    private static final int INDENT_STEP_ON_DEFAULT_ZOOM = 64;

    // Colors
    @Getter @Setter
    private Color backgroundColor = new Color(45, 45, 51);
    @Getter @Setter
    private Color xAxisColor = new Color(231, 121, 161);
    @Getter @Setter
    private Color yAxisColor = new Color(153, 206, 137);
    @Getter @Setter
    private Color keyPointColor = new Color(67, 102, 168);
    @Getter @Setter
    private Color selectedKeyPointColor = new Color(215, 31, 185);
    @Getter @Setter
    private Color brokenLineColor = new Color(227, 170, 153);
    @Getter @Setter
    private Color splineColor = Color.GREEN;


    // Stroke sizes
    @Getter @Setter
    private int axisSize = 2;
    @Getter @Setter
    private int splineSize = 2;
    @Getter @Setter
    private int brokenLineSize = 1;
    @Getter @Setter
    private int pointRadius = 10;

    // Navigation
    private int pixelsPerIndentStep = INDENT_STEP_ON_DEFAULT_ZOOM;
    private double zoom = 100;
    private int verticalOffset = 0;
    private int horizontalOffset = 0;

    // Mouse actions
    private Point dragOrigin;
    private int dragPointIndex;

    // Listeners
    List<BiFunction<Integer, Point2D.Double, Void>> pointModifiedListeners = new ArrayList<>();

    // Spline
    @Getter
    private BSpline spline = new BSpline();

    public BSplinePane() {
        super();
        this.addMouseWheelListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Paints the background, the dented axes and the spline.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;

        // Fill background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Paint axes
        g2d.setStroke(new BasicStroke(axisSize));

        int verticalPosition = this.getHeight()/2 + verticalOffset;
        int horizontalPosition = this.getWidth()/2 + horizontalOffset;

        // Paint X axis
        g2d.setColor(xAxisColor);
        g2d.drawLine(0, verticalPosition, this.getWidth() - 1, verticalPosition);
        for (int i = horizontalPosition + pixelsPerIndentStep; i <= this.getWidth(); i += pixelsPerIndentStep) {
            g2d.drawLine(i, verticalPosition - axisSize * 2,i, verticalPosition + axisSize * 2);
        }
        for (int i = horizontalPosition - pixelsPerIndentStep; i > 0; i -= pixelsPerIndentStep) {
            g2d.drawLine(i, verticalPosition - axisSize * 2,i, verticalPosition + axisSize * 2);
        }

        // Paint Y axis
        g2d.setColor(yAxisColor);
        g2d.drawLine(horizontalPosition, 0, horizontalPosition, this.getHeight() - 1);
        for (int i = verticalPosition + pixelsPerIndentStep; i <= this.getHeight(); i += pixelsPerIndentStep) {
            g2d.drawLine(horizontalPosition + axisSize * 2, i,horizontalPosition - axisSize * 2, i);
        }
        for (int i = verticalPosition - pixelsPerIndentStep; i > 0; i -= pixelsPerIndentStep) {
            g2d.drawLine(horizontalPosition + axisSize * 2, i,horizontalPosition - axisSize * 2, i);
        }

        this.paintSpline(g2d);
    }

    /**
     * Paints key points, connects them with a broken line, and draws the spline determined by the key points.
     * @param g2d the <code>Graphics2D</code> object to protect
     */
    private void paintSpline(Graphics2D g2d) {
        if (spline.getKeyPoints().isEmpty())
            return;

        // Connect points with straight lines
        g2d.setStroke(new BasicStroke(brokenLineSize));
        g2d.setColor(brokenLineColor);
        Point previousScreenKeyPoint = getPointOnScreen(spline.getKeyPoints().get(0));
        for (var point: spline.getKeyPoints()) {
            Point screenPoint = getPointOnScreen(point);

            g2d.drawLine(previousScreenKeyPoint.x, previousScreenKeyPoint.y, screenPoint.x, screenPoint.y);

            previousScreenKeyPoint = screenPoint;
        }

        // Show key points
        g2d.setColor(keyPointColor);
        for (int keyPointIndex = 0; keyPointIndex < spline.getKeyPoints().size(); keyPointIndex++) {
            Point2D.Double keyPoint = spline.getKeyPoints().get(keyPointIndex);

            Point screenPoint = this.getPointOnScreen(keyPoint.x, keyPoint.y);

            if (keyPointIndex == dragPointIndex)
                g2d.setColor(selectedKeyPointColor);
            else
                g2d.setColor(keyPointColor);
            g2d.drawOval(screenPoint.x - pointRadius, screenPoint.y - pointRadius, pointRadius * 2, pointRadius * 2);
        }

        // Paint spline
        g2d.setStroke(new BasicStroke(splineSize));
        g2d.setColor(splineColor);
        var splinePoints = spline.getSplinePoints();
        if (!splinePoints.isEmpty()) {
            Point previousScreenSplinePoint = getPointOnScreen(splinePoints.get(0));
            for (var point : splinePoints) {
                Point screenPoint = getPointOnScreen(point);

                g2d.drawLine(previousScreenSplinePoint.x, previousScreenSplinePoint.y, screenPoint.x, screenPoint.y);

                previousScreenSplinePoint = screenPoint;
            }
        }
    }

    // Translation between spline points and screen points
    private Point getPointOnScreen(Point2D.Double point) {
        return getPointOnScreen(point.getX(), point.getY());
    }
    private Point getPointOnScreen(double x, double y) {
        int xPixelDistance = (int) (x * pixelsPerIndentStep);
        int yPixelDistance = (int) (-y * pixelsPerIndentStep);

        int xAbsolutePos = xPixelDistance + (this.getWidth()/2 + horizontalOffset);
        int yAbsolutePos = yPixelDistance + (this.getHeight()/2 + verticalOffset);

        return new Point(xAbsolutePos, yAbsolutePos);
    }
    private Point2D.Double getContinuousPoint(int x, int y) {
        int xPixelDistance = x - (this.getWidth()/2 + horizontalOffset);
        int yPixelDistance = y - (this.getHeight()/2 + verticalOffset);

        double xContinuous = (double)xPixelDistance / pixelsPerIndentStep;
        double yContinuous = (double)yPixelDistance / pixelsPerIndentStep;

        return new Point2D.Double(xContinuous, -yContinuous);
    }

    /**
     * Updates plane's on-screen scale to fit <code>zoom</code>.
     * @param zoom the percentage of default size to be shown. The parameter <code>zoom = 100</code> means that
     *             one continuous unit will be scaled to <code>INDENT_STEP_ON_DEFAULT_ZOOM</code> pixels.
     */
    public void setZoom(double zoom) {
        if (zoom <= 0 || zoom > 800)
            return;

        this.zoom = zoom;
        pixelsPerIndentStep = (int) (INDENT_STEP_ON_DEFAULT_ZOOM / (zoom / 100));

        this.repaint();
    }

    private int findSelectedKeyPoint(Point point) {
        // Find the key point
        for (int pointIndex = 0; pointIndex < spline.getKeyPoints().size(); pointIndex++) {
            // Get key point's bounds
            Point2D.Double keyPoint = spline.getKeyPoints().get(pointIndex);

            // Get key point on screen
            Point pointOnScreen = this.getPointOnScreen(keyPoint.x, keyPoint.y);
            pointOnScreen.x -= pointRadius;
            pointOnScreen.y -= pointRadius;

            // Find the screen point's bound box
            Rectangle2D newBounds = new Rectangle(pointOnScreen, new Dimension(pointRadius * 2, pointRadius * 2));

            // If the mouse was pressed on this point
            if (newBounds.contains(point)) {
                return pointIndex;
            }
        }
        return -1;
    }

    /**
     * Updates plane's on-screen scale based on mouse input.
     * @param e the event to be processed
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.setZoom(zoom + 10 * e.getPreciseWheelRotation());
    }

    /**
     * Drags a key point or a plane depending on what was selected.
     * @param e the mouse event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragOrigin != null) { // If the plane is being dragged
            // Get mouse shift vector
            int deltaX = dragOrigin.x - e.getX();
            int deltaY = dragOrigin.y - e.getY();

            // Update plane's shift vector
            horizontalOffset -= deltaX;
            verticalOffset -= deltaY;

            // Update origin point of dragging
            dragOrigin = new Point(e.getPoint());
        } else if (dragPointIndex != -1) { // If the key point is being dragged
            // Get the new continuous point
            Point2D.Double continuousPoint = this.getContinuousPoint(e.getX(), e.getY());

            // Update this point's position
            spline.setKeyPoint(dragPointIndex, continuousPoint.x, continuousPoint.y);

            for (var l: pointModifiedListeners) {
                l.apply(dragPointIndex, spline.getKeyPoints().get(dragPointIndex));
            }
        }
        this.repaint();
    }

    /**
     * Produces one of the several results:
     * <ul>
     *     <li> LMB pressed on a key point selects it for moving. </li>
     *     <li> RMB pressed on a key point removes it. </li>
     *     <li> If the mouse wasn't pressed on any key point, the plane will be marked for movement. </li>
     * </ul>
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int pointIndex = findSelectedKeyPoint(e.getPoint());
        if (pointIndex != -1) {
            if (e.getButton() == MouseEvent.BUTTON1) { // LMB - drag point
                dragPointIndex = pointIndex;

                for (var l : pointModifiedListeners) {
                    l.apply(dragPointIndex, spline.getKeyPoints().get(pointIndex));
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) { // RMB - delete point
                spline.removeKeyPoint(pointIndex);
                dragPointIndex = -1;
                for (var l: pointModifiedListeners) {
                    l.apply(dragPointIndex, new Point2D.Double(0, 0));
                }
            }
            return;
        }

        // Mark the origin point of dragging
        dragOrigin = new Point(e.getPoint());
    }

    /**
     * Creates a spline key point.
     * @param e the mouse event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int pointIndex = findSelectedKeyPoint(e.getPoint());
        if (pointIndex != -1) {
            dragPointIndex = pointIndex;
            return;
        }

        // Get point in continuous coordinates
        Point2D.Double point = this.getContinuousPoint(e.getX(), e.getY());

        // Add key point
        if (e.getButton() == MouseEvent.BUTTON1) { // LMB adds a key point
            spline.addKeyPoint(point);

            dragPointIndex = spline.getKeyPoints().size() - 1;

            for (var l: pointModifiedListeners) {
                l.apply(dragPointIndex, spline.getKeyPoints().get(dragPointIndex));
            }
        }

        this.repaint();
    }

    // Release moving point and plane
    @Override
    public void mouseReleased(MouseEvent e) {
        dragOrigin = null;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        dragOrigin = null;
    }

    // Ignored mouse events
    @Override
    public void mouseEntered(MouseEvent e) {
        // Ignore
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Ignore
    }

    /**
     * Adds functional listeners that are called when a new point is selected or when its coordinates change.
     * @param listener functional listener to be called on point modifications
     */
    public void addPointModifiedListener(BiFunction<Integer, Point2D.Double, Void> listener) {
        pointModifiedListeners.add(listener);
    }
    public void setSelectedX(double x) {
        spline.setKeyPointX(dragPointIndex, x);
        this.repaint();
    }
    public void setSelectedY(double y) {
        spline.setKeyPointY(dragPointIndex, y);
        this.repaint();
    }

    public void setSplinePointsPerSegment(int splinePointsPerSegment) {
        spline.setSplinePointsPerSegment(splinePointsPerSegment);
        this.repaint();
    }

    public void setSpline(BSpline spline) {
        this.spline = spline;
        this.repaint();
    }
}
