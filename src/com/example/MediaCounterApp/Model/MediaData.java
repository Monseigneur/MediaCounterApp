package com.example.MediaCounterApp.Model;

import android.util.Log;

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

    private static final String UNKNOWN_DATE = "UNKNOWN";

    private String mediaName;
    private int count;
    private boolean complete;
    private List<String> epDates;

    public MediaData(String name)
    {
        init(name, false, new ArrayList<String>());
    }

    private MediaData(String name, boolean complete, List<String> epDates)
    {
        init(name, complete, epDates);
    }

    private void init(String name, boolean complete, List<String> epDates)
    {
        this.mediaName = name;
        this.count = epDates.size();
        this.complete = complete;
        this.epDates = epDates;

        verifyEpDates();
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
                // We do not want to get out of sync with the epDates list, in case of
                // UNKNOWN markers from legacy.
                if (count > epDates.size())
                {
                    epDates.add(getCurrentDate());
                }
            }
        }
        else
        {
            count--;
            complete = false;

            // We do not want to delete the UNKNOWN markers, for legacy support.
            if (!epDates.isEmpty() && !epDates.get(epDates.size() - 1).equals(UNKNOWN_DATE))
            {
                epDates.remove(epDates.size() - 1);
            }
        }
    }

    public String getMediaName()
    {
        return mediaName;
    }

    public String toString()
    {
        String completeText = (complete) ? " COMPLETE" : "";
        return "[" + mediaName + ": " + count + completeText + "]->" + epDates;
    }

    public boolean isComplete()
    {
        return complete;
    }

    public void setComplete(boolean complete)
    {
        this.complete = complete;
    }

    public List<String> getEpDates()
    {
        return epDates;
    }

    private String getCurrentDate()
    {
        Calendar rightNow = Calendar.getInstance();

        int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
        int month = rightNow.get(Calendar.MONTH) + 1;       // January is 0?
        int year = rightNow.get(Calendar.YEAR);

        String dateString = month + "-" + dayOfMonth + "-" + year;

        return dateString;
    }

    private void verifyEpDates()
    {
        if (count > epDates.size())
        {
            // We are missing dates. Pad front with the unknown date.
            int numToAdd = count - epDates.size();
            for (int i = 0; i < numToAdd; i++)
            {
                epDates.add(0, UNKNOWN_DATE);
            }
        }
        else if (count < epDates.size())
        {
            // Count is smaller than number of dates. Just update count?
            count = epDates.size();
        }
    }

    // Static methods

    // File format:
    // <MEDIA NAME>
    // <COUNT> <COMPLETE_FLAG> <TIME_STAMP_1> ...
    public static MediaData parseString(String firstLine, String secondLine)
    {
        try
        {
            Scanner s = new Scanner(secondLine);
            // Count is a legacy field. It should always exist.
            //int count = s.nextInt();

            int completeFlag = 0;
            if (s.hasNextInt())
            {
                completeFlag = s.nextInt();
            }

            List<String> epDates = new ArrayList<String>();

            while (s.hasNext())
            {
                epDates.add(s.next());
            }

            return new MediaData(firstLine, (completeFlag == 1), epDates);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String writeOut(MediaData md)
    {
        String firstLine = md.getMediaName();
        //String secondLine = md.getCount() + " " + (md.isComplete() ? 1 : 0);
        String secondLine = (md.isComplete() ? "1" : "0");
        for (String date : md.getEpDates())
        {
            secondLine += " " + date;
        }

        return firstLine + "\n" + secondLine;
    }
}
