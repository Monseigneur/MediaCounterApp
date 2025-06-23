package com.monseigneur.mediacounterapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.databinding.MediaStatsActivityBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.IDataSerializer;
import com.monseigneur.mediacounterapp.model.IonEpisodeDataSerializer;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milan on 8/5/2016.
 */
public class MediaStatsActivity extends AppCompatActivity
{
    public static final String EPISODE_DATA = "episode_date";

    public static final boolean STATS_USE_BINARY_SERIALIZATION = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        MediaStatsActivityBinding binding = MediaStatsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        Bundle b = i.getExtras();

        Log.i("statsActivity", "onCreate");

        byte[] arr = null;
        if (b != null)
        {
            arr = b.getSerializable(EPISODE_DATA, byte[].class);
        }

        IDataSerializer<EpisodeData> dm = new IonEpisodeDataSerializer(STATS_USE_BINARY_SERIALIZATION);
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);

        List<EpisodeData> edList = new ArrayList<>();
        if (!dm.deserialize(bis, edList))
        {
            Log.i("statsActivity", "failed to decode");
        }

        Log.i("statsActivity", "num episodes: " + edList.size());

        String text = "Total episodes: " + edList.size();
        binding.mediaStatsTotalLabel.setText(text);

        MediaStatsAdapter adapter = new MediaStatsAdapter(edList);

        binding.mediaStatsList.setAdapter(adapter);
        binding.mediaStatsList.setLayoutManager(new LinearLayoutManager(this));
    }
}