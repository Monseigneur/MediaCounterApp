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
import com.monseigneur.mediacounterapp.model.Util;
import com.monseigneur.mediacounterapp.viewmodel.MediaInfoViewModel;

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

        MediaInfoViewModel viewModel = b.getSerializable(MEDIA_INFO, MediaInfoViewModel.class);

        Log.i(TAG, "onCreate: " + viewModel.toString());

        name = viewModel.mediaName;
        binding.mediaInfoName.setText(name);

        String addedDate = Util.timestampToString(viewModel.addedDate);
        binding.mediaInfoAddedDate.setText("Added: " + addedDate);

        binding.mediaInfoCount.setText(String.valueOf(viewModel.epDates.size()));

        binding.mediaInfoStatusButton.setOnClickListener(this::changeStatus);

        currentStatus = viewModel.status;
        // Set the initial state
        setButtonText();

        MediaInfoAdapter adapter = new MediaInfoAdapter(viewModel.epDates);

        binding.mediaInfoEpList.setAdapter(adapter);
        binding.mediaInfoEpList.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Sets the button text depending on the current status.
     */
    private void setButtonText()
    {
        int textId;
        switch (currentStatus)
        {
            default:
            case NEW:
                textId = R.string.new_text;
                break;
            case ONGOING:
                textId = R.string.ongoing_text;
                break;
            case COMPLETE:
                textId = R.string.complete_text;
                break;
            case DROPPED:
                textId = R.string.dropped_text;
                break;
        }

        binding.mediaInfoStatusButton.setText(textId);
    }

    private void changeStatus(View view)
    {
        Log.i(TAG, "changeStatus: changing status");

        // Cycle through the statuses
        MediaCounterStatus newStatus;
        switch (currentStatus)
        {
            default:
            case NEW:
                newStatus = MediaCounterStatus.ONGOING;
                break;
            case ONGOING:
                newStatus = MediaCounterStatus.COMPLETE;
                break;
            case COMPLETE:
                newStatus = MediaCounterStatus.DROPPED;
                break;
            case DROPPED:
                newStatus = MediaCounterStatus.NEW;
                break;
        }
        currentStatus = newStatus;
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
