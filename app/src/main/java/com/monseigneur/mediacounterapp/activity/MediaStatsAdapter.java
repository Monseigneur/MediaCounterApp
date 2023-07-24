package com.monseigneur.mediacounterapp.activity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;

import java.util.List;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaStatsAdapter extends BaseAdapter
{
    private final LayoutInflater inflater;
    private final int resource;
    private final List<EpisodeData> edList;

    public MediaStatsAdapter(Context c, int r, List<EpisodeData> edl)
    {
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resource = r;

        edList = edl;
    }

    @Override
    public int getCount()
    {
        return edList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return edList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MediaStatsAdapter.ViewHolder vh;
        if (convertView == null)
        {
            convertView = inflater.inflate(resource, parent, false);
            vh = new ViewHolder(convertView);

            convertView.setTag(vh);
        }
        else
        {
            vh = (MediaStatsAdapter.ViewHolder) convertView.getTag();
        }

        EpisodeData ed = (EpisodeData) getItem(position);

        vh.setData(ed);

        return convertView;
    }

    public void remove(int position)
    {
        edList.remove(position);
    }

    @Override
    public boolean isEmpty()
    {
        return edList.isEmpty();
    }

    // ViewHolder pattern to increase Adapter performance
    private static class ViewHolder
    {
        private final TextView num;
        private final TextView name;
        private final TextView date;

        public ViewHolder(View view)
        {
            num = view.findViewById(R.id.stats_episode_number);
            name = view.findViewById(R.id.stats_media_name);
            date = view.findViewById(R.id.stats_episode_date);
        }

        public void setData(EpisodeData ed)
        {
            int textColor;
            switch (ed.getMediaStatus())
            {
                case COMPLETE:
                    textColor = Color.GREEN;
                    break;
                case DROPPED:
                    textColor = Color.RED;
                    break;
                case ONGOING:
                    textColor = Color.YELLOW;
                    break;
                default:
                    textColor = Color.WHITE;
                    break;
            }

            num.setText(String.valueOf(ed.getEpNum()));
            num.setTextColor(textColor);

            name.setText(ed.getMediaName());
            name.setTextColor(textColor);

            date.setText(MediaCounterDB.dateString(ed.getEpDate()));
            date.setTextColor(textColor);
        }
    }
}