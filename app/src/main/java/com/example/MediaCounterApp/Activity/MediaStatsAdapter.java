package com.example.MediaCounterApp.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.MediaCounterApp.Model.EpisodeData;
import com.example.MediaCounterApp.Model.MediaCounterDB;
import com.example.MediaCounterApp.R;

import java.util.List;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaStatsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private int resource;
    private List<EpisodeData> edList;
    private boolean checkBoxEnable;

    public MediaStatsAdapter(Context c, int r, List<EpisodeData> edl)
    {
        context = c;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resource = r;

        edList = edl;
        checkBoxEnable = false;
    }

    @Override
    public int getCount() {
        return edList.size();
    }

    @Override
    public Object getItem(int position) {
        return edList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = null;

        if (convertView == null)
        {
            itemView = inflater.inflate(resource, parent, false);
        }
        else
        {
            itemView = convertView;
        }

        EpisodeData ed = (EpisodeData)getItem(position);

        TextView num = (TextView)itemView.findViewById(R.id.stats_episode_number);
        num.setText("" + ed.getEpNum());

        TextView name = (TextView)itemView.findViewById(R.id.stats_media_name);
        name.setText(ed.getMediaName());

        TextView date = (TextView)itemView.findViewById(R.id.stats_episode_date);
        date.setText(MediaCounterDB.dateString(context, ed.getEpDate()) + "");

        return itemView;
    }

    public void remove(int position)
    {
        edList.remove(position);
    }

    @Override
    public boolean isEmpty() {
        return edList.isEmpty();
    }
}