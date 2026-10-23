package benchmark;

import algorithms.*;
import graph.Graph;
import metrics.Metrics;

public class ReferenceCheck {

    public static void main(String[] args) {
        int n = 1000;
        int m = 4000;

        Graph undirected = GraphGenerator.undirected(n, m);

        MSTResult kruskal =
                Kruskal.run(undirected, new Metrics());

        MSTResult prim =
                Prim.run(undirected, new Metrics());

        Graph directed = GraphGenerator.directed(n, m);
        Metrics sccMetrics = new Metrics();

        SCCResult scc = Kosaraju.run(directed, sccMetrics);

        Graph dag = GraphGenerator.dag(n, m);
        Metrics kahnMetrics = new Metrics();
        Metrics pathMetrics = new Metrics();

        TopologicalResult order = Kahn.run(dag, kahnMetrics);

        ShortestPathResult paths =
                DagShortestPath.run(dag, pathMetrics);

        System.out.println("Kruskal weight: " + kruskal.totalWeight());
        System.out.println("Prim weight: " + prim.totalWeight());
        System.out.println("SCC count: " + scc.components());
        System.out.println("DAG distance sum: " + paths.sumDistances());

        check(kruskal.totalWeight() == 147970L,
                "Kruskal reference mismatch");

        check(prim.totalWeight() == 147970L,
                "Prim reference mismatch");

        check(scc.components() == 27,
                "SCC reference mismatch");

        check(paths.sumDistances() == 396274L,
                "DAG reference mismatch");

        check(!order.hasCycle() && order.order().size() == n,
                "Invalid topological result");

        check(sccMetrics.edgeScans() == 2L * m,
                "Kosaraju must scan 2m edges");

        check(kahnMetrics.edgeScans() == m,
                "Kahn must scan m edges");

        check(pathMetrics.edgeScans() == m,
                "DAG relaxation must scan m edges");

        System.out.println("All reference checks passed!");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}