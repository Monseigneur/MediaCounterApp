package com.monseigneur.mediacounterapp.activity;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.monseigneur.mediacounterapp.databinding.MediaStatsListEntryBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaStatsAdapter extends RecyclerView.Adapter<MediaStatsAdapter.ViewHolder>
{
    private List<EpisodeData> episodes;

    public MediaStatsAdapter()
    {
        episodes = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        MediaStatsListEntryBinding binding = MediaStatsListEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        EpisodeData ed = episodes.get(position);

        holder.setData(ed);
    }

    @Override
    public int getItemCount()
    {
        return episodes.size();
    }

    public void setData(List<EpisodeData> newEpisodes)
    {
        if (episodes == null || episodes.isEmpty())
        {
            episodes = newEpisodes;

            notifyItemRangeChanged(0, episodes.size());

            return;
        }

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback()
        {
            @Override
            public int getOldListSize()
            {
                return episodes.size();
            }

            @Override
            public int getNewListSize()
            {
                return newEpisodes.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
            {
                return episodes.get(oldItemPosition).getMediaName().equals(newEpisodes.get(newItemPosition).getMediaName());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
            {
                EpisodeData oldEpisode = episodes.get(oldItemPosition);
                EpisodeData newEpisode = newEpisodes.get(newItemPosition);

                return oldEpisode.equals(newEpisode);
            }
        });

        episodes = newEpisodes;
        result.dispatchUpdatesTo(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final MediaStatsListEntryBinding binding;

        public ViewHolder(@NonNull MediaStatsListEntryBinding b)
        {
            super(b.getRoot());

            binding = b;
        }

        public void setData(EpisodeData ed)
        {
            int statusAppearance = Util.getStatusAppearance(ed.getMediaStatus());

            binding.statsEpisodeNumber.setText(String.valueOf(ed.getEpNum()));
            binding.statsEpisodeNumber.setTextAppearance(statusAppearance);

            binding.statsMediaName.setText(ed.getMediaName());
            binding.statsMediaName.setTextAppearance(statusAppearance);

            binding.statsEpisodeDate.setText(Util.timestampToString(ed.getEpDate()));
            binding.statsEpisodeDate.setTextAppearance(statusAppearance);
        }
    }
}