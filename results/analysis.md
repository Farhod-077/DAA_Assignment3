# Measured benchmark analysis

Operations = dsu_ops + edge_scans + comparisons.
These are selected counters, not every CPU operation.
DFS max_depth is a maximum, so it is not added to operations.

## Normalized counters

| Algorithm | Density | Counter / formula | Min | Max |
|---|---|---|---:|---:|
| Kruskal | sparse | comparisons / (m log2(m)) | 0.7447 | 0.9069 |
| Kruskal | sparse | dsu_ops / m | 1.7908 | 2.2483 |
| Kruskal | dense | comparisons / (m log2(m)) | 0.6662 | 0.9027 |
| Kruskal | dense | dsu_ops / m | 0.4647 | 0.7936 |
| Prim | sparse | comparisons / (m log2(m)) | 1.8291 | 1.9344 |
| Prim | dense | comparisons / (m log2(m)) | 1.8248 | 1.8896 |
| Kosaraju | sparse | edge_scans / (n + m) | 1.6000 | 1.6000 |
| Kosaraju | dense | edge_scans / (n + m) | 1.9048 | 1.9048 |
| Kahn | sparse | edge_scans / (n + m) | 0.8000 | 0.8000 |
| Kahn | dense | edge_scans / (n + m) | 0.9524 | 0.9524 |
| DagShortestPath | sparse | edge_scans / (n + m) | 0.8000 | 0.8000 |
| DagShortestPath | dense | edge_scans / (n + m) | 0.9524 | 0.9524 |

## Log-log slopes against n

| Algorithm | Density | Edge scans | Operations | Comparisons |
|---|---|---:|---:|---:|
| Kruskal | sparse | 1.0550 | 1.0537 | 1.0545 |
| Kruskal | dense | 1.1194 | 1.0219 | 1.0159 |
| Prim | sparse | 1.0000 | 1.1007 | 1.1079 |
| Prim | dense | 1.0000 | 1.0850 | 1.0903 |
| Kosaraju | sparse | 1.0000 | 1.0000 | N/A |
| Kosaraju | dense | 1.0000 | 1.0000 | N/A |
| Kahn | sparse | 1.0000 | 1.0000 | N/A |
| Kahn | dense | 1.0000 | 1.0000 | N/A |
| DagShortestPath | sparse | 1.0000 | 1.0000 | N/A |
| DagShortestPath | dense | 1.0000 | 1.0000 | N/A |

Kruskal's sorting log factor appears in comparisons.
Its edge-scan counter excludes sorting and can stop early.
Therefore its scan slope need not exceed one.

## Kruskal edge share

| n | Density | Examined / m |
|---:|---|---:|
| 1000 | sparse | 0.7705 |
| 3162 | sparse | 0.8846 |
| 10000 | sparse | 0.9659 |
| 31623 | sparse | 0.9909 |
| 100000 | sparse | 0.9992 |
| 1000 | dense | 0.2151 |
| 3162 | dense | 0.2281 |
| 10000 | dense | 0.2074 |
| 31623 | dense | 0.3020 |
| 100000 | dense | 0.3718 |

## Median runtime ranges

| Algorithm | Minimum ms | Maximum ms |
|---|---:|---:|
| Kruskal | 2.076 | 456.711 |
| Prim | 3.258 | 1282.475 |
| Kosaraju | 1.360 | 264.478 |
| Kahn | 0.455 | 56.445 |
| DagShortestPath | 1.006 | 113.309 |

Maximum observed Kosaraju stack depth: 91920.

Times are medians of five runs per case.
No separate warm-up phase was used.
JIT compilation, garbage collection and background load
can affect these times.
