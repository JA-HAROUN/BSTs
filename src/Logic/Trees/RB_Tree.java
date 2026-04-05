package Logic.Trees;

import Logic.Enums.RB_Color;
import Logic.Nodes.BinaryNode;
import Logic.Nodes.RB_Node;

public class RB_Tree extends AbstractBinaryTree {

    private final RB_Node NIL;

    public RB_Tree() {
        super();
        NIL = new RB_Node();
        NIL.setLeft(NIL);
        NIL.setRight(NIL);
        NIL.setParent(null);
        root = NIL;
    }

    // RB Tree Helpers
    @Override
    protected boolean isNIL(BinaryNode node) {
        return node == null || node == NIL;
    }

    @Override
    public int height() {
        if (isNIL(root)) {
            return 0;
        }
        return super.height();
    }

    public RB_Node createNode(int v) {
        RB_Node node = new RB_Node(v, RB_Color.RED);
        node.setLeft(NIL);
        node.setRight(NIL);
        node.setParent(null);
        logger.debug("Created node with value {}", v);
        return node;
    }

    public void leftRotate(RB_Node node) {
        RB_Node rightChild = (RB_Node) node.getRight();
        if (isNIL(rightChild)) {
            logger.debug("Cannot left rotate on a NIL node");
            return;
        }

        // Move rightChild's left subtree to node's right
        node.setRight(rightChild.getLeft());
        if (!isNIL((RB_Node) rightChild.getLeft())) {
            rightChild.getLeft().setParent(node);
        }

        // Link rightChild into node's parent spot
        rightChild.setParent(node.getParent());
        if (node.getParent() == null) {
            root = rightChild;
        } else if (node == node.getParent().getLeft()) {
            node.getParent().setLeft(rightChild);
        } else {
            node.getParent().setRight(rightChild);
        }

        rightChild.setLeft(node);
        node.setParent(rightChild);
        logger.debug("Left rotated node with value {}", node.getValue());
    }

    public void rightRotate(RB_Node node) {
        RB_Node leftChild = (RB_Node) node.getLeft();
        if (isNIL(leftChild)) {
            logger.debug("Cannot right rotate on a NIL node");
            return;
        }

        // Move leftChild's right subtree to node's left
        node.setLeft(leftChild.getRight());
        if (!isNIL((RB_Node) leftChild.getRight())) {
            leftChild.getRight().setParent(node);
        }

        // Link leftChild into node's parent spot
        leftChild.setParent(node.getParent());
        if (node.getParent() == null) {
            root = leftChild;
        } else if (node == node.getParent().getRight()) {
            node.getParent().setRight(leftChild);
        } else {
            node.getParent().setLeft(leftChild);
        }

        leftChild.setRight(node);
        node.setParent(leftChild);
        logger.debug("Right rotated node with value {}", node.getValue());
    }

    // Insert
    @Override
    protected BinaryNode insertNode(int v) {
        if (isNIL(root)) {
            root = createNode(v);
            root.setParent(null);
            size++;
            logger.debug("Inserted node with value {}", v);
            return root;
        }

        RB_Node current = (RB_Node) root;
        RB_Node parent = null;
        while (!isNIL(current)) {
            parent = current;
            int currentValue = current.getValue();
            if (currentValue == v) {
                // Duplicate
                return null;
            }
            if (currentValue > v) {
                current = (RB_Node) current.getLeft();
            } else {
                current = (RB_Node) current.getRight();
            }
        }

        RB_Node newNode = createNode(v);
        newNode.setParent(parent);

        if (parent.getValue() > v) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }

