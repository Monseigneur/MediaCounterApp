package com.example.MediaCounterApp.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.example.MediaCounterApp.Model.MediaData;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.MediaCounterApp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MediaCounterActivity extends Activity
{
    public static final int NEW_MEDIA_COUNTER_REQUEST = 1;
    public static final String MEDIA_COUNTER_NAME = "media_name";
    private static final String DATA_FILENAME = "media_counter_data";

    private List<MediaData> mdList;
    private MediaCounterAdapter adapter;

    private ListView lv;
    private boolean incLocked;
    private Drawable defaultButtonBg;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        defaultButtonBg = findViewById(R.id.lock_button).getBackground();

        mdList = getData();

        lv = (ListView) findViewById(R.id.media_list);

        adapter = new MediaCounterAdapter(this, R.layout.media_counter_list_entry, mdList);
        lv.setAdapter(adapter);

        incLocked = true;
        setLockState(true);
    }

    @Override
    public void onPause()
    {
        writeData(mdList);
        super.onPause();

    }

    /**
        New view methods
     */
    public void newMediaCounter(View view)
    {
        Log.i("newMediaCounter", "send!");

        Intent intent = new Intent(this, MediaCounterAddActivity.class);

        startActivityForResult(intent, NEW_MEDIA_COUNTER_REQUEST);
    }

    public void chooseRandomMedia(View view)
    {
        Random rand = new Random();
        int index = rand.nextInt(adapter.getCount());

        Toast toast = Toast.makeText(this, mdList.get(index).getMediaName(), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void viewMediaInfo(View view)
    {
        Intent intent = new Intent(this, MediaInfoActivity.class);

        Bundle b = new Bundle();

        LinearLayout ll = (LinearLayout) view.getParent();
        int pos = lv.getPositionForView(ll);

        b.putSerializable(MediaInfoActivity.MEDIA_INFO, mdList.get(pos));
        intent.putExtras(b);

        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == NEW_MEDIA_COUNTER_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                String name = data.getStringExtra(MEDIA_COUNTER_NAME);

                MediaData md = new MediaData(name);

                if (!mdList.contains(md))
                {
                    mdList.add(md);
                }
                System.out.println(data.getStringExtra(MEDIA_COUNTER_NAME));
                Log.i("onActivityResult", data.getStringExtra(MEDIA_COUNTER_NAME));
            }
        }
    }

    public void incMediaCount(View view)
    {
        Log.i("incMediaCount", "start");
        changeCount(view, true);
    }

    public void decMediaCount(View view)
    {
        Log.i("decMediaCount", "start");
        changeCount(view, false);
    }

    /**
     * Helper methods
     */
    public void changeLockState(View view)
    {
        setLockState(!incLocked);
    }

    public void setLockState(boolean lock)
    {
        Button lockButton = (Button) findViewById(R.id.lock_button);

        incLocked = lock;
        if (lock)
        {
            lockButton.setText(R.string.unlock_inc);
            lockButton.setBackground(defaultButtonBg);
        }
        else
        {
            lockButton.setText(R.string.lock_inc);
            lockButton.setBackgroundColor(Color.RED);
        }

        adapter.enableDone(!lock);
    }

    private void changeCount(View view, boolean increment)
    {
        if (!incLocked)
        {
            LinearLayout ll = (LinearLayout) view.getParent();
            int pos = lv.getPositionForView(ll);
            MediaData md = (MediaData) lv.getAdapter().getItem(pos);

            md.adjustCount(increment);

            if (!increment && (md.getCount() < 0))
            {
                adapter.remove(pos);
            }

            adapter.notifyDataSetChanged();
        }
    }

    // File stuff

    private List<MediaData> getData()
    {
        List<MediaData> mediaDataList = new ArrayList<MediaData>();
        Scanner input = null;
        try
        {
            File f = new File(getApplicationContext().getFilesDir(), DATA_FILENAME);
            if (f.exists())
            {
                input = new Scanner(openFileInput(DATA_FILENAME));

                while (input.hasNextLine())
                {
                    String firstLine = input.nextLine();
                    String secondLine = input.nextLine();

                    MediaData md = MediaData.parseString(firstLine, secondLine);
                    Log.i("getData", md.toString());
                    mediaDataList.add(md);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
        }

        return mediaDataList;
    }

    private void writeData(List<MediaData> mdList)
    {
        PrintStream output = null;
        try
        {
            output = new PrintStream(openFileOutput(DATA_FILENAME, Context.MODE_PRIVATE));

            for (MediaData md : mdList)
            {
                Log.i("writeData", md.toString());
                output.println(MediaData.writeOut(md));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (output != null)
            {
                output.close();
            }
        }
    }
}
