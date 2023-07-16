package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.model;

import ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math.Vector4;

import java.util.List;

/**
 * Describes object's topology with vertices and edges.
 */
public class Geometry {
    private List<Vector4> vertexList;
    private List<Integer> edgeList;

    public Geometry(List<Vector4> vertexList, List<Integer> edgeList) {
        this.vertexList = vertexList;
        this.edgeList = edgeList;
    }

    /**
     * Vertices are described in vectors in coordinates it is attached to.
     * @return list of vertices
     */
    public List<Vector4> getVertexList() {
        return vertexList;
    }

    /**
     * Each pair in this list describes an edge by indexing its vertices in vertex buffer.
     * Starting vertex of an edge is at index that is multiple of 2, while the ending vertex is the next one.
     * Correct interpretation is: <pre> {@code for (int i = 0; i < edgeList.size()/2; i++) {
     *     int edgeStart = edgeList.get(2 * i);
     *     int edgeEnd = edgeList.get(2 * i + 1);
     * }} </pre>
     * @return list of edges
     */
    public List<Integer> getEdgeList() {
        return edgeList;
    }
}
