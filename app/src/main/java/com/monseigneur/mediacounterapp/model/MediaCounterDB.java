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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Created by Milan on 6/19/2016.
 */
public class MediaCounterDB extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "media_counter_db";

    // Have 2 dbs:
    // titles(tid INTEGER, title TEXT, status INTEGER, added date INTEGER)
    // episodes(tid INTEGER, epNum INTEGER, date INTEGER)
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

    private SQLiteDatabase db;

    public MediaCounterDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        db = getWritableDatabase();
    }

    @Override
    public synchronized void close()
    {
        Log.i("close", "closing database");
        db.close();

        super.close();
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

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, md.getMediaName());
        values.put(KEY_STATUS, md.getStatus().value);
        values.put(KEY_ADDED_DATE, md.getAddedDate());

        db.insert(TABLE_TITLES, null, values);

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

        ContentValues values = new ContentValues();
        values.put(KEY_TID, tid);
        values.put(KEY_EPNUM, num);
        values.put(KEY_DATE, date);

        Log.i("addEpisode", values.toString());

        db.insert(TABLE_EPISODES, null, values);
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
            return new ArrayList<>();
        }

        List<Long> epDates = new ArrayList<>();

        try (Cursor cursor = db.query(TABLE_EPISODES, EPISODES_COLUMNS, KEY_TID + SQL_PARAMETER, new String[]{String.valueOf(tid)},
                null, null, null, null))
        {
            if (cursor == null || !cursor.moveToFirst())
            {
                return epDates;
            }

            do
            {
                epDates.add(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_DATE)));
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            Log.e("getEpDates", "caught exception " + e);

            // Don't return partial data.
            return new ArrayList<>();
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
        if (statuses.isEmpty())
        {
            return new ArrayList<>();
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
        Log.i("getMediaCounters", "getMediaCounters(enum): selection [" + selection + "] params " + Arrays.toString(parameters));

        List<MediaData> mdList = new ArrayList<>();

        try (Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, selection.toString(), parameters, null, null, null, null))
        {
            if (cursor == null || !cursor.moveToFirst())
            {
                return mdList;
            }

            do
            {
                String mediaName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE));
                int statusVal = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS));
                MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                List<Long> epDates = getEpDates(mediaName);

                long addedDate = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ADDED_DATE));
                Log.i("getMediaCounters", "name [" + mediaName + "] -> S " + status + ", EP# " + epDates.size() + ", AD " + addedDate);

                MediaData md = new MediaData(mediaName, status, addedDate, epDates);
                mdList.add(md);
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            Log.e("getMediaCounters", "caught exception " + e);

            // Don't return partial data.
            return new ArrayList<>();
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
        return getMediaCounters(MediaCounterStatus.ALL_STATUSES);
    }

    /**
     * Checks if the database is empty or not
     *
     * @return if there are any MediaData present in the database.
     */
    public boolean isEmpty()
    {
        List<MediaData> mdList = getMediaCounters();

        return mdList.isEmpty();
    }

    /**
     * Chooses a random Media that is not Complete or Dropped
     *
     * @return a random Media, or null if none available
     */
    public String getRandomMedia()
    {
        List<MediaData> watchable = getMediaCounters(MediaCounterStatus.WATCHABLE_STATUSES);

        if (watchable.isEmpty())
        {
            return null;
        }

        Random rand = new Random();
        int index = rand.nextInt(watchable.size());

        return watchable.get(index).getMediaName();
    }

    /**
     * Gets Episodes for all Medias
     *
     * @return a List of all Episodes
     */
    public List<EpisodeData> getEpisodeData()
    {
        List<EpisodeData> data = new ArrayList<>();

        Map<String, MediaCounterStatus> mediaStatus = new HashMap<>();

        try (Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, null, null, null, null, null, null))
        {
            if (cursor == null || !cursor.moveToFirst())
            {
                return data;
            }

            do
            {
                String mediaName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE));
                int statusVal = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS));
                MediaCounterStatus status = MediaCounterStatus.from(statusVal);
                mediaStatus.put(mediaName, status);
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            Log.e("getEpisodeData", "caught exception " + e);

            // Don't return partial data.
            return new ArrayList<>();
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

                EpisodeData ed = new EpisodeData(name, i + 1, epDates.get(i), status);
                data.add(ed);
            }

            Log.i("getEpisodeData", "[" + name + "]->" + epDates.size() + " " + status);
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
        try (Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, KEY_TITLE + SQL_PARAMETER, new String[]{mediaName},
                null, null, null, null))
        {
            if (cursor == null || !cursor.moveToFirst())
            {
                return UNKNOWN_MEDIA;
            }

            if (cursor.getCount() > 1)
            {
                // We have a problem
                Log.e("getIdForMedia", "More than one media with the name [" + mediaName + "]!");
            }

            return cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TID));
        }
        catch (Exception e)
        {
            Log.e("getIdForMedia", "caught exception " + e);

            return UNKNOWN_MEDIA;
        }
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
     * @param val millisecond time
     * @return the date string for the given time
     */
    public static String dateString(long val)
    {
        if (val == MediaCounterDB.UNKNOWN_DATE)
        {
            return "Unknown date";
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
     * Imports data into the database
     *
     * @param importList list of MediaData to import
     * @return true if successful
     */
    public boolean importData(List<MediaData> importList)
    {
        if (importList == null)
        {
            Log.e("importData", "No import data!");
            return false;
        }

        for (MediaData md : importList)
        {
            // Remove the original one. Probably want to change to some kind of merging scheme.
            deleteMedia(md.getMediaName());

            if (!addMedia(md))
            {
                Log.e("readData", "Failed to add MediaData " + md);
            }

            int i = 1;
            for (long epDate : md.getEpDates())
            {
                addEpisode(md.getMediaName(), i, epDate);
                i++;
            }

            Log.i("importData", "Imported " + md);
        }

        return true;
    }
}