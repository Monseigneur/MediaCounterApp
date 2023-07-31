package com.monseigneur.mediacounterapp.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.monseigneur.mediacounterapp.databinding.MediaCounterListEntryBinding;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.model.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class MediaCounterAdapter extends RecyclerView.Adapter<MediaCounterAdapter.ViewHolder>
{
    private final List<MediaData> originalData;
    private final List<MediaData> filteredData;
    private final View.OnClickListener onItemClickListener;

    private EnumSet<MediaCounterStatus> currentFilter;

    public MediaCounterAdapter(List<MediaData> mdl, View.OnClickListener itemClickListener)
    {
        originalData = mdl;
        filteredData = new ArrayList<>(mdl);
        onItemClickListener = itemClickListener;

        currentFilter = EnumSet.allOf(MediaCounterStatus.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        MediaCounterListEntryBinding binding = MediaCounterListEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        MediaData md = filteredData.get(position);

        holder.setData(md);
    }

    @Override
    public int getItemCount()
    {
        return filteredData.size();
    }

    @Nullable
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

        Collections.sort(originalData, MediaData.BY_LAST_EPISODE);

        setFilterMask(currentFilter);
    }

    public void remove(MediaData md)
    {
        // If an element is removed from the filtered view, it needs to be removed from the original data as well.
        for (int i = 0; i < filteredData.size(); i++)
        {
            if (filteredData.get(i).getMediaName().equals(md.getMediaName()))
            {
                filteredData.remove(i);
                break;
            }
        }

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
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final MediaCounterListEntryBinding binding;

        ViewHolder(@NonNull MediaCounterListEntryBinding b, View.OnClickListener onItemClickListener)
        {
            super(b.getRoot());

            binding = b;

            setOnClickListener(binding.nameLabel, onItemClickListener);
            setOnClickListener(binding.incButton, onItemClickListener);
            setOnClickListener(binding.decButton, onItemClickListener);
        }

        public void setData(MediaData md)
        {
            int nameColor = Util.getStatusColor(md.getStatus());

            binding.nameLabel.setTextColor(nameColor);
            binding.nameLabel.setText(md.getMediaName());

            binding.countLabel.setText(String.valueOf(md.getCount()));
        }

        private void setOnClickListener(View view, View.OnClickListener onItemClickListener)
        {
            view.setTag(this);
            view.setOnClickListener(onItemClickListener);
        }
    }
}