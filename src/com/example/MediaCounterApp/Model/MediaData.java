package com.example.MediaCounterApp.Model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaData implements Serializable
{
    private static final long serialVersionUID = 0L;

    private String mediaName;
    private int count;
    private boolean complete;
    private List<String> epDates;

    public MediaData(String name)
    {
        init(name, false, new ArrayList<>());
    }

    public MediaData(String name, boolean complete, List<String> epDates)
    {
        init(name, complete, epDates);
    }

    private void init(String name, boolean complete, List<String> epDates)
    {
        this.mediaName = name;
        this.count = epDates.size();
        this.complete = complete;
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

    public List<String> getEpDates()
    {
        return epDates;
    }

    public void setEpDates(List<String> epDates)
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
}
