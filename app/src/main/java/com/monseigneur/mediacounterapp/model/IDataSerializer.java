package com.monseigneur.mediacounterapp.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IDataSerializer<T>
{
    /**
     * Deserialize data from an input
     *
     * @param is       input to read from
     * @param itemList list to add items to
     * @return true if successful
     */
    boolean deserialize(InputStream is, List<T> itemList);

    /**
     * Serialize data to an output
     *
     * @param os       output to write to
     * @param itemList list of items to write
     * @return true if successful
     */
    boolean serialize(OutputStream os, List<T> itemList);
}
