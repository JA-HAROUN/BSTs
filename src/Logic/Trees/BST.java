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
            logger.debug("Inserted node with value {}", v);
            return root;
        }

        BinaryNode current = root;
        BinaryNode parent = null;
        while (current != null) {
            parent = current;
            if (current.getValue() == v) {
                // Duplicate
                logger.debug("Node with value {} already exists", v);
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
        logger.debug("Inserted node with value {}", v);
        if (Validator.VALIDATE) Validator.check(this);
        return newNode;

    }

    @Override
    protected void rebalance(BinaryNode node) {
        return;
    }

    // Delete
    @Override
    protected void deleteNode(BinaryNode node) {
        if (node.getLeft() == null) {
            transplant(node, node.getRight());
            logger.debug("Deleted node with value {}", node.getValue());
            return;
        }

        if (node.getRight() == null) {
            transplant(node, node.getLeft());
            logger.debug("Deleted node with value {}", node.getValue());
            return;
        }

        BinaryNode successor = successorChild(node);

        if (successor.getParent() != node) {
            transplant(successor, successor.getRight());
            successor.setRight(node.getRight());
            successor.getRight().setParent(successor);
        }

        transplant(node, successor);
        successor.setLeft(node.getLeft());
        successor.getLeft().setParent(successor);
        logger.debug("Deleted node with value {}", node.getValue());
    }

    private void transplant(BinaryNode toReplace, BinaryNode replacement) {
        BinaryNode parent = toReplace.getParent();

        if (parent == null) {
            root = replacement;
        } else if (parent.getLeft() == toReplace) {
            parent.setLeft(replacement);
        } else {
            parent.setRight(replacement);
        }

        if (replacement != null) {
            replacement.setParent(parent);
        }

        Integer replacementValue = (replacement == null) ? null : replacement.getValue();
        logger.debug("Transplanted node with value {} with node with value {}", toReplace.getValue(), replacementValue);
        if (Validator.VALIDATE) Validator.check(this);
    }

}
