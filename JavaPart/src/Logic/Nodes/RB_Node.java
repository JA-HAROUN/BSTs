package Logic.Nodes;

import Logic.Enums.RB_Color;

public class RB_Node extends BinaryNode {

    RB_Color color;

    // Default constructor -> NIL node
    public RB_Node() {
        super();
        this.color = RB_Color.BLACK;
    }

    public RB_Node(int value, RB_Color color) {
        super(value);
        this.color = color;
    }

    public RB_Node(int value, RB_Color color, BinaryNode parent) {
        super(value, parent);
        this.color = color;
    }

    public RB_Node(int value, RB_Color color, BinaryNode parent, BinaryNode left, BinaryNode right) {
        super(value, parent, left, right);
        this.color = color;
    }

    public RB_Color getColor() {
        return this.color;
    }

    public void setColor(RB_Color color) {
        this.color = color;
    }

}

    