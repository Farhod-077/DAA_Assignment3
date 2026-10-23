package benchmark;

import metrics.Metrics;

import java.util.Locale;

public record BenchmarkRow(
        String algorithm,
        String graph,
        int n,
        int m,
        double timeMs,
        Metrics metrics,
        long result
) {

    public static String csvHeader() {
        return "algorithm,graph,n,m,time_ms,"
                + "dsu_ops,edge_scans,comparisons,max_depth,result";
    }

    public String toCsv() {
        return String.format(
                Locale.ROOT,
                "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d",
                algorithm,
                graph,
                n,
                m,
                timeMs,
                metrics.dsuOps(),
                metrics.edgeScans(),
                metrics.comparisons(),
                metrics.maxDepth(),
                result
        );
    }
}