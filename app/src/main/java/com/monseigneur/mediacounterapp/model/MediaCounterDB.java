package com.monseigneur.mediacounterapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.monseigneur.mediacounterapp.R;
import software.amazon.ion.*;
import software.amazon.ion.system.IonSystemBuilder;

import java.io.*;
import java.util.*;

/**
 * Created by Milan on 6/19/2016.
 */
public class MediaCounterDB extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "media_counter_db";

    // Have 2 dbs:
    // Titles: tid, title, status
    // Episodes: tid, epid, date
    private static final String TABLE_TITLES = "titles";

    private static final String KEY_TID = "tid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_STATUS = "status";
    private static final String KEY_ADDED_DATE = "added_date";
    private static final String[] TITLES_COLUMNS = {KEY_TID, KEY_TITLE, KEY_STATUS, KEY_ADDED_DATE};

    private static final String TABLE_EPISODES = "episodes";

    private static final String KEY_EPNUM = "epNum";
    private static final String KEY_DATE = "date";
    private static final String[] EPISODES_COLUMNS = {KEY_TID, KEY_EPNUM, KEY_DATE};

    private static final String SQL_PARAMETER = " = ?";
    private static final String SQL_AND = " and ";
    private static final String SQL_OR = " or ";

    public static final int UNKNOWN_MEDIA = -1;
    public static final long UNKNOWN_DATE = 0;

    // Constants for data import / export
    private static final String DATA_FIELD_TITLE = "title";
    private static final String DATA_FIELD_STATUS = "status";
    private static final String DATA_FIELD_ADDED = "added_date";
    private static final String DATA_FIELD_EPISODES = "episodes";

    private static final String TAG = "MediaCounterDB";

    private Context c;
    private IonSystem ionSys;

    public MediaCounterDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c = context;

        ionSys = IonSystemBuilder.standard().build();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTitleDB = "CREATE TABLE titles ( " +
                KEY_TID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TITLE + " TEXT, " +
                KEY_STATUS + " INTEGER, " +
                KEY_ADDED_DATE + " INTEGER )";

        db.execSQL(createTitleDB);

        String createEpisodesDB = "CREATE TABLE episodes ( " +
                KEY_TID + " INTEGER, " +
                KEY_EPNUM + " INTEGER, " +
                KEY_DATE + " INTEGER )";

        db.execSQL(createEpisodesDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TITLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EPISODES);

        this.onCreate(db);
    }

    // Methods we need:
    // - Add a new media counter
    // - Add an episode date
    // - Get all media counters
    // - Get all episode dates for a media counter
    //
    // Possible:
    // - Delete an episode
    // - Delete a media counter
    // - Change a media counter's state

    public boolean addMedia(String mediaName)
    {
        return addMedia(mediaName, MediaCounterStatus.NEW, getCurrentDate());
    }

    private boolean addMedia(String mediaName, MediaCounterStatus status, long date)
    {
        if (getIdForMedia(mediaName) != UNKNOWN_MEDIA)
        {
            Log.e("addMedia", "media already exists!");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, mediaName);
        values.put(KEY_STATUS, status.value);
        values.put(KEY_ADDED_DATE, date);

        db.insert(TABLE_TITLES, null, values);

        db.close();

        return true;
    }

    public void addEpisode(String mediaName)
    {
        int epNum = getNumEpisodes(mediaName) + 1;
        addEpisode(mediaName, epNum, getCurrentDate());
    }

    private void addEpisode(String mediaName, int num, long date)
    {
        int tid = getIdForMedia(mediaName);

        if (tid == UNKNOWN_MEDIA)
        {
            Log.e("addEpisode", "media does not exist");
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TID, tid);
        values.put(KEY_EPNUM, num);
        values.put(KEY_DATE, date);

        Log.i("addEpisode", values.toString());

        db.insert(TABLE_EPISODES, null, values);

        db.close();
    }

    public void setStatus(String mediaName, MediaCounterStatus status)
    {
        Log.i("setStatus", "setting status for [" + mediaName + "] to " + status);
        int tid = getIdForMedia(mediaName);

        if (tid == UNKNOWN_MEDIA)
        {
            Log.e("setStatus", "media does not exist");
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status.value);

        db.beginTransaction();
        int rowsUpdated = db.update(TABLE_TITLES, values, KEY_TID + SQL_PARAMETER, new String[]{String.valueOf(tid)});

        Log.i("rows", "rows = " + rowsUpdated);
        if (rowsUpdated == 1)
        {
            db.setTransactionSuccessful();
        }

        db.endTransaction();
    }

    public void deleteMedia(String mediaName)
    {
        int tid = getIdForMedia(mediaName);

        if (tid == UNKNOWN_MEDIA)
        {
            Log.e("deleteMedia", "media does not exist");
            return;
        }

        int numEpisodes = getNumEpisodes(mediaName);

        for (int i = 0; i < numEpisodes; i++)
        {
            deleteEpisode(mediaName);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        int rowsDeleted = db.delete(TABLE_TITLES, KEY_TID + SQL_PARAMETER, new String[] { String.valueOf(tid) });

        if (rowsDeleted == 1)
        {
            db.setTransactionSuccessful();
        }
        else
        {
            Log.e("deleteMedia", "Deleting media counter removed more than 1 row! rows deleted = " + rowsDeleted);
        }

        db.endTransaction();

        db.close();
    }

    public void deleteEpisode(String mediaName)
    {
        int tid = getIdForMedia(mediaName);

        if (tid == UNKNOWN_MEDIA)
        {
            Log.e("deleteEpisode", "media does not exist");
            return;
        }

        List<Long> epDates = getEpDates(mediaName);

        SQLiteDatabase db = this.getWritableDatabase();

        if (!epDates.isEmpty())
        {
            db.beginTransaction();
            int rowsDeleted = db.delete(TABLE_EPISODES, KEY_TID + SQL_PARAMETER + SQL_AND + KEY_EPNUM + SQL_PARAMETER,
                                        new String[] { String.valueOf(tid), String.valueOf(epDates.size()) });

            if (rowsDeleted == 1)
            {
                db.setTransactionSuccessful();
            }
            else
            {
                Log.e("deleteEpisode", "Deleting episode removed more than 1 row! rows deleted = " + rowsDeleted);
            }

            db.endTransaction();
        }
        else
        {
            // If we didn't have any episodes, just delete the media counter itself.
            db.beginTransaction();
            int rowsDeleted = db.delete(TABLE_TITLES, KEY_TID + SQL_PARAMETER, new String[] { String.valueOf(tid) });

            if (rowsDeleted == 1)
            {
                db.setTransactionSuccessful();
            }
            else
            {
                Log.e("deleteEpisode", "Deleting media counter removed more than 1 row! rows deleted = " + rowsDeleted);
            }

            db.endTransaction();
        }
        db.close();
    }

    public int getNumEpisodes(String mediaName)
    {
        List<Long> epDates = getEpDates(mediaName);

        return epDates.size();
    }

    public List<Long> getEpDates(String mediaName)
    {
        int tid = getIdForMedia(mediaName);

        if (tid == UNKNOWN_MEDIA)
        {
            Log.e("getEpDates", "media does not exist");
        }

        SQLiteDatabase db = this.getReadableDatabase();

        List<Long> epDates = new ArrayList<>();

        Cursor cursor = db.query(TABLE_EPISODES, EPISODES_COLUMNS, KEY_TID + SQL_PARAMETER, new String[]{String.valueOf(tid)},
                                 null, null, null, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    epDates.add(cursor.getLong(cursor.getColumnIndex(KEY_DATE)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return epDates;
    }

    public List<MediaData> getMediaCounters(EnumSet<MediaCounterStatus> statuses)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        List<MediaData> mdList = new ArrayList<>();

        if (statuses.isEmpty())
        {
            return mdList;
        }

        String selection = "";
        String[] parameters = new String[statuses.size()];
        int i = 0;

        for (MediaCounterStatus mcs : statuses)
        {
            parameters[i] = "" + mcs.value;
            selection += KEY_STATUS + SQL_PARAMETER;

            if (i < (statuses.size() - 1))
            {
                selection += SQL_OR;
            }

            i++;
        }
        Log.i(TAG, "getMediaCounters(enum): selection [" + selection + "] params " + Arrays.toString(parameters));

        Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, selection, parameters, null, null, null, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    String mediaName = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                    int statusVal = cursor.getInt(cursor.getColumnIndex(KEY_STATUS));
                    MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                    List<Long> epDates = getEpDates(mediaName);

                    long addedDate = cursor.getLong(cursor.getColumnIndex(KEY_ADDED_DATE));
                    Log.i("getMediaCounters", "name [" + mediaName + "] -> S " + status + ", EP# " + epDates.size() + ", AD " + addedDate);

                    MediaData md = new MediaData(mediaName, status, addedDate, epDates);
                    mdList.add(md);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        Collections.sort(mdList);

        return mdList;
    }

    public List<MediaData> getMediaCounters()
    {
        EnumSet<MediaCounterStatus> statuses = EnumSet.allOf(MediaCounterStatus.class);

        return getMediaCounters(statuses);
    }

    public String getRandomMedia()
    {
        List<MediaData> mdList = getMediaCounters();

        List<MediaData> incomplete = new ArrayList<>();

        for (MediaData md : mdList)
        {
            MediaCounterStatus status = md.getStatus();
            if ((status != MediaCounterStatus.COMPLETE) && (status != MediaCounterStatus.DROPPED))
            {
                incomplete.add(md);
            }
        }

        String randomMediaName;
        if (!incomplete.isEmpty())
        {
            Random rand = new Random();
            int index = rand.nextInt(incomplete.size());

            randomMediaName = incomplete.get(index).getMediaName();
        }
        else
        {
            randomMediaName = "No media counters to select from!";
        }

        return randomMediaName;
    }

    public List<EpisodeData> getEpisodeData()
    {
        List<EpisodeData> data = new ArrayList<>();

        // Get all media names
        SQLiteDatabase db = this.getReadableDatabase();

        Map<String, MediaCounterStatus> names = new HashMap<>();

        Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, null, null, null, null, null, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    String mediaName = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                    int statusVal = cursor.getInt(cursor.getColumnIndex(KEY_STATUS));
                    MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                    names.put(mediaName, status);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        // For each media, add its episodes to the list.
        for (String name : names.keySet())
        {
            List<Long> epDates = getEpDates(name);
            MediaCounterStatus status = MediaCounterStatus.NEW;

            for (int i = 0; i < epDates.size(); i++)
            {
                // Set the status of the last Episode according to the Media
                if (i == (epDates.size() - 1))
                {
                    status = names.get(name);
                }

                EpisodeData ed = new EpisodeData(name, i + 1, epDates.get(i), status);
                data.add(ed);
            }
        }

        Collections.sort(data);
        Collections.reverse(data);

        return data;
    }

    private int getIdForMedia(String mediaName)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, KEY_TITLE + SQL_PARAMETER, new String[]{mediaName},
                                 null, null, null, null);

        if (cursor == null)
        {
            return UNKNOWN_MEDIA;
        }

        int result;

        if (cursor.getCount() == 0)
        {
            result = UNKNOWN_MEDIA;
        }
        else
        {
            if (cursor.getCount() > 1)
            {
                // We have a problem
                Log.e("getIdForMedia", "More than one media with the name [" + mediaName + "]!");
            }

            cursor.moveToFirst();

            result = cursor.getInt(cursor.getColumnIndex(KEY_TID));

            cursor.close();
        }

        return result;
    }

    private long getCurrentDate()
    {
        Calendar rightNow = Calendar.getInstance();

        return rightNow.getTimeInMillis();
    }

    public static String dateString(Context c, long val)
    {
        if (val == MediaCounterDB.UNKNOWN_DATE)
        {
            return c.getString(R.string.unknown_date);
        }
        else
        {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(val);

            int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH) + 1;       // January is 0?
            int year = date.get(Calendar.YEAR);
            int hour = date.get(Calendar.HOUR_OF_DAY);
            int minute = date.get(Calendar.MINUTE);

            Log.i("dateString", "DATE STRING: M=" + month + " D=" + dayOfMonth + " Y=" + year + " H=" + hour + " M=" + minute);

            return String.format("%d-%d-%d %02d:%02d", month, dayOfMonth, year, hour, minute);
        }
    }

    public boolean backupData()
    {
        File base = getBackupDirectory();
        if (base != null)
        {
            File backupFile = new File(base, "media_counter_backup.txt");
            return writeData(backupFile);
        }

        return false;
    }

    public boolean importData()
    {
        File base = getBackupDirectory();

        if (base != null)
        {
            File backupFile = new File(base, "media_counter_backup_TEMP.txt");

            // First backup the data, in case something goes wrong.
            if (!writeData(backupFile))
            {
                return false;
            }

            File importFile = new File(base, "media_counter_import.txt");

            return readData(importFile);
        }

        return false;
    }

    private File getBackupDirectory()
    {
        try
        {
            File base = new File(System.getenv("EXTERNAL_STORAGE"));
            Log.i("getBackupDirectory", "base dir = [" + base + "}");
            File backupDir = new File(base, "MediaCounterBackup");
            Log.i("getBackupDirector", "dir = [" + backupDir + "]");

            if (!backupDir.exists())
            {
                boolean result = backupDir.mkdirs();
                Log.i("getBackupDirectory", "create dir returned " + result);
            }

            return backupDir;
        }
        catch (Exception e)
        {
            Log.i("getBackupDirectory", "error");
        }

        return null;
    }

    private boolean readData(File file)
    {
        try
        {
            if (file.exists())
            {
                FileInputStream fis = new FileInputStream(file);

                Iterator<IonValue> iter = ionSys.iterate(fis);

                IonList elements = (IonList)iter.next();

                for (IonValue iv : elements)
                {
                    IonStruct val = (IonStruct)iv;
                    String mediaName = ((IonText)val.get(DATA_FIELD_TITLE)).stringValue();
                    int statusVal = ((IonInt)val.get(DATA_FIELD_STATUS)).intValue();
                    MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                    long addedDate = ((IonInt)val.get(DATA_FIELD_ADDED)).longValue();
                    Log.i("import", "[" + mediaName + "] [" + status + "] [" + addedDate + "]");

                    // Remove the original one. Probably want to change to some kind of merging scheme.
                    deleteMedia(mediaName);

                    addMedia(mediaName, status, addedDate);

                    IonList episodes = (IonList)val.get(DATA_FIELD_EPISODES);
                    int i = 1;
                    for (IonValue epIv : episodes)
                    {
                        Long epDate = ((IonInt)epIv).longValue();
                        addEpisode(mediaName, i, epDate);
                        i++;
                        Log.i("import", "\t" + epDate);
                    }
                }

                fis.close();
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    private boolean writeData(File file)
    {
        List<MediaData> mdList = getMediaCounters();

        IonList backupData = ionSys.newEmptyList();

        try
        {
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
        }
        catch (Exception e)
        {
            return false;
        }

        try
        {
            Log.i("writeData", backupData.toPrettyString());

            if (file != null)
            {
                IonWriter writer = ionSys.newTextWriter(new FileOutputStream(file));

                backupData.writeTo(writer);

                writer.close();
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }
}