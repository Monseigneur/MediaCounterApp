package com.monseigneur.mediacounterapp.activity;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.monseigneur.mediacounterapp.databinding.MediaCounterListEntryBinding;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.model.Util;

import java.util.ArrayList;
import java.util.List;

public class MediaCounterAdapter extends RecyclerView.Adapter<MediaCounterAdapter.ViewHolder>
{
    private final ListItemClickCallback listItemCallback;
    private List<MediaData> mediaList;

    public MediaCounterAdapter(ListItemClickCallback listItemClickCallback)
    {
        listItemCallback = listItemClickCallback;

        mediaList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        MediaCounterListEntryBinding binding = MediaCounterListEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        MediaData md = mediaList.get(position);

        holder.setData(md, listItemCallback);
    }

    @Override
    public int getItemCount()
    {
        return mediaList.size();
    }

    @Nullable
    public MediaData getItem(int position)
    {
        return mediaList.get(position);
    }

    public void setMedia(List<MediaData> newMediaList)
    {
        if (mediaList == null || mediaList.isEmpty())
        {
            mediaList = newMediaList;

            notifyItemRangeChanged(0, mediaList.size());

            return;
        }

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback()
        {
            @Override
            public int getOldListSize()
            {
                return mediaList.size();
            }

            @Override
            public int getNewListSize()
            {
                return newMediaList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
            {
                return mediaList.get(oldItemPosition).getMediaName().equals(newMediaList.get(newItemPosition).getMediaName());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
            {
                MediaData oldMedia = mediaList.get(oldItemPosition);
                MediaData newMedia = newMediaList.get(newItemPosition);

                return oldMedia.equals(newMedia);
            }
        });

        mediaList = newMediaList;
        result.dispatchUpdatesTo(this);
    }

    // ViewHolder pattern to increase Adapter performance
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final MediaCounterListEntryBinding binding;
        private MediaData media;

        ViewHolder(@NonNull MediaCounterListEntryBinding b)
        {
            super(b.getRoot());

            binding = b;
        }

        public void setData(MediaData md, ListItemClickCallback listItemCallback)
        {
            media = md;

            int statusAppearance = Util.getStatusAppearance(md.getStatus());

            binding.nameLabel.setTextAppearance(statusAppearance);
            binding.nameLabel.setText(md.getMediaName());
            binding.countLabel.setText(String.valueOf(md.getCount()));

            binding.nameLabel.setOnClickListener(_ -> listItemCallback.onClick(media, ListItemClickCallback.ItemClickType.INFO));
            binding.countLabel.setOnClickListener(_ -> listItemCallback.onClick(media, ListItemClickCallback.ItemClickType.INFO));
            binding.incButton.setOnClickListener(_ -> listItemCallback.onClick(media, ListItemClickCallback.ItemClickType.INCREMENT));
            binding.decButton.setOnClickListener(_ -> listItemCallback.onClick(media, ListItemClickCallback.ItemClickType.DECREMENT));

        }
    }
}