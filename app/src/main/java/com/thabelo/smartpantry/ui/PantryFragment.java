package com.thabelo.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thabelo.smartpantry.R;

// My Pantry screen. For now it only shows a title, I add the list next.
public class PantryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TextView text = new TextView(requireContext());
        text.setText(R.string.title_pantry);
        text.setPadding(48, 48, 48, 48);
        return text;
    }
}