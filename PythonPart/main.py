import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.gridspec import GridSpec
import warnings
warnings.filterwarnings('ignore')

# ── Styling ────────────────────────────────────────────────────────────────────
BST_COLOR  = "#E07B54"   # warm orange-red
RB_COLOR   = "#4C8BCC"   # steel blue
QS_COLOR   = "#6BBF6A"   # muted green (quicksort reference)
ALPHA_BAR  = 0.85
ALPHA_ERR  = 0.9
FIG_BG     = "#F7F7F7"
AX_BG      = "#FFFFFF"
GRID_COLOR = "#E0E0E0"

plt.rcParams.update({
    "font.family":       "DejaVu Sans",
    "axes.facecolor":    AX_BG,
    "figure.facecolor":  FIG_BG,
    "axes.edgecolor":    "#CCCCCC",
    "axes.grid":         True,
    "grid.color":        GRID_COLOR,
    "grid.linestyle":    "--",
    "grid.linewidth":    0.6,
    "axes.spines.top":   False,
    "axes.spines.right": False,
    "xtick.labelsize":   9,
    "ytick.labelsize":   9,
    "axes.titlesize":    11,
    "axes.titleweight":  "bold",
    "axes.labelsize":    9,
})

# ── Load & Merge ───────────────────────────────────────────────────────────────
bst = pd.read_csv("../Photos/benchmark_results_bst.csv")
rb  = pd.read_csv("../Photos/benchmark_results_rb_tree.csv")
df  = pd.concat([bst, rb], ignore_index=True)

# Friendly distribution labels
def dist_label(row):
    if row["generation_mode"] == "Fully_Random":
        return "Fully Random"
    return f"Nearly Sorted {int(row['ratio'])}%"

df["dist"] = df.apply(dist_label, axis=1)
DISTS = ["Fully Random", "Nearly Sorted 1%", "Nearly Sorted 5%", "Nearly Sorted 10%"]

NS = 1e6   # convert ns → ms
def to_ms(x): return x / NS

# ── Helper: aggregate stats ────────────────────────────────────────────────────
def stats(group, col):
    vals = to_ms(group[col])
    return vals.median(), vals.mean(), vals.std()

def build_stats(metric):
    rows = []
    for dist in DISTS:
        for tree, label in [("NORMAL", "BST"), ("RB_TREE", "RB-Tree")]:
            g = df[(df["dist"] == dist) & (df["tree_type"] == tree)]
            med, mean, std = stats(g, metric)
            rows.append({"dist": dist, "tree": label, "median": med, "mean": mean, "std": std})
    return pd.DataFrame(rows)

# ── Helper: grouped bar chart ──────────────────────────────────────────────────
def grouped_bars(ax, stats_df, title, ylabel, speedup=True):
    x       = np.arange(len(DISTS))
    w       = 0.32
    colors  = {"BST": BST_COLOR, "RB-Tree": RB_COLOR}

    for i, (tree, color) in enumerate(colors.items()):
        sub  = stats_df[stats_df["tree"] == tree].set_index("dist").loc[DISTS]
        bars = ax.bar(x + (i - 0.5) * w, sub["median"], w,
                      color=color, alpha=ALPHA_BAR, label=tree,
                      zorder=3, edgecolor="white", linewidth=0.5)
        ax.errorbar(x + (i - 0.5) * w, sub["median"], yerr=sub["std"],
                    fmt="none", color="black", capsize=3, linewidth=1,
                    alpha=ALPHA_ERR, zorder=4)

    ax.set_title(title)
    ax.set_ylabel(ylabel)
    ax.set_xticks(x)
    ax.set_xticklabels(DISTS, rotation=20, ha="right")
    ax.legend(framealpha=0.9, fontsize=8)
    ax.set_ylim(bottom=0)

    # Speedup annotations
    if speedup:
        bst_vals = stats_df[stats_df["tree"] == "BST"].set_index("dist").loc[DISTS, "median"]
        rb_vals  = stats_df[stats_df["tree"] == "RB-Tree"].set_index("dist").loc[DISTS, "median"]
        for xi, (bv, rv) in enumerate(zip(bst_vals, rb_vals)):
            if rv > 0:
                su = bv / rv
                color = "#2E7D32" if su >= 1 else "#B71C1C"
                ax.text(xi, max(bv, rv) * 1.04, f"×{su:.1f}",
                        ha="center", va="bottom", fontsize=7.5,
                        fontweight="bold", color=color)

