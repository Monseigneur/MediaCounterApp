package com.example.MediaCounterApp.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Milan on 6/19/2016.
 */
public class MediaCounterDB extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "media_counter_db";

    // Have 2 dbs:
    // Titles: tid, title, complete_status
    // Episodes: tid, epid, date
    private static final String TABLE_TITLES = "titles";

    private static final String KEY_TID = "tid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_COMPLETE = "complete_status";
    private static final String[] TITLES_COLUMNS = {KEY_TID, KEY_TITLE, KEY_COMPLETE};

    private static final String TABLE_EPISODES = "episodes";

    private static final String KEY_EPNUM = "epNum";
    private static final String KEY_DATE = "date";
    private static final String[] EPISODES_COLUMNS = {KEY_TID, KEY_EPNUM, KEY_DATE};

    private static final String SQL_PARAMETER = " = ?";
    private static final String SQL_AND = " and ";

    public static final String UNKNOWN_DATE = "UNKNOWN";

    public MediaCounterDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTitleDB = "CREATE TABLE titles ( " +
                KEY_TID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TITLE + " TEXT, " +
                KEY_COMPLETE + " TEXT )";

        db.execSQL(createTitleDB);

        String createEpisodesDB = "CREATE TABLE episodes ( " +
                KEY_TID + " INTEGER, " +
                KEY_EPNUM + " INTEGER, " +
                KEY_DATE + " TEXT )";

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
    // - Mark a media counter complete

    public void addMedia(String mediaName)
    {
        if (getIdForMedia(mediaName) != -1)
        {
            Log.e("addMedia", "media already exists!");
            return;
        }
        SQLiteDatabase  db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, mediaName);
        values.put(KEY_COMPLETE, Boolean.FALSE);

        db.insert(TABLE_TITLES, null, values);
    }

    public void addEpisode(String mediaName)
    {
        addEpisode(mediaName, true);
    }

    public void addEpisode(String mediaName, boolean current)
    {
        int tid = getIdForMedia(mediaName);

        if (tid == -1)
        {
            Log.e("addEpisode", "media does not exist");
            return;
        }

        // Hack to count
        int newEpNumber = getNumEpisodes(mediaName) + 1;

        SQLiteDatabase db = this.getWritableDatabase();

        String date;
        if (current)
        {
            date = getCurrentDate();
        }
        else
        {
            date = UNKNOWN_DATE;
        }

        ContentValues values = new ContentValues();
        values.put(KEY_TID, tid);
        values.put(KEY_EPNUM, newEpNumber);
        values.put(KEY_DATE, date);

        Log.i("addEpisode", values.toString());

        db.insert(TABLE_EPISODES, null, values);

        db.close();
    }

    public void deleteMedia(String mediaName)
    {
        int tid = getIdForMedia(mediaName);

        if (tid == -1)
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

        if (tid == -1)
        {
            Log.e("deleteEpisode", "media does not exist");
            return;
        }

        List<String> epDates = getEpDates(mediaName);

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
        List<String> epDates = getEpDates(mediaName);

        return epDates.size();
    }

    public List<String> getEpDates(String mediaName)
    {
        int tid = getIdForMedia(mediaName);

        if (tid == -1)
        {
            Log.e("getEpDates", "media does not exist");
        }

        SQLiteDatabase db = this.getReadableDatabase();

        List<String> epDates = new ArrayList<>();

        Cursor cursor = db.query(TABLE_EPISODES, EPISODES_COLUMNS, KEY_TID + SQL_PARAMETER, new String[]{String.valueOf(tid)},
                                 null, null, null, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    epDates.add(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return epDates;
    }

    public List<MediaData> getMediaCounters()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        List<MediaData> mdList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, null, null, null, null, null, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    String mediaName = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                    boolean completeStatus = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(KEY_COMPLETE)));
                    List<String> epDates = getEpDates(mediaName);

                    MediaData md = new MediaData(mediaName, completeStatus, epDates);
                    mdList.add(md);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return mdList;
    }

    public String getRandomMedia()
    {
        List<MediaData> mdList = getMediaCounters();

        List<MediaData> incomplete = new ArrayList<>();

        for (MediaData md : mdList)
        {
            if (!md.isComplete())
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

        List<String> names = new ArrayList<>();

        Cursor cursor = db.query(TABLE_TITLES, TITLES_COLUMNS, null, null, null, null, null, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    String mediaName = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                    names.add(mediaName);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        // For each media, add its episodes to the list.
        for (String name : names)
        {
            List<String> epDates = getEpDates(name);

            for (int i = 0; i < epDates.size(); i++)
            {
                EpisodeData ed = new EpisodeData(name, i + 1, epDates.get(i));
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
            return -1;
        }

        int result;

        if (cursor.getCount() == 0)
        {
            result = -1;
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

    private String getCurrentDate()
    {
        Calendar rightNow = Calendar.getInstance();

        int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
        int month = rightNow.get(Calendar.MONTH) + 1;       // January is 0?
        int year = rightNow.get(Calendar.YEAR);
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);

        String dateString = String.format("%d-%d-%d %02d:%02d", month, dayOfMonth, year, hour, minute);

        return dateString;
    }
}