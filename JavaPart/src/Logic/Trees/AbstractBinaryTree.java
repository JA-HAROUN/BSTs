package Logic.Trees;

import java.util.ArrayList;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Logic.Nodes.BinaryNode;

public abstract class AbstractBinaryTree implements BinaryTree {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractBinaryTree.class.getName());

    BinaryNode root;
    int size;
    ArrayList<Integer> orderedSet;
    Stack<BinaryNode> stack = new Stack<>();

    public AbstractBinaryTree() {
        root = null;
        size = 0;
        orderedSet = new ArrayList<>();
        stack = new Stack<>();
    }

    // Checks if a node is effectively empty (null or a NIL sentinel)
    protected boolean isNIL(BinaryNode node) {
        return node == null || node.getValue() == null;
    }

    // Basic Tree Operations
    public boolean insert(int v) {
        BinaryNode node = insertNode(v);
        if (node == null) {
            // Duplicate
            return false;
        }

        // Rebalance
        rebalance(node);
        
        // Validate structural invariants after modifications
        if (Validator.VALIDATE) Validator.check(this);

        return true;
    }

    public boolean delete(int v) {
        BinaryNode node = search(v);
        if (node == null) {
            return false;
        }

        deleteNode(node);
        size--;
        
        // Validate structural invariants after modifications
        if (Validator.VALIDATE) Validator.check(this);
        
        return true;
    }

    public boolean contains(int v) {
        BinaryNode node = search(v);
        return (node == null) ? false : true;
    }

    public int height() {
        if (isNIL(root)) {
            return 0;
        }
        return DFS(root);
    }

    public int size() {
        return size;
    }

    public int[] inOrder() {
        orderedSet.clear();
        stack.clear();
        inTraverse(root);
        logger.debug("In-order traversal: {}", orderedSet);
        return orderedSet.stream().mapToInt(i -> i).toArray();
    }

    // Search & Successor / Predecessor Helpers
    public BinaryNode search(int v) {
        if (root == null) {
            return null;
        }

        BinaryNode current = root;

        while (current != null && current.getValue() != null) {
            int currentValue = current.getValue();
            if (currentValue == v) {
                logger.debug("Found node with value {}", v);
                return current;
            } else if (currentValue > v) {
                logger.debug("Going left from node with value {}", currentValue);
                current = current.getLeft();
            } else {
                logger.debug("Going right from node with value {}", currentValue);
                current = current.getRight();
            }
        }

        logger.debug("Node with value {} not found", v);
        return null;
    }

    public BinaryNode successorChild(BinaryNode node) {
        BinaryNode current = node.getRight();
        while (!isNIL(current.getLeft())) {
            current = current.getLeft();
        }
        logger.debug("Successor child of node with value {} is node with value {}", node.getValue(), current.getValue());
        return current;
    }

    public BinaryNode successorTree(BinaryNode node) {
        if (node.getRight() != null) {
            return successorChild(node);
        } 
        else {
            BinaryNode parent = node.getParent();
            while (parent != null && parent.getLeft() != node) {
                node = parent;
                parent = node.getParent();
            }
            Integer parentValue = (parent == null) ? null : parent.getValue();
            logger.debug("Successor tree of node with value {} is node with value {}", node.getValue(), parentValue);
            return parent;
        }
    }

    public BinaryNode predecessorChild(BinaryNode node) {
        BinaryNode current = node.getLeft();
        while (current.getRight() != null) {
            current = current.getRight();
        }
        logger.debug("Predecessor child of node with value {} is node with value {}", node.getValue(), current.getValue());
        return current;
    }

    public BinaryNode predecessorTree(BinaryNode node) {
        if (node.getLeft() != null) {
            return predecessorChild(node);
        } else {
            BinaryNode parent = node.getParent();
            while (parent != null && parent.getRight() != node) {
                node = parent;
                parent = node.getParent();
            }
            Integer parentValue = (parent == null) ? null : parent.getValue();
            logger.debug("Predecessor tree of node with value {} is node with value {}", node.getValue(), parentValue);
            return parent;
        }
    }

    // Tree Traversals
    public void inTraverse(BinaryNode node) {
        if (isNIL(node)) {
            return;
        }

        // Iterative traverse
        goDeepLeft(node);
        while (!stack.isEmpty()) {
            node = stack.pop();
            orderedSet.add(node.getValue());
            goDeepLeft(node.getRight());
        }
    }

    public void preTraverse(BinaryNode node) {
        if (isNIL(node)) {
            return;
        }

        stack.push(node);

        while (!stack.isEmpty()) {
            node = stack.pop();
            orderedSet.add(node.getValue());
            if (!isNIL(node.getRight())) {
                stack.push(node.getRight());
            }
            if (!isNIL(node.getLeft())) {
                stack.push(node.getLeft());
            }
        }
    }

    public void postTraverse(BinaryNode node) {
        if (isNIL(node)) {
            return;
        }

        goDeepRight(node);
        while (!stack.isEmpty()) {
            node = stack.pop();
            orderedSet.add(node.getValue());
            goDeepRight(node.getLeft());
            // Go Deep right
            BinaryNode iterator = node.getLeft();
            while (!isNIL(iterator)) {
                stack.push(iterator);
                iterator = iterator.getRight();
            }
        }
    }

    public void goDeepLeft(BinaryNode node) {
        while (!isNIL(node)) {
            stack.push(node);
            node = node.getLeft();
        }
    }

    public void goDeepRight(BinaryNode node) {
        while (!isNIL(node)) {
            stack.push(node);
            node = node.getRight();
        }
    }

    public int DFS(BinaryNode node) {
        if (isNIL(node)) {
            return 0;
        }

        int leftHeight = 0;
        int rightHeight = 0;
        if (!isNIL(node.getLeft())) {
            leftHeight = DFS(node.getLeft());
        }
        if (!isNIL(node.getRight())) {
            rightHeight = DFS(node.getRight());
        }
        return Math.max(leftHeight, rightHeight) + 1;
    }

    // Abstract Methods
    protected abstract BinaryNode insertNode(int v);

    protected abstract void rebalance(BinaryNode node);

    protected abstract void deleteNode(BinaryNode node);

}
