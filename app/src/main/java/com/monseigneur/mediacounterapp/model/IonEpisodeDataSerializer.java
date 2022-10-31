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
import java.util.Iterator;
import java.util.List;

public class IonEpisodeDataSerializer implements IDataSerializer<EpisodeData>
{
    public static boolean VERBOSE = true;

    // Ion structure:
    // [{
    //      media_name:<NAME>,
    //      ep_num:<EP_NUM>,
    //      status:<STATUS>
    //      episode_date:<DATE>
    //  },
    //  ...]

    // Constants for data serialization
    private static final String DATA_FIELD_NAME = "media_name";
    private static final String DATA_FIELD_EPNUM = "ep_num";
    private static final String DATA_FIELD_STATUS = "status";
    private static final String DATA_FIELD_EPDATE = "episode_date";

    private final IonSystem ionSys;
    private final IonReaderBuilder readerBuilder;
    private final IonWriterBuilder writerBuilder;

    /**
     * Constructor
     *
     * @param writeBinary whether to write data in binary or text.
     */
    public IonEpisodeDataSerializer(boolean writeBinary)
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
    public boolean readData(InputStream is, List<EpisodeData> itemList)
    {
        if (is == null || itemList == null)
        {
            return false;
        }

        itemList.clear();

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
                String mediaName = ((IonText) val.get(DATA_FIELD_NAME)).stringValue();
                int epNum = ((IonInt) val.get(DATA_FIELD_EPNUM)).intValue();
                int statusVal = ((IonInt) val.get(DATA_FIELD_STATUS)).intValue();
                MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                long epDate = ((IonInt) val.get(DATA_FIELD_EPDATE)).longValue();

                EpisodeData ed = new EpisodeData(mediaName, epNum, epDate, status);

                if (VERBOSE)
                {
                    Log.i("readData", "imported " + ed);
                }

                itemList.add(ed);
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

        return !itemList.isEmpty();
    }

    @Override
    public boolean writeData(OutputStream os, List<EpisodeData> itemList)
    {
        if (os == null || itemList == null || itemList.isEmpty())
        {
            return false;
        }

        IonList backupData = ionSys.newEmptyList();

        for (EpisodeData ed : itemList)
        {
            IonStruct episode = ionSys.newNullStruct();
            episode.put(DATA_FIELD_NAME).newString(ed.getMediaName());
            episode.put(DATA_FIELD_EPNUM).newInt(ed.getEpNum());
            episode.put(DATA_FIELD_STATUS).newInt(ed.getMediaStatus().value);
            episode.put(DATA_FIELD_EPDATE).newInt(ed.getEpDate());

            backupData.add(episode);
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
