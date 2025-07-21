package com.monseigneur.mediacounterapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.databinding.FragmentStatsBinding;
import com.monseigneur.mediacounterapp.viewmodel.MediaViewModel;

public class StatsFragment extends Fragment
{
    private FragmentStatsBinding binding;
    MediaViewModel mediaViewModel;
    MediaStatsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        binding = FragmentStatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new MediaStatsAdapter();
        binding.mediaStatsList.setAdapter(adapter);
        binding.mediaStatsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mediaViewModel.getAllEpisodes().observe(getViewLifecycleOwner(), episodes -> {
            String text = "Total episodes: " + episodes.size();
            binding.mediaStatsTotalLabel.setText(text);
            adapter.setData(episodes);
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
