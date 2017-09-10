package com.example.MediaCounterApp.Model;

public enum MediaCounterStatus
{
    NEW(0),
    ONGOING(1),
    COMPLETE(2),
    DROPPED(3);

    public final int value;
    private static MediaCounterStatus[] cachedValues = null;

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
