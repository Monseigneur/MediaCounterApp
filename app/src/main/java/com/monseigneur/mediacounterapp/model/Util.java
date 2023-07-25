package com.monseigneur.mediacounterapp.model;

import android.graphics.Color;

public class Util
{
    public static int getStatusColor(MediaCounterStatus status)
    {
        switch (status)
        {
            default:
            case NEW:
                return Color.WHITE;
            case ONGOING:
                return Color.YELLOW;
            case COMPLETE:
                return Color.GREEN;
            case DROPPED:
                return Color.RED;
        }
    }
}
