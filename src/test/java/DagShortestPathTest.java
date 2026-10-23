import algorithms.DagShortestPath;
import algorithms.ShortestPathResult;
import graph.Graph;
import graph.GraphReader;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DagShortestPathTest {

    @Test
    void findsDistancesAndRestoresPath() throws IOException {
        Graph graph = GraphReader.read("data/dag.txt");
        Metrics metrics = new Metrics();

        ShortestPathResult result =
                DagShortestPath.run(graph, metrics);

        assertArrayEquals(
                new long[]{0, 0, 2, 0, 1, 3},
                result.distances()
        );

        assertEquals(
                List.of(1, 2, 3, 4, 5),
                result.pathTo(5)
        );

        assertEquals(-1, result.parentOf(1));
        assertEquals(6L, result.sumDistances());
        assertEquals(graph.edgeCount(), metrics.edgeScans());
    }

    @Test
    void rejectsCyclicGraph() throws IOException {
        Graph graph = GraphReader.read("data/cyclic.txt");

        assertThrows(
                IllegalArgumentException.class,
                () -> DagShortestPath.run(graph, new Metrics())
        );
    }

    @Test
    void handlesMultipleSourcesAndIsolatedVertex() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 2, 5);
        graph.addEdge(1, 2, -3);

        ShortestPathResult result =
                DagShortestPath.run(graph, new Metrics());

        assertArrayEquals(
                new long[]{0, 0, -3, 0},
                result.distances()
        );

        assertEquals(List.of(1, 2), result.pathTo(2));
        assertEquals(List.of(3), result.pathTo(3));
    }

    @Test
    void handlesEmptyGraph() {
        Graph graph = new Graph(0, true);

        ShortestPathResult result =
                DagShortestPath.run(graph, new Metrics());

        assertArrayEquals(new long[0], result.distances());
        assertEquals(0L, result.sumDistances());
    }
    @Test
    void matchesBellmanFordOnRandomDags() {
        java.util.Random random = new java.util.Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 1 + random.nextInt(30);
            Graph graph = new Graph(n, true);

            for (int from = 0; from < n; from++) {
                for (int to = from + 1; to < n; to++) {
                    if (random.nextDouble() < 0.2) {
                        int weight = random.nextInt(41) - 20;
                        graph.addEdge(from, to, weight);
                    }
                }
            }

            Metrics metrics = new Metrics();
            ShortestPathResult result =
                    DagShortestPath.run(graph, metrics);

            assertArrayEquals(
                    bellmanFord(graph),
                    result.distances(),
                    "Distances differ in graph " + test
            );

            assertEquals(
                    graph.edgeCount(),
                    metrics.edgeScans()
            );
        }
    }

    private long[] bellmanFord(Graph graph) {
        int n = graph.vertexCount();
        int[] inDegree = new int[n];

        for (graph.Edge edge : graph.edges()) {
            inDegree[edge.to()]++;
        }

        long[] distances = new long[n];
        java.util.Arrays.fill(distances, Long.MAX_VALUE);

        for (int vertex = 0; vertex < n; vertex++) {
            if (inDegree[vertex] == 0) {
                distances[vertex] = 0;
            }
        }

        for (int pass = 0; pass < n - 1; pass++) {
            boolean changed = false;

            for (graph.Edge edge : graph.edges()) {
                if (distances[edge.from()] == Long.MAX_VALUE) {
                    continue;
                }

                long candidate =
                        distances[edge.from()] + edge.weight();

                if (candidate < distances[edge.to()]) {
                    distances[edge.to()] = candidate;
                    changed = true;
                }
            }

            if (!changed) {
                break;
            }
        }

        return distances;
    }
}