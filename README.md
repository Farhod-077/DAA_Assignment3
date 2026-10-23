# DAA Assignment 3 — Graph Algorithms

A Java Maven project implementing graph algorithms for the
City Network & Job Scheduler assignment.

## Requirements

- JDK 17 or newer.
- Maven 3.8 or newer.
- Python 3 with matplotlib for plots.
- Run all commands from the project root, where pom.xml is located.

## Implemented algorithms

- DSU with union by size and iterative path compression.
- Kruskal minimum spanning forest.
- Prim minimum spanning forest using a priority queue.
- Iterative Kosaraju strongly connected components.
- Kahn topological ordering with cycle detection.
- DAG shortest paths with multiple sources and path restoration.

## Project structure

| Path | Contents |
|---|---|
| src/main/java/graph | Graph, Edge and file reader |
| src/main/java/algorithms | Algorithms and result classes |
| src/main/java/metrics | Operation counters |
| src/main/java/benchmark | Graph generator and benchmark |
| src/test/java | JUnit 5 tests |
| data | Four small input graphs |
| scripts/plot_results.py | Plotting and numerical analysis |
| results/results.csv | 50 benchmark rows |
| results/analysis.md | Normalized counters and slopes |
| results/plots | Three PNG charts |
| REPORT.md | Complexity, correctness and measured results |

## Build and run all tests

```bash
mvn clean verify
```

Expected: 20 tests, no failures or errors, and BUILD SUCCESS.

The tests compare MST algorithms on 100 random connected graphs,
compare SCCs with BFS reachability, and compare DAG shortest paths
with Bellman-Ford on 100 random DAGs.

A path of 100,000 vertices checks that SCC traversal is stack-safe.

## Quick reference check

After building:

```bash
java -cp target/classes benchmark.ReferenceCheck
```

Expected output:

```text
Kruskal weight: 147970
Prim weight: 147970
SCC count: 27
DAG distance sum: 396274
All reference checks passed!
```

## Run the full benchmark

After building:

```bash
java -Xmx2g -cp target/classes benchmark.Benchmark
```

The benchmark uses a new Random(42) for each graph kind and case.

Sizes: 1000, 3162, 10000, 31623 and 100000 vertices.
Densities: m = 4n and m = 20n.

Each algorithm-case runs five times with fresh metrics.
The CSV records the median runtime and reproducible counters.
Reference results and repeatability of counters are checked
on every run.

The benchmark also checks MST agreement and validates every edge
against the returned topological order.

Expected final output:

```text
All benchmark checks passed!
Saved: results/results.csv
Rows: 50
```

Running the benchmark replaces results/results.csv.
Timings may change between runs and machines.

## Generate plots and analysis

On Windows:

```bash
py -m pip install matplotlib
py scripts/plot_results.py
```

On Linux or macOS, use python3 instead of py.

Generated files:

- results/plots/operations_vs_n.png
- results/plots/scans_per_edge.png
- results/plots/kruskal_edge_share.png
- results/analysis.md

The report contains measurements from the submitted benchmark run.
If measurements are regenerated, update its numerical tables too.

## Input format

The first line contains:

```text
n m directed|undirected
```

The next m lines contain:

```text
from to weight
```

Vertices are numbered from 0 to n - 1.
Weights are integers.

Example:

```text
3 2 undirected
0 1 10
1 2 5
```

Input files:

- connected.txt: MST weight 10, one component.
- disconnected.txt: forest weight 7, three components.
- cyclic.txt: three SCCs; topological ordering is impossible.
- dag.txt: distances [0, 0, 2, 0, 1, 3].

For DAG shortest paths, every vertex with in-degree zero starts
at distance zero. Negative edge weights are supported.

## Metrics conventions

- dsu_ops: find calls plus successful unions.
- edge_scans: edges examined in the main algorithm stage.
- comparisons: weight comparisons.
- max_depth: maximum explicit-stack occupancy.

Kosaraju counts 2m traversal scans, excluding graph reversal.
Kahn counts m processing scans on a DAG, excluding in-degree setup.
DAG shortest paths count m relaxation scans, excluding preprocessing.

Full counter definitions and limitations are described in REPORT.md.

## Git workflow

Feature branches:

- feature/dsu
- feature/mst
- feature/scc
- feature/paths
- feature/metrics

The completed submission is on main with release tag v1.0.

## AI-use acknowledgement

I used ChatGPT for step-by-step guidance, code suggestions,
test design, benchmark and plotting scripts, and drafting
documentation. I entered and ran the code locally and checked
the test and benchmark outputs. AI assistance covered substantial
parts of the implementation and documentation.