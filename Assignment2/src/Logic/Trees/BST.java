package Logic.Trees;

import java.util.ArrayList;

import Logic.Nodes.BinaryNode;

public class BST extends AbstractBinaryTree {

    public BST() {
        super();
    }

    public boolean insert(int v) {
        BinaryNode node = insertNode(v);
        if (node == null) {
            // Duplicate
            return false;
        } else {
            return true;
        }
    }

    public boolean delete(int v) {
        BinaryNode node = search(v);
        if (node == null) {
            // Not found
            return false;
        }

        boolean isLeftChild = node.getParent().getLeft() == node;
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

        return true;
    }

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
        }
        else {
            root = successor;
        }

    }

}
