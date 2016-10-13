package ru.interosite.datastructures;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.interosite.datastructures.Rope.RopeNode;

@RunWith(JUnit4.class) public class RopeTest {

    @Rule public final ExpectedException thrown = ExpectedException.none();

    @Test public void shouldConcatLeafs() {
        RopeNode left = new RopeNode("Hel");
        RopeNode right = new RopeNode("lo");
        RopeNode concat = Rope.concat(left, right);
        assertEquals("Hello", concat.buildData());
    }

    @Test public void shouldConcatConcatAndLeaf() {
        RopeNode left = new RopeNode("Hel");
        RopeNode right = new RopeNode("lo");
        RopeNode concat = Rope.concat(left, right);
        RopeNode leaf = new RopeNode("world");
        concat = Rope.concat(concat, leaf);
        assertEquals('e', Rope.index(concat, 1));
        assertEquals('d', Rope.index(concat, 9));
        assertEquals("Helloworld", concat.buildData());
    }

    @Test public void shouldGetIndexFromLeft() {
        RopeNode left = new RopeNode("Hel");
        RopeNode right = new RopeNode("lo");
        RopeNode concat = Rope.concat(left, right);
        assertEquals('l', Rope.index(concat, 2));
    }

    @Test public void shouldGetIndexFromRight() {
        RopeNode left = new RopeNode("Hel");
        RopeNode right = new RopeNode("lo");
        RopeNode concat = Rope.concat(left, right);
        assertEquals('o', Rope.index(concat, 4));
    }

    @Test public void shouldGetIndexFromLeaf() {
        RopeNode node = new RopeNode("Hello world");
        assertEquals('w', Rope.index(node, 6));
    }

    @Test public void shouldThrowInLeafOnLargerIndex() {
        RopeNode node = new RopeNode("Hello world");
        thrown.expect(IndexOutOfBoundsException.class);
        Rope.index(node, 11);
    }

    @Test public void shouldThrowInLeafOnNegativeIndex() {
        RopeNode node = new RopeNode("Hello world");
        thrown.expect(IndexOutOfBoundsException.class);
        Rope.index(node, -1);
    }

    @Test public void shouldThrowInConcatOnLargerIndex() {
        RopeNode left = new RopeNode("Hello");
        RopeNode right = new RopeNode(" world");
        RopeNode concat = Rope.concat(left, right);
        thrown.expect(IndexOutOfBoundsException.class);
        Rope.index(concat, 11);
    }

    @Test public void shouldThrowInConcatOnNegativeIndex() {
        RopeNode left = new RopeNode("Hello");
        RopeNode right = new RopeNode(" world");
        RopeNode concat = Rope.concat(left, right);
        thrown.expect(IndexOutOfBoundsException.class);
        Rope.index(concat, -1);
    }

    @Test public void shouldSplitSingleLeaf() {
        RopeNode node = new RopeNode("Hello world");
        List<RopeNode> nodes = Rope.split(node, 5);
        assertEquals(2, nodes.size());
        assertEquals("Hello", nodes.get(0).buildData());
        assertEquals(" world", nodes.get(1).buildData());
    }

    @Test public void shouldSplitAtLeftBeginning() {
        RopeNode left = new RopeNode("Hello ");
        RopeNode right = new RopeNode("world");
        RopeNode concat = Rope.concat(left, right);
        List<RopeNode> nodes = Rope.split(concat, 0);
        assertTrue(nodes.size() == 1);
        assertTrue(nodes.get(0) == concat);
    }

    @Test public void shouldSplitAtRightBeginning() {
        RopeNode left = new RopeNode("Hello ");
        RopeNode right = new RopeNode("world");
        RopeNode concat = Rope.concat(left, right);
        List<RopeNode> nodes = Rope.split(concat, 6);
        assertTrue(nodes.size() == 2);
        assertTrue(nodes.get(0) == concat);
        assertTrue(nodes.get(1) == right);
    }

    @Test public void shouldSplitAtLeftMiddle() {
        RopeNode left = new RopeNode("Hello ");
        RopeNode right = new RopeNode("world");
        RopeNode concat = Rope.concat(left, right);
        List<RopeNode> nodes = Rope.split(concat, 2);
        assertTrue(nodes.size() == 2);
        assertEquals(new Rope.RopeNode(new RopeNode(new RopeNode("He"), null, 2), null, 2),
            nodes.get(0));
        assertEquals(new RopeNode(new RopeNode("llo "), right, 4), nodes.get(1));
    }

    @Test public void shouldSplitAtRightMiddle() {
        RopeNode left = new RopeNode("Hello ");
        RopeNode right = new RopeNode("world");
        RopeNode concat = Rope.concat(left, right);
        List<RopeNode> nodes = Rope.split(concat, 8);
        assertTrue(nodes.size() == 2);
        assertEquals(
            new RopeNode(new RopeNode("Hello "), new RopeNode(new RopeNode("wo"), null, 2), 6),
            nodes.get(0));
        assertEquals(new RopeNode("rld"), nodes.get(1));
        assertEquals("Hello wo", nodes.get(0).buildData());
        assertEquals("rld", nodes.get(1).buildData());
    }

    @Test public void shouldSplitLastLeaf() {
        RopeNode root = new RopeNode(new RopeNode(
            new RopeNode("01"),
            new RopeNode("23"),
            2),
            new RopeNode(
                new RopeNode("45"),
                new RopeNode("67"), 2),
            4);
        assertEquals("01234567", root.buildData());
        List<RopeNode> splits = Rope.split(root, 6);
        assertTrue(splits.size() == 2);
        assertEquals(new RopeNode(
            new RopeNode(new RopeNode("01"), new RopeNode("23"), 2),
            new RopeNode(new RopeNode("45"), null, 2), 4), splits.get(0));
        assertEquals(new RopeNode("67"), splits.get(1));
    }

    @Test public void shouldBeConsistentOnConcatsAndSplits() {
        RopeNode node = new RopeNode("0123456789");
        List<RopeNode> splits1 = Rope.split(node, 5);
        List<RopeNode> splits21 = Rope.split(splits1.get(0), 2);
        List<RopeNode> splits22 = Rope.split(splits1.get(1), 3);
        assertEquals("01", splits21.get(0).buildData());
        assertEquals("234", splits21.get(1).buildData());
        assertEquals("567", splits22.get(0).buildData());
        assertEquals("89", splits22.get(1).buildData());

        assertEquals("8956723401",
            Rope.concat(Rope.concat(splits22.get(1), splits22.get(0)), // 89567
                Rope.concat(splits21.get(1), splits21.get(0)) // 23401
            ).buildData());
    }

    @Test public void shouldInsertIntoLeaf() {
        RopeNode leaf = new RopeNode("012");
        RopeNode result = Rope.insert(leaf, 1, "666");
        assertEquals("066612", result.buildData());
    }

    @Test public void shouldPrependToLeaf() {
        RopeNode leaf = new RopeNode("012");
        RopeNode result = Rope.insert(leaf, 0, "666");
        assertEquals("666012", result.buildData());
    }

    @Test public void shouldAppendToLeaf() {
        RopeNode leaf = new RopeNode("012");
        RopeNode result = Rope.insert(leaf, 3, "666");
        assertEquals("012666", result.buildData());
    }

    @Test public void shouldInsertIntoConcat() {
        RopeNode result =
            Rope.insert(Rope.concat(new RopeNode("012"), new RopeNode("34")), 1, "666");
        assertEquals("06661234", result.buildData());
        result = Rope.insert(result, 6, "99");
        assertEquals("0666129934", result.buildData());
    }
}
