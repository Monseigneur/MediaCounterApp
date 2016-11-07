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
    private boolean complete;
    private long addedDate;
    private List<Long> epDates;


    public MediaData(String name)
    {
        init(name, false, 0, new ArrayList<>());
    }

    public MediaData(String name, boolean complete, long date, List<Long> epDates)
    {
        init(name, complete, date, epDates);
    }

    private void init(String name, boolean complete, long date, List<Long> epDates)
    {
        this.mediaName = name;
        this.count = epDates.size();
        this.complete = complete;
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
            if (!complete)
            {
                count++;
            }
        }
        else
        {
            count--;
            complete = false;
        }
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

    public boolean isComplete()
    {
        return complete;
    }

    public String toString()
    {
        String completeText = (complete) ? " COMPLETE" : "";
        return "[" + mediaName + ": " + count + completeText + "]";
    }

    @Override
    public int compareTo(MediaData another)
    {
        String thisName = mediaName.toLowerCase();
        String otherName = another.getMediaName().toLowerCase();

        return thisName.compareTo(otherName);
    }
}
