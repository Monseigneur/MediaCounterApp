package com.monseigneur.mediacounterapp.activity;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.monseigneur.mediacounterapp.databinding.EpisodesListEntryBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milan on 5/21/2016.
 */
public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder>
{
    private List<EpisodeData> episodes;

    public EpisodesAdapter()
    {
        episodes = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        EpisodesListEntryBinding binding = EpisodesListEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

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
        private final EpisodesListEntryBinding binding;

        public ViewHolder(@NonNull EpisodesListEntryBinding b)
        {
            super(b.getRoot());

            binding = b;
        }

        public void setData(EpisodeData ed)
        {
            int statusAppearance = Util.getStatusAppearance(ed.getMediaStatus());

            binding.episodesEpisodeNumber.setText(String.valueOf(ed.getEpNum()));
            binding.episodesEpisodeNumber.setTextAppearance(statusAppearance);

            binding.episodesMediaName.setText(ed.getMediaName());
            binding.episodesMediaName.setTextAppearance(statusAppearance);

            binding.episodesEpisodeDate.setText(Util.timestampToString(ed.getEpDate()));
            binding.episodesEpisodeDate.setTextAppearance(statusAppearance);
        }
    }
}