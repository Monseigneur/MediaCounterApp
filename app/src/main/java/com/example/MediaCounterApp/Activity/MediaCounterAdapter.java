package com.example.MediaCounterApp.Activity;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.MediaCounterApp.Model.MediaCounterStatus;
import com.example.MediaCounterApp.Model.MediaData;
import com.example.MediaCounterApp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class MediaCounterAdapter extends ArrayAdapter<MediaData>
{
    private LayoutInflater inflater;
    private int resource;
    private List<MediaData> originalData;
    private List<MediaData> filteredData;

    private EnumSet<MediaCounterStatus> currentFilter;

    public MediaCounterAdapter(Context c, int r, List<MediaData> mdl)
    {
        super(c, r, mdl);
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resource = r;

        originalData = mdl;
        filteredData = new ArrayList<>(mdl);

        currentFilter = EnumSet.allOf(MediaCounterStatus.class);
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

    @Override
    public int getCount()
    {
        return filteredData.size();
    }

    @Nullable
    @Override
    public MediaData getItem(int position)
    {
        return filteredData.get(position);
    }

    public MediaData getItem(String mediaName)
    {
        for (MediaData md : originalData)
        {
            if (md.getMediaName().equals(mediaName))
            {
                return md;
            }
        }

        return null;
    }

    public void setFilterMask(EnumSet<MediaCounterStatus> filterMask)
    {
        currentFilter = filterMask;
        filteredData.clear();
        for (MediaData md : originalData)
        {
            if (filterMask.contains(md.getStatus()))
            {
                filteredData.add(md);
            }
        }

        notifyDataSetChanged();
    }

    public void add(MediaData md)
    {
        originalData.add(md);

        Collections.sort(originalData);

        setFilterMask(currentFilter);
    }

    public void remove(int position)
    {
        // If an element is removed from the filtered view, it needs to be removed from the original data as well.
        MediaData md = filteredData.remove(position);

        for (int i = 0; i < originalData.size(); i++)
        {
            if (originalData.get(i).getMediaName().equals(md.getMediaName()))
            {
                originalData.remove(i);
                break;
            }
        }
    }

    public void update()
    {
        setFilterMask(currentFilter);

        notifyDataSetChanged();
    }

    // ViewHolder pattern to increase Adapter performance
    private class ViewHolder
    {
        public TextView name;
        public TextView count;
    }
}