package com.monseigneur.mediacounterapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.monseigneur.mediacounterapp.R;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.databinding.FragmentMediaBinding;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.viewmodel.MediaViewModel;

public class MediaFragment extends Fragment
{
    public static final String MEDIA_COUNTER_NAME = "media_name";
    public static final String MEDIA_INFO_STATUS = "media_info_status";

    private FragmentMediaBinding binding;
    private MediaCounterAdapter adapter;
    private boolean incLocked;
    private MediaViewModel mediaViewModel;

    private final ActivityResultLauncher<Intent> newMediaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK)
                {
                    handleNewMedia(result.getData());
                }
            });

    private final ActivityResultLauncher<Intent> showInfoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK)
                {
                    handleStatusChange(result.getData());
                }
            });

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

        adapter = new MediaCounterAdapter(listItemCallback);
        binding.mediaList.setAdapter(adapter);
        binding.mediaList.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mediaViewModel.getAllMedia().observe(getViewLifecycleOwner(), mediaData -> adapter.setMedia(mediaData));

        binding.viewCheckBox.setOnCheckedChangeListener((_, _) -> showToast("Not yet implemented"));
        binding.randomMediaButton.setOnClickListener(_ -> getRandomMedia());
        binding.lockButton.setOnClickListener(_ -> setLockState(!incLocked));

        binding.fab.setOnClickListener(_ -> newMediaLauncher.launch(new Intent(getActivity(), MediaCounterAddActivity.class)));

        getParentFragmentManager().setFragmentResultListener(InfoFragment.INFO_RESULT, this, (_, result) -> {
            String mediaName = result.getString(InfoFragment.INFO_RESULT_NAME);
            MediaCounterStatus status = result.getSerializable(InfoFragment.INFO_RESULT_STATUS, MediaCounterStatus.class);

            mediaViewModel.changeStatus(mediaName, status);
        });

        setLockState(true);

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

    private void handleNewMedia(Intent newMediaIntent)
    {
        if (newMediaIntent == null)
        {
            return;
        }

        String name = newMediaIntent.getStringExtra(MEDIA_COUNTER_NAME);

        Log.i("handleNewMedia", "new media [" + name + "]");

        if (name == null || name.isEmpty())
        {
            showToast("Invalid name");
            return;
        }

        if (!mediaViewModel.addNewMedia(name))
        {
            // Media already exists, show a toast
            showToast(getString(R.string.duplicate_media));
        }
    }

    private void handleStatusChange(Intent statusChangeIntent)
    {
        if (statusChangeIntent == null)
        {
            return;
        }

        MediaCounterStatus newStatus = statusChangeIntent.getSerializableExtra(MEDIA_INFO_STATUS, MediaCounterStatus.class);
        String name = statusChangeIntent.getStringExtra(MediaCounterActivity.MEDIA_COUNTER_NAME);

        Log.i("handleStatusChange", "media info status change " + newStatus + " for media [" + name + "]");
        mediaViewModel.changeStatus(name, newStatus);
    }

    private void getRandomMedia()
    {
        String randomMedia = mediaViewModel.getRandomMediaName();

        if (randomMedia != null)
        {
            showToast(randomMedia);
        }
        else
        {
            showToast(getString(R.string.no_random));
        }
    }

    private void showToast(String text)
    {
        Log.i("showToast", "showing toast [" + text + "]");
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setLockState(boolean lock)
    {
        incLocked = lock;
        if (lock)
        {
            binding.lockButton.setText(R.string.unlock_inc);
            binding.lockButton.setBackgroundColor(0);
        }
        else
        {
            binding.lockButton.setText(R.string.lock_inc);
            binding.lockButton.setBackgroundColor(Color.RED);
        }
    }
}
