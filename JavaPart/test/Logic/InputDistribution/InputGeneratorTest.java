package Logic.InputDistribution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import Logic.Enums.Generation_Mode;

class InputGeneratorTest {

    @Test
    void fullyRandomProducesRequestedCountAndTracksFrequencies() {
        InputGenerator generator = new InputGenerator(200);

        ArrayList<Integer> output = generator.fullyRandom();

        assertEquals(200, output.size());
        assertEquals(Generation_Mode.Fully_Random, generator.getGenerationMode());

        HashMap<Integer, Integer> frequencies = generator.getExistingValues();
        assertNotNull(frequencies);
        int totalFrequency = frequencies.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(200, totalFrequency);
    }

    @Test
    void fullyRandomIsReproducibleWithFixedSeed() {
        InputGenerator first = new InputGenerator(200);
        InputGenerator second = new InputGenerator(200);

        ArrayList<Integer> firstRun = first.fullyRandom();
        ArrayList<Integer> secondRun = second.fullyRandom();

        assertIterableEquals(firstRun, secondRun);
    }

    @Test
    void nearlySortedPreservesElementsAndSwitchesToNearlyMode() {
        InputGenerator generator = new InputGenerator(120);

        generator.normalSequence();
        ArrayList<Integer> original = new ArrayList<>(generator.output);
        ArrayList<Integer> nearly = generator.nearlySorted(20);

        assertEquals(Generation_Mode.Nearly, generator.getGenerationMode());
        assertEquals(120, nearly.size());
        assertIterableEquals(original, generator.output);

        ArrayList<Integer> sortedActual = new ArrayList<>(nearly);
        Collections.sort(sortedActual);

        ArrayList<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            expected.add(i);
        }

        assertIterableEquals(expected, sortedActual);

        long misplacedCount = 0;
        for (int i = 0; i < nearly.size(); i++) {
            if (!nearly.get(i).equals(i)) {
                misplacedCount++;
            }
        }
        assertTrue(misplacedCount > 0);
    }
}
