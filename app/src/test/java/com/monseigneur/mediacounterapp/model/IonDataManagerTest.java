package com.monseigneur.mediacounterapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IonDataManagerTest
{
    public static String validInput = "[" +
            "{title:\"First media\"," +
            "status:0," +
            "added_date:10," +
            "episodes:[]" +
            "}," +
            "{title:\"Second media\"," +
            "status:1," +
            "added_date:20," +
            "episodes:[25, 30, 35]}" +
            "]";

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

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void readAndWriteData(boolean writeBinary)
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
        List<MediaData> mdList = new ArrayList<>();
        assertTrue(dm.readData(bis, mdList));

        assertEquals(originalMdList, mdList);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void readDataConditions(boolean writeBinary)
    {
        IDataManager dm = new IonDataManager(writeBinary);

        ByteArrayInputStream bis = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));
        List<MediaData> mdList = new ArrayList<>();

        // Reading data will fail if any parameters are null.
        assertFalse(dm.readData(null, null));
        assertFalse(dm.readData(bis, null));
        assertFalse(dm.readData(null, mdList));
        assertTrue(dm.readData(bis, mdList));

        // Reading bad data should fail.
        ByteArrayInputStream bad = new ByteArrayInputStream("bad data".getBytes(StandardCharsets.UTF_8));
        assertFalse(dm.readData(bad, mdList));

        // Reading data that would produce empty results should fail.
        ByteArrayInputStream empty = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        assertFalse(dm.readData(empty, mdList));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void writeDataConditions(boolean writeBinary)
    {
        IDataManager dm = new IonDataManager(writeBinary);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        List<MediaData> mdList = buildList();

        // Writing data will fail if any parameters are null.
        assertFalse(dm.writeData(null, null));
        assertFalse(dm.writeData(bos, null));
        assertFalse(dm.writeData(null, mdList));
        assertTrue(dm.writeData(bos, mdList));

        // Writing an empty list should fail.
        assertFalse(dm.writeData(bos, new ArrayList<>()));
    }
}
