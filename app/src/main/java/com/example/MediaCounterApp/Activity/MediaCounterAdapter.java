package com.example.MediaCounterApp.Activity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.MediaCounterApp.Model.MediaData;
import com.example.MediaCounterApp.R;

import java.util.List;

public class MediaCounterAdapter extends ArrayAdapter<MediaData>
{
    private LayoutInflater inflater;
    private int resource;
    private List<MediaData> mdList;

    public MediaCounterAdapter(Context c, int r, List<MediaData> mdl)
    {
        super(c, r, mdl);
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resource = r;

        mdList = mdl;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MediaCounterAdapter.ViewHolder vh;
        if (convertView == null)
        {
            convertView = inflater.inflate(resource, parent, false);
            vh = new MediaCounterAdapter.ViewHolder();

            vh.name = (TextView) convertView.findViewById(R.id.name_label);
            vh.count = (TextView) convertView.findViewById(R.id.count_label);

            convertView.setTag(vh);
        }
        else
        {
            vh = (MediaCounterAdapter.ViewHolder) convertView.getTag();
        }

        MediaData md = getItem(position);

        TextView name = vh.name;
        name.setText(md.getMediaName());

        int nameColor;
        switch (md.getStatus())
        {
            default:
            case NEW:
                nameColor = Color.WHITE;
                break;
            case ONGOING:
                nameColor = Color.YELLOW;
                break;
            case COMPLETE:
                nameColor = Color.GREEN;
                break;
            case DROPPED:
                nameColor = Color.RED;
                break;
        }

        name.setTextColor(nameColor);

        TextView count = vh.count;
        count.setText(md.getCount() + "");

        return convertView;
    }

    public void remove(int position)
    {
        mdList.remove(position);
    }

    // ViewHolder pattern to increase Adapter performance
    private class ViewHolder
    {
        public TextView name;
        public TextView count;
    }
}