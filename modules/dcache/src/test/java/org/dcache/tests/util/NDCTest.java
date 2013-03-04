package org.dcache.tests.util;

import org.junit.After;
import org.junit.Test;
import org.slf4j.MDC;

import org.dcache.commons.util.NDC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NDCTest
{
    static public final String MESSAGE = "Hellow World";

    private String getNdc()
    {
        return MDC.get(NDC.KEY_NDC);
    }

    @After
    public void clear()
    {
        NDC.clear();
    }

    @Test
    public void testPushPop()
    {
        NDC.push(MESSAGE);
        assertEquals(MESSAGE, getNdc());
        NDC.push(MESSAGE);
        assertEquals(MESSAGE + " " + MESSAGE, getNdc());
        NDC.pop();
        assertEquals(MESSAGE, getNdc());
        NDC.pop();
        assertNull(getNdc());
    }

    @Test
    public void testPopOnEmpty()
    {
        NDC.pop();
    }

    @Test
    public void testClear()
    {
        NDC.push(MESSAGE);
        NDC.clear();
        assertNull(getNdc());
    }

    @Test
    public void testClone()
    {
        NDC.push(MESSAGE);
        NDC.push(MESSAGE);
        NDC ndc = NDC.cloneNdc();
        NDC.clear();
        NDC.set(ndc);
        assertEquals(MESSAGE + " " + MESSAGE, getNdc());
        NDC.pop();
        assertEquals(MESSAGE, getNdc());
        NDC.pop();
        assertNull(getNdc());
    }
}
