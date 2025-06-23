package com.monseigneur.mediacounterapp.model;

import com.monseigneur.mediacounterapp.R;

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
}
