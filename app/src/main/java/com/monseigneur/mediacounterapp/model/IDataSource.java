package com.monseigneur.mediacounterapp.model;

import java.util.List;

public interface IDataSource
{
    List<MediaData> getAllMedia();

    List<EpisodeData> getAllEpisodes();

    String getRandomMedia();

    boolean addNewMedia(String mediaName);

    void addEpisodeNow(String mediaName);

    void setStatus(String mediaName, MediaCounterStatus status);

    void deleteEpisode(String mediaName);

    void deleteAllMedia();

    boolean importData(List<MediaData> mediaDataList);
}
