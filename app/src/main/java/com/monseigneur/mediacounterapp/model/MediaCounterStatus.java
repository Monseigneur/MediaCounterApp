package com.monseigneur.mediacounterapp.model;

import java.util.EnumSet;

public enum MediaCounterStatus
{
    NEW(0),
    ONGOING(1),
    COMPLETE(2),
    DROPPED(3);

    public final int value;
    private static MediaCounterStatus[] cachedValues = null;

    public static final EnumSet<MediaCounterStatus> ALL_STATUSES = EnumSet.allOf(MediaCounterStatus.class);
    public static final EnumSet<MediaCounterStatus> WATCHABLE_STATUSES = EnumSet.of(MediaCounterStatus.NEW, MediaCounterStatus.ONGOING);

    MediaCounterStatus(int value)
    {
        this.value = value;
    }

    public static MediaCounterStatus from(int i)
    {
        if (cachedValues == null)
        {
            cachedValues = MediaCounterStatus.values();
        }

        return cachedValues[i];
    }
}
