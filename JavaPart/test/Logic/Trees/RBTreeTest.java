package Logic.Trees;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;

import org.junit.jupiter.api.Test;

import Logic.Enums.RB_Color;
import Logic.Nodes.BinaryNode;
import Logic.Nodes.RB_Node;

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

    @Test
    void structuralInvariantsHoldAfterBulkInsertAndDelete() {
        RB_Tree tree = new RB_Tree();
        Random random = new Random(7L);
        HashSet<Integer> inserted = new HashSet<>();

        for (int i = 0; i < 2000; i++) {
            int value = random.nextInt(20000);
            if (tree.insert(value)) {
                inserted.add(value);
            }
            assertRbInvariants(tree);
        }

        int deleted = 0;
        for (int value : inserted) {
            if (deleted >= 500) {
                break;
            }
            assertTrue(tree.delete(value));
            assertRbInvariants(tree);
            deleted++;
        }
    }

    private void assertRbInvariants(RB_Tree tree) {
        if (tree.size() == 0) {
            assertEquals(0, tree.height());
            return;
        }

        RB_Node root = (RB_Node) tree.root;
        assertNotNull(root);
        assertEquals(RB_Color.BLACK, root.getColor());

        assertRedNodesHaveBlackChildren(tree, root);
        int targetBlackHeight = blackHeightAlongLeftPath(tree, root);
        assertEqualBlackHeight(tree, root, 0, targetBlackHeight);
    }

    private void assertRedNodesHaveBlackChildren(RB_Tree tree, RB_Node node) {
        if (tree.isNIL(node)) {
            return;
        }

        RB_Node left = (RB_Node) node.getLeft();
        RB_Node right = (RB_Node) node.getRight();

        if (node.getColor() == RB_Color.RED) {
            if (!tree.isNIL(left)) {
                assertEquals(RB_Color.BLACK, left.getColor());
            }
            if (!tree.isNIL(right)) {
                assertEquals(RB_Color.BLACK, right.getColor());
            }
        }

        assertRedNodesHaveBlackChildren(tree, left);
        assertRedNodesHaveBlackChildren(tree, right);
    }

    private int blackHeightAlongLeftPath(RB_Tree tree, RB_Node node) {
        int black = 0;
        RB_Node current = node;

        while (!tree.isNIL(current)) {
            if (current.getColor() == RB_Color.BLACK) {
                black++;
            }
            current = (RB_Node) current.getLeft();
        }

        return black;
    }

    private void assertEqualBlackHeight(RB_Tree tree, RB_Node node, int currentBlackHeight, int targetBlackHeight) {
        if (tree.isNIL(node)) {
            assertEquals(targetBlackHeight, currentBlackHeight);
            return;
        }

        int nextBlackHeight = currentBlackHeight;
        if (node.getColor() == RB_Color.BLACK) {
            nextBlackHeight++;
        }

        assertEqualBlackHeight(tree, (RB_Node) node.getLeft(), nextBlackHeight, targetBlackHeight);
        assertEqualBlackHeight(tree, (RB_Node) node.getRight(), nextBlackHeight, targetBlackHeight);
    }
}
