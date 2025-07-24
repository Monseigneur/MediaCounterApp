package com.monseigneur.mediacounterapp.activity;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monseigneur.mediacounterapp.databinding.InfoListEntryBinding;
import com.monseigneur.mediacounterapp.model.Util;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder>
{
    private final List<Long> episodeDates;

    public InfoAdapter(List<Long> dates)
    {
        episodeDates = dates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        InfoListEntryBinding binding = InfoListEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        long date = episodeDates.get(position);

        holder.setData(position, date);
    }

    @Override
    public int getItemCount()
    {
        return episodeDates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final InfoListEntryBinding binding;

        public ViewHolder(@NonNull InfoListEntryBinding b)
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
