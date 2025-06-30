package com.monseigneur.mediacounterapp.model;

import android.util.Log;

import com.monseigneur.mediacounterapp.R;

import java.util.Calendar;
import java.util.Locale;

public class Util
{
    public static int getStatusAppearance(MediaCounterStatus status)
    {
        switch (status)
        {
            case ONGOING:
                return R.style.MediaStatus_Ongoing;
            case COMPLETE:
                return R.style.MediaStatus_Complete;
            case DROPPED:
                return R.style.MediaStatus_Dropped;
            case NEW:
            default:
                return R.style.MediaStatus_Default;
        }
    }

    /**
     * Gets the current time
     *
     * @return the current time, in milliseconds
     */
    public static long getTimeNow()
    {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Converts a millisecond time into a date string
     *
     * @param val millisecond time
     * @return the date string for the given time
     */
    public static String timestampToString(long val)
    {
        if (val == 0)
        {
            return "Unknown date";
        }
        else
        {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(val);

            int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH) + 1;       // January is 0?
            int year = date.get(Calendar.YEAR);
            int hour = date.get(Calendar.HOUR_OF_DAY);
            int minute = date.get(Calendar.MINUTE);

            Log.i("dateString", "DATE STRING: M=" + month + " D=" + dayOfMonth + " Y=" + year + " H=" + hour + " M=" + minute);

            return String.format(Locale.US, "%d-%d-%d %02d:%02d", month, dayOfMonth, year, hour, minute);
        }
    }
}
