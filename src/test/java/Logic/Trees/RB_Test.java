package test.java.Logic.Trees;

import Logic.Trees.RB_Tree;

public class RB_Test {

    public void testInsertion() {
        RB_Tree tree = new RB_Tree();
        if (!tree.insert(10)) {
            throw new AssertionError("Insert failed");
        }
        if (!tree.contains(10)) {
            throw new AssertionError("Contains failed");
        }
    }

}
