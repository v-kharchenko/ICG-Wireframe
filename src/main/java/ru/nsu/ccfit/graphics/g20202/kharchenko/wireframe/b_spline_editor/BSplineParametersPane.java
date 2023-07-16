package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.b_spline_editor;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.BSpline;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.ModelFactory;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model.SplineModel;

import javax.swing.*;
import java.awt.*;

public class BSplineParametersPane extends JPanel {

    private final BSplinePane splinePane;

    private SpinnerNumberModel rotationSpinnerModel = new SpinnerNumberModel(6, 1, 360, 1);
    private SpinnerNumberModel alongLayersSpinnerModel = new SpinnerNumberModel(6, 1, 6, 1);
    private SpinnerNumberModel acrossLayersSpinnerModel = new SpinnerNumberModel(0, 0, 0, 1);
    private SpinnerNumberModel splinePointsPerSegmentSpinnerModel = new SpinnerNumberModel(10, 1, 100, 1);

    public BSplineParametersPane(BSplineEditor splineEditor, BSplinePane splinePane) {
        super();
        this.splinePane = splinePane;

        // Spline parameters
        this.add(this.getSplineParametersPane());

        // Selected point parameters
        this.add(this.getKeyPointParametersPane());

        // Model parameters
        this.add(this.getModelParametersPane());

        // Apply spline to model
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> splineEditor.applySplineModel());
        this.add(applyButton);
    }

    private JPanel getSplineParametersPane() {
        JPanel splineParametersPane = new JPanel();
        splineParametersPane.setLayout(new BoxLayout(splineParametersPane, BoxLayout.PAGE_AXIS));

        // Spline points per segment
        JSpinner splinePointsPerSegmentSpinner = new JSpinner(splinePointsPerSegmentSpinnerModel);
        splineParametersPane.add(getSpinnerPane("Points per spline segment", splinePointsPerSegmentSpinner));

        splinePointsPerSegmentSpinner.addChangeListener(l -> {
            splinePane.setSplinePointsPerSegment((int)splinePointsPerSegmentSpinnerModel.getValue());

            acrossLayersSpinnerModel.setMaximum(splinePane.getSpline().getSplinePoints().size());
            acrossLayersSpinnerModel.setValue(Math.min((Integer) acrossLayersSpinnerModel.getNumber(), (Integer) acrossLayersSpinnerModel.getMaximum()));
        });

        return splineParametersPane;
    }

    private JPanel getKeyPointParametersPane() {
        JPanel keyPointParametersPane = new JPanel();
        keyPointParametersPane.setLayout(new BoxLayout(keyPointParametersPane, BoxLayout.PAGE_AXIS));

        // Key point index
        JPanel indexPane = new JPanel();
        indexPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        indexPane.add(new JLabel("Index: "));

        JTextArea indexField = new JTextArea();
        indexField.setEditable(false);
        indexPane.add(indexField);

        keyPointParametersPane.add(indexPane);

        // X coordinate
        SpinnerNumberModel xSpinnerModel = new SpinnerNumberModel(0.0, -100.0, 100.0, 1.0);
        JSpinner xSpinner = new JSpinner(xSpinnerModel);
        keyPointParametersPane.add(getSpinnerPane("X", xSpinner));

        // Y coordinate
        SpinnerNumberModel ySpinnerModel = new SpinnerNumberModel(0.0, -100.0, 100.0, 1.0);
        JSpinner ySpinner = new JSpinner(ySpinnerModel);
        keyPointParametersPane.add(getSpinnerPane("Y", ySpinner));

        xSpinner.addChangeListener(c -> splinePane.setSelectedX((double)xSpinnerModel.getNumber()));
        ySpinner.addChangeListener(c -> splinePane.setSelectedY((double)ySpinnerModel.getNumber()));

        splinePane.addPointModifiedListener((i, p) -> {
            if (i == -1) {
                indexField.setText("None");
                xSpinner.setEnabled(false);
                ySpinner.setEnabled(false);
                return null;
            }
            indexField.setText(String.valueOf(i));

            xSpinner.setEnabled(true);
            ySpinner.setEnabled(true);

            xSpinnerModel.setValue(p.x);
            ySpinnerModel.setValue(p.y);

            return null;
        });

        return keyPointParametersPane;
    }

    private JPanel getModelParametersPane() {
        JPanel modelParametersPane = new JPanel();
        modelParametersPane.setLayout(new BoxLayout(modelParametersPane, BoxLayout.PAGE_AXIS));

        // Rotation count
        JSpinner rotationSpinner = new JSpinner(rotationSpinnerModel);
        modelParametersPane.add(getSpinnerPane("Rotation count", rotationSpinner));

        // Along-layer count
        JSpinner alongLayersSpinner = new JSpinner(alongLayersSpinnerModel);
        modelParametersPane.add(getSpinnerPane("Number of along-layers", alongLayersSpinner));

        // Rotation count
        JSpinner acrossLayersSpinner = new JSpinner(acrossLayersSpinnerModel);
        modelParametersPane.add(getSpinnerPane("Number of across-layers", acrossLayersSpinner));

        rotationSpinner.addChangeListener(e -> {
            alongLayersSpinnerModel.setMaximum((int)rotationSpinnerModel.getNumber());
            alongLayersSpinnerModel.setValue(Math.min((Integer) alongLayersSpinnerModel.getNumber(), (Integer) alongLayersSpinnerModel.getMaximum()));
        });

        splinePane.addPointModifiedListener((i, p) -> {
            int splinePointCount = splinePane.getSpline().getSplinePoints().size();

            acrossLayersSpinnerModel.setMaximum(splinePointCount);
            acrossLayersSpinnerModel.setValue(Math.min((Integer) acrossLayersSpinnerModel.getNumber(), (Integer) acrossLayersSpinnerModel.getMaximum()));

            return null;
        });

        return modelParametersPane;
    }

    private JPanel getSpinnerPane(String name, JSpinner spinner) {
        JPanel pane = new JPanel();
        pane.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(name + ": ");
        pane.add(label);
        pane.add(spinner);
        return pane;
    }

    public SplineModel getSplineModel() {
        // Spline
        BSpline spline = splinePane.getSpline();

        // Spline parameters
        int rotationCount = (int)rotationSpinnerModel.getNumber();
        int alongLayerCount = (int)alongLayersSpinnerModel.getNumber();
        int acrossLayerCount = (int)acrossLayersSpinnerModel.getNumber();

        // Model
        return ModelFactory.createRotatedSplineModel(spline, rotationCount, alongLayerCount, acrossLayerCount);
    }

    public void setParameters(int rotationCount, int alongLayerCount, int acrossLayerCount) {
    }

}
