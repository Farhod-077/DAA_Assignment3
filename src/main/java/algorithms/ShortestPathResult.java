package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShortestPathResult {
    private final long[] distances;
    private final int[] parents;

    public ShortestPathResult(long[] distances, int[] parents) {
        this.distances = distances.clone();
        this.parents = parents.clone();
    }

    public long distanceTo(int vertex) {
        checkVertex(vertex);
        return distances[vertex];
    }

    public int parentOf(int vertex) {
        checkVertex(vertex);
        return parents[vertex];
    }

    public long[] distances() {
        return distances.clone();
    }

    public long sumDistances() {
        long sum = 0;

        for (long distance : distances) {
            sum += distance;
        }

        return sum;
    }

    public List<Integer> pathTo(int vertex) {
        checkVertex(vertex);
        List<Integer> path = new ArrayList<>();

        int current = vertex;

        while (current != -1) {
            path.add(current);
            current = parents[current];
        }

        Collections.reverse(path);
        return List.copyOf(path);
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= distances.length) {
            throw new IllegalArgumentException(
                    "Invalid vertex: " + vertex
            );
        }
    }
}