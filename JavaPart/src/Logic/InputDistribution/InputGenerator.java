package Logic.InputDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import Logic.Enums.Generation_Mode;

public class InputGenerator {

    private static final long FIXED_SEED = 42L;

    int n;
    Random random;
    ArrayList<Integer> output;
    HashMap<Integer, Integer> existingValues;
    Generation_Mode mode;

    public HashMap<Integer, Integer> getExistingValues() {
        return existingValues;
    }

    public Generation_Mode getGenerationMode() {
        return mode;
    }

    // Constructor
    public InputGenerator() {
        this.n = 100000;
        this.random = new Random(FIXED_SEED);
        this.output = new ArrayList<>();
        this.existingValues = new HashMap<>();
    }

    public InputGenerator(int n) {
        this.n = n;
        this.random = new Random(FIXED_SEED);
        this.output = new ArrayList<>();
        this.existingValues = new HashMap<>();
    }

    // Fully Random
    public ArrayList<Integer> fullyRandom() {
        output.clear();
        existingValues.clear();
        mode = Generation_Mode.Fully_Random;
        int max = (10 * n) + 1;
        ArrayList<Integer> output = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int value = random.nextInt(max);
            output.add(value);
            existingValues.put(value, existingValues.getOrDefault(value, 0) + 1);
        }

        return output;
    }

    // Nearly Sorted Functions
    public void normalSequence() {
        output.clear();
        existingValues.clear();
        mode = Generation_Mode.Nearly;
        for (int i = 0; i < n; i++) {
            int value = i;
            output.add(value);
            existingValues.put(value, existingValues.getOrDefault(value, 0) + 1);
        }
    }

    public ArrayList<Integer> nearlySorted(int ratio) {
        int numOfSwaps = ratio * n / 100;
        ArrayList<Integer> copy = new ArrayList<>(output);

        while (numOfSwaps > 0) {

            // Random indices
            int first = random.nextInt(n);
            int second = random.nextInt(n);

            Collections.swap(copy, first, second);

            numOfSwaps--;
        }

        return copy;
    }

}
