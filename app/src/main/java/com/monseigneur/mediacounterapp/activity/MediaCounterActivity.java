package com.monseigneur.mediacounterapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.MainActivityBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.IDataSerializer;
import com.monseigneur.mediacounterapp.model.IonEpisodeDataSerializer;
import com.monseigneur.mediacounterapp.model.IonMediaDataSerializer;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;
import com.monseigneur.mediacounterapp.model.MediaCounterRepository;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.viewmodel.MediaViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MediaCounterActivity extends AppCompatActivity
{
    public static final String MEDIA_COUNTER_NAME = "media_name";
    public static final String MEDIA_INFO_STATUS = "media_info_status";

    private MainActivityBinding binding;

    private IDataSerializer<EpisodeData> episodeDataSerializer;

    private MediaViewModel mediaViewModel;

    private MediaCounterAdapter adapter;

    private boolean incLocked;
    private Drawable defaultButtonBg;

    private final ActivityResultLauncher<Intent> newMediaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK)
                {
                    handleNewMedia(result.getData());
                }
            });

    private final ActivityResultLauncher<Intent> showInfoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK)
                {
                    handleStatusChange(result.getData());
                }
            });

    private final ActivityResultLauncher<String> exportLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"),
            uri -> {
                boolean exportSuccess = exportData(uri);

                showToast(exportSuccess, getString(R.string.export_succeeded), getString(R.string.export_failed));

                setLockState(true);
            });

    private final ActivityResultLauncher<String[]> importLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
            uri -> {
                boolean importSuccess = importData(uri);

                showToast(importSuccess, getString(R.string.import_succeeded), getString(R.string.import_failed));

                setLockState(true);
            });

    private final ListItemClickCallback listItemCallback = (mediaData, clickType) -> {
        Log.i("listItemCallback", "got a click on md " + mediaData.getMediaName() + " type " + clickType);

        switch (clickType)
        {
            case ListItemClickCallback.ItemClickType.INFO -> viewMediaInfo(mediaData);
            case ListItemClickCallback.ItemClickType.INCREMENT -> changeCount(mediaData, true);
            case ListItemClickCallback.ItemClickType.DECREMENT -> changeCount(mediaData, false);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        defaultButtonBg = binding.lockButton.getBackground();

        episodeDataSerializer = new IonEpisodeDataSerializer(MediaStatsActivity.STATS_USE_BINARY_SERIALIZATION);

        adapter = new MediaCounterAdapter(listItemCallback);
        binding.mediaList.setAdapter(adapter);
        binding.mediaList.setLayoutManager(new LinearLayoutManager(this));

        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel.setRepository(new MediaCounterRepository(new MediaCounterDB(this), new IonMediaDataSerializer(false)));
        mediaViewModel.getAllMedia().observe(this, mediaData -> adapter.setMedia(mediaData));

        binding.viewCheckBox.setOnCheckedChangeListener((_, _) -> showToast("Not yet implemented"));

        binding.randomMediaButton.setOnClickListener(_ -> getRandomMedia());

        binding.statsButton.setOnClickListener(_ -> showStats());

        binding.lockButton.setOnClickListener(_ -> setLockState(!incLocked));

        binding.fab.setOnClickListener(_ -> newMediaLauncher.launch(new Intent(this, MediaCounterAddActivity.class)));

        incLocked = true;
        setLockState(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_import)
        {
            importLauncher.launch(new String[]{"text/plain"});
            return true;
        }
        else if (id == R.id.action_export)
        {
            if (!mediaViewModel.isEmpty())
            {
                exportLauncher.launch(getDefaultExportFilename());
            }
            else
            {
                showToast(getString(R.string.export_empty));
            }

            return true;
        }
        else if (id == R.id.action_settings)
        {
            showToast("Not yet implemented");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     * Start the Media Info view for a selected Media view
     *
     * @param media the MediaData in the tapped view
     */
    public void viewMediaInfo(MediaData media)
    {
        Intent intent = new Intent(this, MediaInfoActivity.class);

        Bundle b = new Bundle();

        if (media == null)
        {
            Log.w("viewMediaInfo", "tapped MediaData in view is null!");
            return;
        }

        b.putSerializable(MediaInfoActivity.MEDIA_INFO, media);
        intent.putExtras(b);

        showInfoLauncher.launch(intent);
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
        }
        else
        {
            binding.lockButton.setText(R.string.lock_inc);
            binding.lockButton.setBackgroundColor(Color.RED);
        }
    }

    /**
     * Change the count of a tapped Media
     *
     * @param media     the media tapped on
     * @param increment true to increment, false to decrement
     */
    private void changeCount(MediaData media, boolean increment)
    {
        if (incLocked)
        {
            Log.i("changeCount", "Locked for increment, exit early. increment = " + increment);
            return;
        }

        if (media == null)
        {
            Log.w("changeCount", "MediaData is null!");
            return;
        }

        Log.i("changeCount", "increment " + increment + " " + media);

        if (increment)
        {
            mediaViewModel.addEpisode(media.getMediaName());
        }
        else
        {
            mediaViewModel.removeEpisode(media.getMediaName());
        }
    }

    private void handleNewMedia(Intent newMediaIntent)
    {
        if (newMediaIntent == null)
        {
            return;
        }

        String name = newMediaIntent.getStringExtra(MEDIA_COUNTER_NAME);

        Log.i("handleNewMedia", "new media [" + name + "]");

        if (name == null || name.isEmpty())
        {
            showToast("Invalid name");
            return;
        }

        if (!mediaViewModel.addNewMedia(name))
        {
            // Media already exists, show a toast
            showToast(getString(R.string.duplicate_media));
        }
    }

    private void handleStatusChange(Intent statusChangeIntent)
    {
        if (statusChangeIntent == null)
        {
            return;
        }

        MediaCounterStatus newStatus = statusChangeIntent.getSerializableExtra(MEDIA_INFO_STATUS, MediaCounterStatus.class);
        String name = statusChangeIntent.getStringExtra(MediaCounterActivity.MEDIA_COUNTER_NAME);

        Log.i("handleStatusChange", "media info status change " + newStatus + " for media [" + name + "]");
        mediaViewModel.changeStatus(name, newStatus);
    }

    /**
     * Show a toast message
     *
     * @param text message to show
     */
    private void showToast(String text)
    {
        Log.i("showToast", "showing toast [" + text + "]");
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

    private void getRandomMedia()
    {
        String randomMedia = mediaViewModel.getRandomMediaName();

        if (randomMedia != null)
        {
            showToast(randomMedia);
        }
        else
        {
            showToast(getString(R.string.no_random));
        }
    }

    private void showStats()
    {
        List<EpisodeData> epData = mediaViewModel.getAllEpisodes();
        Log.i("showStats", "epData size " + epData.size());

        if (epData.isEmpty())
        {
            showToast(getString(R.string.no_stats));

            return;
        }

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

        b.putSerializable(MediaStatsActivity.EPISODE_DATA, bos.toByteArray());

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

    private boolean importData(Uri uri)
    {
        if (uri == null)
        {
            return false;
        }

        Log.i("importData", "uri " + uri.getPath());

        boolean success = false;
        try (InputStream is = getContentResolver().openInputStream(uri))
        {
            success = mediaViewModel.importData(is);
        }
        catch (IOException e)
        {
            Log.e("importData", "caught exception " + e);
        }

        return success;
    }

    private boolean exportData(Uri uri)
    {
        if (uri == null)
        {
            return false;
        }

        Log.i("exportData", "uri " + uri.getPath());

        boolean success = false;
        try (OutputStream os = getContentResolver().openOutputStream(uri))
        {
            success = mediaViewModel.exportData(os);
        }
        catch (IOException e)
        {
            Log.e("exportData", "caught exception " + e);
        }

        return success;
    }

    private static String getDefaultExportFilename()
    {
        Calendar date = Calendar.getInstance();

        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;       // January is 0?
        int year = date.get(Calendar.YEAR);
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        int second = date.get(Calendar.SECOND);

        Log.i("fileTimestamp", "DATE STRING: Y=" + year + " M=" + month + " D=" + dayOfMonth + " H=" + hour + " M=" + minute + " S=" + second);

        return String.format(Locale.US, "MCB_%d%02d%02d_%02d%02d%02d.txt", year, month, dayOfMonth, hour, minute, second);
    }
}
