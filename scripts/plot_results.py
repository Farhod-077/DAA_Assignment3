import csv
import math
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt


ROOT = Path(__file__).resolve().parents[1]
RESULTS = ROOT / "results"
PLOTS = RESULTS / "plots"
PLOTS.mkdir(parents=True, exist_ok=True)

with (RESULTS / "results.csv").open(
    encoding="utf-8", newline=""
) as file:
    rows = list(csv.DictReader(file))

if len(rows) != 50:
    raise ValueError("Expected 50 benchmark rows")

for row in rows:
    for key in (
        "n", "m", "dsu_ops", "edge_scans",
        "comparisons", "max_depth", "result"
    ):
        row[key] = int(row[key])

    row["time_ms"] = float(row["time_ms"])
    row["density"] = row["graph"].rsplit("_", 1)[1]
    row["operations"] = (
        row["dsu_ops"]
        + row["edge_scans"]
        + row["comparisons"]
    )

ALGORITHMS = [
    "Kruskal", "Prim", "Kosaraju",
    "Kahn", "DagShortestPath"
]

COLORS = {
    "Kruskal": "#2563eb",
    "Prim": "#dc2626",
    "Kosaraju": "#16a34a",
    "Kahn": "#9333ea",
    "DagShortestPath": "#ea580c"
}


def select(algorithm, density):
    return sorted(
        [
            row for row in rows
            if row["algorithm"] == algorithm
            and row["density"] == density
        ],
        key=lambda row: row["n"]
    )


def slope(points, key):
    xs = [math.log(row["n"]) for row in points]
    ys = [math.log(row[key]) for row in points]

    mean_x = sum(xs) / len(xs)
    mean_y = sum(ys) / len(ys)

    return sum(
        (x - mean_x) * (y - mean_y)
        for x, y in zip(xs, ys)
    ) / sum((x - mean_x) ** 2 for x in xs)


def finish_plot(fig, ax, filename):
    ax.grid(True, which="both", alpha=0.25)
    ax.legend(
        loc="upper left",
        bbox_to_anchor=(1.02, 1),
        fontsize=8
    )
    fig.tight_layout()
    fig.savefig(PLOTS / filename, dpi=180)
    plt.close(fig)


# 1. Counted operations vs number of vertices.
fig, ax = plt.subplots(figsize=(11, 6))

for algorithm in ALGORITHMS:
    for density, style in (
        ("sparse", "-"),
        ("dense", "--")
    ):
        points = select(algorithm, density)

        ax.plot(
            [row["n"] for row in points],
            [row["operations"] for row in points],
            linestyle=style,
            marker="o",
            color=COLORS[algorithm],
            label=f"{algorithm} / {density}"
        )

ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("Vertices n")
ax.set_ylabel("dsu_ops + edge_scans + comparisons")
ax.set_title("Counted operations vs graph size")
finish_plot(fig, ax, "operations_vs_n.png")


# 2. Scans per input edge.
fig, ax = plt.subplots(figsize=(11, 6))

for algorithm in ALGORITHMS:
    for density, style in (
        ("sparse", "-"),
        ("dense", "--")
    ):
        points = select(algorithm, density)

        ax.plot(
            [row["n"] for row in points],
            [row["edge_scans"] / row["m"] for row in points],
            linestyle=style,
            marker="o",
            color=COLORS[algorithm],
            label=f"{algorithm} / {density}"
        )

ax.set_xscale("log")
ax.set_xlabel("Vertices n")
ax.set_ylabel("edge_scans / m")
ax.set_title("Edge scans per input edge (some curves overlap)")
finish_plot(fig, ax, "scans_per_edge.png")


# 3. Fraction of edges examined by Kruskal.
fig, ax = plt.subplots(figsize=(9, 5))

for density, style in (
    ("sparse", "-"),
    ("dense", "--")
):
    points = select("Kruskal", density)

    ax.plot(
        [row["n"] for row in points],
        [row["edge_scans"] / row["m"] for row in points],
        linestyle=style,
        marker="o",
        label=density
    )

