import algorithms.Kruskal;
import algorithms.MSTResult;
import algorithms.Prim;
import graph.Graph;
import graph.GraphReader;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MSTTest {

    @Test
    void connectedGraphHasExpectedTree() throws IOException {
        Graph graph = GraphReader.read("data/connected.txt");

        MSTResult kruskal = Kruskal.run(graph, new Metrics());
        MSTResult prim = Prim.run(graph, new Metrics());

        assertEquals(10L, kruskal.totalWeight());
        assertEquals(10L, prim.totalWeight());

        assertEquals(4, kruskal.edges().size());
        assertEquals(4, prim.edges().size());

        assertEquals(1, kruskal.components());
        assertEquals(1, prim.components());
    }

    @Test
    void disconnectedGraphHasExpectedForest() throws IOException {
        Graph graph = GraphReader.read("data/disconnected.txt");

        MSTResult kruskal = Kruskal.run(graph, new Metrics());
        MSTResult prim = Prim.run(graph, new Metrics());

        assertEquals(7L, kruskal.totalWeight());
        assertEquals(7L, prim.totalWeight());

        assertEquals(3, kruskal.edges().size());
        assertEquals(3, prim.edges().size());

        assertEquals(3, kruskal.components());
        assertEquals(3, prim.components());
    }

    @Test
    void directedGraphIsRejected() throws IOException {
        Graph graph = GraphReader.read("data/cyclic.txt");

        assertThrows(
                IllegalArgumentException.class,
                () -> Kruskal.run(graph, new Metrics())
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Prim.run(graph, new Metrics())
        );
    }
    @Test
    void algorithmsAgreeOn100RandomConnectedGraphs() {
        java.util.Random random = new java.util.Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(49);
            Graph graph = new Graph(n, false);


            for (int vertex = 1; vertex < n; vertex++) {
                int parent = random.nextInt(vertex);
                int weight = random.nextInt(201) - 100;

                graph.addEdge(parent, vertex, weight);
            }


            for (int i = 0; i < 4 * n; i++) {
                int from = random.nextInt(n);
                int to = random.nextInt(n);
                int weight = random.nextInt(201) - 100;

                graph.addEdge(from, to, weight);
            }

            MSTResult kruskal = Kruskal.run(graph, new Metrics());
            MSTResult prim = Prim.run(graph, new Metrics());

            assertEquals(
                    kruskal.totalWeight(),
                    prim.totalWeight(),
                    "Different MST weights in graph " + test
            );

            assertEquals(n - 1, kruskal.edges().size());
            assertEquals(n - 1, prim.edges().size());

            assertEquals(1, kruskal.components());
            assertEquals(1, prim.components());
        }
    }
}