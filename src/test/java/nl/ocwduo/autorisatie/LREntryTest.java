package nl.ocwduo.autorisatie;

import org.easymock.EasyMock;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author arjen
 */
public class LREntryTest
{
    static final String TESTKEY1 = "Bax:Arjen";
    static final String TESTKEY2 = "Bax:Arjen";

    @Test
    public void testCreateLeftEntry() {
        SyncableObject mockLeft = EasyMock.mock(SyncableObject.class);
        EasyMock.expect(mockLeft.getKey()).andReturn(TESTKEY1).anyTimes();
        SyncableObject mockRight = EasyMock.mock(SyncableObject.class);
        EasyMock.expect(mockRight.getKey()).andReturn(TESTKEY2).anyTimes();

        EasyMock.replay(mockLeft, mockRight);

        LREntry testEntry = LREntry.createLeftEntry(mockLeft);
        assertSame(mockLeft, testEntry.getLeftItem());
        assertNull(testEntry.getRightItem());
        assertEquals(TESTKEY1, testEntry.getKey());

        testEntry.addRightItem(mockRight);
        assertSame(mockRight, testEntry.getRightItem());
        assertEquals(TESTKEY2, mockRight.getKey());

        try {
            testEntry.addRightItem(mockRight);
            fail("should never arrive here");
        } catch (Exception ex) {
            assertTrue(ex instanceof IllegalStateException);
        }
    }

    @Test
    public void testCreateRightEntry() {
        SyncableObject mockRight = EasyMock.mock(SyncableObject.class);
        EasyMock.expect(mockRight.getKey()).andReturn(TESTKEY2).anyTimes();

        EasyMock.replay(mockRight);

        LREntry testEntry = LREntry.createRightEntry(mockRight);
        assertSame(mockRight, testEntry.getRightItem());
        assertNull(testEntry.getLeftItem());
        assertEquals(TESTKEY2, testEntry.getKey());
    }
}
