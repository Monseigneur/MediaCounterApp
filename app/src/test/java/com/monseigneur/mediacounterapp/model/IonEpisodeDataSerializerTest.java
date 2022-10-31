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

class IonEpisodeDataSerializerTest
{
    public static String validInput = "{" +
            "names:[\"First media\", \"Second media\"]," +
            "data:[" +
            "{media_name_idx:0," +
            "ep_num:1," +
            "status:0," +
            "episode_date:100" +
            "}," +
            "{media_name_idx:0," +
            "ep_num:2," +
            "status:1," +
            "episode_date:200" +
            "}," +
            "{media_name_idx:1," +
            "ep_num:1," +
            "status:1," +
            "episode_date:300" +
            "}" +
            "]}";

    @BeforeEach
    void setUp()
    {
        IonEpisodeDataSerializer.VERBOSE = false;
    }

    @AfterEach
    void tearDown()
    {
        IonEpisodeDataSerializer.VERBOSE = true;
    }

    private List<EpisodeData> buildList()
    {
        List<EpisodeData> edList = new ArrayList<>();

        edList.add(new EpisodeData("First media", 1, 100L, MediaCounterStatus.NEW));
        edList.add(new EpisodeData("First media", 2, 200L, MediaCounterStatus.ONGOING));
        edList.add(new EpisodeData("Second media", 1, 200L, MediaCounterStatus.DROPPED));
        edList.add(new EpisodeData("Third media", 1, 300L, MediaCounterStatus.NEW));
        edList.add(new EpisodeData("Third media", 2, 400L, MediaCounterStatus.NEW));
        edList.add(new EpisodeData("Third media", 3, 500L, MediaCounterStatus.COMPLETE));

        return edList;
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void readAndWriteData(boolean writeBinary)
    {
        List<EpisodeData> originalEdList = buildList();
        IDataSerializer<EpisodeData> dm = new IonEpisodeDataSerializer(writeBinary);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Convert data to the serialized format.
        assertTrue(dm.serialize(bos, originalEdList));

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
        List<EpisodeData> edList = new ArrayList<>();
        assertTrue(dm.deserialize(bis, edList));

        assertEquals(originalEdList, edList);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void readDataConditions(boolean writeBinary)
    {
        IDataSerializer<EpisodeData> dm = new IonEpisodeDataSerializer(writeBinary);

        ByteArrayInputStream bis = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));
        List<EpisodeData> edList = new ArrayList<>();

        // Reading data will fail if any parameters are null.
        assertFalse(dm.deserialize(null, null));
        assertFalse(dm.deserialize(bis, null));
        assertFalse(dm.deserialize(null, edList));
        assertTrue(dm.deserialize(bis, edList));

        // Reading bad data should fail.
        ByteArrayInputStream bad = new ByteArrayInputStream("bad data".getBytes(StandardCharsets.UTF_8));
        assertFalse(dm.deserialize(bad, edList));

        // Reading data that would produce empty results should fail.
        ByteArrayInputStream empty = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        assertFalse(dm.deserialize(empty, edList));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void writeDataConditions(boolean writeBinary)
    {
        IDataSerializer<EpisodeData> dm = new IonEpisodeDataSerializer(writeBinary);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        List<EpisodeData> edList = buildList();

        // Writing data will fail if any parameters are null.
        assertFalse(dm.serialize(null, null));
        assertFalse(dm.serialize(bos, null));
        assertFalse(dm.serialize(null, edList));
        assertTrue(dm.serialize(bos, edList));

        // Writing an empty list should fail.
        assertFalse(dm.serialize(bos, new ArrayList<>()));
    }
}