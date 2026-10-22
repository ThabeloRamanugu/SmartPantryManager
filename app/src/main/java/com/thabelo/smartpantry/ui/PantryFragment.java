package com.thabelo.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thabelo.smartpantry.R;

// My Pantry screen. It shows every ingredient the user has.
public class PantryFragment extends Fragment {

    private PantryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // I push the header down so it does not hide under the status bar
        View header = view.findViewById(R.id.header);
        int basePad = header.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), basePad + bars.top,
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        RecyclerView recycler = view.findViewById(R.id.recycler_pantry);
        TextView emptyText = view.findViewById(R.id.text_empty);
        FloatingActionButton fab = view.findViewById(R.id.fab_add);

        // the click on a card does nothing yet, I add the edit screen later
        adapter = new PantryAdapter(item -> { });
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        PantryViewModel viewModel = new ViewModelProvider(this).get(PantryViewModel.class);

        // every time the database changes, I update the list and the empty message
        viewModel.getItems().observe(getViewLifecycleOwner(), list -> {
            adapter.setItems(list);
            emptyText.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // the add button does nothing yet, I build that screen next
        fab.setOnClickListener(v -> { });
    }
}