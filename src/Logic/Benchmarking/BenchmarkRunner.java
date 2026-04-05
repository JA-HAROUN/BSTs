package Logic.Benchmarking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import Logic.Enums.Generation_Mode;
import Logic.Enums.Tree_Type;
import Logic.InputDistribution.InputGenerator;
import Logic.Records.Benchmark_Record;

public class BenchmarkRunner {

    Benchmark benchmark;
    int numberOfRuns;
    ArrayList<Benchmark_Record> BST_Records;
    ArrayList<Benchmark_Record> RB_Records;

    public BenchmarkRunner(Benchmark benchmark, int numberOfRuns) {
        this.benchmark = benchmark;
        this.numberOfRuns = numberOfRuns;
        this.BST_Records = new ArrayList<>();
        this.RB_Records = new ArrayList<>();
    }

    public void runBenchmarks() {
        runForConfig(Generation_Mode.Fully_Random, 0);

        int[] ratios = { 1, 5, 10 };
        for (int ratio : ratios) {
            runForConfig(Generation_Mode.Nearly, ratio);
        }
    }

    private void runForConfig(Generation_Mode mode, int ratio) {
        System.out.println("\n=== " + mode +
                (ratio > 0 ? " " + ratio + "%" : "") + " ===");

        InputGenerator inputGenerator = new InputGenerator(benchmark.getInputSize());
        ArrayList<Integer> sharedInput;
        if (mode == Generation_Mode.Fully_Random) {
            sharedInput = inputGenerator.fullyRandom();
        } else {
            inputGenerator.normalSequence();
            sharedInput = inputGenerator.nearlySorted(ratio);
        }
        HashMap<Integer, Integer> sharedInputMap = inputGenerator.getExistingValues();

        ArrayList<Benchmark_Record> bstRecords = runTypeForConfig(Tree_Type.NORMAL, mode, sharedInput, sharedInputMap);
        ArrayList<Benchmark_Record> rbRecords = runTypeForConfig(Tree_Type.RB_TREE, mode, sharedInput, sharedInputMap);

        BST_Records.addAll(bstRecords);
        RB_Records.addAll(rbRecords);

        printStatistics("BST", bstRecords);
        printStatistics("RB_TREE", rbRecords);
        printSpeedupForConfig(bstRecords, rbRecords);
    }

    private ArrayList<Benchmark_Record> runTypeForConfig(Tree_Type type, Generation_Mode mode,
            ArrayList<Integer> sharedInput, HashMap<Integer, Integer> sharedInputMap) {
        ArrayList<Benchmark_Record> records = new ArrayList<>();

        // Warmup run (discard)
        benchmark.treeType = type;
        benchmark.createTree();
        benchmark.setRunSeed(0L);
        benchmark.setInputData(mode, sharedInput, sharedInputMap);
        benchmark.run();

        for (int i = 0; i < numberOfRuns; i++) {
            benchmark.treeType = type;
            benchmark.createTree();
            benchmark.setRunSeed(i + 1L);
            benchmark.setInputData(mode, sharedInput, sharedInputMap);
            records.add(benchmark.run());
        }

        return records;
    }

    private void printStatistics(String label, ArrayList<Benchmark_Record> records) {
        System.out.println("  [" + label + "]");

        Stats insertion = toMillisStats(records.stream().mapToLong(Benchmark_Record::insertionTime).toArray());
        Stats containsExisting = toMillisStats(records.stream().mapToLong(Benchmark_Record::containsExistingTime).toArray());
        Stats containsNonExisting = toMillisStats(records.stream().mapToLong(Benchmark_Record::containsNonExistingTime).toArray());
        Stats deletion = toMillisStats(records.stream().mapToLong(Benchmark_Record::deletionTime).toArray());
        Stats traversal = toMillisStats(records.stream().mapToLong(Benchmark_Record::traversalTime).toArray());
        Stats treeSort = toMillisStats(records.stream().mapToLong(Benchmark_Record::treeSortTime).toArray());
        Stats baselineSort = toMillisStats(records.stream().mapToLong(Benchmark_Record::baselineSortTime).toArray());
        Stats height = toRawStats(records.stream().mapToLong(Benchmark_Record::treeHeight).toArray());

        printStatLine("Insertion", insertion, "ms");
        printHeightLine(height);
        printStatLine("Contains (Existing)", containsExisting, "ms");
        printStatLine("Contains (Non-Existing)", containsNonExisting, "ms");
        printStatLine("Deletion", deletion, "ms");
        printStatLine("Traversal", traversal, "ms");
        printStatLine("Tree Sort (Build + InOrder)", treeSort, "ms");
        printStatLine("Baseline Sort (Arrays.sort)", baselineSort, "ms");
        System.out.printf("    Tree Sort vs Baseline (mean): %.2fx%n", treeSort.mean / Math.max(0.000001, baselineSort.mean));
    }

