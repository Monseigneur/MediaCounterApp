package com.monseigneur.mediacounterapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.monseigneur.mediacounterapp.model.DataManager;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataManagerUnitTest
{
    @Before
    public void setup()
    {
        DataManager.VERBOSE = false;
    }

    @After
    public void teardown()
    {
        DataManager.VERBOSE = true;
    }

    private List<MediaData> buildList()
    {
        List<MediaData> mdList = new ArrayList<>();

        mdList.add(new MediaData("First media", 100L));
        mdList.add(new MediaData("Second media", MediaCounterStatus.DROPPED, 200L));

        List<Long> episodes = new ArrayList<>();
        episodes.add(1000L);
        episodes.add(2000L);
        episodes.add(3000L);
        mdList.add(new MediaData("Third media", MediaCounterStatus.ONGOING, 300L, episodes));

        return mdList;
    }

    private void flipData(boolean writeBinary)
    {
        List<MediaData> originalMdList = buildList();
        DataManager dm = new DataManager(writeBinary);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Convert data to the serialized format.
        assertTrue(dm.writeData(bos, originalMdList));

        try
        {
            bos.flush();
        }
        catch (IOException e)
        {
            fail();
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

        // Convert back to the structured format.
        List<MediaData> mdList = dm.readData(bis);

        assertEquals(originalMdList, mdList);
    }

    @Test
    public void readAndWriteDataText()
    {
        flipData(false);
    }

    @Test
    public void readAndWriteDataBinary()
    {
        flipData(true);
    }
}