# ══════════════════════════════════════════════════════════════════════════════
# FIGURE 1 – Core operations (Insert · Contains · Delete)
# ══════════════════════════════════════════════════════════════════════════════
fig1, axes1 = plt.subplots(1, 3, figsize=(16, 5))
fig1.suptitle("BST vs RB-Tree — Core Operation Latency (median ± 1σ, n=100k)",
              fontsize=13, fontweight="bold", y=1.02)

grouped_bars(axes1[0], build_stats("insertion_ns"),       "Insert (all 100k elements)",   "Time (ms)")
grouped_bars(axes1[1], build_stats("contains_existing_ns"), "Contains – Existing keys",   "Time (ms)")
grouped_bars(axes1[2], build_stats("deletion_ns"),         "Delete (20% of elements)",    "Time (ms)")

fig1.tight_layout()
fig1.savefig("../Photos/fig1_core_operations.png", dpi=150, bbox_inches="tight")
print("Saved fig1_core_operations.png")

# ══════════════════════════════════════════════════════════════════════════════
# FIGURE 2 – Tree Sort vs Quicksort
# ══════════════════════════════════════════════════════════════════════════════
fig2, ax2 = plt.subplots(figsize=(11, 5))
fig2.suptitle("Tree Sort (BST / RB-Tree) vs Quicksort — Build + Traversal Time",
              fontsize=13, fontweight="bold")

x   = np.arange(len(DISTS))
w   = 0.25
tss = build_stats("tree_sort_ns")

for i, (tree, color, off) in enumerate([("BST", BST_COLOR, -1), ("RB-Tree", RB_COLOR, 0)]):
    sub  = tss[tss["tree"] == tree].set_index("dist").loc[DISTS]
    bars = ax2.bar(x + off * w, sub["median"], w, color=color, alpha=ALPHA_BAR,
                   label=f"Tree Sort ({tree})", zorder=3, edgecolor="white", linewidth=0.5)
    ax2.errorbar(x + off * w, sub["median"], yerr=sub["std"],
                 fmt="none", color="black", capsize=3, linewidth=1, zorder=4)

# Quicksort bars (same for both trees → use BST rows only, average both)
for tree in ["NORMAL", "RB_TREE"]:
    pass
qs_rows = []
for dist in DISTS:
    g = df[df["dist"] == dist]
    med, mean, std = stats(g, "quick_sort_ns")
    qs_rows.append({"dist": dist, "median": med, "std": std})
qs_df = pd.DataFrame(qs_rows).set_index("dist").loc[DISTS]

ax2.bar(x + 1 * w, qs_df["median"], w, color=QS_COLOR, alpha=ALPHA_BAR,
        label="Quicksort", zorder=3, edgecolor="white", linewidth=0.5)
ax2.errorbar(x + 1 * w, qs_df["median"], yerr=qs_df["std"],
             fmt="none", color="black", capsize=3, linewidth=1, zorder=4)

ax2.set_ylabel("Time (ms)")
ax2.set_xticks(x)
ax2.set_xticklabels(DISTS, rotation=20, ha="right")
ax2.legend(framealpha=0.9, fontsize=8)
ax2.set_ylim(bottom=0)
fig2.tight_layout()
fig2.savefig("../Photos/fig2_tree_sort_vs_quicksort.png", dpi=150, bbox_inches="tight")
print("Saved fig2_tree_sort_vs_quicksort.png")

# ══════════════════════════════════════════════════════════════════════════════
# FIGURE 3 – Tree Height Comparison
# ══════════════════════════════════════════════════════════════════════════════
fig3, ax3 = plt.subplots(figsize=(10, 5))
fig3.suptitle("Tree Height After Insertion — BST vs RB-Tree",
              fontsize=13, fontweight="bold")

h_rows = []
for dist in DISTS:
    for tree, label in [("NORMAL", "BST"), ("RB_TREE", "RB-Tree")]:
        g = df[(df["dist"] == dist) & (df["tree_type"] == tree)]
        h_rows.append({"dist": dist, "tree": label,
                        "median": g["tree_height"].median(),
                        "std":    g["tree_height"].std()})
h_df = pd.DataFrame(h_rows)

