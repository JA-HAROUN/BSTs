package Logic.Trees;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BSTTest {

    @Test
    void insertContainsSizeAndInOrderWorkTogether() {
        BST tree = new BST();

        assertTrue(tree.insert(10));
        assertTrue(tree.insert(5));
        assertTrue(tree.insert(12));
        assertTrue(tree.insert(7));

        assertEquals(4, tree.size());
        assertTrue(tree.contains(7));
        assertFalse(tree.contains(99));
        assertArrayEquals(new int[] { 5, 7, 10, 12 }, tree.inOrder());
    }

    @Test
    void insertRejectsDuplicates() {
        BST tree = new BST();

        assertTrue(tree.insert(10));
        assertFalse(tree.insert(10));
        assertEquals(1, tree.size());
    }

    @Test
    void deleteHandlesLeafAndTwoChildCases() {
        BST tree = new BST();

        tree.insert(10);
        tree.insert(5);
        tree.insert(15);
        tree.insert(12);
        tree.insert(18);

        assertTrue(tree.delete(12));
        assertFalse(tree.contains(12));
        assertEquals(4, tree.size());

        assertTrue(tree.delete(15));
        assertFalse(tree.contains(15));
        assertEquals(3, tree.size());
        assertArrayEquals(new int[] { 5, 10, 18 }, tree.inOrder());
    }
}
