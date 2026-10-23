package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.Arrays;
import java.util.List;

public class Kosaraju {

    public static SCCResult run(Graph graph, Metrics metrics) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kosaraju requires a directed graph"
            );
        }

        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        int[] finishOrder = new int[n];
        int finishCount = 0;

        int[] stack = new int[n];
        int[] nextEdge = new int[n];

        // Первый обход: записываем порядок завершения вершин.
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            int depth = 1;
            stack[0] = start;
            visited[start] = true;
            metrics.recordDepth(depth);

            while (depth > 0) {
                int vertex = stack[depth - 1];
                List<Edge> neighbors = graph.neighbors(vertex);

                if (nextEdge[vertex] < neighbors.size()) {
                    Edge edge = neighbors.get(nextEdge[vertex]++);
                    metrics.recordEdgeScan();

                    int to = edge.to();

                    if (!visited[to]) {
                        visited[to] = true;
                        stack[depth++] = to;
                        metrics.recordDepth(depth);
                    }
                } else {
                    depth--;
                    finishOrder[finishCount++] = vertex;
                }
            }
        }

        Graph reversed = graph.reversed();
        int[] componentIds = new int[n];
        Arrays.fill(componentIds, -1);
        int componentCount = 0;


        for (int i = finishCount - 1; i >= 0; i--) {
            int start = finishOrder[i];

            if (componentIds[start] != -1) {
                continue;
            }

            int depth = 1;
            stack[0] = start;
            componentIds[start] = componentCount;
            metrics.recordDepth(depth);

            while (depth > 0) {
                int vertex = stack[--depth];

                for (Edge edge : reversed.neighbors(vertex)) {
                    metrics.recordEdgeScan();
                    int to = edge.to();

                    if (componentIds[to] == -1) {
                        componentIds[to] = componentCount;
                        stack[depth++] = to;
                        metrics.recordDepth(depth);
                    }
                }
            }

            componentCount++;
        }

        return new SCCResult(componentIds, componentCount);
    }
}