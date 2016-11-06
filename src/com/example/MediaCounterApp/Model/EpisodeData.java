package com.example.MediaCounterApp.Model;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Milan on 8/5/2016.
 */
public class EpisodeData implements Comparable<EpisodeData>, Serializable
{
    private String mediaName;
    private int epNum;
    private long epDate;

    public EpisodeData(String name, int num, long date)
    {
        mediaName = name;
        epNum = num;
        epDate = date;
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
                ", epDate='" + epDate + '\'' +
                '}';
    }

    public static String dateString(long val)
    {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(val);

        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;       // January is 0?
        int year = date.get(Calendar.YEAR);
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        Log.i("dateString", "DATE STRING: M=" + month + " D=" + dayOfMonth + " Y=" + year + " H=" + hour + " M=" + minute);

        return String.format("%d-%d-%d %02d:%02d", month, dayOfMonth, year, hour, minute);
    }
}
