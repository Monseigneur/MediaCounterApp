package com.monseigneur.mediacounterapp.activity;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.MediaStatsListEntryBinding;
import com.monseigneur.mediacounterapp.model.EpisodeData;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;
import com.monseigneur.mediacounterapp.model.Util;

import java.util.List;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaStatsAdapter extends RecyclerView.Adapter<MediaStatsAdapter.ViewHolder>
{
    private final String TAG = "MediaStatsAdapter";

    private final List<EpisodeData> edList;

    public MediaStatsAdapter(List<EpisodeData> edl)
    {
        edList = edl;

        Log.i(TAG, "Constructor: episode count " + edList.size());
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
        EpisodeData ed = edList.get(position);

        holder.setData(ed);
    }

    @Override
    public int getItemCount()
    {
        return edList.size();
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
            int textColor = Util.getStatusColor(ed.getMediaStatus());

            binding.statsEpisodeNumber.setText(String.valueOf(ed.getEpNum()));
            binding.statsEpisodeNumber.setTextColor(textColor);

            binding.statsMediaName.setText(ed.getMediaName());
            binding.statsMediaName.setTextColor(textColor);

            binding.statsEpisodeDate.setText(MediaCounterDB.dateString(ed.getEpDate()));
            binding.statsEpisodeDate.setTextColor(textColor);
        }
    }
}