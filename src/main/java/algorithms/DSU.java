package algorithms;

import metrics.Metrics;

public class DSU {
    private int[] parent;
    private int[] size;
    private int componentCount;
    private final Metrics metrics;

    public DSU(int n, Metrics metrics) {
        this.metrics = java.util.Objects.requireNonNull(metrics);
        makeSet(n);
    }

    public void makeSet(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(
                    "Size cannot be negative"
            );
        }

        parent = new int[n];
        size = new int[n];
        componentCount = n;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        checkVertex(x);
        metrics.recordFind();

        int root = x;

        while (root != parent[root]) {
            root = parent[root];
        }

        while (x != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }

        return root;
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;
        }

        if (size[rootX] < size[rootY]) {
            int temp = rootX;
            rootX = rootY;
            rootY = temp;
        }

        parent[rootY] = rootX;
        size[rootX] += size[rootY];
        componentCount--;

        metrics.recordUnion();
        return true;
    }

    public int components() {
        return componentCount;
    }

    private void checkVertex(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IllegalArgumentException(
                    "Invalid vertex: " + x
            );
        }
    }
}