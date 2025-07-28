package com.monseigneur.mediacounterapp.model;

import java.util.Objects;

/**
 * Created by Milan on 8/5/2016.
 */
public class EpisodeData implements Comparable<EpisodeData>
{
    private final String mediaName;
    private final int number;
    private final long date;
    private final String dateString;
    private final MediaCounterStatus mediaStatus;

    public EpisodeData(String name, int num, long date, MediaCounterStatus status)
    {
        mediaName = name;
        number = num;
        this.date = date;
        dateString = Util.timestampToString(this.date);
        mediaStatus = status;
    }

    public String getMediaName()
    {
        return mediaName;
    }

    public int getNumber()
    {
        return number;
    }

    public long getDateNumber()
    {
        return date;
    }

    public String getDate()
    {
        return dateString;
    }

    public MediaCounterStatus getMediaStatus()
    {
        return mediaStatus;
    }

    @Override
    public int compareTo(EpisodeData another)
    {
        long delta = date - another.date;

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
                ", epNum=" + number +
                ", epDate=" + date +
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
        return number == that.number && date == that.date && Objects.equals(mediaName, that.mediaName) && mediaStatus == that.mediaStatus;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(mediaName, number, date, mediaStatus);
    }
}
