package Logic.Trees;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RBTreeTest {

    @Test
    void emptyTreeHasZeroHeight() {
        RB_Tree tree = new RB_Tree();

        assertEquals(0, tree.height());
        assertEquals(0, tree.size());
    }

    @Test
    void insertAndContainsWorkWithDuplicateRejection() {
        RB_Tree tree = new RB_Tree();

        assertTrue(tree.insert(10));
        assertTrue(tree.insert(20));
        assertTrue(tree.insert(5));
        assertTrue(tree.insert(15));

        assertFalse(tree.insert(10));

        assertEquals(4, tree.size());
        assertTrue(tree.contains(15));
        assertFalse(tree.contains(99));
        assertArrayEquals(new int[] { 5, 10, 15, 20 }, tree.inOrder());
    }

    @Test
    void deleteUpdatesMembershipAndOrdering() {
        RB_Tree tree = new RB_Tree();

        int[] values = { 10, 5, 15, 2, 7, 12, 20 };
        for (int value : values) {
            assertTrue(tree.insert(value));
        }

        assertTrue(tree.delete(2));
        assertFalse(tree.contains(2));

        assertTrue(tree.delete(10));
        assertFalse(tree.contains(10));

        assertFalse(tree.delete(999));
        assertEquals(5, tree.size());
        assertArrayEquals(new int[] { 5, 7, 12, 15, 20 }, tree.inOrder());
    }
}
