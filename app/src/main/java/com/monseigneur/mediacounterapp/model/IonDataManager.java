package com.monseigneur.mediacounterapp.model;

import android.util.Log;

import com.amazon.ion.IonInt;
import com.amazon.ion.IonList;
import com.amazon.ion.IonReader;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonSystem;
import com.amazon.ion.IonText;
import com.amazon.ion.IonValue;
import com.amazon.ion.IonWriter;
import com.amazon.ion.system.IonBinaryWriterBuilder;
import com.amazon.ion.system.IonReaderBuilder;
import com.amazon.ion.system.IonSystemBuilder;
import com.amazon.ion.system.IonTextWriterBuilder;
import com.amazon.ion.system.IonWriterBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IonDataManager implements IDataManager
{
    public static boolean VERBOSE = true;

    // Constants for data import / export
    private static final String DATA_FIELD_TITLE = "title";
    private static final String DATA_FIELD_STATUS = "status";
    private static final String DATA_FIELD_ADDED = "added_date";
    private static final String DATA_FIELD_EPISODES = "episodes";

    private final IonSystem ionSys;
    private final IonReaderBuilder readerBuilder;
    private final IonWriterBuilder writerBuilder;

    /**
     * Constructor
     *
     * @param writeBinary whether to write data in binary or text.
     */
    public IonDataManager(boolean writeBinary)
    {
        this.ionSys = IonSystemBuilder.standard().build();
        this.readerBuilder = IonReaderBuilder.standard();

        if (writeBinary)
        {
            writerBuilder = IonBinaryWriterBuilder.standard();
        }
        else
        {
            writerBuilder = IonTextWriterBuilder.standard();
        }
    }

    @Override
    public boolean readData(InputStream is, List<MediaData> mdList)
    {
        if (is == null || mdList == null)
        {
            return false;
        }

        mdList.clear();

        try (IonReader reader = readerBuilder.build(is))
        {
            Iterator<IonValue> iter = ionSys.iterate(reader);

            IonList elements = (IonList) iter.next();

            if (elements == null)
            {
                if (VERBOSE)
                {
                    Log.i("readData", "No data to read");
                }

                return false;
            }

            for (IonValue iv : elements)
            {
                IonStruct val = (IonStruct) iv;
                String mediaName = ((IonText) val.get(DATA_FIELD_TITLE)).stringValue();
                int statusVal = ((IonInt) val.get(DATA_FIELD_STATUS)).intValue();
                MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                long addedDate = ((IonInt) val.get(DATA_FIELD_ADDED)).longValue();

                MediaData md = new MediaData(mediaName, status, addedDate);

                IonList episodes = (IonList) val.get(DATA_FIELD_EPISODES);
                for (IonValue epIv : episodes)
                {
                    long epDate = ((IonInt) epIv).longValue();
                    md.addEpisode(epDate);
                }

                if (VERBOSE)
                {
                    Log.i("readData", "imported " + md);
                }

                mdList.add(md);
            }
        }
        catch (Exception e)
        {
            if (VERBOSE)
            {
                Log.e("readData", "caught exception " + e);
            }

            return false;
        }

        return !mdList.isEmpty();
    }

    @Override
    public boolean writeData(OutputStream os, List<MediaData> mdList)
    {
        if (os == null || mdList == null || mdList.isEmpty())
        {
            return false;
        }

        IonList backupData = ionSys.newEmptyList();

        for (MediaData md : mdList)
        {
            IonStruct media = ionSys.newNullStruct();
            media.put(DATA_FIELD_TITLE).newString(md.getMediaName());
            media.put(DATA_FIELD_STATUS).newInt(md.getStatus().value);
            media.put(DATA_FIELD_ADDED).newInt(md.getAddedDate());

            List<Long> epDates = md.getEpDates();
            IonList epList = ionSys.newEmptyList();
            for (int i = 0; i < epDates.size(); i++)
            {
                epList.add(ionSys.newInt(epDates.get(i)));
            }

            media.put(DATA_FIELD_EPISODES, epList);

            backupData.add(media);
        }

        try (IonWriter writer = writerBuilder.build(os))
        {
            backupData.writeTo(writer);
        }
        catch (IOException e)
        {
            if (VERBOSE)
            {
                Log.e("writeData", "caught exception while writing backup data " + e);
            }

            return false;
        }

        return true;
    }
}
