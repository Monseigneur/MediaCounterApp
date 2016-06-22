package com.example.MediaCounterApp.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    public MediaCounterDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTitleDB = "CREATE TABLE titles ( " +
                "tid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "complete_status TEXT )";

        db.execSQL(createTitleDB);

        String createEpisodesDB = "CREATE TABLE episodes ( " +
                "tid INTEGER, " +
                "epNum INTEGER, " +
                "date TEXT )";

        db.execSQL(createEpisodesDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS titles");
        db.execSQL("DROP TABLE IF EXISTS episodes");

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
        int tid = getIdForMedia(mediaName);

        if (tid == -1)
        {
            Log.e("addEpisode", "media does not exist");
            return;
        }

        // Hack to count
        List<String> epDates = getEpDates(mediaName);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TID, tid);
        values.put(KEY_EPNUM, epDates.size() + 1);
        values.put(KEY_DATE, getCurrentDate());

        Log.i("addEpisode", values.toString());

        db.insert(TABLE_EPISODES, null, values);

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
            db.delete(TABLE_EPISODES, KEY_EPNUM + " = ?", new String[] { String.valueOf(epDates.size()) });
        }

        if (epDates.size() == 1)
        {
            db.delete(TABLE_TITLES, KEY_TID + " = ?", new String[] { String.valueOf(tid) });
        }
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

        Cursor cursor = db.query(TABLE_EPISODES,
                EPISODES_COLUMNS,
                " tid = ?",
                new String[]{String.valueOf(tid)},
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst())
        {
            do
            {
                epDates.add(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            } while (cursor.moveToNext());
        }
        return epDates;
    }

    public List<MediaData> getMediaCounters()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        List<MediaData> mdList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_TITLES,
                TITLES_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);

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

        return mdList;
    }

    private int getIdForMedia(String mediaName)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TITLES,
                TITLES_COLUMNS,
                " title = ?",
                new String[]{mediaName},
                null,
                null,
                null,
                null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        if (cursor.getCount() == 0)
        {
            return -1;
        }
        else if (cursor.getCount() > 1)
        {
            // We have a problem
            Log.e("getIdForMedia", "More than one media with the name [" + mediaName + "]!");
        }

        int tid = cursor.getInt(cursor.getColumnIndex(KEY_TID));

        return tid;
    }

    private String getCurrentDate()
    {
        Calendar rightNow = Calendar.getInstance();

        int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
        int month = rightNow.get(Calendar.MONTH) + 1;       // January is 0?
        int year = rightNow.get(Calendar.YEAR);
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);

        String dateString = month + "-" + dayOfMonth + "-" + year + " " + hour + ":" + minute;

        return dateString;
    }
}