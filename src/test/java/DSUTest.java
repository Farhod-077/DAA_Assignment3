import algorithms.DSU;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DSUTest {

    @Test
    void startsWithSeparateComponents() {
        Metrics metrics = new Metrics();
        DSU dsu = new DSU(5, metrics);

        assertEquals(5, dsu.components());

        for (int i = 0; i < 5; i++) {
            assertEquals(i, dsu.find(i));
        }

        assertEquals(5, metrics.findCalls());
        assertEquals(0, metrics.successfulUnions());
    }

    @Test
    void mergesComponentsAndCountsOperations() {
        Metrics metrics = new Metrics();
        DSU dsu = new DSU(5, metrics);

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));
        assertFalse(dsu.union(0, 2));

        assertEquals(3, dsu.components());

        assertEquals(6, metrics.findCalls());
        assertEquals(2, metrics.successfulUnions());
        assertEquals(8, metrics.dsuOps());

        assertEquals(dsu.find(0), dsu.find(2));
        assertNotEquals(dsu.find(0), dsu.find(3));
    }
    @Test
    void randomUnionsMatchReference() {
        int n = 100;
        Metrics metrics = new Metrics();
        DSU dsu = new DSU(n, metrics);

        int[] labels = new int[n];

        for (int i = 0; i < n; i++) {
            labels[i] = i;
        }

        java.util.Random random = new java.util.Random(42);

        for (int step = 0; step < 1000; step++) {
            int x = random.nextInt(n);
            int y = random.nextInt(n);

            int oldLabel = labels[y];
            int newLabel = labels[x];
            boolean shouldMerge = oldLabel != newLabel;

            assertEquals(shouldMerge, dsu.union(x, y));

            if (shouldMerge) {
                for (int i = 0; i < n; i++) {
                    if (labels[i] == oldLabel) {
                        labels[i] = newLabel;
                    }
                }
            }

            java.util.Set<Integer> groups =
                    new java.util.HashSet<>();

            for (int label : labels) {
                groups.add(label);
            }

            assertEquals(groups.size(), dsu.components());

            for (int vertex = 0; vertex < n; vertex++) {
                assertEquals(
                        labels[x] == labels[vertex],
                        dsu.find(x) == dsu.find(vertex)
                );
            }
        }
    }
}