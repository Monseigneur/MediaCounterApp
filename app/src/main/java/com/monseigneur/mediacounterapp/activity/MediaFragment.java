package com.monseigneur.mediacounterapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.monseigneur.mediacounterapp.R;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.databinding.FragmentMediaBinding;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.viewmodel.MediaViewModel;

public class MediaFragment extends Fragment
{
    private FragmentMediaBinding binding;
    private MediaAdapter adapter;
    private boolean incLocked;
    private MediaViewModel mediaViewModel;

    private final ListItemClickCallback listItemCallback = (mediaData, clickType) -> {
        Log.i("listItemCallback", "got a click on md " + mediaData.getMediaName() + " type " + clickType);

        switch (clickType)
        {
            case ListItemClickCallback.ItemClickType.INFO -> viewMediaInfo(mediaData);
            case ListItemClickCallback.ItemClickType.INCREMENT -> changeCount(mediaData, true);
            case ListItemClickCallback.ItemClickType.DECREMENT -> changeCount(mediaData, false);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        binding = FragmentMediaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new MediaAdapter(listItemCallback);
        binding.mediaList.setAdapter(adapter);
        binding.mediaList.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mediaViewModel.getAllMedia().observe(getViewLifecycleOwner(), mediaData -> adapter.setMedia(mediaData));

        binding.fab.setOnClickListener(_ -> {
            NavHostFragment.findNavController(MediaFragment.this)
                    .navigate(R.id.action_navigation_media_to_addItemDialogFragment);
        });

        getParentFragmentManager().setFragmentResultListener(InfoFragment.INFO_RESULT, this, (_, result) -> {
            String mediaName = result.getString(InfoFragment.INFO_RESULT_NAME);
            MediaCounterStatus newStatus = result.getSerializable(InfoFragment.INFO_RESULT_STATUS, MediaCounterStatus.class);

            mediaViewModel.changeStatus(mediaName, newStatus);
        });

        getParentFragmentManager().setFragmentResultListener(AddItemDialogFragment.ADD_MEDIA_RESULT, this, (_, result) -> {
            String newMediaName = result.getString(AddItemDialogFragment.ADD_MEDIA_RESULT_NAME);

            handleNewMedia(newMediaName);
        });

        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater)
            {
                MenuItem filter = menu.findItem(R.id.action_lock);
                filter.setVisible(true);
                filter.setTitle(R.string.unlock_inc);
                incLocked = true;
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem)
            {
                if (menuItem.getItemId() != R.id.action_lock)
                {
                    return false;
                }

                incLocked = !incLocked;
                if (incLocked)
                {
                    menuItem.setTitle(R.string.unlock_inc);
                }
                else
                {
                    menuItem.setTitle(R.string.lock_inc);
                }

                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        incLocked = true;

        return root;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

    private void changeCount(MediaData media, boolean increment)
    {
        if (incLocked)
        {
            Log.i("changeCount", "Locked for increment, exit early. increment = " + increment);
            return;
        }

        if (media == null)
        {
            Log.w("changeCount", "MediaData is null!");
            return;
        }

        Log.i("changeCount", "increment " + increment + " " + media);

        if (increment)
        {
            mediaViewModel.addEpisode(media.getMediaName());
        }
        else
        {
            mediaViewModel.removeEpisode(media.getMediaName());
        }
    }

    public void viewMediaInfo(MediaData media)
    {
        Bundle b = new Bundle();
        b.putSerializable(InfoFragment.INFO_MEDIA, media);

        NavHostFragment.findNavController(MediaFragment.this)
                .navigate(R.id.action_MediaFragment_to_InfoFragment, b);
    }

    private void handleNewMedia(String newMediaName)
    {
        Log.i("handleNewMedia", "new media [" + newMediaName + "]");

        if (newMediaName == null || newMediaName.isEmpty())
        {
            showToast("Invalid name");
            return;
        }

        if (!mediaViewModel.addNewMedia(newMediaName))
        {
            // Media already exists, show a toast
            showToast(getString(R.string.duplicate_media));
        }
    }

    private void showToast(String text)
    {
        Log.i("showToast", "showing toast [" + text + "]");
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
