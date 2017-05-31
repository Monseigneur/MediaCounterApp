package com.example.MediaCounterApp.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaData implements Serializable, Comparable<MediaData>
{
    private static final long serialVersionUID = 0L;

    private String mediaName;
    private int count;
    private MediaCounterStatus status;
    private long addedDate;
    private List<Long> epDates;


    public MediaData(String name)
    {
        init(name, MediaCounterStatus.NEW, 0, new ArrayList<>());
    }

    public MediaData(String name, MediaCounterStatus status, long date, List<Long> epDates)
    {
        init(name, status, date, epDates);
    }

    private void init(String name, MediaCounterStatus status, long date, List<Long> epDates)
    {
        this.mediaName = name;
        this.count = epDates.size();
        this.status = status;
        this.addedDate = date;
        this.epDates = epDates;

    }

    public int getCount()
    {
        return count;
    }

    public void adjustCount(boolean increment)
    {
        if (increment)
        {
            if (status != MediaCounterStatus.COMPLETE)
            {
                count++;
            }
        }
        else
        {
            count--;

            if (count == 0)
            {
                status = MediaCounterStatus.NEW;
            }
            else
            {
                status = MediaCounterStatus.ONGOING;
            }
        }
    }

    public long getAddedDate()
    {
        return addedDate;
    }

    public String getMediaName()
    {
        return mediaName;
    }

    public List<Long> getEpDates()
    {
        return epDates;
    }

    public void setEpDates(List<Long> epDates)
    {
        this.epDates = epDates;
    }

    public MediaCounterStatus getStatus()
    {
        return status;
    }

    public void setStatus(MediaCounterStatus status)
    {
        this.status = status;
    }

    public String toString()
    {
        return "[" + mediaName + ": " + count + "(" + status + ")]";
    }

    @Override
    public int compareTo(MediaData another)
    {
        String thisName = mediaName.toLowerCase();
        String otherName = another.getMediaName().toLowerCase();

        return thisName.compareTo(otherName);
    }
}
