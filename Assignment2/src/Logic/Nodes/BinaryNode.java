package Logic.Nodes;

public abstract class BinaryNode {

    private BinaryNode parent;
    private BinaryNode left;
    private BinaryNode right;
    private int value;

    public BinaryNode(int value) {
        this.value = value;
        this.parent = null;
        this.left = null;
        this.right = null;
    }

    public BinaryNode(int value, BinaryNode parent) {
        this.value = value;
        this.parent = parent;
        this.left = null;
        this.right = null;
    }

    public BinaryNode(int value, BinaryNode parent, BinaryNode left, BinaryNode right) {
        this.value = value;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public BinaryNode getParent() {
        return parent;
    }

    public void setParent(BinaryNode parent) {
        this.parent = parent;
    }

    public BinaryNode getLeft() {
        return left;
    }

    public void setLeft(BinaryNode left) {
        this.left = left;
    }

    public BinaryNode getRight() {
        return right;
    }

    public void setRight(BinaryNode right) {
        this.right = right;
    }

}
