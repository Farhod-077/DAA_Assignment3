package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {

    public static MSTResult run(Graph graph, Metrics metrics) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kruskal requires an undirected graph"
            );
        }

        List<Edge> sortedEdges = new ArrayList<>(graph.edges());

        sortedEdges.sort((first, second) -> {
            metrics.recordComparison();

            return Integer.compare(
                    first.weight(),
                    second.weight()
            );
        });

        DSU dsu = new DSU(graph.vertexCount(), metrics);
        List<Edge> selectedEdges = new ArrayList<>();
        long totalWeight = 0;

        for (Edge edge : sortedEdges) {
            if (dsu.components() <= 1) {
                break;
            }

            metrics.recordEdgeScan();

            if (dsu.union(edge.from(), edge.to())) {
                selectedEdges.add(edge);
                totalWeight += edge.weight();
            }
        }

        return new MSTResult(
                selectedEdges,
                totalWeight,
                dsu.components()
        );
    }
}