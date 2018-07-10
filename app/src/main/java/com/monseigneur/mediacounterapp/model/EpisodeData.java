package com.monseigneur.mediacounterapp.model;

import java.io.Serializable;

/**
 * Created by Milan on 8/5/2016.
 */
public class EpisodeData implements Comparable<EpisodeData>, Serializable
{
    private String mediaName;
    private int epNum;
    private long epDate;
    private MediaCounterStatus mediaStatus;

    public EpisodeData(String name, int num, long date, MediaCounterStatus status)
    {
        mediaName = name;
        epNum = num;
        epDate = date;
        mediaStatus = status;
    }

    public String getMediaName()
    {
        return mediaName;
    }

    public int getEpNum()
    {
        return epNum;
    }

    public long getEpDate()
    {
        return epDate;
    }

    public MediaCounterStatus getMediaStatus()
    {
        return mediaStatus;
    }

    @Override
    public int compareTo(EpisodeData another)
    {
        long delta = epDate - another.epDate;

        int result = 0;
        if (delta < 0)
        {
            result = -1;
        }
        else if (delta > 0)
        {
            result = 1;
        }

        return result;
    }

    @Override
    public String toString()
    {
        return "EpisodeData{" +
                "mediaName='" + mediaName + '\'' +
                ", epNum=" + epNum +
                ", epDate=" + epDate +
                ", mediaStatus=" + mediaStatus +
                '}';
    }

}
