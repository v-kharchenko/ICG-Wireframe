package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.BSpline;
import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Vector4;

import java.util.List;

public class SplineModel extends Geometry {

    @Getter @Setter
    private int acrossLayerCount;
    @Getter @Setter
    private int alongLayerCount;
    @Getter @Setter
    private int rotationCount;

    public SplineModel(List<Vector4> vertexList, List<Integer> edgeList) {
        super(vertexList, edgeList);
    }

}
