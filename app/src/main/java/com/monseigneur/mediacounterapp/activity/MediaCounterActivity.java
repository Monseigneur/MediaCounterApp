package com.monseigneur.mediacounterapp.activity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.core.app.ActivityCompat;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.viewmodel.MediaInfoViewModel;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;

public class MediaCounterActivity extends Activity
{
    // Activity message identifiers
    public static final int NEW_MEDIA_COUNTER_REQUEST = 1;
    public static final int MEDIA_INFO_STATUS_CHANGE_REQUEST = 2;
    public static final String MEDIA_COUNTER_NAME = "media_name";
    public static final String MEDIA_INFO_STATUS = "media_info_status";

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

        verifyStoragePermissions(this);

        defaultButtonBg = findViewById(R.id.lock_button).getBackground();

        db = new MediaCounterDB(this);
        List<MediaData> mdList = db.getMediaCounters();
        Log.i("onCreate", "list = " + mdList);

        lv = (ListView) findViewById(R.id.media_list);

        adapter = new MediaCounterAdapter(this, R.layout.media_counter_list_entry, mdList);
        lv.setAdapter(adapter);

        CheckBox viewToggle = (CheckBox) findViewById(R.id.view_check_box);
        viewToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                EnumSet<MediaCounterStatus> filter;
                if (isChecked)
                {
                    filter = EnumSet.of(MediaCounterStatus.NEW, MediaCounterStatus.ONGOING);
                }
                else
                {
                    filter = EnumSet.allOf(MediaCounterStatus.class);
                }

                adapter.setFilterMask(filter);
            }
        });

        incLocked = true;
        setLockState(true);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    public static void verifyStoragePermissions(Activity act)
    {
        int permission = ActivityCompat.checkSelfPermission(act, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(act, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        permission = ActivityCompat.checkSelfPermission(act, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(act, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    /**
     * New view methods
     */
    public void viewMediaInfo(View view)
    {
        Intent intent = new Intent(this, MediaInfoActivity.class);

        Bundle b = new Bundle();

        LinearLayout ll = (LinearLayout) view.getParent();
        int pos = lv.getPositionForView(ll);

        MediaData md = adapter.getItem(pos);

        String name = md.getMediaName();
        MediaInfoViewModel mivm = new MediaInfoViewModel(name, md.getStatus(), md.getAddedDate(), db.getEpDates(name));

        b.putSerializable(MediaInfoActivity.MEDIA_INFO, mivm);
        intent.putExtras(b);

        startActivityForResult(intent, MEDIA_INFO_STATUS_CHANGE_REQUEST);
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
                        adapter.add(md);
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
                    MediaCounterStatus newStatus = (MediaCounterStatus) data.getSerializableExtra(MEDIA_INFO_STATUS);
                    name = data.getStringExtra(MediaCounterActivity.MEDIA_COUNTER_NAME);
                    Log.i("onActivityResult", "media info status change " + newStatus + " for media [" + name + "]");
                    db.setStatus(name, newStatus);

                    adapter.getItem(name).setStatus(newStatus);
                    adapter.update();
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
    public void buttonOnClick(View view)
    {
        switch (view.getId())
        {
            case R.id.import_data_button:
                if (!incLocked)
                {
                    if (db.importData())
                    {
                        showToast("Import complete");
                    }
                    else
                    {
                        showToast("Import failed");
                    }
                }
                break;
            case R.id.export_data_button:
                if (!incLocked)
                {
                    if (db.backupData())
                    {
                        showToast("Export complete");
                    }
                    else
                    {
                        showToast("Export failed");
                    }

                }
                break;
            case R.id.random_media_button:
                String randomMedia = db.getRandomMedia();
                showToast(randomMedia);
                break;
            case R.id.stats_button:
                Log.i("showStats", "start!");
                List<EpisodeData> epData = db.getEpisodeData();

                Intent statsIntent = new Intent(this, MediaStatsActivity.class);
                Bundle b = new Bundle();

                b.putSerializable(MediaStatsActivity.MEDIA_STATS, (Serializable) epData);
                statsIntent.putExtras(b);

                startActivity(statsIntent);
                break;
            case R.id.lock_button:
                setLockState(!incLocked);
                break;
            case R.id.new_media_button:
                Log.i("newMediaCounter", "send!");
                Intent newMediaIntent = new Intent(this, MediaCounterAddActivity.class);
                startActivityForResult(newMediaIntent, NEW_MEDIA_COUNTER_REQUEST);
                break;
        }
    }

    public void setLockState(boolean lock)
    {
        Button lockButton = (Button) findViewById(R.id.lock_button);
        Button importButton = (Button) findViewById(R.id.import_data_button);
        Button exportButton = (Button) findViewById(R.id.export_data_button);

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
            MediaData md = adapter.getItem(pos);

            if (md.adjustCount(increment))
            {
                db.setStatus(md.getMediaName(), md.getStatus());
                if (increment)
                {
                    db.addEpisode(md.getMediaName());
                }
                else
                {
                    db.deleteEpisode(md.getMediaName());
                }
            }

            if (!increment && (md.getCount() < 0))
            {
                adapter.remove(pos);
            }

            adapter.update();
        }
    }

    private void showToast(String text)
    {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}