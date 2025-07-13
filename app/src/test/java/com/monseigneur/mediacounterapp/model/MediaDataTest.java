package com.monseigneur.mediacounterapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MediaDataTest
{
    @Test
    public void constructBasic_fieldsInitialized()
    {
        String name = "Test Media";
        MediaData md = new MediaData(name, 1L);

        assertNotNull(md);

        assertEquals(name, md.getMediaName());
        assertEquals(0, md.getCount());
        assertEquals(1, md.getAddedDate());
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
    public void comparator_byName()
    {
        Comparator<MediaData> byName = MediaData.BY_NAME;

        MediaData md1 = new MediaData("a", 1L);
        MediaData md2 = new MediaData("b", 1L);

        int result = byName.compare(md1, md2);
        assertTrue(result < 0);

        MediaData md3 = new MediaData("a", 1L);

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
        MediaData md4 = new MediaData("d", 250L);
        MediaData md5 = new MediaData("e", 150L);
        MediaData md6 = new MediaData("f", 200L);

        result = byLastEpisode.compare(md4, md2);
        assertTrue(result < 0);

        result = byLastEpisode.compare(md5, md2);
        assertTrue(result > 0);

        result = byLastEpisode.compare(md6, md2);
        assertEquals(0, result);
    }
}
