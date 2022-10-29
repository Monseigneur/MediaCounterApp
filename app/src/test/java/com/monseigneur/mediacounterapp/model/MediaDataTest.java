package com.monseigneur.mediacounterapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;

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
    public void addAndRemoveEpisodes_modifiesCount()
    {
        MediaData md = new MediaData("a", 1L);

        assertEquals(md.getCount(), 0);

        // Removing an episode when there aren't any should fail.
        assertFalse(md.removeEpisode());
        assertEquals(md.getCount(), 0);
        assertTrue(md.getEpDates().isEmpty());

        // Adding episodes should work and change the status.
        assertTrue(md.addEpisode(1L));
        assertEquals(md.getCount(), 1);
        assertEquals(md.getEpDates().size(), md.getCount());

        assertTrue(md.addEpisode(2L));
        assertEquals(md.getCount(), 2);
        assertEquals(md.getEpDates().size(), md.getCount());

        // Removing an episode should work.
        assertTrue(md.removeEpisode());
        assertEquals(md.getCount(), 1);
        assertEquals(md.getEpDates().size(), md.getCount());

        // Changing to COMPLETE should not allow new adds.
        md.setStatus(MediaCounterStatus.COMPLETE);
        assertFalse(md.addEpisode(3L));
        assertEquals(md.getEpDates().size(), md.getCount());

        // Removing the last episode should return the count to 0, and the status to NEW.
        assertTrue(md.removeEpisode());
        assertEquals(md.getCount(), 0);
        assertEquals(md.getEpDates().size(), md.getCount());

        // A subsequent remove when empty should fail.
        assertFalse(md.removeEpisode());
    }

    @Test
    public void addAndRemoveEpisodes_changesStatus()
    {
        long date = 1L;
        MediaData md = new MediaData("Test Media", date);

        assertNotNull(md);
        assertEquals(MediaCounterStatus.NEW, md.getStatus());

        boolean result;

        // Add 2 counts
        result = md.addEpisode(date);
        assertTrue(result);
        result = md.addEpisode(date);
        assertTrue(result);
        assertEquals(MediaCounterStatus.ONGOING, md.getStatus());

        // Set to complete
        md.setStatus(MediaCounterStatus.COMPLETE);
        assertEquals(MediaCounterStatus.COMPLETE, md.getStatus());

        // Try to add another count
        result = md.addEpisode(date);
        assertFalse(result);
        assertEquals(MediaCounterStatus.COMPLETE, md.getStatus());

        // Try to reduce a count
        result = md.removeEpisode();
        assertTrue(result);
        assertEquals(MediaCounterStatus.ONGOING, md.getStatus());

        // Add another count
        result = md.addEpisode(date);
        assertTrue(result);

        // Set to dropped
        md.setStatus(MediaCounterStatus.DROPPED);
        assertEquals(MediaCounterStatus.DROPPED, md.getStatus());

        // Try to add a count
        result = md.addEpisode(date);
        assertTrue(result);
        assertEquals(MediaCounterStatus.ONGOING, md.getStatus());

        // Set to dropped again
        md.setStatus(MediaCounterStatus.DROPPED);
        assertEquals(MediaCounterStatus.DROPPED, md.getStatus());

        // Try to reduce a count
        result = md.removeEpisode();
        assertTrue(result);
        assertEquals(MediaCounterStatus.ONGOING, md.getStatus());

        // Reduce down to no counts
        result = md.removeEpisode();
        assertTrue(result);
        result = md.removeEpisode();
        assertTrue(result);

        assertEquals(MediaCounterStatus.NEW, md.getStatus());
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

    @Test
    public void comparator_byLastEpisode_changeEpisodes()
    {
        Comparator<MediaData> byLastEpisode = MediaData.BY_LAST_EPISODE;

        MediaData md1 = new MediaData("a", 10L);
        MediaData md2 = new MediaData("b", 15L);

        // md2 is newer.
        assertTrue(byLastEpisode.compare(md1, md2) > 0);

        // Adding a new episode to md1, though older, shouldn't change the ordering.
        assertTrue(md1.addEpisode(12L));
        assertTrue(byLastEpisode.compare(md1, md2) > 0);

        // Adding a new episode to md1 that is newer should change the ordering.
        assertTrue(md1.addEpisode(20L));
        assertFalse(byLastEpisode.compare(md1, md2) > 0);

        // Adding a new episode to md2 that is equal to md1 should set the ordering to equal.
        assertTrue(md2.addEpisode(20L));
        assertEquals(byLastEpisode.compare(md1, md2), 0);

        // Removing an episode from md2 should return the ordering to md1 being newer.
        assertTrue(md2.removeEpisode());
        assertFalse(byLastEpisode.compare(md1, md2) > 0);
    }
}