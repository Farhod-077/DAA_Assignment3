package algorithms;

import java.util.List;

public class TopologicalResult {
    private final List<Integer> order;
    private final boolean hasCycle;

    public TopologicalResult(
            List<Integer> order,
            boolean hasCycle
    ) {
        if (hasCycle) {
            this.order = List.of();
        } else {
            this.order = List.copyOf(order);
        }

        this.hasCycle = hasCycle;
    }

    public List<Integer> order() {
        return order;
    }

    public boolean hasCycle() {
        return hasCycle;
    }
}