    private void printSpeedupForConfig(ArrayList<Benchmark_Record> bstRecords, ArrayList<Benchmark_Record> rbRecords) {
        Stats bstInsert = toRawStats(bstRecords.stream().mapToLong(Benchmark_Record::insertionTime).toArray());
        Stats rbInsert = toRawStats(rbRecords.stream().mapToLong(Benchmark_Record::insertionTime).toArray());
        Stats bstContainsExisting = toRawStats(bstRecords.stream().mapToLong(Benchmark_Record::containsExistingTime).toArray());
        Stats rbContainsExisting = toRawStats(rbRecords.stream().mapToLong(Benchmark_Record::containsExistingTime).toArray());
        Stats bstContainsNonExisting = toRawStats(bstRecords.stream().mapToLong(Benchmark_Record::containsNonExistingTime).toArray());
        Stats rbContainsNonExisting = toRawStats(rbRecords.stream().mapToLong(Benchmark_Record::containsNonExistingTime).toArray());
        Stats bstDeletion = toRawStats(bstRecords.stream().mapToLong(Benchmark_Record::deletionTime).toArray());
        Stats rbDeletion = toRawStats(rbRecords.stream().mapToLong(Benchmark_Record::deletionTime).toArray());
        Stats bstTraversal = toRawStats(bstRecords.stream().mapToLong(Benchmark_Record::traversalTime).toArray());
        Stats rbTraversal = toRawStats(rbRecords.stream().mapToLong(Benchmark_Record::traversalTime).toArray());
        Stats bstTreeSort = toRawStats(bstRecords.stream().mapToLong(Benchmark_Record::treeSortTime).toArray());
        Stats rbTreeSort = toRawStats(rbRecords.stream().mapToLong(Benchmark_Record::treeSortTime).toArray());

        System.out.println("  Speedup (BST mean / RB mean):");
        System.out.printf("    Insertion: %.2fx%n", safeSpeedup(bstInsert.mean, rbInsert.mean));
        System.out.printf("    Contains (Existing): %.2fx%n", safeSpeedup(bstContainsExisting.mean, rbContainsExisting.mean));
        System.out.printf("    Contains (Non-Existing): %.2fx%n", safeSpeedup(bstContainsNonExisting.mean, rbContainsNonExisting.mean));
        System.out.printf("    Deletion: %.2fx%n", safeSpeedup(bstDeletion.mean, rbDeletion.mean));
        System.out.printf("    Traversal: %.2fx%n", safeSpeedup(bstTraversal.mean, rbTraversal.mean));
        System.out.printf("    Tree Sort (Build + InOrder): %.2fx%n", safeSpeedup(bstTreeSort.mean, rbTreeSort.mean));
    }

    private void printStatLine(String name, Stats stats, String unit) {
        System.out.printf("    %s -> mean: %.2f %s | median: %.2f %s | stddev: %.2f %s%n",
                name, stats.mean, unit, stats.median, unit, stats.stddev, unit);
    }

    private void printHeightLine(Stats heightStats) {
        System.out.printf("    Height after insertion -> mean: %d | median: %d | stddev: %.2f%n",
                Math.round(heightStats.mean), Math.round(heightStats.median), heightStats.stddev);
    }

    private double safeSpeedup(double baseline, double contender) {
        if (contender == 0) {
            return 0;
        }
        return baseline / contender;
    }

