package com.monseigneur.mediacounterapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IonDataManagerTest
{
    @BeforeEach
    public void setUp()
    {
        IonDataManager.VERBOSE = false;
    }

    @AfterEach
    public void tearDown()
    {
        IonDataManager.VERBOSE = true;
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
        IDataManager dm = new IonDataManager(writeBinary);
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

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void readAndWriteData(boolean writeBinary)
    {
        flipData(writeBinary);
    }
}
