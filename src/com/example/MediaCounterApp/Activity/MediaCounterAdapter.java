package com.example.MediaCounterApp.Activity;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.MediaCounterApp.Model.MediaData;
import com.example.MediaCounterApp.R;

import java.util.List;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaCounterAdapter extends BaseAdapter {//implements ListAdapter {
    private LayoutInflater inflater;
    private int resource;
    private List<MediaData> mdList;
    private boolean checkBoxEnable;

    public MediaCounterAdapter(Context c, int r, List<MediaData> mdl)
    {
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resource = r;

        mdList = mdl;
        checkBoxEnable = false;
    }

    @Override
    public int getCount() {
        return mdList.size();
    }

    @Override
    public Object getItem(int position) {
        return mdList.get(position);
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

        MediaData md = (MediaData)getItem(position);

        CheckBox cb = (CheckBox)itemView.findViewById(R.id.done_checkbox);
        if (checkBoxEnable)
        {
            cb.setVisibility(View.VISIBLE);
        }
        else
        {
            cb.setVisibility(View.GONE);
        }

        TextView name = (TextView)itemView.findViewById(R.id.name_label);
        name.setText(md.getMediaName());

        TextView count = (TextView)itemView.findViewById(R.id.count_label);
        count.setText(md.getCount() + "");

        return itemView;
    }

    public void remove(int position)
    {
        mdList.remove(position);
    }

    @Override
    public boolean isEmpty() {
        return mdList.isEmpty();
    }

    public void enableDone(boolean enable)
    {
        if (checkBoxEnable != enable)
        {
            checkBoxEnable = enable;
            notifyDataSetChanged();
        }
    }
}