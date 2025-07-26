package com.monseigneur.mediacounterapp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsData
{
    public int mediaCount;
    public Map<MediaCounterStatus, Integer> countByStatus;
    public int episodeCount;

    public StatsData()
    {
        mediaCount = 0;
        countByStatus = new HashMap<>();
        episodeCount = 0;
    }

    public StatsData(List<MediaData> mediaList)
    {
        this();

        mediaCount = mediaList.size();

        for (MediaData media : mediaList)
        {
            episodeCount += media.getCount();

            countByStatus.compute(media.getStatus(), (_, val) ->
                    (val == null) ? 1 : val + 1
            );
        }
    }
}