    private Stats toMillisStats(long[] valuesNs) {
        if (valuesNs.length == 0) {
            return new Stats(0, 0, 0);
        }

        ArrayList<Double> numbers = new ArrayList<>(valuesNs.length);
        double sum = 0;
        for (long valueNs : valuesNs) {
            double valueMs = valueNs / 1_000_000.0;
            numbers.add(valueMs);
            sum += valueMs;
        }

        Collections.sort(numbers);
        double mean = sum / valuesNs.length;
        double median;
        int mid = valuesNs.length / 2;
        if (valuesNs.length % 2 == 0) {
            median = (numbers.get(mid - 1) + numbers.get(mid)) / 2.0;
        } else {
            median = numbers.get(mid);
        }

        double variance = 0;
        for (double number : numbers) {
            double diff = number - mean;
            variance += diff * diff;
        }
        variance /= valuesNs.length;
        double stddev = Math.sqrt(variance);

        return new Stats(mean, median, stddev);
    }

    private Stats toRawStats(long[] values) {
        if (values.length == 0) {
            return new Stats(0, 0, 0);
        }

        ArrayList<Double> numbers = new ArrayList<>(values.length);
        double sum = 0;
        for (long value : values) {
            numbers.add((double) value);
            sum += value;
        }

        Collections.sort(numbers);
        double mean = sum / values.length;
        double median;
        int mid = values.length / 2;
        if (values.length % 2 == 0) {
            median = (numbers.get(mid - 1) + numbers.get(mid)) / 2.0;
        } else {
            median = numbers.get(mid);
        }

        double variance = 0;
        for (double number : numbers) {
            double diff = number - mean;
            variance += diff * diff;
        }
        variance /= values.length;
        double stddev = Math.sqrt(variance);

        return new Stats(mean, median, stddev);
    }

    private static class Stats {
        final double mean;
        final double median;
        final double stddev;

        Stats(double mean, double median, double stddev) {
            this.mean = mean;
            this.median = median;
            this.stddev = stddev;
        }
    }
    
    public void printSpeedupSummary() {
        System.out.println("\n========== OVERALL SPEEDUP SUMMARY (RB-Tree vs BST) ==========");
        
        double bstInsertMean = BST_Records.stream()
            .mapToLong(Benchmark_Record::insertionTime).average().orElse(1);
        double rbtInsertMean = RB_Records.stream()
            .mapToLong(Benchmark_Record::insertionTime).average().orElse(1);
            
        double bstContainsExistMean = BST_Records.stream()
            .mapToLong(Benchmark_Record::containsExistingTime).average().orElse(1);
        double rbtContainsExistMean = RB_Records.stream()
            .mapToLong(Benchmark_Record::containsExistingTime).average().orElse(1);
            
        double bstContainsNonExistMean = BST_Records.stream()
            .mapToLong(Benchmark_Record::containsNonExistingTime).average().orElse(1);
        double rbtContainsNonExistMean = RB_Records.stream()
            .mapToLong(Benchmark_Record::containsNonExistingTime).average().orElse(1);
            
        double bstDeletionMean = BST_Records.stream()
            .mapToLong(Benchmark_Record::deletionTime).average().orElse(1);
        double rbtDeletionMean = RB_Records.stream()
            .mapToLong(Benchmark_Record::deletionTime).average().orElse(1);
            
        double bstTraversalMean = BST_Records.stream()
            .mapToLong(Benchmark_Record::traversalTime).average().orElse(1);
        double rbtTraversalMean = RB_Records.stream()
            .mapToLong(Benchmark_Record::traversalTime).average().orElse(1);

        double bstTreeSortMean = BST_Records.stream()
            .mapToLong(Benchmark_Record::treeSortTime).average().orElse(1);
        double rbtTreeSortMean = RB_Records.stream()
            .mapToLong(Benchmark_Record::treeSortTime).average().orElse(1);
        
        System.out.printf("  Insertion: %.2fx speedup%n", bstInsertMean / rbtInsertMean);
        System.out.printf("  Contains (Existing): %.2fx speedup%n", bstContainsExistMean / rbtContainsExistMean);
        System.out.printf("  Contains (Non-Existing): %.2fx speedup%n", bstContainsNonExistMean / rbtContainsNonExistMean);
        System.out.printf("  Deletion: %.2fx speedup%n", bstDeletionMean / rbtDeletionMean);
        System.out.printf("  Traversal: %.2fx speedup%n", bstTraversalMean / rbtTraversalMean);
        System.out.printf("  Tree Sort (Build + InOrder): %.2fx speedup%n", bstTreeSortMean / rbtTreeSortMean);
    }
    
}
