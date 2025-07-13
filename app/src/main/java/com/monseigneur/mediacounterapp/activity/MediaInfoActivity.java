package com.monseigneur.mediacounterapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.MediaInfoActivityBinding;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.model.Util;

public class MediaInfoActivity extends AppCompatActivity
{
    private static final String TAG = "MediaInfoActivity";

    public static final String MEDIA_INFO = "media_info";

    private MediaInfoActivityBinding binding;

    private MediaCounterStatus currentStatus;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = MediaInfoActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        Bundle b = i.getExtras();

        MediaData media = b.getSerializable(MEDIA_INFO, MediaData.class);

        Log.i(TAG, "onCreate: " + media.toString());

        name = media.getMediaName();
        binding.mediaInfoName.setText(name);

        String addedDate = Util.timestampToString(media.getAddedDate());
        binding.mediaInfoAddedDate.setText("Added: " + addedDate);

        binding.mediaInfoCount.setText(String.valueOf(media.getCount()));

        binding.mediaInfoStatusButton.setOnClickListener(this::changeStatus);

        currentStatus = media.getStatus();
        // Set the initial state
        setButtonText();

        MediaInfoAdapter adapter = new MediaInfoAdapter(media.getEpDates());

        binding.mediaInfoEpList.setAdapter(adapter);
        binding.mediaInfoEpList.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Sets the button text depending on the current status.
     */
    private void setButtonText()
    {
        int textId = switch (currentStatus)
        {
            case ONGOING -> R.string.ongoing_text;
            case COMPLETE -> R.string.complete_text;
            case DROPPED -> R.string.dropped_text;
            default -> R.string.new_text;
        };

        binding.mediaInfoStatusButton.setText(textId);
    }

    private void changeStatus(View view)
    {
        Log.i(TAG, "changeStatus: changing status");

        // Cycle through the statuses
        currentStatus = switch (currentStatus)
        {
            case ONGOING -> MediaCounterStatus.COMPLETE;
            case COMPLETE -> MediaCounterStatus.DROPPED;
            case DROPPED -> MediaCounterStatus.NEW;
            default -> MediaCounterStatus.ONGOING;
        };

        setButtonText();
    }

    @Override
    public void finish()
    {
        Intent result = new Intent();
        result.putExtra(MediaCounterActivity.MEDIA_INFO_STATUS, currentStatus);
        result.putExtra(MediaCounterActivity.MEDIA_COUNTER_NAME, name);
        setResult(AppCompatActivity.RESULT_OK, result);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            // Respond to the action bar's Up/Home button
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