ax.set_xscale("log")
ax.set_xlabel("Vertices n")
ax.set_ylabel("Kruskal edge_scans / m")
ax.set_title("Share of edges examined after sorting")
finish_plot(fig, ax, "kruskal_edge_share.png")


# Numerical evidence for the report.
lines = [
    "# Measured benchmark analysis",
    "",
    "Operations = dsu_ops + edge_scans + comparisons.",
    "These are selected counters, not every CPU operation.",
    "DFS max_depth is a maximum, so it is not added to operations.",
    "",
    "## Normalized counters",
    "",
    "| Algorithm | Density | Counter / formula | Min | Max |",
    "|---|---|---|---:|---:|"
]

for algorithm in ALGORITHMS:
    for density in ("sparse", "dense"):
        points = select(algorithm, density)

        if algorithm in ("Kruskal", "Prim"):
            formula = "comparisons / (m log2(m))"
            values = [
                row["comparisons"]
                / (row["m"] * math.log2(row["m"]))
                for row in points
            ]
        else:
            formula = "edge_scans / (n + m)"
            values = [
                row["edge_scans"] / (row["n"] + row["m"])
                for row in points
            ]

        lines.append(
            f"| {algorithm} | {density} | {formula} | "
            f"{min(values):.4f} | {max(values):.4f} |"
        )

        if algorithm == "Kruskal":
            values = [
                row["dsu_ops"] / row["m"]
                for row in points
            ]
            lines.append(
                f"| Kruskal | {density} | dsu_ops / m | "
                f"{min(values):.4f} | {max(values):.4f} |"
            )

lines += [
    "",
    "## Log-log slopes against n",
    "",
    "| Algorithm | Density | Edge scans | Operations | Comparisons |",
    "|---|---|---:|---:|---:|"
]

for algorithm in ALGORITHMS:
    for density in ("sparse", "dense"):
        points = select(algorithm, density)

        comparison_slope = (
            f"{slope(points, 'comparisons'):.4f}"
            if all(row["comparisons"] > 0 for row in points)
            else "N/A"
        )

        lines.append(
            f"| {algorithm} | {density} | "
            f"{slope(points, 'edge_scans'):.4f} | "
            f"{slope(points, 'operations'):.4f} | "
            f"{comparison_slope} |"
        )

lines += [
    "",
    "Kruskal's sorting log factor appears in comparisons.",
    "Its edge-scan counter excludes sorting and can stop early.",
    "Therefore its scan slope need not exceed one.",
    "",
    "## Kruskal edge share",
    "",
    "| n | Density | Examined / m |",
    "|---:|---|---:|"
]

for density in ("sparse", "dense"):
    for row in select("Kruskal", density):
        lines.append(
            f"| {row['n']} | {density} | "
            f"{row['edge_scans'] / row['m']:.4f} |"
        )

lines += [
    "",
    "## Median runtime ranges",
    "",
    "| Algorithm | Minimum ms | Maximum ms |",
    "|---|---:|---:|"
]

for algorithm in ALGORITHMS:
    times = [
        row["time_ms"] for row in rows
        if row["algorithm"] == algorithm
    ]

    lines.append(
        f"| {algorithm} | {min(times):.3f} | {max(times):.3f} |"
    )

max_depth = max(
    row["max_depth"] for row in rows
    if row["algorithm"] == "Kosaraju"
)

lines += [
    "",
    f"Maximum observed Kosaraju stack depth: {max_depth}.",
    "",
    "Times are medians of five runs per case.",
    "No separate warm-up phase was used.",
    "JIT compilation, garbage collection and background load",
    "can affect these times."
]

(RESULTS / "analysis.md").write_text(
    "\n".join(lines) + "\n",
    encoding="utf-8"
)

print("Created 3 PNG plots in results/plots/")
print("Created results/analysis.md")