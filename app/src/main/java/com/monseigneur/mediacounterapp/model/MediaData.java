package com.monseigneur.mediacounterapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaData implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0L;

    private final String mediaName;
    private final MediaCounterStatus status;
    private final long addedDate;
    private final String addedDateString;
    private final List<Long> episodeDates;

    /**
     * Creates a new Media
     *
     * @param name name of the new Media
     * @param date date the Media was added
     */
    public MediaData(String name, long date)
    {
        this(name, MediaCounterStatus.NEW, date, new ArrayList<>());
    }

    /**
     * Creates a new Media
     *
     * @param name   name of the new Media
     * @param status status of the Media
     * @param date   date the Media was added
     */
    public MediaData(String name, MediaCounterStatus status, long date)
    {
        this(name, status, date, new ArrayList<>());
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
        this.addedDateString = Util.timestampToString(this.addedDate);

        if (epDates == null)
        {
            this.episodeDates = new ArrayList<>();
        }
        else
        {
            this.episodeDates = epDates;
        }
    }

    /**
     * Gets the number of Episodes
     *
     * @return the number of Episodes
     */
    public int getCount()
    {
        return episodeDates.size();
    }

    /**
     * Get the date the Media was added as an integer
     *
     * @return the date the Media was added
     */
    public long getAddedDateNumber()
    {
        return addedDate;
    }

    /**
     * Get the date the Media was added
     *
     * @return the date the media was added
     */
    public String getAddedDate()
    {
        return addedDateString;
    }

    /**
     * Get the name of the Media
     *
     * @return the name of the Media
     */
    public String getName()
    {
        return mediaName;
    }

    /**
     * Get the list of Episode dates
     *
     * @return List of Episode dates
     */
    public List<Long> getEpisodeDates()
    {
        return episodeDates;
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

    @Override
    public String toString()
    {
        return "[" + mediaName + ": " + episodeDates.size() + " (" + status + ")]";
    }

    /**
     * Comparator to sort by name
     */
    public static final Comparator<MediaData> BY_NAME =
            (o1, o2) -> {
                String thisName = o1.mediaName.toLowerCase();
                String otherName = o2.getName().toLowerCase();

                return thisName.compareTo(otherName);
            };

    /**
     * Comparator to sort by latest Episode date. Positive means o2 is newer, negative
     * means o1 is newer.
     */
    // How do you handle the screen update? If I push + on a show that isn't the first, should it
    // move underneath my hand? Probably not. Do I want to change the way episodes are updated,
    // through the info view?
    public static final Comparator<MediaData> BY_LAST_EPISODE =
            (o1, o2) -> {
                // Compare by date of latest episode, or add date
                long o1Date = o1.addedDate;
                if (o1.getCount() > 0)
                {
                    o1Date = o1.getEpisodeDates().get(o1.getCount() - 1);
                }

                long o2Date = o2.addedDate;
                if (o2.getCount() > 0)
                {
                    o2Date = o2.getEpisodeDates().get(o2.getCount() - 1);
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
            };

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

        MediaData mediaData = (MediaData) o;

        return addedDate == mediaData.addedDate && Objects.equals(mediaName, mediaData.mediaName) && status == mediaData.status && episodeDates.equals(mediaData.episodeDates);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(mediaName, status, addedDate, episodeDates);
    }
}
