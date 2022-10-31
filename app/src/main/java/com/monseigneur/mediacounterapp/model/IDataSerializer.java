package com.monseigneur.mediacounterapp.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IDataSerializer<T>
{
    /**
     * Read data from input
     *
     * @param is       input to read from
     * @param itemList list to add items to
     * @return true if successful
     */
    boolean readData(InputStream is, List<T> itemList);

    /**
     * Write data to an output
     *
     * @param os       output to write to
     * @param itemList list of items to write
     * @return true if successful
     */
    boolean writeData(OutputStream os, List<T> itemList);
}
