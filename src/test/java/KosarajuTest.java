import algorithms.Kosaraju;
import algorithms.SCCResult;
import graph.Graph;
import graph.GraphReader;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class KosarajuTest {

    @Test
    void findsComponentsInCyclicGraph() throws IOException {
        Graph graph = GraphReader.read("data/cyclic.txt");
        Metrics metrics = new Metrics();

        SCCResult result = Kosaraju.run(graph, metrics);

        assertEquals(3, result.components());

        assertTrue(result.sameComponent(0, 1));
        assertTrue(result.sameComponent(1, 2));
        assertTrue(result.sameComponent(3, 4));

        assertFalse(result.sameComponent(0, 3));
        assertFalse(result.sameComponent(3, 5));
        assertFalse(result.sameComponent(0, 5));

        assertEquals(2L * graph.edgeCount(), metrics.edgeScans());
    }

    @Test
    void handlesPathOf100000Vertices() {
        int n = 100_000;
        Graph graph = new Graph(n, true);

        for (int vertex = 0; vertex < n - 1; vertex++) {
            graph.addEdge(vertex, vertex + 1, 1);
        }

        Metrics metrics = new Metrics();

        SCCResult result = assertDoesNotThrow(
                () -> Kosaraju.run(graph, metrics)
        );

        assertEquals(n, result.components());
        assertEquals(2L * (n - 1), metrics.edgeScans());
        assertEquals(n, metrics.maxDepth());
    }

    @Test
    void handlesEmptyGraph() {
        Graph graph = new Graph(0, true);
        Metrics metrics = new Metrics();

        SCCResult result = Kosaraju.run(graph, metrics);

        assertEquals(0, result.components());
        assertEquals(0L, metrics.edgeScans());
        assertEquals(0, metrics.maxDepth());
    }
    @Test
    void matchesBruteForceOnRandomGraphs() {
        java.util.Random random = new java.util.Random(42);

        for (int test = 0; test < 30; test++) {
            int n = 1 + random.nextInt(200);
            Graph graph = new Graph(n, true);

            for (int i = 0; i < 4 * n; i++) {
                graph.addEdge(
                        random.nextInt(n),
                        random.nextInt(n),
                        1
                );
            }

            boolean[][] reachable = findReachability(graph);
            Metrics metrics = new Metrics();
            SCCResult result = Kosaraju.run(graph, metrics);

            int expectedComponents = 0;

            for (int first = 0; first < n; first++) {
                boolean hasEarlierMember = false;

                for (int second = 0; second < n; second++) {
                    boolean same =
                            reachable[first][second]
                                    && reachable[second][first];

                    assertEquals(
                            same,
                            result.sameComponent(first, second),
                            "SCC differs from BFS reference"
                    );

                    if (second < first && same) {
                        hasEarlierMember = true;
                    }
                }

                if (!hasEarlierMember) {
                    expectedComponents++;
                }
            }

            assertEquals(expectedComponents, result.components());
            assertEquals(
                    2L * graph.edgeCount(),
                    metrics.edgeScans()
            );
        }
    }

    private boolean[][] findReachability(Graph graph) {
        int n = graph.vertexCount();
        boolean[][] reachable = new boolean[n][n];

        for (int start = 0; start < n; start++) {
            java.util.ArrayDeque<Integer> queue =
                    new java.util.ArrayDeque<>();

            reachable[start][start] = true;
            queue.addLast(start);

            while (!queue.isEmpty()) {
                int vertex = queue.removeFirst();

                for (graph.Edge edge : graph.neighbors(vertex)) {
                    int to = edge.to();

                    if (!reachable[start][to]) {
                        reachable[start][to] = true;
                        queue.addLast(to);
                    }
                }
            }
        }

        return reachable;
    }
}