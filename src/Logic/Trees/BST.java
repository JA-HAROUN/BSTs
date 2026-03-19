package Logic.Trees;

import Logic.Nodes.BinaryNode;

public class BST extends AbstractBinaryTree {

    public BST() {
        super();
    }

    // Insert
    @Override
    protected BinaryNode insertNode(int v) {

        if (root == null) {
            root = new BinaryNode(v);
            size++;
            return root;
        }

        BinaryNode current = root;
        BinaryNode parent = null;
        while (current != null) {
            parent = current;
            if (current.getValue() == v) {
                // Duplicate
                return null;
            }
            if (current.getValue() > v) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        BinaryNode newNode = new BinaryNode(v, parent);

        if (parent.getValue() > v) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }

        size++;
        return newNode;

    }

    @Override
    protected void rebalance(BinaryNode node) {
        return;
    }

    // Delete
    @Override
    protected void deleteNode(BinaryNode node) {
        boolean hasLeftChild = node.getLeft() != null;
        boolean hasRightChild = node.getRight() != null;
        // Case 1: No Children
        if (!hasLeftChild && !hasRightChild) {
            deleteCaseOne(node);
        }
        // Case 2: One Child
        else if (hasLeftChild && !hasRightChild || !hasLeftChild && hasRightChild) {
            deleteCaseTwo(node);
        }
        // Case 3: Two Children
        else {
            deleteCaseThree(node);
        }
    }

    // Delete Helpers
    // Case 1: No Children
    public void deleteCaseOne(BinaryNode node) {
        BinaryNode parent = node.getParent();
        if (parent.getLeft() == node) {
            parent.setLeft(null);
        } else {
            parent.setRight(null);
        }
    }

    // Case 2: One Child
    public void deleteCaseTwo(BinaryNode node) {
        BinaryNode parent = node.getParent();
        if (node.getLeft() != null) {
            node.getLeft().setParent(parent);
            parent.setLeft(node.getLeft());
        } else {
            node.getRight().setParent(parent);
            parent.setRight(node.getRight());
        }
    }

    // Case 3: Two Children
    public void deleteCaseThree(BinaryNode node) {
        BinaryNode successor = successorChild(node);
        // Cut successor
        BinaryNode successorParent = successor.getParent();
        if (successorParent.getLeft() == successor) {
            successorParent.setLeft(successor.getRight());
        } else {
            successorParent.setRight(successor.getRight());
        }

        // Connect successor
        successor.setLeft(node.getLeft());
        successor.setRight(node.getRight());
        successor.setParent(node.getParent());

        // replace connection
        node.getRight().setParent(successor);
        node.getLeft().setParent(successor);
        if (node.getParent() != null) {
            if (node.getParent().getLeft() == node) {
                node.getParent().setLeft(successor);
            } else {
                node.getParent().setRight(successor);
            }
        } else {
            root = successor;
        }

    }

}
