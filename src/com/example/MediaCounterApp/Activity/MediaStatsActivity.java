package com.example.MediaCounterApp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import com.example.MediaCounterApp.Model.EpisodeData;
import com.example.MediaCounterApp.Model.MediaData;
import com.example.MediaCounterApp.R;

import java.util.List;

/**
 * Created by Milan on 8/5/2016.
 */
public class MediaStatsActivity extends Activity
{
    public static final String MEDIA_STATS = "media_stats";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_stats_activity);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        List<EpisodeData> edList = (List<EpisodeData>)b.getSerializable(MEDIA_STATS);
        Log.i("statsActivity", edList.toString());

        TextView totalCount = (TextView)findViewById(R.id.media_stats_total_label);
        totalCount.setText("Total episodes: " + edList.size());

        ListView listView = (ListView)findViewById(R.id.media_stats_list);
        Log.i("before constructor", R.layout.media_stats_list_entry + " " + edList);
        MediaStatsAdapter adapter = new MediaStatsAdapter(this, R.layout.media_stats_list_entry, edList);

        listView.setAdapter(adapter);

    }
}