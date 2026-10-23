package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {

    public static MSTResult run(Graph graph, Metrics metrics) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Prim requires an undirected graph"
            );
        }

        boolean[] visited = new boolean[graph.vertexCount()];
        List<Edge> selectedEdges = new ArrayList<>();
        long totalWeight = 0;
        int components = 0;

        PriorityQueue<Edge> queue = new PriorityQueue<>(
                (first, second) -> {
                    metrics.recordComparison();

                    return Integer.compare(
                            first.weight(),
                            second.weight()
                    );
                }
        );

        for (int start = 0; start < graph.vertexCount(); start++) {
            if (visited[start]) {
                continue;
            }

            components++;
            visited[start] = true;

            for (Edge edge : graph.neighbors(start)) {
                metrics.recordEdgeScan();

                if (!visited[edge.to()]) {
                    queue.add(edge);
                }
            }

            while (!queue.isEmpty()) {
                Edge edge = queue.poll();
                int vertex = edge.to();

                if (visited[vertex]) {
                    continue;
                }

                visited[vertex] = true;
                selectedEdges.add(edge);
                totalWeight += edge.weight();

                for (Edge next : graph.neighbors(vertex)) {
                    metrics.recordEdgeScan();

                    if (!visited[next.to()]) {
                        queue.add(next);
                    }
                }
            }
        }

        return new MSTResult(
                selectedEdges,
                totalWeight,
                components
        );
    }
}