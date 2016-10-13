package ru.interosite.datastructures;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Strings;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class Rope {

    public static class RopeNode {

        private int weight;
        private RopeNode left;
        private RopeNode right;
        private String data;

        public RopeNode(String data) {
            this(null, null, data.length(), data);
        }

        public RopeNode(RopeNode left, RopeNode right, int weight) {
            this(left, right, weight, null);
        }

        public RopeNode(RopeNode left, RopeNode right, int weight, String data) {
            this.data = data;
            this.weight = weight;
            this.left = left;
            this.right = right;
        }

        public String buildData() {
            StringBuilder sb = new StringBuilder();
            buildData(this, sb);
            return sb.toString();
        }

        public void setLeft(RopeNode left) {
            this.left = left;
        }

        public void setRight(RopeNode right) {
            this.right = right;
        }

        private static void buildData(RopeNode node, StringBuilder sb) {
            if (node.data != null) {
                sb.append(node.data);
            } else {
                buildData(node.left, sb);
                if (node.right != null) {
                    buildData(node.right, sb);
                }
            }
        }

        @Override public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof RopeNode) {
                RopeNode that = (RopeNode) obj;
                return Objects.equals(data, that.data) && Objects.equals(left, that.left) && Objects
                    .equals(right, that.right) && Objects.equals(weight, that.weight);
            }
            return super.equals(obj);
        }

        @Override public int hashCode() {
            return Objects.hash(data, left, right, weight);
        }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            strValue(this, sb, 0);
            return sb.toString();
        }

        private static void strValue(RopeNode node, StringBuilder sb, int level) {
            if (node == null) {
                sb.append("null");
                return;
            }
            String padding = Strings.repeat("  ", level);
            sb.append("{\n");
            sb.append(padding).append("  w: ").append(node.weight).append(", \n");

            sb.append(padding).append("  l: ");
            strValue(node.left, sb, level + 1);
            sb.append(", \n");

            sb.append(padding).append("  r: ");
            strValue(node.right, sb, level + 1);
            sb.append(", \n");
            sb.append(padding).append("  d: ").append(node.data);
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
            return getLength(node.left) + (node.right == null ? 0 : getLength(node.right));
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
            RopeNode prev = path.get(path.size() - 1);
            if (prev == null) {
                return Arrays.asList(node);
            }
            if (pos > prev.weight - 1) {
                pos = pos - prev.weight;
                path.add(prev.right);
            } else if (prev.left != null) {
                path.add(prev.left);
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

    public static RopeNode insert(RopeNode node, int pos, String str) {
        List<RopeNode> splits = Rope.split(node, pos);
        RopeNode inserted = new RopeNode(null, null, str.length(), str);
        RopeNode result;
        if (splits.size() == 1) {
            if (pos == 0) {
                result = Rope.concat(inserted, splits.get(0));
            } else {
                result = Rope.concat(splits.get(0), inserted);
            }
        } else {
            result = Rope.concat(splits.get(0), inserted);
            result = Rope.concat(result, splits.get(1));
        }
        return result;
    }

    public static RopeNode delete(RopeNode node, int start, int end) {
        checkArgument(start < end);
        RopeNode preNode = null;
        RopeNode postNode;
        List<RopeNode> splits1 = Rope.split(node, start);
        if (splits1.size() == 1) {
            postNode = splits1.get(0);
        } else {
            preNode = splits1.get(0);
            postNode = splits1.get(1);
        }
        List<RopeNode> splits2 = Rope.split(postNode, end - start);
        if (splits2.size() == 1) {
            return preNode;
        }
        return preNode == null ? splits2.get(1) : Rope.concat(preNode, splits2.get(1));
    }
}
