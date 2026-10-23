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
}