package Logic.Trees;

import java.util.ArrayList;

import Logic.Nodes.BinaryNode;

public abstract class AbstractBinaryTree implements BinaryTree {

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

    public boolean insert(int v);

    public boolean delete(int v);

    public boolean contains(int v) {
        BinaryNode node = search(v);
        return (node == null) ? false : true;
    }

    public int[] orderedSet() {
        orderedSet.clear();
        stack.clear();
        inTraverse(root);
        return orderedSet.toArray(new Integer[0]);
    }

    public int height() {
        return DFS(root);
    }

    public int size() {
        return size;
    }

    // Helper functions
    public BinaryNode search(int v) {
        if (root == null) {
            return null;
        }

        BinaryNode current = root;

        while (current != null && current.value != v) {
            if (current.getValue() == v) {
                return current;
            } else if (current.getValue() >= v) {
                // go left
                current = current.getLeft();
            } else {
                // go right
                current = current.getRight();
            }
        }

        return null;
    }

    public BinaryNode successorChild(BinaryNode node) {
        BinaryNode current = node.getRight();
        while (current.getLeft() != null) {
            current = current.getLeft();
        }
        return current;
    }

    public BinaryNode successorTree(BinaryNode node) {
        if (node.getRight() != null) {
            return successorChild(node);
        } else {
            BinaryNode parent = node.getParent();
            while (parent != null && parent.getLeft() != node) {
                node = parent;
                parent = node.getParent();
            }
            return parent;
        }
    }

    public BinaryNode predecessorChild(BinaryNode node) {
        BinaryNode current = node.getLeft();
        while (current.getRight() != null) {
            current = current.getRight();
        }
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
            return parent;
        }
    }

    public int DFS(BinaryNode node) {
        int leftHeight = 0;
        int rightHeight = 0;
        if (node.getLeft() != null) {
            leftHeight = DFS(node.getLeft());
        }
        if (node.getRight() != null) {
            rightHeight = DFS(node.getRight());
        }
        return Math.max(leftHeight, rightHeight) + 1;
    }

    public BinaryTree insertNode(int v) {

        if (root == null) {
            root = new BinaryNode(v);
            size++;
            return this;
        }

        BinaryNode current = root;
        BinaryNode parent = null;
        while (current != null) {
            parent = current;
            if (current.getValue == v) {
                // Duplicate
                return null;
            }
            if (current.getValue > v) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        BinaryNode newNode = new BinaryNode(v, parent);

        if (parent.getValue > v) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }

        size++;
        return newNode;

    }

    public void inTraverse(BinaryNode node) {
        if (node == null) {
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

        if (node == null) {
            return;
        }

        stack.push(node);

        while (!stack.isEmpty()) {
            node = stack.pop();
            orderedSet.add(node.getValue());
            if (node.getRight() != null) {
                stack.push(node.getRight());
            }
            if (node.getLeft() != null) {
                stack.push(node.getLeft());
            }
        }

    }

    public void postTraverse(BinaryNode node) {

        if (node == null) {
            return;
        }

        goDeepRight(node);
        while (!stack.isEmpty()) {
            node = stack.pop();
            orderedSet.add(node.getValue());
            goDeepRight(node.getLeft());
            // Go Deep right
            BinaryNode iterator = node.getLeft();
            while (iterator != null) {
                stack.push(iterator);
                iterator = iterator.getRight();
            }
        }

    }

    public void goDeepLeft(BinaryNode node) {
        while (node != null) {
            stack.push(node);
            node = node.getLeft();
        }
    }

}
