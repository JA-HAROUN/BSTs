package Logic.sorts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuickSort {

    private final Random random;

    public QuickSort() {
        this.random = new Random();
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    public void sort(ArrayList<Integer> numberArray) {
        if (numberArray == null || numberArray.size() < 2) {
            return;
        }
        quickSort(numberArray, 0, numberArray.size() - 1);
    }

    private void quickSort(ArrayList<Integer> numberArray, int start, int end) {
        if (start >= end) {
            return;
        }

        int pivotIndex = random.nextInt(end - start + 1) + start;
        int partitionIndex = partition(numberArray, pivotIndex, start, end);

        quickSort(numberArray, start, partitionIndex - 1);
        quickSort(numberArray, partitionIndex + 1, end);
    }

    private int partition(ArrayList<Integer> numberArray, int index, int start, int end) {
        int pivot = numberArray.get(index);
        Collections.swap(numberArray, index, start);

        int i = start + 1;
        for (int j = start + 1; j <= end; j++) {
            if (numberArray.get(j) < pivot) {
                Collections.swap(numberArray, j, i);
                i++;
            }
        }

        Collections.swap(numberArray, start, i - 1);
        return i - 1;
    }
}