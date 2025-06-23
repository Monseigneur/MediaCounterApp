package com.monseigneur.mediacounterapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.monseigneur.mediacounterapp.databinding.MediaCounterAddActivityBinding;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaCounterAddActivity extends AppCompatActivity
{
    private MediaCounterAddActivityBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = MediaCounterAddActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addMediaButton.setOnClickListener(view -> {
            Intent result = new Intent();

            String mediaName = binding.mediaName.getText().toString();
            result.putExtra(MediaCounterActivity.MEDIA_COUNTER_NAME, mediaName);

            setResult(AppCompatActivity.RESULT_OK, result);
            finish();
        });
    }
}