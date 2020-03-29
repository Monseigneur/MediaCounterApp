package com.monseigneur.mediacounterapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.MediaStatsActivityBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Milan on 8/5/2016.
 */
public class MediaStatsActivity extends Activity
{
    public static final String MEDIA_STATS = "media_stats";
    public static final String MEDIA_NAMES = "media_names";

    private MediaStatsActivityBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = MediaStatsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent i = getIntent();
        Bundle b = i.getExtras();

        Log.i("statsActivity", "oncreate");

        List<EpisodeData> edList = new ArrayList<>();
        List<Long> lData;
        Map<Integer, String> nameData;

        if (b != null)
        {
            lData = (List<Long>) b.getSerializable(MEDIA_STATS);
            nameData = (Map<Integer, String>) b.getSerializable(MEDIA_NAMES);
        }
        else
        {
            lData = new ArrayList<>();
            nameData = new HashMap<>();
        }

        // For the best performance, the data comes across as a list of Longs: [name ID, episode number, date, status]
        for (int j = 0; j < lData.size(); j += 4)
        {
            String name = nameData.get(lData.get(j).intValue());
            edList.add(new EpisodeData(name, lData.get(j + 1).intValue(), lData.get(j + 2), MediaCounterStatus.from(lData.get(j + 3).intValue())));
        }

        Log.i("statsActivity", edList.toString());

        binding.mediaStatsTotalLabel.setText("Total episodes: " + edList.size());

        MediaStatsAdapter adapter = new MediaStatsAdapter(this, R.layout.media_stats_list_entry, edList);

        binding.mediaStatsList.setAdapter(adapter);

    }
}