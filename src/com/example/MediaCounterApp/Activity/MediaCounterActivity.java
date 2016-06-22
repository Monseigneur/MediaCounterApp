package com.example.MediaCounterApp.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.example.MediaCounterApp.Model.MediaCounterDB;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.MediaCounterApp.Model.MediaData;
import com.example.MediaCounterApp.R;

import java.util.List;
import java.util.Random;

public class MediaCounterActivity extends Activity
{
    public static final int NEW_MEDIA_COUNTER_REQUEST = 1;
    public static final String MEDIA_COUNTER_NAME = "media_name";

    private List<MediaData> mdList;
    private MediaCounterAdapter adapter;

    private MediaCounterDB db;

    private ListView lv;
    private boolean incLocked;
    private Drawable defaultButtonBg;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        defaultButtonBg = findViewById(R.id.lock_button).getBackground();

        db = new MediaCounterDB(this);
        mdList = db.getMediaCounters();
        Log.i("onCreate", "list = " + mdList);

        lv = (ListView) findViewById(R.id.media_list);

        adapter = new MediaCounterAdapter(this, R.layout.media_counter_list_entry, mdList);
        lv.setAdapter(adapter);

        incLocked = true;
        setLockState(true);
    }

    @Override
    public void onPause()
    {
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

        MediaData md = mdList.get(pos);
        md.setEpDates(db.getEpDates(md.getMediaName()));
        b.putSerializable(MediaInfoActivity.MEDIA_INFO, md);
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

                db.addMedia(name);

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

            if (increment)
            {
                db.addEpisode(md.getMediaName());
            }
            else
            {
                db.deleteEpisode(md.getMediaName());
            }

            if (!increment && (md.getCount() < 0))
            {
                adapter.remove(pos);
            }

            adapter.notifyDataSetChanged();
        }
    }
}
