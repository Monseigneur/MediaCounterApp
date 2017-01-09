package com.example.MediaCounterApp.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.example.MediaCounterApp.Model.EpisodeData;
import com.example.MediaCounterApp.Model.MediaCounterDB;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.MediaCounterApp.Model.MediaData;
import com.example.MediaCounterApp.R;
import com.example.MediaCounterApp.ViewModel.MediaInfoViewModel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MediaCounterActivity extends Activity
{
    // Activity message identifiers
    public static final int NEW_MEDIA_COUNTER_REQUEST = 1;
    public static final int MEDIA_INFO_STATUS_CHANGE_REQUEST = 2;
    public static final String MEDIA_COUNTER_NAME = "media_name";
    public static final String MEDIA_INFO_COMPLETE_STATUS = "media_info_complete_status";

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
     * New view methods
     */
    public void newMediaCounter(View view)
    {
        Log.i("newMediaCounter", "send!");

        Intent intent = new Intent(this, MediaCounterAddActivity.class);

        startActivityForResult(intent, NEW_MEDIA_COUNTER_REQUEST);
    }

    public void chooseRandomMedia(View view)
    {
        String randomMedia = db.getRandomMedia();
        showToast(randomMedia);
    }

    public void viewMediaInfo(View view)
    {
        Intent intent = new Intent(this, MediaInfoActivity.class);

        Bundle b = new Bundle();

        LinearLayout ll = (LinearLayout) view.getParent();
        int pos = lv.getPositionForView(ll);

        MediaData md = mdList.get(pos);

        String name = md.getMediaName();
        MediaInfoViewModel mivm = new MediaInfoViewModel(name, md.isComplete(), md.getAddedDate(), db.getEpDates(name));

        b.putSerializable(MediaInfoActivity.MEDIA_INFO, mivm);
        intent.putExtras(b);

        startActivityForResult(intent, MEDIA_INFO_STATUS_CHANGE_REQUEST);
    }

    public void showStats(View view)
    {
        Log.i("showStats", "start!");
        List<EpisodeData> epData = db.getEpisodeData();

        Intent intent = new Intent(this, MediaStatsActivity.class);

        Bundle b = new Bundle();

        b.putSerializable(MediaStatsActivity.MEDIA_STATS, (Serializable)epData);
        intent.putExtras(b);

        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("onActivityResult", "requestCode " + requestCode + " resultCode " + resultCode);
        if (resultCode == RESULT_OK)
        {
            String name;
            switch (requestCode)
            {
                case NEW_MEDIA_COUNTER_REQUEST:
                    name = data.getStringExtra(MEDIA_COUNTER_NAME);

                    boolean result = db.addMedia(name);

                    if (result)
                    {
                        MediaData md = new MediaData(name);
                        mdList.add(md);

                        sortData(mdList);
                    }
                    else
                    {
                        // Media already exists, show a toast
                        showToast(getString(R.string.duplicate_media));
                    }
                    System.out.println(name);
                    Log.i("onActivityResult", name);
                    break;
                case MEDIA_INFO_STATUS_CHANGE_REQUEST:
                    boolean newStatus = data.getBooleanExtra(MEDIA_INFO_COMPLETE_STATUS, false);
                    name = data.getStringExtra(MediaCounterActivity.MEDIA_COUNTER_NAME);
                    Log.i("onActivityResult", "media info status change " + newStatus + " for media [" + name + "]");
                    db.setCompleteStatus(name, newStatus ? 1 : 0);

                    // Update the completeStatus in the list.
                    for (int i = 0; i < mdList.size(); i++)
                    {
                        if (mdList.get(i).getMediaName().equals(name))
                        {
                            mdList.get(i).setComplete(newStatus);
                        }
                    }
                    adapter.notifyDataSetChanged();
                default:
                    break;
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

    public void importData(View view)
    {
        if (!incLocked)
        {
            db.importData();
        }
    }

    public void exportData(View view)
    {
        if (!incLocked)
        {
            db.backupData();
        }
    }

    public void setLockState(boolean lock)
    {
        Button lockButton = (Button)findViewById(R.id.lock_button);
        Button importButton = (Button)findViewById(R.id.import_data_button);
        Button exportButton = (Button)findViewById(R.id.export_data_button);

        incLocked = lock;
        if (lock)
        {
            lockButton.setText(R.string.unlock_inc);
            lockButton.setBackground(defaultButtonBg);

            importButton.setVisibility(View.GONE);
            exportButton.setVisibility(View.GONE);
        }
        else
        {
            lockButton.setText(R.string.lock_inc);
            lockButton.setBackgroundColor(Color.RED);

            importButton.setVisibility(View.VISIBLE);
            exportButton.setVisibility(View.VISIBLE);
        }
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

    private void showToast(String text)
    {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void sortData(List<MediaData> mdList)
    {
        Collections.sort(mdList);
    }
}
