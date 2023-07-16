package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.b_spline_editor;

import com.formdev.flatlaf.FlatDarkLaf;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.BSpline;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.Geometry;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.SplineModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * B-Spline editing GUI.
 */
public class BSplineEditor extends JFrame {

    private final BSplinePane splinePane = new BSplinePane();
    private final BSplineParametersPane parametersPane = new BSplineParametersPane(this, splinePane);
    private final List<Function<SplineModel, Void>> splineChangedListeners = new ArrayList<>();

    public BSplineEditor() {
        super("B-Spline Editor");

        FlatDarkLaf.setup();
        SwingUtilities.updateComponentTreeUI(this);

        this.setMinimumSize(new Dimension(640, 480));
        this.setLocation(600, 160);
        this.setVisible(true);

        // EditorPane
        this.add(splinePane, BorderLayout.CENTER);

        // Parameters
        this.add(parametersPane, BorderLayout.PAGE_END);
    }

    /**
     * Returns spline created in the editor
     * @return spline
     */
    public BSpline getSpline() {
        return splinePane.getSpline();
    }

    public SplineModel getSplineModel() {
        return parametersPane.getSplineModel();
    }

    public void addSplineModelChangedListener(Function<SplineModel, Void> listener) {
        splineChangedListeners.add(listener);
    }

    public void applySplineModel() {
        for (var l: splineChangedListeners) {
            l.apply(parametersPane.getSplineModel());
        }
    }

    public void setSpline(BSpline spline) {
        splinePane.setSpline(spline);
    }
}
