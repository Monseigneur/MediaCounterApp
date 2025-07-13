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
import java.util.stream.Collectors;

public class IonMediaDataSerializer implements IDataSerializer<MediaData>
{
    public static boolean VERBOSE = true;

    // Ion structure:
    // [{
    //      title:<NAME>,
    //      status:<STATUS>,
    //      added_date:<ADDED_DATE>
    //      episodes:[<EP_1_DATE>, <EP_2_DATE>, ...]
    //  },
    //  ...]

    // Constants for data serialization
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
    public IonMediaDataSerializer(boolean writeBinary)
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
    public boolean deserialize(InputStream is, List<MediaData> itemList)
    {
        if (is == null || itemList == null)
        {
            return false;
        }

        itemList.clear();

        try (IonReader reader = readerBuilder.build(is))
        {
            Iterator<IonValue> iter = ionSys.iterate(reader);

            if (!iter.hasNext())
            {
                Log.i("deserialize", "top level iterator doesn't have anything");

                return false;
            }

            IonList elements = (IonList) iter.next();

            if (elements == null)
            {
                if (VERBOSE)
                {
                    Log.i("deserialize", "No data to read");
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

                IonList episodesDates = (IonList) val.get(DATA_FIELD_EPISODES);

                List<Long> episodes = episodesDates.stream().map(epIv -> ((IonInt) epIv).longValue()).collect(Collectors.toList());

                MediaData media = new MediaData(mediaName, status, addedDate, episodes);

                if (VERBOSE)
                {
                    Log.i("deserialize", "imported " + media);
                }

                itemList.add(media);
            }
        }
        catch (Exception e)
        {
            if (VERBOSE)
            {
                Log.e("deserialize", "caught exception " + e);
            }

            return false;
        }

        return !itemList.isEmpty();
    }

    @Override
    public boolean serialize(OutputStream os, List<MediaData> itemList)
    {
        if (os == null || itemList == null || itemList.isEmpty())
        {
            return false;
        }

        IonList backupData = ionSys.newEmptyList();

        for (MediaData md : itemList)
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
                Log.e("serialize", "caught exception while writing backup data " + e);
            }

            return false;
        }

        return true;
    }
}
