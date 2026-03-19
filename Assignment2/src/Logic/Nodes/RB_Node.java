package Logic.Nodes;

import Logic.Enums.RB_Color;

public class RB_Node extends BinaryNode {

    RB_Color color;

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

}
