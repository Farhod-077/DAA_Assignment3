package benchmark;

import graph.Graph;

import java.util.Random;

public class GraphGenerator {

    public static Graph undirected(int n, int m) {
        checkSize(n, m);

        if (m < n - 1) {
            throw new IllegalArgumentException(
                    "A connected graph needs at least n - 1 edges"
            );
        }

        Random random = new Random(42);
        Graph graph = new Graph(n, false);
        int[] perm = shuffled(n, random);

        // Создаём дерево, чтобы граф был связным.
        for (int i = 1; i < n; i++) {
            int from = perm[random.nextInt(i)];
            int to = perm[i];
            int weight = 1 + random.nextInt(1000);

            graph.addEdge(from, to, weight);
        }

        addRandomEdges(graph, m - (n - 1), random, null);
        return graph;
    }

    public static Graph directed(int n, int m) {
        checkSize(n, m);

        Random random = new Random(42);
        Graph graph = new Graph(n, true);

        addRandomEdges(graph, m, random, null);
        return graph;
    }

    public static Graph dag(int n, int m) {
        checkSize(n, m);

        Random random = new Random(42);
        Graph graph = new Graph(n, true);
        int[] rank = shuffled(n, random);

        addRandomEdges(graph, m, random, rank);
        return graph;
    }

    private static int[] shuffled(int n, Random random) {
        int[] values = new int[n];

        for (int i = 0; i < n; i++) {
            values[i] = i;
        }

        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }

        return values;
    }

    private static void addRandomEdges(
            Graph graph,
            int count,
            Random random,
            int[] rank
    ) {
        int n = graph.vertexCount();

        for (int i = 0; i < count; i++) {
            int from = random.nextInt(n);
            int to = random.nextInt(n);

            while (to == from) {
                to = random.nextInt(n);
            }

            if (rank != null && rank[from] > rank[to]) {
                int temp = from;
                from = to;
                to = temp;
            }

            int weight = 1 + random.nextInt(1000);
            graph.addEdge(from, to, weight);
        }
    }

    private static void checkSize(int n, int m) {
        if (n < 2 || m < 0) {
            throw new IllegalArgumentException(
                    "Generator requires n >= 2 and m >= 0"
            );
        }
    }
}