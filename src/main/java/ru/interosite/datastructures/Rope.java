package ru.interosite.datastructures;

import com.google.common.base.Preconditions;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class Rope {

    public static class RopeNode {
        private int weight;
        private RopeNode left;
        private RopeNode right;
        private String data;

        public RopeNode(String data) {
            requireNonNull(data);
            weight = data.length();
            this.data = data;
            left = null;
            right = null;
        }

        private RopeNode(RopeNode left, RopeNode right, int weight) {
            requireNonNull(left);
            requireNonNull(right);
            data = null;
            this.weight = weight;
            this.left = left;
            this.right = right;
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
        if ((i < 0) || (i > getLength(node) - 1)) {
            throw new IndexOutOfBoundsException();
        }
        if (i > node.weight - 1) {
            return index(node.right, i - node.weight);
        } else if (node.left != null) {
            return index(node.left, i);
        } else {
            return node.data.charAt(i);
        }
    }

    private static int getLength(RopeNode node) {
        if (node.data != null) {
            return node.weight;
        } else {
            return getLength(node.left) + getLength(node.right);
        }
    }

    public static RopeNode concat(RopeNode node1, RopeNode node2) {
        requireNonNull(node1);
        requireNonNull(node2);

        int weight = getLength(node1);
        return new RopeNode(node1, node2, weight);
    }

    /**
     * Splits rope to two ropes at position i.
     */
    public static List<RopeNode> split(RopeNode node, int i) {
        List<RopeNode> path = new ArrayList<>();
        path.add(node);
        int pos = i;
        while (true) {
            if (pos > path.get(path.size() - 1).weight - 1) {
                pos = pos - node.weight;
                path.add(node.right);
            } else if (node.left != null) {
                path.add(node.left);
            } else {
                break;
            }
        }

        if (pos > 0) {
            RopeNode parent = path.get(path.size() - 1);
            RopeNode newLeft = new RopeNode(parent.data.substring(0, pos));
            RopeNode newRight = new RopeNode(parent.data.substring(pos));
            parent.data = null;
            parent.weight = pos;
            parent.left = newLeft;
            parent.right = newRight;
            path.add(newRight);
        }

        List<RopeNode> toConcat = new ArrayList<>();

        // Find first right-hand side node.
        boolean steppedBack = false;
        RopeNode prev = path.get(path.size() - 1);
        while (!path.isEmpty()) {
            RopeNode next = path.remove(path.size() - 1);
            RopeNode cut = null;
            if (steppedBack) {
                if (next.left == prev && next.right != null) {
                    cut = next.right;
                }
            } else if (next.right == prev) {
                cut = prev;
                steppedBack = true;
            }

            prev = next;

            if (cut != null) {
                toConcat.add(cut);
                next.right = null;
                for (int k = path.size() - 1; k >= 0; k--) {
                    if (path.get(k).left == next) {
                        path.get(k).weight -= cut.weight;
                    }
                    next = path.get(k);
                }
            }

        }

        List<RopeNode> result = new ArrayList<>();
        result.add(node);

        Iterator<RopeNode> cutIter = toConcat.iterator();
        if (cutIter.hasNext()) {
            RopeNode concat = cutIter.next();
            while (cutIter.hasNext()) {
                concat = Rope.concat(concat, cutIter.next());
            }
            result.add(concat);
        }

        return result;
    }
}
