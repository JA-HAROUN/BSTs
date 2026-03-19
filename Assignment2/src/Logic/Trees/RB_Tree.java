package Logic.Trees;

import java.util.ArrayList;

import Logic.Nodes.BinaryNode;
import Logic.Nodes.RB_Node;

public class RB_Tree extends AbstractBinaryTree {

    public RB_Tree() {
        super();
    }

    @Override
    public boolean insert(int v) {
        RB_Node node = insertNode(v);
        if (node == null) {
            // Duplicate
            return false;
        }

        // Rebalance
        node.setColor(RB_Color.RED);
        insertionFixup(node);

        return true;
    }

    @Override
    public boolean delete(int v) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    // Helper functions
    public RB_Node insertionFixup(RB_Node node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertionFixup'");
    }

 

}