x = np.arange(len(DISTS))
w = 0.32
for i, (tree, color) in enumerate([("BST", BST_COLOR), ("RB-Tree", RB_COLOR)]):
    sub  = h_df[h_df["tree"] == tree].set_index("dist").loc[DISTS]
    bars = ax3.bar(x + (i - 0.5) * w, sub["median"], w, color=color,
                   alpha=ALPHA_BAR, label=tree, zorder=3, edgecolor="white", linewidth=0.5)
    for xi, (bar, val) in enumerate(zip(bars, sub["median"])):
        ax3.text(bar.get_x() + bar.get_width()/2, val + 20,
                 f"{int(val)}", ha="center", va="bottom", fontsize=8, fontweight="bold")

ax3.set_ylabel("Height (nodes)")
ax3.set_xticks(x)
ax3.set_xticklabels(DISTS, rotation=20, ha="right")
ax3.legend(framealpha=0.9, fontsize=8)
ax3.set_ylim(bottom=0)
fig3.tight_layout()
fig3.savefig("../Photos/fig3_tree_height.png", dpi=150, bbox_inches="tight")
print("Saved fig3_tree_height.png")

# ══════════════════════════════════════════════════════════════════════════════
# FIGURE 4 – Speedup heatmap  (BST time / RB-Tree time)
# ══════════════════════════════════════════════════════════════════════════════
METRICS = {
    "Insert":             "insertion_ns",
    "Contains\n(existing)": "contains_existing_ns",
    "Contains\n(missing)":  "contains_non_existing_ns",
    "Delete":             "deletion_ns",
    "Tree Sort":          "tree_sort_ns",
}

speedup_matrix = np.zeros((len(METRICS), len(DISTS)))
for j, dist in enumerate(DISTS):
    for i, (mlabel, mcol) in enumerate(METRICS.items()):
        bst_med = to_ms(df[(df["dist"] == dist) & (df["tree_type"] == "NORMAL")][mcol].median())
        rb_med  = to_ms(df[(df["dist"] == dist) & (df["tree_type"] == "RB_TREE")][mcol].median())
        speedup_matrix[i, j] = bst_med / rb_med if rb_med > 0 else np.nan

fig4, ax4 = plt.subplots(figsize=(10, 5))
fig4.suptitle("Speedup Heatmap  (BST time ÷ RB-Tree time)\n"
              "Values > 1 mean RB-Tree is faster; < 1 means BST is faster",
              fontsize=12, fontweight="bold")

import matplotlib.colors as mcolors
cmap   = plt.cm.RdYlGn
norm   = mcolors.TwoSlopeNorm(vmin=0.5, vcenter=1.0, vmax=speedup_matrix.max())
im     = ax4.imshow(speedup_matrix, cmap=cmap, norm=norm, aspect="auto")

ax4.set_xticks(range(len(DISTS)))
ax4.set_xticklabels(DISTS, rotation=20, ha="right", fontsize=9)
ax4.set_yticks(range(len(METRICS)))
ax4.set_yticklabels(list(METRICS.keys()), fontsize=9)

for i in range(len(METRICS)):
    for j in range(len(DISTS)):
        val = speedup_matrix[i, j]
        txt = f"×{val:.2f}"
        textcolor = "black" if 0.6 < val < 3.5 else "white"
        ax4.text(j, i, txt, ha="center", va="center", fontsize=9,
                 fontweight="bold", color=textcolor)

plt.colorbar(im, ax=ax4, label="Speedup (BST / RB-Tree)", fraction=0.03, pad=0.04)
fig4.tight_layout()
fig4.savefig("../Photos/fig4_speedup_heatmap.png", dpi=150, bbox_inches="tight")
print("Saved fig4_speedup_heatmap.png")

# ══════════════════════════════════════════════════════════════════════════════
# FIGURE 5 – Contains (existing vs non-existing) side-by-side
# ══════════════════════════════════════════════════════════════════════════════
fig5, axes5 = plt.subplots(1, 2, figsize=(13, 5), sharey=False)
fig5.suptitle("Contains Latency — Existing vs Non-Existing Keys",
              fontsize=13, fontweight="bold")

grouped_bars(axes5[0], build_stats("contains_existing_ns"),
             "Contains – Key EXISTS",     "Time (ms)")
grouped_bars(axes5[1], build_stats("contains_non_existing_ns"),
             "Contains – Key NOT PRESENT", "Time (ms)")

fig5.tight_layout()
fig5.savefig("../Photos/fig5_contains_breakdown.png", dpi=150, bbox_inches="tight")
print("Saved fig5_contains_breakdown.png")

print("\nAll figures saved to ../Photos/")