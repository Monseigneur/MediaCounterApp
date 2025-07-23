package com.monseigneur.mediacounterapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.databinding.FragmentEpisodesBinding;
import com.monseigneur.mediacounterapp.viewmodel.MediaViewModel;

public class EpisodesFragment extends Fragment
{
    private FragmentEpisodesBinding binding;
    private EpisodesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        MediaViewModel mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        binding = FragmentEpisodesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new EpisodesAdapter();
        binding.episodesList.setAdapter(adapter);
        binding.episodesList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mediaViewModel.getAllEpisodes().observe(getViewLifecycleOwner(), episodes -> {
            String text = "Total episodes: " + episodes.size();
            binding.episodesTotalLabel.setText(text);
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
