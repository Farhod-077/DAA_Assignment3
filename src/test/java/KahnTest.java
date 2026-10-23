import algorithms.Kahn;
import algorithms.TopologicalResult;
import graph.Edge;
import graph.Graph;
import graph.GraphReader;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KahnTest {

    @Test
    void producesValidOrderForDag() throws IOException {
        Graph graph = GraphReader.read("data/dag.txt");
        Metrics metrics = new Metrics();

        TopologicalResult result = Kahn.run(graph, metrics);

        assertFalse(result.hasCycle());
        assertValidOrder(graph, result.order());
        assertEquals(graph.edgeCount(), metrics.edgeScans());
    }

    @Test
    void detectsCycle() throws IOException {
        Graph graph = GraphReader.read("data/cyclic.txt");

        TopologicalResult result =
                Kahn.run(graph, new Metrics());

        assertTrue(result.hasCycle());
        assertTrue(result.order().isEmpty());
    }

    @Test
    void handlesIsolatedVertices() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);

        Metrics metrics = new Metrics();
        TopologicalResult result = Kahn.run(graph, metrics);

        assertFalse(result.hasCycle());
        assertValidOrder(graph, result.order());
        assertEquals(1L, metrics.edgeScans());
    }

    @Test
    void detectsSelfLoop() {
        Graph graph = new Graph(1, true);
        graph.addEdge(0, 0, 1);

        TopologicalResult result =
                Kahn.run(graph, new Metrics());

        assertTrue(result.hasCycle());
        assertTrue(result.order().isEmpty());
    }

    private void assertValidOrder(
            Graph graph,
            List<Integer> order
    ) {
        int n = graph.vertexCount();
        assertEquals(n, order.size());

        int[] position = new int[n];
        Arrays.fill(position, -1);

        for (int i = 0; i < order.size(); i++) {
            int vertex = order.get(i);

            assertTrue(vertex >= 0 && vertex < n);
            assertEquals(-1, position[vertex]);

            position[vertex] = i;
        }

        for (Edge edge : graph.edges()) {
            assertTrue(
                    position[edge.from()] < position[edge.to()],
                    "An edge violates the topological order"
            );
        }
    }
}