package com.monseigneur.mediacounterapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.MainActivityBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.viewmodel.MediaInfoViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaCounterActivity extends Activity
{
    // Activity message identifiers
    private static final int NEW_MEDIA_COUNTER_REQUEST = 1;
    private static final int MEDIA_INFO_STATUS_CHANGE_REQUEST = 2;
    public static final String MEDIA_COUNTER_NAME = "media_name";
    public static final String MEDIA_INFO_STATUS = "media_info_status";

    private static final int PERMISSION_MANIPULATE_EXTERNAL_STORAGE_REQUEST = 1;

    private MainActivityBinding binding;

    private MediaCounterAdapter adapter;
    private MediaCounterDB db;

    private boolean incLocked;
    private Drawable defaultButtonBg;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        verifyStoragePermissions(this);

        defaultButtonBg = binding.lockButton.getBackground();

        db = new MediaCounterDB(this);
        List<MediaData> mdList = db.getMediaCounters();
        Log.i("onCreate", "list = " + mdList);

        adapter = new MediaCounterAdapter(this, R.layout.media_counter_list_entry, mdList);
        binding.mediaList.setAdapter(adapter);

        binding.viewCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
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

    /**
     * Verify and prompt for storage permissions
     *
     * @param act the Activity
     */
    private void verifyStoragePermissions(Activity act)
    {
        if (!checkPermissions(act))
        {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

            ActivityCompat.requestPermissions(act, permissions, PERMISSION_MANIPULATE_EXTERNAL_STORAGE_REQUEST);
        }
    }

    /**
     * Check storage permissions
     *
     * @param act activity
     * @return true if storage permissions are granted, false otherwise
     */
    private boolean checkPermissions(Activity act)
    {
        int readPermission = ActivityCompat.checkSelfPermission(act, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(act, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return ((readPermission == PackageManager.PERMISSION_GRANTED) && (writePermission == PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSION_MANIPULATE_EXTERNAL_STORAGE_REQUEST)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                Log.i("requestPermissions", "Permission " + permissions[i] + " result " + grantResults[i]);
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Start the Media Info view for a selected Media view
     *
     * @param view the tapped view
     */
    public void viewMediaInfo(View view)
    {
        Intent intent = new Intent(this, MediaInfoActivity.class);

        Bundle b = new Bundle();

        LinearLayout ll = (LinearLayout) view.getParent();
        int pos = binding.mediaList.getPositionForView(ll);

        MediaData md = adapter.getItem(pos);

        String name = md.getMediaName();
        MediaInfoViewModel viewModel = new MediaInfoViewModel(name, md.getStatus(), md.getAddedDate(), db.getEpDates(name));

        b.putSerializable(MediaInfoActivity.MEDIA_INFO, viewModel);
        intent.putExtras(b);

        startActivityForResult(intent, MEDIA_INFO_STATUS_CHANGE_REQUEST);
    }

    /**
     * Handle Activity results
     *
     * @param requestCode the Activity request code
     * @param resultCode  the Activity result code
     * @param data        return data
     */
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

                    MediaData result = db.addMedia(name);

                    if (result != null)
                    {
                        adapter.add(result);
                    }
                    else
                    {
                        // Media already exists, show a toast
                        showToast(getString(R.string.duplicate_media));
                    }

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

    /**
     * Increment Media count touch handler
     *
     * @param view the tapped Media view
     */
    public void incMediaCount(View view)
    {
        Log.i("incMediaCount", "start");
        changeCount(view, true);
    }

    /**
     * Decrement Media count touch handler
     *
     * @param view the tapped Media view
     */
    public void decMediaCount(View view)
    {
        Log.i("decMediaCount", "start");
        changeCount(view, false);
    }

    /**
     * Button handler
     *
     * @param view the tapped view
     */
    public void buttonOnClick(View view)
    {
        switch (view.getId())
        {
            case R.id.import_data_button:
                if (!incLocked)
                {
                    if (checkPermissions(this))
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
                    else
                    {
                        showToast("Import failed due to missing permissions");
                    }

                }
                break;
            case R.id.export_data_button:
                if (!incLocked)
                {
                    if (checkPermissions(this))
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
                    else
                    {
                        showToast("Export failed due to missing permissions");
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
                Log.i("showStats", "epData size " + epData.size());

                Intent statsIntent = new Intent(this, MediaStatsActivity.class);
                Bundle b = new Bundle();

                try
                {
                    List<Long> edList = new ArrayList<>();
                    Map<String, Integer> namesMap = new HashMap<>();

                    int nameIndex = 1;
                    for (EpisodeData ed : epData)
                    {
                        int nameKey;
                        if (namesMap.containsKey(ed.getMediaName()))
                        {
                            nameKey = namesMap.get(ed.getMediaName());
                        }
                        else
                        {
                            nameKey = nameIndex;
                            nameIndex++;

                            namesMap.put(ed.getMediaName(), nameKey);
                        }

                        edList.add((long) nameKey);
                        edList.add((long) ed.getEpNum());
                        edList.add(ed.getEpDate());
                        edList.add((long) ed.getMediaStatus().value);
                    }

                    // Create a list of the names to be used in the reverse mapping
                    Map<Integer, String> reverseNameMap = new HashMap<>();

                    for (String name : namesMap.keySet())
                    {
                        reverseNameMap.put(namesMap.get(name), name);
                    }

                    b.putSerializable(MediaStatsActivity.MEDIA_STATS, (Serializable) edList);
                    b.putSerializable(MediaStatsActivity.MEDIA_NAMES, (Serializable) reverseNameMap);
                    statsIntent.putExtras(b);

                    Parcel p = Parcel.obtain();
                    p.writeBundle(b);
                    Log.i("showStats", "size2 " + p.dataSize() + " num keys " + reverseNameMap.keySet().size());
                    p.recycle();

                    startActivity(statsIntent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
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

    /**
     * Change the update lock state
     *
     * @param lock true to lock, false to unlock
     */
    private void setLockState(boolean lock)
    {
        incLocked = lock;
        if (lock)
        {
            binding.lockButton.setText(R.string.unlock_inc);
            binding.lockButton.setBackground(defaultButtonBg);

            binding.importDataButton.setVisibility(View.GONE);
            binding.exportDataButton.setVisibility(View.GONE);
        }
        else
        {
            binding.lockButton.setText(R.string.lock_inc);
            binding.lockButton.setBackgroundColor(Color.RED);

            binding.importDataButton.setVisibility(View.VISIBLE);
            binding.exportDataButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Change the count of a tapped Media
     *
     * @param view      the tapped Media view
     * @param increment true to increment, false to decrement
     */
    private void changeCount(View view, boolean increment)
    {
        if (incLocked)
        {
            Log.i("changeCount", "Locked for increment, exit early. increment = " + increment);
            return;
        }

        LinearLayout ll = (LinearLayout) view.getParent();
        int pos = binding.mediaList.getPositionForView(ll);
        MediaData md = adapter.getItem(pos);

        if (md == null)
        {
            Log.w("changeCount", "MediaData at " + pos + " is null");
            return;
        }

        Log.i("changeCount", "increment " + increment + " " + md);

        if (increment)
        {
            long now = MediaCounterDB.getCurrentDate();
            if (md.addEpisode(now))
            {
                db.addEpisode(md.getMediaName(), now);
                db.setStatus(md.getMediaName(), md.getStatus());
            }
        }
        else
        {
            db.deleteEpisode(md.getMediaName());
            if (!md.removeEpisode())
            {
                adapter.remove(pos);
            }
        }

        adapter.update();
    }

    /**
     * Show a toast message
     *
     * @param text message to show
     */
    private void showToast(String text)
    {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
