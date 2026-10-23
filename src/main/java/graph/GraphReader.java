package graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class GraphReader {

    public static Graph read(String filename) throws IOException {
        try (Scanner scanner = new Scanner(Path.of(filename))) {
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            String type = scanner.next();

            if (m < 0) {
                throw new IllegalArgumentException(
                        "Edge count cannot be negative"
                );
            }

            if (!type.equals("directed")
                    && !type.equals("undirected")) {
                throw new IllegalArgumentException(
                        "Expected directed or undirected"
                );
            }

            Graph graph = new Graph(n, type.equals("directed"));

            for (int i = 0; i < m; i++) {
                int from = scanner.nextInt();
                int to = scanner.nextInt();
                int weight = scanner.nextInt();

                graph.addEdge(from, to, weight);
            }

            if (scanner.hasNext()) {
                throw new IllegalArgumentException(
                        "Unexpected data after the edges"
                );
            }

            return graph;
        }
    }
}