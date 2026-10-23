package metrics;

public class Metrics {
    private long findCalls;
    private long successfulUnions;
    private long edgeScans;
    private long comparisons;
    private int maxDepth;

    public void recordFind() {
        findCalls++;
    }

    public void recordUnion() {
        successfulUnions++;
    }

    public void recordEdgeScan() {
        edgeScans++;
    }

    public void recordComparison() {
        comparisons++;
    }

    public void recordDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
    }

    public long findCalls() {
        return findCalls;
    }

    public long successfulUnions() {
        return successfulUnions;
    }

    public long dsuOps() {
        return findCalls + successfulUnions;
    }

    public long edgeScans() {
        return edgeScans;
    }

    public long comparisons() {
        return comparisons;
    }

    public int maxDepth() {
        return maxDepth;
    }
}