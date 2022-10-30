package com.monseigneur.mediacounterapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.MainActivityBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.IDataManager;
import com.monseigneur.mediacounterapp.model.IonDataManager;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.viewmodel.MediaInfoViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MediaCounterActivity extends Activity
{
    // Activity message identifiers
    private static final int NEW_MEDIA_COUNTER_REQUEST = 1;
    private static final int MEDIA_INFO_STATUS_CHANGE_REQUEST = 2;
    private static final int CREATE_BACKUP_FILE = 3;
    private static final int CREATE_BACKUP_FILE_IMPORT = 4;
    private static final int OPEN_IMPORT_FILE = 5;

    public static final String MEDIA_COUNTER_NAME = "media_name";
    public static final String MEDIA_INFO_STATUS = "media_info_status";

    private MainActivityBinding binding;

    private IDataManager dm;
    private MediaCounterDB db;
    private MediaCounterAdapter adapter;

    private boolean incLocked;
    private Drawable defaultButtonBg;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        defaultButtonBg = binding.lockButton.getBackground();

        dm = new IonDataManager(false);
        db = new MediaCounterDB(this);

        List<MediaData> mdList = db.getMediaCounters();
        Log.i("onCreate", "list = " + mdList);

        adapter = new MediaCounterAdapter(this, R.layout.media_counter_list_entry, mdList);
        binding.mediaList.setAdapter(adapter);

        binding.viewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
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

        if (md == null)
        {
            Log.w("viewMediaInfo", "MediaData in view at pos " + pos + " is null!");
            return;
        }

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

        if (resultCode != RESULT_OK)
        {
            return;
        }

        if (requestCode == NEW_MEDIA_COUNTER_REQUEST)
        {
            String name = data.getStringExtra(MEDIA_COUNTER_NAME);

            if (name == null || name.isEmpty())
            {
                showToast("Invalid name");
                return;
            }

            MediaData newMd = new MediaData(name, MediaCounterDB.getCurrentDate());

            if (db.addMedia(newMd))
            {
                adapter.add(newMd);
                adapter.update();
            }
            else
            {
                // Media already exists, show a toast
                showToast(getString(R.string.duplicate_media));
            }

            Log.i("onActivityResult", name);
        }
        else if (requestCode == MEDIA_INFO_STATUS_CHANGE_REQUEST)
        {
            MediaCounterStatus newStatus = (MediaCounterStatus) data.getSerializableExtra(MEDIA_INFO_STATUS);
            String name = data.getStringExtra(MediaCounterActivity.MEDIA_COUNTER_NAME);
            Log.i("onActivityResult", "media info status change " + newStatus + " for media [" + name + "]");
            db.setStatus(name, newStatus);

            adapter.getItem(name).setStatus(newStatus);
            adapter.update();
        }
        else if (requestCode == CREATE_BACKUP_FILE || requestCode == CREATE_BACKUP_FILE_IMPORT)
        {
            boolean importPath = (requestCode == CREATE_BACKUP_FILE_IMPORT);

            Log.i("onActivityResult", "write backup data file, importPath " + importPath);
            if (data != null)
            {
                Uri uri = data.getData();
                boolean success = exportData(uri);

                showFileMetadata(uri);

                showToast(success, "Export succeeded", "Export failed");
            }

            if (importPath)
            {
                // If import, kick off the open import path.
                openImportFile();
            }
            else
            {
                // Return to a locked state if just exporting.
                setLockState(true);
            }
        }
        else if (requestCode == OPEN_IMPORT_FILE)
        {
            Log.i("onActivityResult", "open import data file");
            if (data != null)
            {
                Uri uri = data.getData();

                showFileMetadata(uri);

                boolean success = importData(uri);

                showToast(success, "Import succeeded", "Import failed");

                // Return to a locked state.
                setLockState(true);
            }
        }
        else
        {
            Log.e("onActivityResult", "unknown requestCode " + requestCode);
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
        int id = view.getId();

        if (id == R.id.import_data_button)
        {
            // First create a backup, and then try to import.
            createBackupFile(true);
        }
        else if (id == R.id.export_data_button)
        {
            createBackupFile(false);
        }
        else if (id == R.id.random_media_button)
        {
            String randomMedia = db.getRandomMedia();
            showToast(randomMedia);
        }
        else if (id == R.id.stats_button)
        {
            showStats();
        }
        else if (id == R.id.lock_button)
        {
            setLockState(!incLocked);
        }
        else if (id == R.id.new_media_button)
        {
            Intent newMediaIntent = new Intent(this, MediaCounterAddActivity.class);
            startActivityForResult(newMediaIntent, NEW_MEDIA_COUNTER_REQUEST);
        }
        else
        {
            Log.e("buttonOnClick", "received click from unknown view " + id);
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
            Log.w("changeCount", "MediaData in view at pos " + pos + " is null!");
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

    /**
     * Show a toast message based on a condition
     *
     * @param condition condition for message
     * @param trueText  text when condition is true
     * @param falseText text when condition is false
     */
    private void showToast(boolean condition, String trueText, String falseText)
    {
        showToast(condition ? trueText : falseText);
    }

    private void showStats()
    {
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
    }

    private void openImportFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, OPEN_IMPORT_FILE);
    }

    private void createBackupFile(boolean importPath)
    {
        if (incLocked)
        {
            return;
        }

        String fileName = "MCB_" + fileTimeStamp() + ".txt";

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        Log.i("createBackupFile", "importPath " + importPath);

        startActivityForResult(intent, importPath ? CREATE_BACKUP_FILE_IMPORT : CREATE_BACKUP_FILE);
    }

    private boolean importData(Uri uri)
    {
        if (uri == null)
        {
            return false;
        }

        Log.i("importData", "uri " + uri.getPath());

        List<MediaData> mdList = new ArrayList<>();
        try (InputStream is = getContentResolver().openInputStream(uri))
        {
            if (!dm.readData(is, mdList))
            {
                return false;
            }
        }
        catch (IOException e)
        {
            Log.e("importData", "caught exception " + e);
            return false;
        }

        return db.importData(mdList);
    }

    private boolean exportData(Uri uri)
    {
        if (uri == null)
        {
            return false;
        }

        Log.i("exportData", "uri " + uri.getPath());

        List<MediaData> mdList = db.getMediaCounters();

        if (mdList == null || mdList.isEmpty())
        {
            return false;
        }

        boolean success = false;
        try (OutputStream os = getContentResolver().openOutputStream(uri))
        {
            success = dm.writeData(os, mdList);
        }
        catch (IOException e)
        {
            Log.e("exportData", "caught exception " + e);
        }

        return success;
    }

    private static String fileTimeStamp()
    {
        Calendar date = Calendar.getInstance();

        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;       // January is 0?
        int year = date.get(Calendar.YEAR);
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        int second = date.get(Calendar.SECOND);

        Log.i("fileTimestamp", "DATE STRING: Y=" + year + " M=" + month + " D=" + dayOfMonth + " H=" + hour + " M=" + minute + " S=" + second);

        return String.format(Locale.US, "%d%02d%02d_%02d%02d%02d", year, month, dayOfMonth, hour, minute, second);
    }

    private void showFileMetadata(Uri uri)
    {
        // Leveraged from example in documentation:
        // https://developer.android.com/training/data-storage/shared/documents-files#examine-metadata

        if (uri == null)
        {
            return;
        }

        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null, null))
        {
            if (cursor == null || !cursor.moveToFirst())
            {
                return;
            }

            String displayName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));

            // Size may be not exist, so check if it is null first.
            int sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE);

            String size;
            if (!cursor.isNull(sizeIndex))
            {
                size = cursor.getString(sizeIndex);
            }
            else
            {
                size = "Unknown";
            }

            Log.i("showFileMetadata", "Display name: " + displayName + " size: " + size);
        }
        catch (Exception e)
        {
            Log.e("showFileMetadata", "caught exception " + e);
        }
    }
}
