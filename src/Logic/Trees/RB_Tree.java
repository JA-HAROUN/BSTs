package Logic.Trees;

import Logic.Enums.RB_Color;
import Logic.Nodes.BinaryNode;
import Logic.Nodes.RB_Node;

public class RB_Tree extends AbstractBinaryTree {

    public RB_Tree() {
        super();
    }

    // RB Tree Helpers
    public boolean isNIL(RB_Node node) {
        return node == null || node.getValue() == null;
    }

    public RB_Node createNode(int v) {
        RB_Node node = new RB_Node(v, RB_Color.RED);
        RB_Node left = new RB_Node();
        RB_Node right = new RB_Node();
        node.setLeft(left);
        node.setRight(right);
        return node;
    }

    public void leftRotate(RB_Node node) {
        RB_Node rightChild = (RB_Node) node.getRight();
        node.setRight(rightChild.getLeft());
        rightChild.setLeft(node);
        rightChild.setParent(node.getParent());
        node.setParent(rightChild);
    }

    public void rightRotate(RB_Node node) {
        RB_Node leftChild = (RB_Node) node.getLeft();
        node.setLeft(leftChild.getRight());
        leftChild.setRight(node);
        leftChild.setParent(node.getParent());
        node.setParent(leftChild);
    }

    // Insert
    @Override
    protected BinaryNode insertNode(int v) {
        if (root == null) {
            root = createNode(v);
            size++;
            return root;
        }

        RB_Node current = (RB_Node) root;
        RB_Node parent = null;
        while (current != null) {
            parent = current;
            if (current.getValue() == v) {
                // Duplicate
                return null;
            }
            if (current.getValue() > v) {
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
        return newNode;
    }

    @Override
    protected void rebalance(BinaryNode node) {
        RB_Node rbNode = (RB_Node) node;
        // Case 1: if root is red
        if (node == root) {
            rbNode.setColor(RB_Color.BLACK);
            return;
        }

        RB_Node parent = (RB_Node) node.getParent();
        RB_Node grandParent = (RB_Node) parent.getParent();
        RB_Node uncle = null;
        boolean isLeft = parent.getParent().getLeft() == parent;
        boolean parentIsLeft = parent.getParent().getLeft() == parent;
        // get uncle
        if (isLeft) {
            uncle = (RB_Node) grandParent.getRight();
        } else {
            uncle = (RB_Node) grandParent.getLeft();
        }

        // Case 2: Uncle is Red
        if (uncle.getColor() == RB_Color.RED) {
            parent.setColor(RB_Color.BLACK);
            uncle.setColor(RB_Color.BLACK);
            grandParent.setColor(RB_Color.RED);
            rebalance(grandParent);
        }
        // Case 3 & 4: Uncle is Black
        else {
            // Case 3: Triangle
            if (isLeft && parent.getRight() == node) {
                leftRotate(parent);
                node = parent;
                parent = (RB_Node) node.getParent();
            } else if (!isLeft && parent.getLeft() == node) {
                rightRotate(parent);
                node = parent;
                parent = (RB_Node) node.getParent();
            }

            // Case 4: Line
            else if (isLeft && parentIsLeft) {
                rightRotate(grandParent);
                parent.setColor(RB_Color.BLACK);
                grandParent.setColor(RB_Color.RED);
            } else if (!isLeft && !parentIsLeft) {
                leftRotate(grandParent);
                parent.setColor(RB_Color.BLACK);
                grandParent.setColor(RB_Color.RED);
            }
        }
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
    }

    public void deleteFixUp(RB_Node node) {
        RB_Node sibling = null;
        RB_Node current = node;

        while (current != root && current.getColor() == RB_Color.BLACK) {
            RB_Node parent = (RB_Node) current.getParent();
            boolean isLeft = parent.getLeft() == current;
            sibling = isLeft ? (RB_Node) parent.getRight() : (RB_Node) parent.getLeft();

            // Case 1: sibling is red
            if (sibling.getColor() == RB_Color.RED) {
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
                sibling.setColor(RB_Color.RED);
                current = parent;
            }

            else {
                // Case 3: sibling's near child is red, far child is black
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
    }

}
