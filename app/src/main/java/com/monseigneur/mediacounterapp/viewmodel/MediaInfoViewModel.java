package com.monseigneur.mediacounterapp.viewmodel;

import com.monseigneur.mediacounterapp.model.MediaCounterStatus;

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
    public final String mediaName;
    public final MediaCounterStatus status;
    public final long addedDate;
    public final List<Long> epDates;

    public MediaInfoViewModel(String n, MediaCounterStatus s, long ad, List<Long> ed)
    {
        mediaName = n;
        status = s;
        addedDate = ad;
        epDates = ed;
    }

    @Override
    public String toString()
    {
        return "MediaInfoViewModel{" +
                "mediaName='" + mediaName + '\'' +
                ", status=" + status +
                ", addedDate=" + addedDate +
                ", epDates=" + epDates +
                '}';
    }
}
