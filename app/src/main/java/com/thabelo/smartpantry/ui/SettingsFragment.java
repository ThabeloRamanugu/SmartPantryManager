package com.thabelo.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.thabelo.smartpantry.R;
import com.thabelo.smartpantry.util.AppSettings;

// My Settings screen. The choices are saved straight away.
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // I push the header below the status bar
        View header = view.findViewById(R.id.header);
        int basePad = header.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), basePad + bars.top,
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        MaterialSwitch alertSwitch = view.findViewById(R.id.switch_alerts);
        RadioGroup group = view.findViewById(R.id.group_days);

        // I show the saved choices first
        alertSwitch.setChecked(AppSettings.alertsEnabled(requireContext()));
        int saved = AppSettings.soonDays(requireContext());
        int radioId = saved == 1 ? R.id.radio_1
                : saved == 5 ? R.id.radio_5
                  : saved == 7 ? R.id.radio_7
                    : R.id.radio_3;
        group.check(radioId);

        // and I save every change the moment it happens
        alertSwitch.setOnCheckedChangeListener((button, checked) ->
                AppSettings.setAlertsEnabled(requireContext(), checked));

        group.setOnCheckedChangeListener((g, checkedId) -> {
            int days = checkedId == R.id.radio_1 ? 1
                    : checkedId == R.id.radio_5 ? 5
                      : checkedId == R.id.radio_7 ? 7
                        : 3;
            AppSettings.setSoonDays(requireContext(), days);
        });
    }
}