package ru.interosite.datastructures;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class Rope {

    public static class RopeNode {
        private int weight;
        private int length;
        private RopeNode left;
        private RopeNode right;
        private String data;

        public RopeNode(String data) {
            requireNonNull(data);
            weight = this.length = data.length();
            this.data = data;
            left = null;
            right = null;
        }

        private RopeNode(RopeNode left, RopeNode right) {
            requireNonNull(left);
            requireNonNull(right);
            weight = left.length;
            length = left.length + right.length;
            data = null;
            this.left = left;
            this.right = right;
        }

        public int getLength() {
            return length;
        }

        public String getData() {
            StringBuilder sb = new StringBuilder();
            buildData(this, sb);
            return sb.toString();
        }

        private static void buildData(RopeNode node, StringBuilder sb) {
            if (node.data != null) {
                sb.append(node.data);
            } else {
                buildData(node.left, sb);
                buildData(node.right, sb);
            }
        }
    }

    public static char index(RopeNode node, int i) {
        if ((i > node.getLength() - 1) || (i < 0)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Object[] leafData = getLeaf(node, i);
        RopeNode leaf = (RopeNode) leafData[0];
        int pos = (Integer) leafData[1];
        return leaf.data.charAt(pos);
    }

    /** Gets pair (node: RopeNode, pos_in_node: int). */
    private static Object[] getLeaf(RopeNode node, int i) {
        if (i > node.weight - 1) {
            return getLeaf(node.right, i - node.weight);
        } else if (node.left != null) {
            return getLeaf(node.left, i);
        } else {
            return new Object[] {node, i};
        }
    }

    public static RopeNode concat(RopeNode node1, RopeNode node2) {
        return new RopeNode(node1, node2);
    }

    /**
     * Splits rope to two ropes at position i.
     */
    public static List<RopeNode> split(RopeNode node, int i) {
        return null;
    }
}
