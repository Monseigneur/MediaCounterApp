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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IonEpisodeDataSerializer implements IDataSerializer<EpisodeData>
{
    public static boolean VERBOSE = true;

    // Ion structure v2:
    // {
    //      names:[<NAME_1>, <NAME_2>, ...],
    //      data:[{
    //                media_name_idx:<NAME_IDX>,
    //                ep_num:<EP_NUM>,
    //                status:<STATUS>
    //                episode_date:<DATE>
    //            },
    //            ...]

    // Constants for data serialization
    private static final String DATA_FIELD_NAME_LIST = "names";
    private static final String DATA_FIELD_DATA_LIST = "data";
    private static final String DATA_FIELD_NAME_IDX = "media_name_idx";
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
    public boolean deserialize(InputStream is, List<EpisodeData> itemList)
    {
        if (is == null || itemList == null)
        {
            return false;
        }

        itemList.clear();

        try (IonReader reader = readerBuilder.build(is))
        {
            Iterator<IonValue> iter = ionSys.iterate(reader);

            IonStruct data = (IonStruct) iter.next();

            if (data == null)
            {
                if (VERBOSE)
                {
                    Log.i("deserialize", "No data to read");
                }

                return false;
            }

            IonList nameList = (IonList) data.get(DATA_FIELD_NAME_LIST);
            IonList dataList = (IonList) data.get(DATA_FIELD_DATA_LIST);

            if (nameList == null || dataList == null)
            {
                return false;
            }

            HashMap<Integer, String> reverseNameMap = new HashMap<>();

            for (int i = 0; i < nameList.size(); i++)
            {
                reverseNameMap.put(i, ((IonText) nameList.get(i)).stringValue());
            }

            for (IonValue iv : dataList)
            {
                IonStruct val = (IonStruct) iv;
                int nameIdx = ((IonInt) val.get(DATA_FIELD_NAME_IDX)).intValue();
                String mediaName = reverseNameMap.get(nameIdx);
                int epNum = ((IonInt) val.get(DATA_FIELD_EPNUM)).intValue();
                int statusVal = ((IonInt) val.get(DATA_FIELD_STATUS)).intValue();
                MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                long epDate = ((IonInt) val.get(DATA_FIELD_EPDATE)).longValue();

                EpisodeData ed = new EpisodeData(mediaName, epNum, epDate, status);

                if (VERBOSE)
                {
                    Log.i("deserialize", "imported " + ed);
                }

                itemList.add(ed);
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
    public boolean serialize(OutputStream os, List<EpisodeData> itemList)
    {
        if (os == null || itemList == null || itemList.isEmpty())
        {
            return false;
        }

        Map<String, Integer> nameMap = new HashMap<>();

        IonList nameList = ionSys.newEmptyList();
        IonList dataList = ionSys.newEmptyList();

        for (EpisodeData ed : itemList)
        {
            Integer nameIdx;
            if (!nameMap.containsKey(ed.getMediaName()))
            {
                nameIdx = nameList.size();

                nameList.add(ionSys.newString(ed.getMediaName()));
                nameMap.put(ed.getMediaName(), nameIdx);
            }
            else
            {
                nameIdx = nameMap.get(ed.getMediaName());
            }

            IonStruct episode = ionSys.newNullStruct();
            episode.put(DATA_FIELD_NAME_IDX).newInt(nameIdx);
            episode.put(DATA_FIELD_EPNUM).newInt(ed.getEpNum());
            episode.put(DATA_FIELD_STATUS).newInt(ed.getMediaStatus().value);
            episode.put(DATA_FIELD_EPDATE).newInt(ed.getEpDate());

            dataList.add(episode);
        }

        IonStruct data = ionSys.newNullStruct();

        data.put(DATA_FIELD_NAME_LIST, nameList);
        data.put(DATA_FIELD_DATA_LIST, dataList);

        try (IonWriter writer = writerBuilder.build(os))
        {
            data.writeTo(writer);
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
