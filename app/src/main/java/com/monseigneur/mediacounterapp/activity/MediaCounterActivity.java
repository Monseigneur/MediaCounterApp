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
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.MainActivityBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.IDataSerializer;
import com.monseigneur.mediacounterapp.model.IonEpisodeDataSerializer;
import com.monseigneur.mediacounterapp.model.IonMediaDataSerializer;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.viewmodel.MediaInfoViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

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

    private IDataSerializer<MediaData> mediaDataSerializer;
    private IDataSerializer<EpisodeData> episodeDataSerializer;
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

        mediaDataSerializer = new IonMediaDataSerializer(false);
        episodeDataSerializer = new IonEpisodeDataSerializer(MediaStatsActivity.STATS_USE_BINARY_SERIALIZATION);
        db = new MediaCounterDB(this);

        List<MediaData> mdList = db.getMediaCounters();
        Log.i("onCreate", "list = " + mdList);

        View.OnClickListener onClickListener = view -> {
            MediaCounterAdapter.ViewHolder vh = (MediaCounterAdapter.ViewHolder) view.getTag();
            int position = vh.getAbsoluteAdapterPosition();

            if (view.getId() == R.id.name_label)
            {
                MediaData md = adapter.getItem(position);
                viewMediaInfo(md);
            }
            else if (view.getId() == R.id.inc_button)
            {
                changeCount(position, true);
            }
            else if (view.getId() == R.id.dec_button)
            {
                changeCount(position, false);
            }
            else
            {
                Log.i("onClickListener", "Unknown view click, id: " + view.getId());
            }
        };

        adapter = new MediaCounterAdapter(mdList, onClickListener);
        binding.mediaList.setAdapter(adapter);
        binding.mediaList.setLayoutManager(new LinearLayoutManager(this));

        binding.viewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> adapter.setFilterMask(!isChecked));

        binding.importDataButton.setOnClickListener(view -> {
            // First create a backup and then try to import.
            createBackupFile(true);
        });

        binding.exportDataButton.setOnClickListener(view -> createBackupFile(false));

        binding.randomMediaButton.setOnClickListener(view -> {
            String randomMedia = db.getRandomMedia();
            showToast(randomMedia);
        });

        binding.statsButton.setOnClickListener(view -> showStats());

        binding.lockButton.setOnClickListener(view -> setLockState(!incLocked));

        binding.newMediaButton.setOnClickListener(view -> {
            Intent newMediaIntent = new Intent(this, MediaCounterAddActivity.class);
            startActivityForResult(newMediaIntent, NEW_MEDIA_COUNTER_REQUEST);
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
     * @param md the MediaData in the tapped view
     */
    public void viewMediaInfo(MediaData md)
    {
        Intent intent = new Intent(this, MediaInfoActivity.class);

        Bundle b = new Bundle();

        if (md == null)
        {
            Log.w("viewMediaInfo", "tapped MediaData in view is null!");
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

            adapter.updateStatus(name, newStatus);
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

                showToast(success, getString(R.string.export_succeeded), getString(R.string.export_failed));
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

                showToast(success, getString(R.string.import_succeeded), getString(R.string.import_failed));

                if (success)
                {
                    List<MediaData> mdList = db.getMediaCounters();

                    adapter.update(mdList);
                }

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
     * @param position  the position of the tapped view
     * @param increment true to increment, false to decrement
     */
    private void changeCount(int position, boolean increment)
    {
        if (incLocked)
        {
            Log.i("changeCount", "Locked for increment, exit early. increment = " + increment);
            return;
        }

        MediaData md = adapter.getItem(position);

        if (md == null)
        {
            Log.w("changeCount", "MediaData in view at position " + position + " is null!");
            return;
        }

        Log.i("changeCount", "increment " + increment + " " + md);

        boolean removed = false;
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
            if (md.removeEpisode())
            {
                db.setStatus(md.getMediaName(), md.getStatus());
            }
            else
            {
                adapter.remove(position);
                removed = true;
            }
        }

        if (!removed)
        {
            adapter.notifyItemChanged(position);
        }
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
        List<EpisodeData> epData = db.getEpisodeData();
        Log.i("showStats", "epData size " + epData.size());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (!episodeDataSerializer.serialize(bos, epData))
        {
            Log.e("showStats", "failed to serialize episode data");
            return;
        }

        try
        {
            bos.flush();
        }
        catch (IOException e)
        {
            Log.e("showStats", "caught exception " + e);
            return;
        }

        Intent statsIntent = new Intent(this, MediaStatsActivity.class);
        Bundle b = new Bundle();

        b.putSerializable(MediaStatsActivity.EPISODE_DATA, (Serializable) bos.toByteArray());

        statsIntent.putExtras(b);
        showDataSize(b);

        startActivity(statsIntent);
    }

    private void showDataSize(Bundle b)
    {
        Parcel p = Parcel.obtain();
        p.writeBundle(b);

        Log.i("showDataSize", "data size: " + p.dataSize());

        p.recycle();
    }

    private void openImportFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, OPEN_IMPORT_FILE);
    }

    private void createBackupFile(boolean isImport)
    {
        if (incLocked)
        {
            return;
        }

        boolean mediaCountersEmpty = db.isEmpty();

        Log.i("createBackupFile", "isImport " + isImport + " mediaCountersEmpty " + mediaCountersEmpty);

        if (mediaCountersEmpty)
        {
            if (isImport)
            {
                // Empty on the import path, skip trying to export the existing data.
                openImportFile();
            }
            else
            {
                showToast(getString(R.string.export_empty));
            }

            return;
        }

        String fileName = "MCB_" + fileTimeStamp() + ".txt";

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        startActivityForResult(intent, isImport ? CREATE_BACKUP_FILE_IMPORT : CREATE_BACKUP_FILE);
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
            if (!mediaDataSerializer.deserialize(is, mdList))
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
            success = mediaDataSerializer.serialize(os, mdList);
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
