package benchmark;

import algorithms.*;
import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public class Benchmark {

    private static final int[] SIZES = {
            1000, 3162, 10000, 31623, 100000
    };

    // Столбцы: sparse, dense.
    private static final long[][] MST_REFERENCE = {
            {147970, 30363},
            {468911, 97589},
            {1486139, 309550},
            {4715515, 968517},
            {15012094, 3056681}
    };

    private static final long[][] SCC_REFERENCE = {
            {27, 1},
            {137, 1},
            {373, 1},
            {1188, 1},
            {3923, 1}
    };

    private static final long[][] DAG_REFERENCE = {
            {396274, 252411},
            {1271911, 723157},
            {3941828, 2381342},
            {12551891, 7376612},
            {40096539, 23512087}
    };

    public static void main(String[] args) throws Exception {
        List<BenchmarkRow> rows = new ArrayList<>();

        for (int sizeIndex = 0; sizeIndex < SIZES.length; sizeIndex++) {
            int n = SIZES[sizeIndex];

            for (int density = 0; density < 2; density++) {
                int m = n * (density == 0 ? 4 : 20);
                String label = density == 0 ? "sparse" : "dense";

                System.out.println(
                        "\nGraph: n=" + n + ", m=" + m
                                + ", " + label
                );

                benchmarkMst(
                        rows, n, m, label,
                        MST_REFERENCE[sizeIndex][density]
                );

                benchmarkScc(
                        rows, n, m, label,
                        SCC_REFERENCE[sizeIndex][density]
                );

                benchmarkDag(
                        rows, n, m, label,
                        DAG_REFERENCE[sizeIndex][density]
                );

                // Сохраняем после каждого набора графов.
                writeCsv(rows);
            }
        }

        check(rows.size() == 50, "Expected exactly 50 rows");

        System.out.println("\nAll benchmark checks passed!");
        System.out.println("Saved: results/results.csv");
        System.out.println("Rows: " + rows.size());
    }

    private static void benchmarkMst(
            List<BenchmarkRow> rows,
            int n,
            int m,
            String label,
            long expected
    ) {
        Graph graph = GraphGenerator.undirected(n, m);

        ToLongFunction<MSTResult> extract = result -> {
            check(result.components() == 1,
                    "Generated MST graph must be connected");

            check(result.edges().size() == n - 1,
                    "MST must contain n - 1 edges");

            return result.totalWeight();
        };

        BenchmarkRow kruskal = measure(
                "Kruskal", "undirected_" + label, n, m,
                metrics -> Kruskal.run(graph, metrics),
                extract, expected
        );

        BenchmarkRow prim = measure(
                "Prim", "undirected_" + label, n, m,
                metrics -> Prim.run(graph, metrics),
                extract, expected
        );

        check(kruskal.result() == prim.result(),
                "Kruskal and Prim weights differ");

        rows.add(kruskal);
        rows.add(prim);
    }

    private static void benchmarkScc(
            List<BenchmarkRow> rows,
            int n,
            int m,
            String label,
            long expected
    ) {
        Graph graph = GraphGenerator.directed(n, m);

        rows.add(measure(
                "Kosaraju", "directed_" + label, n, m,
                metrics -> Kosaraju.run(graph, metrics),
                result -> result.components(),
                expected
        ));
    }

    private static void benchmarkDag(
            List<BenchmarkRow> rows,
            int n,
            int m,
            String label,
            long expected
    ) {
        Graph graph = GraphGenerator.dag(n, m);

        rows.add(measure(
                "Kahn", "dag_" + label, n, m,
                metrics -> Kahn.run(graph, metrics),
                result -> {
                    validateOrder(graph, result);
                    return result.order().size();
                },
                n
        ));

        rows.add(measure(
                "DagShortestPath", "dag_" + label, n, m,
                metrics -> DagShortestPath.run(graph, metrics),
                result -> result.sumDistances(),
                expected
        ));
    }

    private static <T> BenchmarkRow measure(
            String algorithm,
            String graph,
            int n,
            int m,
            Function<Metrics, T> action,
            ToLongFunction<T> extract,
            long expected
    ) {
        long[] times = new long[5];
        Metrics firstMetrics = null;

        for (int run = 0; run < times.length; run++) {
            Metrics metrics = new Metrics();

            long start = System.nanoTime();
            T result = action.apply(metrics);
            times[run] = System.nanoTime() - start;

            // Проверки не входят в измеренное время.
            long value = extract.applyAsLong(result);

            check(value == expected,
                    algorithm + ": expected " + expected
                            + ", got " + value
                            + " at n=" + n + ", m=" + m);

            if (algorithm.equals("Kosaraju")) {
                check(metrics.edgeScans() == 2L * m,
                        "Kosaraju edge scans must equal 2m");
            }

            if (algorithm.equals("Kahn")
                    || algorithm.equals("DagShortestPath")) {
                check(metrics.edgeScans() == m,
                        algorithm + " edge scans must equal m");
            }

            if (firstMetrics == null) {
                firstMetrics = metrics;
            } else {
                check(
                        firstMetrics.dsuOps() == metrics.dsuOps()
                                && firstMetrics.edgeScans() == metrics.edgeScans()
                                && firstMetrics.comparisons() == metrics.comparisons()
                                && firstMetrics.maxDepth() == metrics.maxDepth(),
                        algorithm + ": counters differ between runs"
                );
            }
        }

        Arrays.sort(times);
        double medianMs = times[2] / 1_000_000.0;

        BenchmarkRow row = new BenchmarkRow(
                algorithm, graph, n, m,
                medianMs, firstMetrics, expected
        );

        System.out.println(row.toCsv());
        return row;
    }

    private static void validateOrder(
            Graph graph,
            TopologicalResult result
    ) {
        int n = graph.vertexCount();

        check(!result.hasCycle(), "Generated DAG has a cycle");
        check(result.order().size() == n,
                "Topological order has wrong size");

        int[] positions = new int[n];
        Arrays.fill(positions, -1);

        for (int i = 0; i < n; i++) {
            int vertex = result.order().get(i);

            check(vertex >= 0 && vertex < n,
                    "Invalid vertex in topological order");

            check(positions[vertex] == -1,
                    "Duplicate vertex in topological order");

            positions[vertex] = i;
        }

        for (Edge edge : graph.edges()) {
            check(positions[edge.from()] < positions[edge.to()],
                    "Topological order violates an edge");
        }
    }

    private static void writeCsv(
            List<BenchmarkRow> rows
    ) throws Exception {
        Path directory = Path.of("results");
        Files.createDirectories(directory);

        List<String> lines = new ArrayList<>();
        lines.add(BenchmarkRow.csvHeader());

        for (BenchmarkRow row : rows) {
            lines.add(row.toCsv());
        }

        Files.write(
                directory.resolve("results.csv"),
                lines,
                StandardCharsets.UTF_8
        );
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}