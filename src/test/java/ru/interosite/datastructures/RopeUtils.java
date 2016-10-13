package ru.interosite.datastructures;

import ru.interosite.datastructures.Rope.RopeNode;

import javax.annotation.Nullable;
import java.util.*;

public class RopeUtils {

    /**
     * Builds rope out of list of objects.
     * <p>
     * <p>Each object can have three possible value types:
     * <ol>
     * <li>String, indicating leaf with data equal to given string</li>
     * <li>Integer for internal node</li>
     * <li>null</li>
     * </ol>
     * <p>
     * <p>For example, this list: [3, "Hel", "lo", null, null, null, null]
     * will build the following rope:<br>
     * <pre>
     *       3
     *      / \
     *     /   \
     *  "Hel"  "lo"
     * </pre>
     * <p>
     * <p>Note that this method does not enforce rope property. It merely sets either weight
     * or string value of nodes and build tree structure.
     */
    static RopeNode buildRope(List<Object> items) {
        if (items.isEmpty()) {
            return null;
        }
        Queue<Integer> queue = new ArrayDeque<>();
        Map<Integer, RopeNode> lefts = new HashMap<>();
        Map<Integer, RopeNode> rights = new HashMap<>();
        RopeNode root = ropeNodeFromObject(items.get(0));
        queue.add(1);
        queue.add(2);
        lefts.put(1, root);
        rights.put(2, root);
        int level = 1;
        while (!queue.isEmpty()) {
            int levelCount = queue.size();
            int levelIdx = 0;
            for (int i = 0; i < levelCount; i++) {
                int idx = queue.poll();
                RopeNode nextNode = ropeNodeFromObject(items.get(idx));
                if (lefts.containsKey(idx)) {
                    lefts.get(idx).setLeft(nextNode);
                } else if (rights.containsKey(idx)) {
                    rights.get(idx).setRight(nextNode);
                }
                if (nextNode != null) {
                    int leftIdx = 2 * (level + levelIdx) + 1;
                    int rightIdx = 2 * (level + levelIdx) + 2;
                    queue.add(leftIdx);
                    queue.add(rightIdx);
                    lefts.put(leftIdx, nextNode);
                    rights.put(rightIdx, nextNode);
                    levelIdx++;
                }
            }
            level++;
        }
        return root;
    }

    @Nullable private static RopeNode ropeNodeFromObject(@Nullable Object o) {
        if (o instanceof String) {
            return new RopeNode((String) o);
        } else if (o instanceof Integer) {
            return new RopeNode(null, null, (Integer) o);
        }
        return null;
    }
}
