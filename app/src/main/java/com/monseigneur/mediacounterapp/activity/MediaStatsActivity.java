package com.monseigneur.mediacounterapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.monseigneur.mediacounterapp.R;
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
public class MediaStatsActivity extends Activity
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
            arr = (byte[]) b.getSerializable(EPISODE_DATA);
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

        MediaStatsAdapter adapter = new MediaStatsAdapter(this, R.layout.media_stats_list_entry, edList);

        binding.mediaStatsList.setAdapter(adapter);

    }
}