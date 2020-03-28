package com.monseigneur.mediacounterapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaData implements Serializable
{
    private static final long serialVersionUID = 0L;

    private final String mediaName;
    private int count;
    private MediaCounterStatus status;
    private final long addedDate;
    private final List<Long> epDates;


    /**
     * Creates a new Media
     *
     * @param name name of the new Media
     */
    public MediaData(String name)
    {
        this(name, MediaCounterStatus.NEW, 0, new ArrayList<Long>());
    }

    /**
     * Creates a new Media
     *
     * @param name    name of the new Media
     * @param status  status of the Media
     * @param date    date the Media was added
     * @param epDates List of millisecond timestamps for each Episode
     */
    public MediaData(String name, MediaCounterStatus status, long date, List<Long> epDates)
    {
        this.mediaName = name;
        this.status = status;
        this.addedDate = date;

        if (epDates == null)
        {
            this.epDates = new ArrayList<>();
        }
        else
        {
            this.epDates = epDates;
        }

        this.count = this.epDates.size();
    }

    /**
     * Gets the number of Episodes
     *
     * @return the number of Episodes
     */
    public int getCount()
    {
        return count;
    }

    /**
     * @param increment whether to increment or decrement
     * @return true if update was successful, false otherwise
     */
    public boolean adjustCount(boolean increment)
    {
        // This flag was added so that the caller would know if the adjustCount call succeeded and wouldn't updated
        // the database incorrectly if it failed (such as when the MediaData is already marked as complete and the
        // count is incremented.
        // TODO Should a "cleaner" be added to the DB code that will find orphaned episodes and remove them?
        boolean updated = false;
        if (increment)
        {
            if (status != MediaCounterStatus.COMPLETE)
            {
                count++;
                updateStatus();
                updated = true;
            }
        }
        else
        {
            count--;
            updateStatus();
            updated = true;
        }

        return updated;
    }

    /**
     * Get the date the Media was added
     *
     * @return the date the Media was added
     */
    public long getAddedDate()
    {
        return addedDate;
    }

    /**
     * Get the name of the Media
     *
     * @return the name of the Media
     */
    public String getMediaName()
    {
        return mediaName;
    }

    /**
     * Get the list of Episode dates
     *
     * @return List of Episode dates
     */
    public List<Long> getEpDates()
    {
        return epDates;
    }

    /**
     * Get the status of the Media
     *
     * @return the status of the Media
     */
    public MediaCounterStatus getStatus()
    {
        return status;
    }

    /**
     * Set the status of the Media
     *
     * @param status the new status of the Media
     */
    public void setStatus(MediaCounterStatus status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "[" + mediaName + ": " + count + "(" + status + ")]";
    }

    /**
     * Update the status of the Media
     */
    private void updateStatus()
    {
        // TODO Want to do some extra checks to not allow any illegal status transitions?
        if (count <= 0)
        {
            status = MediaCounterStatus.NEW;
        }
        else
        {
            status = MediaCounterStatus.ONGOING;
        }
    }

    /**
     * Comparator to sort by name
     */
    public static final Comparator<MediaData> BY_NAME =
            new Comparator<MediaData>()
            {
                @Override
                public int compare(MediaData o1, MediaData o2)
                {
                    String thisName = o1.mediaName.toLowerCase();
                    String otherName = o2.getMediaName().toLowerCase();

                    return thisName.compareTo(otherName);
                }
            };

    /**
     * Comparator to sort by latest Episode date
     */
    // How do you handle the screen update? If I push + on a show that isn't the first, should it
    // move underneath my hand? Probably not. Do I want to change the way episodes are updated,
    // through the info view?
    public static final Comparator<MediaData> BY_LAST_EPISODE =
            new Comparator<MediaData>()
            {
                @Override
                public int compare(MediaData o1, MediaData o2)
                {
                    // Compare by date of latest episode, or add date
                    long o1Date = o1.addedDate;
                    if (o1.getCount() > 0)
                    {
                        o1Date = o1.getEpDates().get(o1.getCount() - 1);
                    }

                    long o2Date = o2.addedDate;
                    if (o2.getCount() > 0)
                    {
                        o2Date = o2.getEpDates().get(o2.getCount() - 1);
                    }

                    long delta = o1Date - o2Date;

                    int result = 0;
                    if (delta < 0)
                    {
                        result = 1;
                    }
                    else if (delta > 0)
                    {
                        result = -1;
                    }

                    return result;
                }
            };
}
