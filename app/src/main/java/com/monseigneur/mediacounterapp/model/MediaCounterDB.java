package com.monseigneur.mediacounterapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.monseigneur.mediacounterapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import software.amazon.ion.IonInt;
import software.amazon.ion.IonList;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonText;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;
import software.amazon.ion.system.IonSystemBuilder;

/**
 * Created by Milan on 6/19/2016.
 */
public class MediaCounterDB extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "media_counter_db";

    // Have 2 dbs:
    // Titles: tid, title, status, added date
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

    private static final int UNKNOWN_MEDIA = -1;
    private static final long UNKNOWN_DATE = 0;

    // Constants for data import / export
    private static final String DATA_FIELD_TITLE = "title";
    private static final String DATA_FIELD_STATUS = "status";
    private static final String DATA_FIELD_ADDED = "added_date";
    private static final String DATA_FIELD_EPISODES = "episodes";

    private static final String TAG = "MediaCounterDB";

    private static final String FILENAME_PREFIX = "media_counter_backup";
    private static final String FILENAME_EXTENSION = ".txt";

    private final IonSystem ionSys;

    public MediaCounterDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

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

    /**
     * Adds a new Media to the database
     *
     * @param md the MediaData to add
     * @return true if successfully added
     */
    public boolean addMedia(MediaData md)
    {
        if (md == null)
        {
            Log.i("addMedia", "MediaData to add is null!");
            return false;
        }

        if (getIdForMedia(md.getMediaName()) != UNKNOWN_MEDIA)
        {
            Log.e("addMedia", "media already exists!");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, md.getMediaName());
        values.put(KEY_STATUS, md.getStatus().value);
        values.put(KEY_ADDED_DATE, md.getAddedDate());

        db.insert(TABLE_TITLES, null, values);

        db.close();

        return true;
    }

    /**
     * Adds a new Episode to an existing Media
     *
     * @param mediaName name of the Media
     * @param date      date the Episode was completed
     */
    public void addEpisode(String mediaName, long date)
    {
        int epNum = getNumEpisodes(mediaName) + 1;
        addEpisode(mediaName, epNum, date);
    }

    /**
     * Adds a new Episode to an existing Media
     *
     * @param mediaName name of the Media to add to
     * @param num       number of the currently added Episode
     * @param date      date the Episode was completed
     */
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

    /**
     * Sets the status of a particular Media
     *
     * @param mediaName name of the Media to set the status of
     * @param status    new status value to set
     */
    public void setStatus(String mediaName, MediaCounterStatus status)
    {
        Log.i("setStatus", "setting status for [" + mediaName + "] to " + status);
        int tid = getIdForMedia(mediaName);

        if (tid == UNKNOWN_MEDIA)
        {
            Log.e("setStatus", "media does not exist");
            return;
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

    /**
     * Removes a Media from the database
     *
     * @param mediaName name of the Media to remove
     */
    private void deleteMedia(String mediaName)
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
        int rowsDeleted = db.delete(TABLE_TITLES, KEY_TID + SQL_PARAMETER, new String[]{String.valueOf(tid)});

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

    /**
     * Removes the latest Episode for a Media
     *
     * @param mediaName the name of the Media to remove from
     */
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
                    new String[]{String.valueOf(tid), String.valueOf(epDates.size())});

            if (rowsDeleted == 1)
            {
                db.setTransactionSuccessful();
            }
            else
            {
                Log.e("deleteEpisode", "Deleting episode would remove more than 1 row! rows deleted = " + rowsDeleted);
            }

            db.endTransaction();
        }
        else
        {
            // If we didn't have any episodes, just delete the media counter itself.
            db.beginTransaction();
            int rowsDeleted = db.delete(TABLE_TITLES, KEY_TID + SQL_PARAMETER, new String[]{String.valueOf(tid)});

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

    /**
     * Gets the number of Episodes seen for a particular Media
     *
     * @param mediaName the name of the Media
     * @return the number of Episodes seen for the given Media
     */
    private int getNumEpisodes(String mediaName)
    {
        List<Long> epDates = getEpDates(mediaName);

        return epDates.size();
    }

    /**
     * Gets the dates of all Episodes for a particular Media
     *
     * @param mediaName the name of the Media
     * @return A List of the Episode dates
     */
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

    /**
     * Gets all Medias for a given set of statuses
     *
     * @param statuses all statuses to search for
     * @return all Medias that match a given set of statuses
     */
    private List<MediaData> getMediaCounters(EnumSet<MediaCounterStatus> statuses)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        List<MediaData> mdList = new ArrayList<>();

        if (statuses.isEmpty())
        {
            return mdList;
        }

        StringBuilder selection = new StringBuilder();
        String[] parameters = new String[statuses.size()];
        int i = 0;

        for (MediaCounterStatus mcs : statuses)
        {
            parameters[i] = "" + mcs.value;
            selection.append(KEY_STATUS + SQL_PARAMETER);

            if (i < (statuses.size() - 1))
            {
                selection.append(SQL_OR);
            }

            i++;
        }
        Log.i(TAG, "getMediaCounters(enum): selection [" + selection + "] params " + Arrays.toString(parameters));

        Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, selection.toString(), parameters, null, null, null, null);

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

        Collections.sort(mdList, MediaData.BY_LAST_EPISODE);

        return mdList;
    }

    /**
     * Gets all Medias
     *
     * @return a List of all Medias
     */
    public List<MediaData> getMediaCounters()
    {
        EnumSet<MediaCounterStatus> statuses = EnumSet.allOf(MediaCounterStatus.class);

        return getMediaCounters(statuses);
    }

    /**
     * Chooses a random Media that is not Complete or Dropped
     *
     * @return a random Media
     */
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

    /**
     * Gets Episodes for all Medias
     *
     * @return a List of all Episodes
     */
    public List<EpisodeData> getEpisodeData()
    {
        List<EpisodeData> data = new ArrayList<>();

        // Get all media names
        SQLiteDatabase db = this.getReadableDatabase();

        Map<String, MediaCounterStatus> mediaStatus = new HashMap<>();

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
                    mediaStatus.put(mediaName, status);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        // For each media, add its episodes to the list.
        for (String name : mediaStatus.keySet())
        {
            List<Long> epDates = getEpDates(name);
            MediaCounterStatus status = MediaCounterStatus.NEW;

            for (int i = 0; i < epDates.size(); i++)
            {
                // Set the status of the last Episode according to the Media
                if (i == (epDates.size() - 1))
                {
                    status = mediaStatus.get(name);
                }

                Log.i("getEpData", "[" + name + "]->" + (i + 1) + " " + status);
                EpisodeData ed = new EpisodeData(name, i + 1, epDates.get(i), status);
                data.add(ed);
            }
        }

        Collections.sort(data);
        Collections.reverse(data);

        return data;
    }

    /**
     * Gets the Media ID for a given Media
     *
     * @param mediaName name of the Media
     * @return ID for the Media
     */
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

    /**
     * Gets the current date
     *
     * @return the current date, in milliseconds
     */
    public static long getCurrentDate()
    {
        Calendar rightNow = Calendar.getInstance();

        return rightNow.getTimeInMillis();
    }

    /**
     * Converts a millisecond time into a date string
     *
     * @param c   context
     * @param val millisecond time
     * @return the date string for the given time
     */
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

            return String.format(Locale.US, "%d-%d-%d %02d:%02d", month, dayOfMonth, year, hour, minute);
        }
    }

    /**
     * Generates a filename timestamp
     *
     * @return timestamp for a filename
     */
    private String fileTimeStamp() {
        long time = getCurrentDate();

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);

        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;       // January is 0?
        int year = date.get(Calendar.YEAR);
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        int second = date.get(Calendar.SECOND);

        Log.i("fileTimestamp", "DATE STRING: Y=" + year + " M=" + month + " D=" + dayOfMonth + " H=" + hour + " M=" + minute + " S=" + second);

        return String.format(Locale.US, "%d%02d%02d_%02d%02d%02d", year, month, dayOfMonth, hour, minute, second);
    }

    /**
     * Backs up the database
     *
     * @return true if successfully backed up, false otherwise
     */
    public boolean backupData()
    {
        File base = getBackupDirectory();
        if (base != null)
        {
            String timeStamp = fileTimeStamp();

            String fileName = FILENAME_PREFIX + "_" + timeStamp + FILENAME_EXTENSION;

            File backupFile = new File(base, fileName);
            return writeData(backupFile);
        }

        return false;
    }

    /**
     * Imports data into the database
     *
     * @return true if successfully imported, false otherwise
     */
    public boolean importData()
    {
        File base = getBackupDirectory();

        if (base != null)
        {
            File backupFile = new File(base, "media_counter_backup_TEMP.txt");

            // First backup the data, in case something goes wrong.
            if (!writeData(backupFile))
            {
                Log.e("importData", "failed to write backup data!");
                return false;
            }

            File importFile = new File(base, "media_counter_import.txt");

            return readData(importFile);
        }

        return false;
    }

    /**
     * Gets the storage directory for the backup files
     *
     * @return The backup storage directory
     */
    private File getBackupDirectory()
    {
        try
        {
            File backupDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MediaCounterData");
            Log.i("getBackupDirectory", "base pub dir = [" + backupDir + "]");
            if (!backupDir.exists())
            {
                boolean res = backupDir.mkdirs();
                Log.i("getBackupDirectory", "mkdir res " + res);
            }

            return backupDir;
        }
        catch (Exception e)
        {
            Log.i("getBackupDirectory", "error");
        }

        return null;
    }

    /**
     * Reads from the import file and populates the database
     *
     * @param file import file
     * @return true if all data was successfully imported, false otherwise
     */
    private boolean readData(File file)
    {
        try
        {
            if (file.exists())
            {
                FileInputStream fis = new FileInputStream(file);

                Iterator<IonValue> iter = ionSys.iterate(fis);

                IonList elements = (IonList) iter.next();

                for (IonValue iv : elements)
                {
                    IonStruct val = (IonStruct) iv;
                    String mediaName = ((IonText) val.get(DATA_FIELD_TITLE)).stringValue();
                    int statusVal = ((IonInt) val.get(DATA_FIELD_STATUS)).intValue();
                    MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                    long addedDate = ((IonInt) val.get(DATA_FIELD_ADDED)).longValue();
                    Log.i("import", "[" + mediaName + "] [" + status + "] [" + addedDate + "]");

                    // Remove the original one. Probably want to change to some kind of merging scheme.
                    deleteMedia(mediaName);

                    MediaData md = new MediaData(mediaName, status, addedDate);

                    if (!addMedia(md))
                    {
                        Log.e("readData", "Failed to add MediaData!");
                    }

                    IonList episodes = (IonList) val.get(DATA_FIELD_EPISODES);
                    int i = 1;
                    for (IonValue epIv : episodes)
                    {
                        long epDate = ((IonInt) epIv).longValue();
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
            Log.e("readData", "caught exception " + e);
            return false;
        }

        return true;
    }

    /**
     * Writes the contents of the database to the backup file
     *
     * @param file the backup file
     * @return true if all data was successfully written, false otherwise
     */
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
            Log.e("writeData", "caught exception when building backup data" + e);
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
            Log.e("writeData", "caught exception when writing backup date " + e);
            return false;
        }

        return true;
    }
}