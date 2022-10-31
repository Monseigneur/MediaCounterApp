package com.monseigneur.mediacounterapp.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Milan on 8/5/2016.
 */
public class EpisodeData implements Comparable<EpisodeData>, Serializable
{
    private final String mediaName;
    private final int epNum;
    private final long epDate;
    private final MediaCounterStatus mediaStatus;

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        EpisodeData that = (EpisodeData) o;
        return epNum == that.epNum && epDate == that.epDate && Objects.equals(mediaName, that.mediaName) && mediaStatus == that.mediaStatus;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(mediaName, epNum, epDate, mediaStatus);
    }
}
