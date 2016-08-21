package com.example.MediaCounterApp.Model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milan on 8/5/2016.
 */
public class EpisodeData implements Comparable<EpisodeData>, Serializable
{
    private String mediaName;
    private int epNum;
    private String epDate;

    public EpisodeData(String name, int num, String date)
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

    public String getEpDate()
    {
        return epDate;
    }

    @Override
    public int compareTo(EpisodeData another)
    {
        int result = 0;
        if (epDate.equals(MediaCounterDB.UNKNOWN_DATE) || another.getEpDate().equals(MediaCounterDB.UNKNOWN_DATE))
        {
            // At least one of them is unknown.
            if (epDate.equals(MediaCounterDB.UNKNOWN_DATE) && another.getEpDate().equals(MediaCounterDB.UNKNOWN_DATE))
            {
                // Both are
                result = mediaName.compareTo(another.mediaName);
            }
            else if (epDate.equals(MediaCounterDB.UNKNOWN_DATE))
            {
                result = -1;
            }
            else
            {
                result = 1;
            }
        }
        else
        {
            result = EpisodeData.compareDates(epDate, another.getEpDate());
            //epDate.compareTo(another.getEpDate());
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

    public static int compareDates(String first, String second)
    {
        List<Integer> firstInfo = parseDate(first);
        List<Integer> secondInfo = parseDate(second);

        int result = 0;
        for (int i = 0; i < firstInfo.size(); i++)
        {
            if (!firstInfo.get(i).equals(secondInfo.get(i)))
            {
                result = firstInfo.get(i) - secondInfo.get(i);
                break;
            }
        }
        return result;
    }

    private static List<Integer> parseDate(String date)
    {
        List<Integer> datePieces = new ArrayList<>();

        try
        {
            // Of the form:
            // <MONTH>-<DAY>-<YEAR> <HOUR>:<MINUTE>
            String[] dateTime = date.split(" ");

            String[] dateInfo = dateTime[0].split("-");
            String[] timeInfo = dateTime[1].split(":");

            datePieces.add(Integer.parseInt(dateInfo[2]));
            datePieces.add(Integer.parseInt(dateInfo[0]));
            datePieces.add(Integer.parseInt(dateInfo[1]));


            datePieces.add(Integer.parseInt(timeInfo[0]));
            datePieces.add(Integer.parseInt(timeInfo[1]));
        }
        catch (Exception e)
        {
            Log.i("parseDate", "Bad parse! Date is of form [" + date + "]");
        }
        return datePieces;
    }
}
