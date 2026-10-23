package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Kahn {

    public static TopologicalResult run(
            Graph graph,
            Metrics metrics
    ) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kahn requires a directed graph"
            );
        }

        int n = graph.vertexCount();
        int[] inDegree = new int[n];

        // Подготовка: считаем входящие рёбра.
        for (Edge edge : graph.edges()) {
            inDegree[edge.to()]++;
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();

        for (int vertex = 0; vertex < n; vertex++) {
            if (inDegree[vertex] == 0) {
                queue.addLast(vertex);
            }
        }

        List<Integer> order = new ArrayList<>(n);

        while (!queue.isEmpty()) {
            int vertex = queue.removeFirst();
            order.add(vertex);

            for (Edge edge : graph.neighbors(vertex)) {
                metrics.recordEdgeScan();

                int to = edge.to();
                inDegree[to]--;

                if (inDegree[to] == 0) {
                    queue.addLast(to);
                }
            }
        }

        boolean hasCycle = order.size() != n;

        return new TopologicalResult(order, hasCycle);
    }
}