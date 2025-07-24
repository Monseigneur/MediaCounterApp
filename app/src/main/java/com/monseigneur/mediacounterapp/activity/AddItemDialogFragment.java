package com.monseigneur.mediacounterapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.monseigneur.mediacounterapp.databinding.DialogAddMediaBinding;

public class AddItemDialogFragment extends DialogFragment
{
    public static String ADD_MEDIA_RESULT = "add_media_result";
    public static String ADD_MEDIA_RESULT_NAME = "add_media_result_name";

    private DialogAddMediaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = DialogAddMediaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.cancelMediaButton.setOnClickListener(_ -> {
            dismiss();
        });

        binding.addMediaButton.setOnClickListener(_ -> {
            String newMediaName = binding.addMediaName.getText().toString();

            Bundle result = new Bundle();
            result.putString(ADD_MEDIA_RESULT_NAME, newMediaName);
            getParentFragmentManager().setFragmentResult(ADD_MEDIA_RESULT, result);

            dismiss();
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
