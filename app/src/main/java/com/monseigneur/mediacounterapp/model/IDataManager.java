package com.monseigneur.mediacounterapp.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IDataManager
{
    /**
     * Read data from input
     *
     * @param is input to read from
     * @return List of MediaData
     */
    List<MediaData> readData(InputStream is);

    /**
     * Write data to an output
     *
     * @param os     output to write to
     * @param mdList list of MediaData to write
     * @return true if successful
     */
    boolean writeData(OutputStream os, List<MediaData> mdList);
}
