package com.monseigneur.mediacounterapp.activity;

import com.monseigneur.mediacounterapp.model.MediaData;

public interface ListItemClickCallback
{
    public enum ItemClickType
    {
        INFO,
        INCREMENT,
        DECREMENT
    }

    void onClick(MediaData md, ItemClickType clickType);
}
