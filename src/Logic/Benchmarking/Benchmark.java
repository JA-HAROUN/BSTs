package Logic.Benchmarking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Arrays;

import Logic.Enums.Generation_Mode;
import Logic.Enums.Tree_Type;
import Logic.InputDistribution.InputGenerator;
import Logic.Records.Benchmark_Record;
import Logic.Trees.BST;
import Logic.Trees.BinaryTree;
import Logic.Trees.RB_Tree;

public class Benchmark {

    Random random;
    InputGenerator generator;
    int inputSize = 100000;

    ArrayList<Integer> input;
    HashMap<Integer, Integer> inputMap;
    Generation_Mode generationMode;

    Tree_Type treeType;
    BinaryTree tree;

    // Benchmark Data
    long insertionTime;
    long treeSortTime;
    long baselineSortTime;
    long containsExistingTime;
    long containsNonExistingTime;
    long deletionTime;
    long traversalTime;
    int treeHeight;

    // Constructor
    public Benchmark() {
        this.random = new Random();
        this.generator = new InputGenerator(inputSize);
    }

    public void createTree() {
        if (treeType == Tree_Type.NORMAL) {
            tree = new BST();
        } else if (treeType == Tree_Type.RB_TREE) {
            tree = new RB_Tree();
        }
    }

    public void generateInput(Generation_Mode mode, int ratio) {
        this.generationMode = mode;
        switch (mode) {
            case Fully_Random:
                input = generator.fullyRandom();
                inputMap = generator.getExistingValues();
                break;
            case Nearly:
                generator.normalSequence();
                input = generator.nearlySorted(ratio);
                inputMap = generator.getExistingValues();
                break;
        }
    }

    public int getInputSize() {
        return inputSize;
    }

    public void setInputData(Generation_Mode mode, ArrayList<Integer> input, HashMap<Integer, Integer> inputMap) {
        this.generationMode = mode;
        this.input = new ArrayList<>(input);
        this.inputMap = new HashMap<>(inputMap);
    }

    public void setRunSeed(long seed) {
        this.random.setSeed(seed);
    }

    // Main method to run the benchmark
    public void insertion() {

        long startTime = System.nanoTime();

        for (int value : input) {
            tree.insert(value);
        }

        long endTime = System.nanoTime();
        long insertionDuration = endTime - startTime;
        // Do something with the duration, e.g., print it or store it
        this.insertionTime = insertionDuration;
    }

    public void contains() {

        existingContains();
        nonExistingContains();

    }

    public void existingContains() {
        int existing = 50000;

        long startTime = System.nanoTime();
        while (existing > 0) {
            int index = random.nextInt(inputSize);
            tree.contains(input.get(index));
            existing--;
        }
        long endTime = System.nanoTime();
        long existingDuration = endTime - startTime;

        // Do something with the duration, e.g., print it or store it
        this.containsExistingTime = existingDuration;
    }

    public void nonExistingContains() {
        int nonExisting = 50000;

        long startTime = System.nanoTime();
        while (nonExisting > 0) {
            int value = 0;
            do {
                value = random.nextInt(10 * inputSize) + 1;
                if (!inputMap.containsKey(value)) {
                    break;
                }
            } while (true);

            tree.contains(value);
            nonExisting--;

        }
        long endTime = System.nanoTime();
        long nonExistingDuration = endTime - startTime;

        // Do something with the duration, e.g., print it or store it
        this.containsNonExistingTime = nonExistingDuration;
    }

    public void delete() {
        int numOfDeletions = (int) (0.20 * inputSize);
        // Create list of indices and shuffle once
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < inputSize; i++)
            indices.add(i);
        Collections.shuffle(indices, random);

        long startTime = System.nanoTime();
        for (int i = 0; i < numOfDeletions; i++) {
            tree.delete(input.get(indices.get(i)));
        }
        long endTime = System.nanoTime();
        this.deletionTime = endTime - startTime;
    }

    public void traverse() {
        long start = System.nanoTime();
        tree.inOrder();
        long end = System.nanoTime();
        long traversalDuration = end - start;
        this.traversalTime = traversalDuration;
    }

    public void benchmarkBaselineSort() {
        int[] values = input.stream().mapToInt(Integer::intValue).toArray();
        long start = System.nanoTime();
        Arrays.sort(values);
        long end = System.nanoTime();
        this.baselineSortTime = end - start;
    }

    public Benchmark_Record run() {
        this.insertion();
        this.treeHeight = tree.height();
        this.traverse();
        this.treeSortTime = this.insertionTime + this.traversalTime;
        this.benchmarkBaselineSort();
        this.contains();
        this.delete();
        return new Benchmark_Record(treeType, generationMode, insertionTime, treeSortTime, baselineSortTime,
                containsExistingTime, containsNonExistingTime, deletionTime, traversalTime, treeHeight);
    }

}
