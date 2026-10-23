package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.Arrays;

public class DagShortestPath {

    public static ShortestPathResult run(
            Graph graph,
            Metrics metrics
    ) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "DAG shortest paths require a directed graph"
            );
        }

        TopologicalResult topological =
                Kahn.run(graph, new Metrics());

        if (topological.hasCycle()) {
            throw new IllegalArgumentException(
                    "Graph contains a cycle"
            );
        }

        int n = graph.vertexCount();
        int[] inDegree = new int[n];

        for (Edge edge : graph.edges()) {
            inDegree[edge.to()]++;
        }

        long[] distances = new long[n];
        int[] parents = new int[n];

        Arrays.fill(distances, Long.MAX_VALUE);
        Arrays.fill(parents, -1);

        // Все вершины без входящих рёбер — источники.
        for (int vertex = 0; vertex < n; vertex++) {
            if (inDegree[vertex] == 0) {
                distances[vertex] = 0;
            }
        }

        for (int vertex : topological.order()) {
            for (Edge edge : graph.neighbors(vertex)) {
                metrics.recordEdgeScan();

                if (distances[vertex] == Long.MAX_VALUE) {
                    continue;
                }

                long candidate =
                        distances[vertex] + edge.weight();

                if (candidate < distances[edge.to()]) {
                    distances[edge.to()] = candidate;
                    parents[edge.to()] = vertex;
                }
            }
        }

        return new ShortestPathResult(distances, parents);
    }
}