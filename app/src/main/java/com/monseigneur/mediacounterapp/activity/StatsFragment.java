package com.monseigneur.mediacounterapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.monseigneur.mediacounterapp.databinding.FragmentStatsBinding;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.viewmodel.MediaViewModel;

public class StatsFragment extends Fragment
{
    private FragmentStatsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MediaViewModel mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        mediaViewModel.getStats().observe(getViewLifecycleOwner(), statsData -> {
            binding.statsMediaCount.setText("Media count: " + String.valueOf(statsData.mediaCount));
            binding.statsMediaNew.setText("New: " + String.valueOf(statsData.countByStatus.getOrDefault(MediaCounterStatus.NEW, 0)));
            binding.statsMediaOngoing.setText("Ongoing: " + String.valueOf(statsData.countByStatus.getOrDefault(MediaCounterStatus.ONGOING, 0)));
            binding.statsMediaComplete.setText("Complete: " + String.valueOf(statsData.countByStatus.getOrDefault(MediaCounterStatus.COMPLETE, 0)));
            binding.statsMediaDropped.setText("Dropped: " + String.valueOf(statsData.countByStatus.getOrDefault(MediaCounterStatus.DROPPED, 0)));
            binding.statsEpisodeCount.setText("Episode count: " + String.valueOf(statsData.episodeCount));
        });

        return root;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
