package com.monseigneur.mediacounterapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.MediaCounterRepository;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MediaViewModel extends ViewModel
{
    private MediaCounterRepository repository;

    private final MutableLiveData<List<MediaData>> mediaData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<EpisodeData>> episodeData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<MediaData>> getAllMedia()
    {
        return mediaData;
    }

    public LiveData<List<EpisodeData>> getAllEpisodes()
    {
        return episodeData;
    }

    public void setRepository(MediaCounterRepository repo)
    {
        repository = repo;

        updateLive();
    }

    public String getRandomMediaName()
    {
        return repository.getRandomMediaName();
    }

    public boolean isEmpty()
    {
        return repository.isEmpty();
    }

    public boolean addNewMedia(String mediaName)
    {
        boolean success = repository.addNewMedia(mediaName);

        if (success)
        {
            updateLive();
        }

        return success;
    }

    public void addEpisode(String mediaName)
    {
        boolean changed = repository.addEpisode(mediaName);

        if (changed)
        {
            updateLive();
        }
    }

    public void removeEpisode(String mediaName)
    {
        boolean changed = repository.removeEpisode(mediaName);

        if (changed)
        {
            updateLive();
        }
    }

    public void changeStatus(String mediaName, MediaCounterStatus newStatus)
    {
        repository.changeStatus(mediaName, newStatus);

        updateLive();
    }

    public boolean importData(InputStream is)
    {
        boolean success = repository.importData(is);

        if (success)
        {
            updateLive();
        }

        return success;
    }

    public boolean exportData(OutputStream os)
    {
        boolean success = repository.exportData(os);

        if (success)
        {
            updateLive();
        }

        return success;
    }

    public void deleteAllMedia()
    {
        repository.deleteAllMedia();

        updateLive();
    }

    private void updateLive()
    {
        mediaData.setValue(repository.getAllMedia());
        episodeData.setValue(repository.getAllEpisodes());
    }
}
