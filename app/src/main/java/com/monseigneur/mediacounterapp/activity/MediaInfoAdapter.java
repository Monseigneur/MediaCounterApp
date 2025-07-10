package com.monseigneur.mediacounterapp.activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monseigneur.mediacounterapp.databinding.MediaInfoListEntryBinding;
import com.monseigneur.mediacounterapp.model.Util;

import java.util.List;

public class MediaInfoAdapter extends RecyclerView.Adapter<MediaInfoAdapter.ViewHolder>
{
    private final String TAG = "MediaInfoEpisodeAdapter";

    private final List<Long> epDates;

    MediaInfoAdapter(List<Long> dates)
    {
        Log.i(TAG, "Constructor: count of dates " + dates.size());

        epDates = dates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        MediaInfoListEntryBinding binding = MediaInfoListEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        long date = epDates.get(position);

        holder.setData(position, date);
    }

    @Override
    public int getItemCount()
    {
        return epDates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final MediaInfoListEntryBinding binding;

        public ViewHolder(@NonNull MediaInfoListEntryBinding b)
        {
            super(b.getRoot());

            binding = b;
        }

        public void setData(int position, long date)
        {
            binding.episodeNumber.setText(String.valueOf(position + 1));
            binding.episodeDate.setText(Util.timestampToString(date));
        }
    }
}