        size++;
        logger.debug("Inserted node with value {}", v);
        return newNode;
    }

    @Override
    protected void rebalance(BinaryNode node) {
        RB_Node rbNode = (RB_Node) node;
        // Case 1: if root, paint black and done
        if (node == root) {
            rbNode.setColor(RB_Color.BLACK);
            logger.debug("Root is black");
            return;
        }

        RB_Node parent = (RB_Node) node.getParent();
        if (parent == null || parent.getColor() == RB_Color.BLACK) {
            logger.debug("Parent is black");
            return;
        }

        RB_Node grandParent = (RB_Node) parent.getParent();
        if (grandParent == null) {
            parent.setColor(RB_Color.BLACK);
            logger.debug("Parent is black");
            return;
        }

        boolean isLeft = grandParent.getLeft() == parent;
        RB_Node uncle = isLeft ? (RB_Node) grandParent.getRight()
                               : (RB_Node) grandParent.getLeft();

        // Case 2: Uncle is Red → recolor and recurse up
        if (!isNIL(uncle) && uncle.getColor() == RB_Color.RED) {
            parent.setColor(RB_Color.BLACK);
            uncle.setColor(RB_Color.BLACK);
            grandParent.setColor(RB_Color.RED);
            rebalance(grandParent);
            logger.debug("Uncle is red");
            return;
        }

        // Uncle is Black
        // Case 3: Triangle
        // convert to line
        // fall through to Case 4
        if (isLeft && parent.getRight() == node) {
            // node is right child of a left-parent → left-rotate parent to make a line
            leftRotate(parent);
            rbNode = parent;           // rbNode is now the lower node after rotation
            parent = (RB_Node) rbNode.getParent();
            logger.debug("Triangle case");
        } else if (!isLeft && parent.getLeft() == node) {
            // node is left child of a right-parent → right-rotate parent to make a line
            rightRotate(parent);
            rbNode = parent;
            parent = (RB_Node) rbNode.getParent();
            logger.debug("Triangle case");
        }

        // Case 4: Line
        // rotate grandparent
        // recolor
        grandParent = (RB_Node) parent.getParent();
        if (parent == grandParent.getLeft()) {
            rightRotate(grandParent);
        } else {
            leftRotate(grandParent);
        }
        parent.setColor(RB_Color.BLACK);
        grandParent.setColor(RB_Color.RED);
        logger.debug("Line case");
    }


    // Delete Helpers
    // u is the node to be replaced, v is the node to replace u
    public void transplant(RB_Node u, RB_Node v) {
        if (u.getParent() == null) {
            this.root = v;
        } else if (u == u.getParent().getLeft()) {
            u.getParent().setLeft(v);
        } else {
            u.getParent().setRight(v);
        }
        v.setParent(u.getParent());
        Integer replacementValue = isNIL(v) ? null : v.getValue();
        logger.debug("Transplanted node with value {} with node with value {}", u.getValue(), replacementValue);
    }

    @Override
    public void deleteNode(BinaryNode node) {
        RB_Node rbNode = (RB_Node) node;
        RB_Color originalColor = rbNode.getColor();
        RB_Node x; // the node that moves into the deleted position

        // Case 1: node has no left child
        if (isNIL((RB_Node) node.getLeft())) {
            x = (RB_Node) node.getRight();
            transplant(rbNode, x);
        }
        // Case 2: node has no right child
        else if (isNIL((RB_Node) node.getRight())) {
            x = (RB_Node) node.getLeft();
            transplant(rbNode, x);
        }
        // Case 3: node has two children
        else {
            RB_Node successor = (RB_Node) successorChild(node);
            RB_Color successorOriginalColor = successor.getColor();
            x = (RB_Node) successor.getRight();

            if (successor.getParent() == node) {
                x.setParent(successor);
            } else {
                transplant(successor, x);
                successor.setRight(node.getRight());
                successor.getRight().setParent(successor);
            }

            transplant(rbNode, successor);
            successor.setLeft(node.getLeft());
            successor.getLeft().setParent(successor);
            successor.setColor(rbNode.getColor());
            originalColor = successorOriginalColor;
        }

        if (originalColor == RB_Color.BLACK) {
            deleteFixUp(x);
        }

        logger.debug("Deleted node with value {}", node.getValue());
    }

    public void deleteFixUp(RB_Node node) {
        RB_Node sibling = null;
        RB_Node current = node;

        while (current != root && current.getColor() == RB_Color.BLACK) {
            RB_Node parent = (RB_Node) current.getParent();
            if (parent == null) {
                break;
            }
            boolean isLeft = parent.getLeft() == current;
            sibling = isLeft ? (RB_Node) parent.getRight() : (RB_Node) parent.getLeft();

            // Case 1: sibling is red
            if (sibling.getColor() == RB_Color.RED) {
                logger.debug("Case 1: sibling is red");
                sibling.setColor(RB_Color.BLACK);
                parent.setColor(RB_Color.RED);
                if (isLeft) {
                    leftRotate(parent);
                } else {
                    rightRotate(parent);
                }

                sibling = isLeft ? (RB_Node) parent.getRight() : (RB_Node) parent.getLeft();
            }

            // Sibling is black
            // Case 2: both of sibling's children are black
            if (((RB_Node) sibling.getLeft()).getColor() == RB_Color.BLACK
                    && ((RB_Node) sibling.getRight()).getColor() == RB_Color.BLACK) {
                logger.debug("Case 2: sibling's children are black");
                sibling.setColor(RB_Color.RED);
                current = parent;
            }

            else {
                // Case 3: sibling's near child is red, far child is black
                logger.debug("Case 3: sibling's near child is red, far child is black");
                if (isLeft && ((RB_Node) sibling.getLeft()).getColor() == RB_Color.RED
                        && ((RB_Node) sibling.getRight()).getColor() == RB_Color.BLACK) {
                    ((RB_Node) sibling.getLeft()).setColor(RB_Color.BLACK);
                    sibling.setColor(RB_Color.RED);
                    rightRotate(sibling);
                    sibling = (RB_Node) parent.getRight();
                } else if (!isLeft && ((RB_Node) sibling.getRight()).getColor() == RB_Color.RED
                        && ((RB_Node) sibling.getLeft()).getColor() == RB_Color.BLACK) {
                    ((RB_Node) sibling.getRight()).setColor(RB_Color.BLACK);
                    sibling.setColor(RB_Color.RED);
                    leftRotate(sibling);
                    sibling = (RB_Node) parent.getLeft();
                }

                // Case 4: sibling's far child is red
                logger.debug("Case 4: sibling's far child is red");
                sibling.setColor(parent.getColor());
                parent.setColor(RB_Color.BLACK);
                if (isLeft) {
                    ((RB_Node) sibling.getRight()).setColor(RB_Color.BLACK);
                    leftRotate(parent);
                } else {
                    ((RB_Node) sibling.getLeft()).setColor(RB_Color.BLACK);
                    rightRotate(parent);
                }
                current = (RB_Node) root;
            }
        }

        current.setColor(RB_Color.BLACK);
        if (isNIL(root)) {
            NIL.setParent(null);
        }
        Integer nodeValue = isNIL(node) ? null : node.getValue();
        logger.debug("Deleted node with value {}", nodeValue);
    }

}
