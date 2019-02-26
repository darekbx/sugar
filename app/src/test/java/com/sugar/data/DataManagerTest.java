package com.sugar.data;

import com.sugar.Entry;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Calendar;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class DataManagerTest extends TestCase {

    @Test
    public void testDeleteOlderThanAYear() throws Exception {
        // given
        DataManager dataManager = new DataManager();

        Calendar calendar = Calendar.getInstance();
        dataManager.saveEntry(RuntimeEnvironment.application, new Entry(null, "Test 1", 1.0, calendar.getTimeInMillis()));
        assertEquals(1, dataManager.doGetEntries(RuntimeEnvironment.application).size());

        calendar.add(Calendar.YEAR, -2);
        dataManager.saveEntry(RuntimeEnvironment.application, new Entry(null, "Test 2", 2.0, calendar.getTimeInMillis()));
        assertEquals(2, dataManager.doGetEntries(RuntimeEnvironment.application).size());

        // when
        dataManager.deleteOlderThanAYear(RuntimeEnvironment.application);

        // then
        List<Entry> entryList = dataManager.doGetEntries(RuntimeEnvironment.application);
        assertEquals(1, entryList.size());
        assertEquals("Test 1", entryList.get(0).getDescription());
    }

}