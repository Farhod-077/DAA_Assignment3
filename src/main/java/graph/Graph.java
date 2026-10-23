package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph {
    private final int vertexCount;
    private final boolean directed;
    private final List<List<Edge>> adjacency;
    private final List<Edge> edges;

    public Graph(int vertexCount, boolean directed) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException(
                    "Vertex count cannot be negative"
            );
        }

        this.vertexCount = vertexCount;
        this.directed = directed;
        this.adjacency = new ArrayList<>(vertexCount);
        this.edges = new ArrayList<>();

        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        checkVertex(from);
        checkVertex(to);

        Edge edge = new Edge(from, to, weight);

        edges.add(edge);
        adjacency.get(from).add(edge);

        if (!directed && from != to) {
            adjacency.get(to).add(new Edge(to, from, weight));
        }
    }

    public int vertexCount() {
        return vertexCount;
    }

    public int edgeCount() {
        return edges.size();
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> neighbors(int vertex) {
        checkVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    public Graph reversed() {
        Graph reversed = new Graph(vertexCount, directed);

        for (Edge edge : edges) {
            reversed.addEdge(
                    edge.to(),
                    edge.from(),
                    edge.weight()
            );
        }

        return reversed;
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "Invalid vertex: " + vertex
            );
        }
    }
}