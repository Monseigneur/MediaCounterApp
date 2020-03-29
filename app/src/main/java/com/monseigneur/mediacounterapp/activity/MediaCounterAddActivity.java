package com.monseigneur.mediacounterapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.monseigneur.mediacounterapp.databinding.MediaCounterAddActivityBinding;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaCounterAddActivity extends Activity {
    private MediaCounterAddActivityBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = MediaCounterAddActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void addNewMediaCounter(View view)
    {
        Intent result = new Intent();

        String mediaName = binding.mediaName.getText().toString();

        result.putExtra(MediaCounterActivity.MEDIA_COUNTER_NAME, mediaName);

        setResult(Activity.RESULT_OK, result);
        finish();
    }
}