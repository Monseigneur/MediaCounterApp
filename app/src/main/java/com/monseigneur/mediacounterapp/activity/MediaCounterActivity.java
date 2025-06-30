package com.monseigneur.mediacounterapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
import com.monseigneur.mediacounterapp.viewmodel.MediaInfoViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MediaCounterActivity extends AppCompatActivity
{
    public static final String MEDIA_COUNTER_NAME = "media_name";
    public static final String MEDIA_INFO_STATUS = "media_info_status";

    private MainActivityBinding binding;

    private IDataSerializer<EpisodeData> episodeDataSerializer;

    private MediaCounterRepository repository;

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

                if (importSuccess)
                {
                    List<MediaData> media = repository.getAllMedia();
                    adapter.update(media);
                }

                setLockState(true);
            });

    private final View.OnClickListener onClickListener = view -> {
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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        defaultButtonBg = binding.lockButton.getBackground();

        episodeDataSerializer = new IonEpisodeDataSerializer(MediaStatsActivity.STATS_USE_BINARY_SERIALIZATION);

        repository = new MediaCounterRepository(new MediaCounterDB(this), new IonMediaDataSerializer(false));

        adapter = new MediaCounterAdapter(onClickListener);
        adapter.update(repository.getAllMedia());
        binding.mediaList.setAdapter(adapter);
        binding.mediaList.setLayoutManager(new LinearLayoutManager(this));

        binding.viewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> adapter.setFilterMask(!isChecked));

        binding.importDataButton.setOnClickListener(_ -> {
            importLauncher.launch(new String[]{"text/plain"});
        });

        binding.exportDataButton.setOnClickListener(_ -> {
            if (repository.isEmpty())
            {
                showToast(getString(R.string.export_empty));

                return;
            }

            exportLauncher.launch(getDefaultExportFilename());
        });

        binding.randomMediaButton.setOnClickListener(view -> getRandomMedia());

        binding.statsButton.setOnClickListener(view -> showStats());

        binding.lockButton.setOnClickListener(view -> setLockState(!incLocked));

        binding.newMediaButton.setOnClickListener(view -> {
            newMediaLauncher.launch(new Intent(this, MediaCounterAddActivity.class));
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

        MediaInfoViewModel viewModel = new MediaInfoViewModel(md);

        b.putSerializable(MediaInfoActivity.MEDIA_INFO, viewModel);
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

        boolean refresh = false;
        if (increment)
        {
            if (repository.addEpisode(md.getMediaName()))
            {
                refresh = true;
            }
        }
        else
        {
            int result = repository.removeEpisode(md.getMediaName());
            if (result == 2)
            {
                adapter.remove(position);
            }

            if (result != 2)
            {
                refresh = true;
            }
        }

        if (refresh)
        {
            adapter.update(repository.getAllMedia());
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

        if (repository.addNewMedia(name))
        {
            MediaData media = repository.getMedia(name);
            adapter.add(media);
        }
        else
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
        repository.changeStatus(name, newStatus);

        adapter.updateStatus(name, newStatus);
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
        String randomMedia = repository.getRandomMediaName();

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
        List<EpisodeData> epData = repository.getAllEpisodes();
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
            success = repository.importData(is);
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
            success = repository.exportData(os);
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
