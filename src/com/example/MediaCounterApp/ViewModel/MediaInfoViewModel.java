package com.example.MediaCounterApp.ViewModel;

import java.io.Serializable;
import java.util.List;

// This is to pass data from the Main Activity to the Media Info View Activity
public class MediaInfoViewModel implements Serializable
{
    private static final long serialVersionUID = 0L;

    // What do I need to send to the other view?
    // - Media Name
    // - Complete status
    // - Added date
    // - Episode list (number, date)
    public String mediaName;
    public boolean completeStatus;
    public long addedDate;
    public List<Long> epDates;

    public MediaInfoViewModel(String n, boolean cs, long ad, List<Long> ed)
    {
        mediaName = n;
        completeStatus = cs;
        addedDate = ad;
        epDates = ed;
    }

    @Override
    public String toString()
    {
        return "MediaInfoViewModel{" +
                "mediaName='" + mediaName + '\'' +
                ", completeStatus=" + completeStatus +
                ", addedDate=" + addedDate +
                ", epDates=" + epDates +
                '}';
    }
}
