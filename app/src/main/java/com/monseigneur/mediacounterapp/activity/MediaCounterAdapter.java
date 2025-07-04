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
    private final View.OnClickListener onItemClickListener;
    private List<MediaData> originalData;
    private List<MediaData> filteredData;
    private boolean showAll;

    public MediaCounterAdapter(View.OnClickListener itemClickListener)
    {
        onItemClickListener = itemClickListener;
        showAll = true;

        originalData = new ArrayList<>();
        filteredData = new ArrayList<>();
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

    public void updateStatus(String mediaName, MediaCounterStatus newStatus)
    {
        int foundIndex = -1;
        for (int i = 0; i < originalData.size(); i++)
        {
            if (originalData.get(i).getMediaName().equals(mediaName))
            {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex == -1)
        {
            return;
        }

        MediaData md = originalData.get(foundIndex);
        md.setStatus(newStatus);

        setFilterMask(showAll);
    }

    public void setFilterMask(boolean filterShowAll)
    {
        EnumSet<MediaCounterStatus> filterMask = filterShowAll ? MediaCounterStatus.ALL_STATUSES : MediaCounterStatus.WATCHABLE_STATUSES;

        showAll = filterShowAll;
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

        originalData.sort(MediaData.BY_LAST_EPISODE);

        setFilterMask(showAll);
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

        notifyItemRemoved(position);
    }

    public void update(List<MediaData> newMdList)
    {
        originalData = newMdList;
        filteredData = new ArrayList<>(newMdList);

        notifyItemRangeChanged(0, originalData.size());
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
            int statusAppearance = Util.getStatusAppearance(md.getStatus());

            binding.nameLabel.setTextAppearance(statusAppearance);
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