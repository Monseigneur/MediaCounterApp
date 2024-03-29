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

public class IonMediaDataSerializerTest
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
        IonMediaDataSerializer.VERBOSE = false;
    }

    @AfterEach
    public void tearDown()
    {
        IonMediaDataSerializer.VERBOSE = true;
    }

    private List<MediaData> buildList()
    {
        List<MediaData> mdList = new ArrayList<>();

        mdList.add(new MediaData("First media", 100L));
        mdList.add(new MediaData("Second media", MediaCounterStatus.DROPPED, 200L));

        List<Long> episodesOngoing = new ArrayList<>();
        episodesOngoing.add(1000L);
        episodesOngoing.add(2000L);
        episodesOngoing.add(3000L);
        mdList.add(new MediaData("Third media", MediaCounterStatus.ONGOING, 300L, episodesOngoing));

        List<Long> episodesComplete = new ArrayList<>();
        episodesComplete.add(1500L);
        episodesComplete.add(2500L);
        episodesComplete.add(3500L);
        mdList.add(new MediaData("Fourth media", MediaCounterStatus.COMPLETE, 400L, episodesComplete));

        return mdList;
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void readAndWriteData(boolean writeBinary)
    {
        List<MediaData> originalMdList = buildList();
        IDataSerializer<MediaData> dm = new IonMediaDataSerializer(writeBinary);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Convert data to the serialized format.
        assertTrue(dm.serialize(bos, originalMdList));

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
        assertTrue(dm.deserialize(bis, mdList));

        assertEquals(originalMdList, mdList);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void readDataConditions(boolean writeBinary)
    {
        IDataSerializer<MediaData> dm = new IonMediaDataSerializer(writeBinary);

        ByteArrayInputStream bis = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));
        List<MediaData> mdList = new ArrayList<>();

        // Reading data will fail if any parameters are null.
        assertFalse(dm.deserialize(null, null));
        assertFalse(dm.deserialize(bis, null));
        assertFalse(dm.deserialize(null, mdList));
        assertTrue(dm.deserialize(bis, mdList));

        // Reading bad data should fail.
        ByteArrayInputStream bad = new ByteArrayInputStream("bad data".getBytes(StandardCharsets.UTF_8));
        assertFalse(dm.deserialize(bad, mdList));

        // Reading data that would produce empty results should fail.
        ByteArrayInputStream empty = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        assertFalse(dm.deserialize(empty, mdList));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void writeDataConditions(boolean writeBinary)
    {
        IDataSerializer<MediaData> dm = new IonMediaDataSerializer(writeBinary);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        List<MediaData> mdList = buildList();

        // Writing data will fail if any parameters are null.
        assertFalse(dm.serialize(null, null));
        assertFalse(dm.serialize(bos, null));
        assertFalse(dm.serialize(null, mdList));
        assertTrue(dm.serialize(bos, mdList));

        // Writing an empty list should fail.
        assertFalse(dm.serialize(bos, new ArrayList<>()));
    }
}
