package com.github.gumtreediff.test;

import static org.junit.Assert.assertEquals;

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.hash.RollingHashGenerator;
import org.junit.Before;
import org.junit.Test;

public class TestHash {

    ITree root;

    @Before // FIXME Could it be before class ?
    public void init() {

    }

    @Test
    public void testRollingJavaHash() {
        ITree root = TreeLoader.getDummySrc();
        new RollingHashGenerator.JavaRollingHashGenerator().hash(root);
        assertEquals(-1381305887, root.getChild(0).getChild(0).getHash()); // for c
        assertEquals(-1380321823, root.getChild(0).getChild(1).getHash()); // for d
        assertEquals(-1762812253, root.getChild(0).getHash()); // for b
        assertEquals(-1407966943, root.getChild(1).getHash()); // for e
        assertEquals(-295599963, root.getHash()); // for a
    }

}
