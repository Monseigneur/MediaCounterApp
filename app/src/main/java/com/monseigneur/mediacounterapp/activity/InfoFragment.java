package com.monseigneur.mediacounterapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.FragmentInfoBinding;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.model.MediaData;
import com.monseigneur.mediacounterapp.model.Util;

public class InfoFragment extends Fragment
{
    public static String INFO_MEDIA = "info_media";
    public static String INFO_RESULT = "info_result";
    public static String INFO_RESULT_NAME = "info_result_name";
    public static String INFO_RESULT_STATUS = "info_result_status";

    private FragmentInfoBinding binding;
    private String mediaName;
    private MediaCounterStatus currentStatus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MediaData media = getArguments().getSerializable(INFO_MEDIA, MediaData.class);

        mediaName = media.getMediaName();
        binding.infoMediaName.setText(mediaName);

        String addedDate = Util.timestampToString(media.getAddedDate());
        binding.infoAddedDate.setText("Added: " + addedDate);

        binding.infoEpisodeCount.setText(String.valueOf(media.getCount()));

        binding.infoStatusButton.setOnClickListener(this::changeStatus);

        currentStatus = media.getStatus();
        setButtonText();

        InfoAdapter adapter = new InfoAdapter(media.getEpDates());

        binding.infoEpisodeList.setAdapter(adapter);
        binding.infoEpisodeList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

    private void setButtonText()
    {
        int textId = switch (currentStatus)
        {
            case ONGOING -> R.string.ongoing_text;
            case COMPLETE -> R.string.complete_text;
            case DROPPED -> R.string.dropped_text;
            default -> R.string.new_text;
        };

        binding.infoStatusButton.setText(textId);
    }

    private void changeStatus(View view)
    {
        // Cycle through the statuses
        currentStatus = switch (currentStatus)
        {
            case ONGOING -> MediaCounterStatus.COMPLETE;
            case COMPLETE -> MediaCounterStatus.DROPPED;
            case DROPPED -> MediaCounterStatus.NEW;
            default -> MediaCounterStatus.ONGOING;
        };

        Bundle result = new Bundle();
        result.putString(INFO_RESULT_NAME, mediaName);
        result.putSerializable(INFO_RESULT_STATUS, currentStatus);
        getParentFragmentManager().setFragmentResult(INFO_RESULT, result);

        setButtonText();
    }
}
