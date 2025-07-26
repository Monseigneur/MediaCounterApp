package com.monseigneur.mediacounterapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.monseigneur.mediacounterapp.R;
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
            binding.statsMediaCount.setText(getString(R.string.stats_media_count, statsData.mediaCount));
            binding.statsMediaNew.setText(getString(R.string.stats_media_count_new,
                    statsData.countByStatus.getOrDefault(MediaCounterStatus.NEW, 0)));
            binding.statsMediaOngoing.setText(getString(R.string.stats_media_count_ongoing,
                    statsData.countByStatus.getOrDefault(MediaCounterStatus.ONGOING, 0)));
            binding.statsMediaComplete.setText(getString(R.string.stats_media_count_complete,
                    statsData.countByStatus.getOrDefault(MediaCounterStatus.COMPLETE, 0)));
            binding.statsMediaDropped.setText(getString(R.string.stats_media_count_dropped,
                    statsData.countByStatus.getOrDefault(MediaCounterStatus.DROPPED, 0)));
            binding.statsEpisodeCount.setText(getString(R.string.stats_episode_count, statsData.episodeCount));
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
