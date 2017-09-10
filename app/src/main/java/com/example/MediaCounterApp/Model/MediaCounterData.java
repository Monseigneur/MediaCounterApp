package com.example.MediaCounterApp.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Milan on 7/17/2016.
 *
 * This class is the interface to the data. It includes caching.
 */
public class MediaCounterData
{
    private Map<String, Boolean> mediaCounterCacheMap;
    private Map<String, MediaData> mediaCounterDataMap;

    private MediaCounterDB db;

    public MediaCounterData(Context context)
    {
        mediaCounterCacheMap = new HashMap<>();
        mediaCounterDataMap = new HashMap<>();

        db = new MediaCounterDB(context);
    }

    private void init()
    {
        List<MediaData> mdList = db.getMediaCounters();

        for (MediaData md : mdList)
        {
            mediaCounterDataMap.put(md.getMediaName(), md);
            mediaCounterCacheMap.put(md.getMediaName(), false);
        }
    }

    public List<String> getMediaCounters()
    {
        List<String> mediaCounters = new ArrayList<>();

        return mediaCounters;
    }

    public int getNumEpisodes(String mediaName)
    {

        return 0;
    }

    public void addNewMedia(String mediaName)
    {

    }

    public void addEpisode(String mediaName)
    {

    }

    public void deleteEpisode(String mediaName)
    {

    }
}
