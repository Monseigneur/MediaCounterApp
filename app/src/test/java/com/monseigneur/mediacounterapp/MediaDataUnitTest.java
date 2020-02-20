package com.monseigneur.mediacounterapp;

import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MediaDataUnitTest
{
    @Test
    public void constructBasic_fieldsInitialized()
    {
        String name = "Test Media";
        MediaData md = new MediaData(name);

        assertNotNull(md);

        assertEquals(name, md.getMediaName());
        assertEquals(0, md.getCount());
        assertEquals(0, md.getAddedDate());
        assertEquals(MediaCounterStatus.NEW, md.getStatus());

        List<Long> dates = md.getEpDates();
        assertNotNull(dates);
        assertTrue(dates.isEmpty());
    }

    @Test
    public void constructImport_fieldsInitialized()
    {
        String name = "Test Media";
        MediaCounterStatus status = MediaCounterStatus.ONGOING;
        long date = 1L;
        List<Long> epDates = new ArrayList<>();
        epDates.add(2L);

        MediaData md1 = new MediaData(name, status, date, epDates);

        assertNotNull(md1);

        assertEquals(name, md1.getMediaName());
        assertEquals(epDates.size(), md1.getCount());
        assertEquals(date, md1.getAddedDate());
        assertEquals(status, md1.getStatus());

        List<Long> dates = md1.getEpDates();
        assertNotNull(dates);
        assertEquals(epDates.size(), dates.size());

        // Import with null epDates
        MediaData md2 = new MediaData(name, status, date, null);

        assertNotNull(md2);

        assertEquals(name, md2.getMediaName());
        assertEquals(0, md2.getCount());
        assertEquals(date, md2.getAddedDate());
        assertEquals(status, md2.getStatus());

        dates = md2.getEpDates();
        assertNotNull(dates);
        assertTrue(dates.isEmpty());
    }

    @Test
    public void adjustCount_modifiesCount()
    {
        MediaData md = new MediaData("Test Media");

        assertNotNull(md);
        assertEquals(0, md.getCount());

        boolean result;

        result = md.adjustCount(true);
        assertTrue(result);
        assertEquals(1, md.getCount());

        result = md.adjustCount(false);
        assertTrue(result);
        assertEquals(0, md.getCount());

        result = md.adjustCount(false);
        assertTrue(result);
        assertEquals(-1, md.getCount());
    }

    @Test
    public void adjustCount_modifiesStatus()
    {
        MediaData md = new MediaData("Test Media");

        assertNotNull(md);
        assertEquals(MediaCounterStatus.NEW, md.getStatus());

        boolean result;

        // Add 2 counts
        result = md.adjustCount(true);
        assertTrue(result);
        result = md.adjustCount(true);
        assertTrue(result);
        assertEquals(MediaCounterStatus.ONGOING, md.getStatus());

        // Set to complete
        md.setStatus(MediaCounterStatus.COMPLETE);
        assertEquals(MediaCounterStatus.COMPLETE, md.getStatus());

        // Try to add another count
        result = md.adjustCount(true);
        assertFalse(result);
        assertEquals(MediaCounterStatus.COMPLETE, md.getStatus());

        // Try to reduce a count
        result = md.adjustCount(false);
        assertTrue(result);
        assertEquals(MediaCounterStatus.ONGOING, md.getStatus());

        // Add another count
        result = md.adjustCount(true);
        assertTrue(result);

        // Set to dropped
        md.setStatus(MediaCounterStatus.DROPPED);
        assertEquals(MediaCounterStatus.DROPPED, md.getStatus());

        // Try to add a count
        result = md.adjustCount(true);
        assertTrue(result);
        assertEquals(MediaCounterStatus.ONGOING, md.getStatus());

        // Set to dropped again
        md.setStatus(MediaCounterStatus.DROPPED);
        assertEquals(MediaCounterStatus.DROPPED, md.getStatus());

        // Try to reduce a count
        result = md.adjustCount(false);
        assertTrue(result);
        assertEquals(MediaCounterStatus.ONGOING, md.getStatus());

        // Reduce down to no counts
        result = md.adjustCount(false);
        assertTrue(result);
        result = md.adjustCount(false);
        assertTrue(result);

        assertEquals(MediaCounterStatus.NEW, md.getStatus());
    }

    @Test
    public void comparator_byName()
    {
        Comparator<MediaData> byName = MediaData.BY_NAME;

        MediaData md1 = new MediaData("a");
        MediaData md2 = new MediaData("b");

        int result = byName.compare(md1, md2);
        assertTrue(result < 0);

        MediaData md3 = new MediaData("a");

        result = byName.compare(md1, md3);
        assertEquals(0, result);
    }

    @Test
    public void comparator_byLastEpisode()
    {
        Comparator<MediaData> byLastEpisode = MediaData.BY_LAST_EPISODE;

        long addedDate1 = 10L;
        List<Long> epDates1 = new ArrayList<>();
        epDates1.add(100L);

        long addedDate2 = 15L;
        List<Long> epDates2 = new ArrayList<>();
        epDates2.add(200L);

        // Compare 2 medias with different episode dates
        MediaData md1 = new MediaData("a", MediaCounterStatus.ONGOING, addedDate1, epDates1);
        MediaData md2 = new MediaData("b", MediaCounterStatus.ONGOING, addedDate2, epDates2);

        int result = byLastEpisode.compare(md1, md2);
        assertTrue(result > 0);

        // Compare 2 medias with the same episode dates but different names
        MediaData md3 = new MediaData("c", MediaCounterStatus.ONGOING, addedDate1, epDates1);

        result = byLastEpisode.compare(md1, md3);
        assertEquals(0, result);

        // Compare 2 medias where one doesn't have any count
        MediaData md4 = new MediaData("d", MediaCounterStatus.NEW, 250L, new ArrayList<Long>());
        MediaData md5 = new MediaData("e", MediaCounterStatus.NEW, 150L, new ArrayList<Long>());
        MediaData md6 = new MediaData("f", MediaCounterStatus.NEW, 200L, new ArrayList<Long>());

        result = byLastEpisode.compare(md4, md2);
        assertTrue(result < 0);

        result = byLastEpisode.compare(md5, md2);
        assertTrue(result > 0);

        result = byLastEpisode.compare(md6, md2);
        assertEquals(0, result);
    }
}