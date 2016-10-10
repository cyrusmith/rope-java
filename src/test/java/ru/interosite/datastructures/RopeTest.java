package ru.interosite.datastructures;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.interosite.datastructures.Rope.RopeNode;

@RunWith(JUnit4.class) public class RopeTest {

    @Rule public final ExpectedException thrown = ExpectedException.none();

    @Test public void shouldConcatLeafs() {
        RopeNode left = new RopeNode("Hel");
        RopeNode right = new RopeNode("lo");
        RopeNode concat = Rope.concat(left, right);
        assertEquals("Hello", concat.getData());
    }

    @Test public void shouldConcatConcatAndLeaf() {
        RopeNode left = new RopeNode("Hel");
        RopeNode right = new RopeNode("lo");
        RopeNode concat = Rope.concat(left, right);
        RopeNode leaf = new RopeNode("world");
        concat = Rope.concat(concat, leaf);
        assertEquals('e', Rope.index(concat, 1));
        assertEquals('d', Rope.index(concat, 9));
        assertEquals("Helloworld", concat.getData());
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
        assertEquals("Hello", nodes.get(0).getData());
        assertEquals(" world", nodes.get(1).getData());
    }
}
