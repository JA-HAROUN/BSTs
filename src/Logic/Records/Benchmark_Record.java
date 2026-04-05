package Logic.Records;

import Logic.Enums.Generation_Mode;
import Logic.Enums.Tree_Type;

public record Benchmark_Record(
    Tree_Type treeType,
    Generation_Mode generationMode,
    long insertionTime,
    long treeSortTime,
    long baselineSortTime,
    long containsExistingTime,
    long containsNonExistingTime,
    long deletionTime,
    long traversalTime,
    int treeHeight
) {

}
