package com.monseigneur.mediacounterapp.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MediaCounterRepository
{
    private final MediaCounterDB db;
    private final IDataSerializer<MediaData> mediaDataSerializer;

    private List<MediaData> mediaList = new ArrayList<>();

    public MediaCounterRepository(MediaCounterDB db, IDataSerializer<MediaData> serializer)
    {
        this.db = db;
        this.mediaDataSerializer = serializer;

        updateCache();
    }

    public List<MediaData> getAllMedia()
    {
        return mediaList;
    }

    public MediaData getMedia(String mediaName)
    {
        return mediaList.stream().filter(md -> md.getMediaName().equals(mediaName)).findFirst().orElse(null);
    }

    public List<EpisodeData> getAllEpisodes()
    {
        return db.getEpisodeData();
    }

    public String getRandomMediaName()
    {
        return db.getRandomMedia();
    }

    public boolean isEmpty()
    {
        return mediaList.isEmpty();
    }

    public boolean addNewMedia(String mediaName)
    {
        boolean success = db.addNewMedia(mediaName);

        updateCache();

        return success;
    }

    public boolean addEpisode(String mediaName)
    {
        MediaData media = getMedia(mediaName);

        if (media == null)
        {
            return false;
        }

        if (media.getStatus() == MediaCounterStatus.COMPLETE)
        {
            return false;
        }

        db.addEpisodeNow(mediaName);
        db.setStatus(mediaName, MediaCounterStatus.ONGOING);

        updateCache();

        return true;
    }

    public int removeEpisode(String mediaName)
    {
        MediaData media = getMedia(mediaName);

        if (media == null)
        {
            return 0;
        }

        int originalEpisodeCount = media.getCount();

        int result = 1;
        MediaCounterStatus newStatus = MediaCounterStatus.ONGOING;
        if (originalEpisodeCount == 1)
        {
            newStatus = MediaCounterStatus.NEW;
        }
        else if (originalEpisodeCount == 0)
        {
            // This part is a hack for now
            result = 2;
        }

        db.deleteEpisode(mediaName);

        if (originalEpisodeCount != 0)
        {
            db.setStatus(mediaName, newStatus);
        }

        updateCache();

        return result;
    }

    public void changeStatus(String mediaName, MediaCounterStatus newStatus)
    {
        db.setStatus(mediaName, newStatus);

        updateCache();
    }

    public boolean importData(InputStream is)
    {
        if (is == null)
        {
            return false;
        }

        List<MediaData> mdList = new ArrayList<>();

        if (!mediaDataSerializer.deserialize(is, mdList))
        {
            return false;
        }

        boolean success = db.importData(mdList);

        updateCache();

        return success;
    }

    public boolean exportData(OutputStream os)
    {
        if (os == null)
        {
            return false;
        }

        List<MediaData> mdList = mediaList;

        if (mdList.isEmpty())
        {
            return false;
        }

        return mediaDataSerializer.serialize(os, mdList);
    }

    private void updateCache()
    {
        mediaList = db.getMediaCounters();
    }
}
