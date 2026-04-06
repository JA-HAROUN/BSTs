package Logic.Trees;

import Logic.Enums.RB_Color;
import Logic.Nodes.BinaryNode;
import Logic.Nodes.RB_Node;

public class Validator {

    static final boolean VALIDATE = false;

    /**
     * Check all invariants for a given tree.
     * Only performs checks if VALIDATE is true.
     *
     * @param tree the tree to validate
     * @throws AssertionError if any invariant is violated
     */
    public static void check(AbstractBinaryTree tree) {
        if (!VALIDATE) {
            return;
        }

        if (tree instanceof RB_Tree) {
            checkRBTree((RB_Tree) tree);
        } else if (tree instanceof BST) {
            checkBST((BST) tree);
        }
    }

    // Check BST invariants
    private static void checkBST(BST tree) {
        checkBSTInvariants(tree.root, Integer.MIN_VALUE, Integer.MAX_VALUE);
        checkSizeConsistency(tree.root, tree.size);
        checkParentChildConsistency(tree.root);
    }

    // Check BST property
    private static void checkBSTInvariants(BinaryNode node, int min, int max) {
        if (node == null) {
            return;
        }

        if (node.getValue() != null) {
            int value = node.getValue();
            assert value > min : "BST invariant violation: node " + value + " <= min " + min;
            assert value < max : "BST invariant violation: node " + value + " >= max " + max;

            checkBSTInvariants(node.getLeft(), min, value);
            checkBSTInvariants(node.getRight(), value, max);
        }
    }

    /**
     * Check RB_Tree invariants:
     * 1. All BST invariants hold
     * 2. Root is black
     * 3. All NIL nodes are black (implicitly satisfied by RB_Node design)
     * 4. Red nodes have only black children
     * 5. All paths have same black-node count
     */
    private static void checkRBTree(RB_Tree tree) {
        if (tree.root == null || tree.isNIL(tree.root)) {
            return;
        }

        RB_Node root = (RB_Node) tree.root;

        // Check root is black
        assert root.getColor() == RB_Color.BLACK : "RB invariant violation: root is not black";

        // Check BST property
        checkBSTInvariants(tree.root, Integer.MIN_VALUE, Integer.MAX_VALUE);

        // Check red-black properties
        checkRedBlackProperties(tree, root);

        // Check equal black height on all root-to-NIL paths.
        int targetBlackHeight = calculateTargetBlackHeight(tree, root);
        checkBlackHeight(tree, root, targetBlackHeight, 0);

        // Check parent-child consistency
        checkParentChildConsistency(tree.root);

        // Check size consistency
        checkSizeConsistency(tree.root, tree.size);
    }

    private static int calculateTargetBlackHeight(RB_Tree tree, RB_Node node) {
        int blackHeight = 0;
        RB_Node current = node;

        while (!tree.isNIL(current)) {
            if (current.getColor() == RB_Color.BLACK) {
                blackHeight++;
            }
            current = (RB_Node) current.getLeft();
        }

        return blackHeight;
    }

    // Check that red nodes have only black children
    private static void checkRedBlackProperties(RB_Tree tree, RB_Node node) {
        if (tree.isNIL(node)) {
            return;
        }

        RB_Node leftChild = (RB_Node) node.getLeft();
        RB_Node rightChild = (RB_Node) node.getRight();

        // Red nodes must have black children
        if (node.getColor() == RB_Color.RED) {
            if (!tree.isNIL(leftChild)) {
                assert leftChild.getColor() == RB_Color.BLACK : "RB invariant: red node has red left child " + node.getValue();
            }
            if (!tree.isNIL(rightChild)) {
                assert rightChild.getColor() == RB_Color.BLACK : "RB invariant: red node has red right child " + node.getValue();
            }
        }

        checkRedBlackProperties(tree, leftChild);
        checkRedBlackProperties(tree, rightChild);
    }

    // Check that all paths from a node to its descendant NIL nodes have the same number of black nodes
    public static int checkBlackHeight(RB_Tree tree, RB_Node node, int targetHeight, int currentHeight) {
        if (tree.isNIL(node)) {
            assert currentHeight == targetHeight : "Black-height invariant violation: path has " + currentHeight + " black nodes, expected " + targetHeight;
            return currentHeight;
        }

        int nextHeight = currentHeight + (node.getColor() == RB_Color.BLACK ? 1 : 0);

        checkBlackHeight(tree, (RB_Node) node.getLeft(), targetHeight, nextHeight);
        checkBlackHeight(tree, (RB_Node) node.getRight(), targetHeight, nextHeight);

        return nextHeight;
    }

    // Check that all paths from root to NIL have same black height
    private static void checkSizeConsistency(BinaryNode node, int expectedSize) {
        int actualSize = countNodes(node);
        assert actualSize == expectedSize : "Size invariant violation: actual size " + actualSize + " != expected " + expectedSize;
    }

    // Count non-NIL nodes in the subtree
    private static int countNodes(BinaryNode node) {
        if (node == null) {
            return 0;
        }
        if (node.getValue() == null) {
            return 0; // NIL node
        }
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    // Check that parent pointers are consistent with child pointers
    private static void checkParentChildConsistency(BinaryNode node) {
        if (node == null) {
            return;
        }

        BinaryNode left = node.getLeft();
        BinaryNode right = node.getRight();

        if (left != null && left.getValue() != null && left.getParent() != node) {
            throw new AssertionError("Parent-child consistency violation: left child's parent is not its actual parent");
        }

        if (right != null && right.getValue() != null && right.getParent() != node) {
            throw new AssertionError("Parent-child consistency violation: right child's parent is not its actual parent");
        }

        checkParentChildConsistency(left);
        checkParentChildConsistency(right);
    }
}
