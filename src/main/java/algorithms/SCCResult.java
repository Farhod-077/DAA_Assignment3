package algorithms;

public class SCCResult {
    private final int[] componentIds;
    private final int componentCount;

    public SCCResult(int[] componentIds, int componentCount) {
        this.componentIds = componentIds.clone();
        this.componentCount = componentCount;
    }

    public int components() {
        return componentCount;
    }

    public int componentOf(int vertex) {
        if (vertex < 0 || vertex >= componentIds.length) {
            throw new IllegalArgumentException(
                    "Invalid vertex: " + vertex
            );
        }

        return componentIds[vertex];
    }

    public boolean sameComponent(int first, int second) {
        return componentOf(first) == componentOf(second);
    }

    public int[] componentIds() {
        return componentIds.clone();
    }
}