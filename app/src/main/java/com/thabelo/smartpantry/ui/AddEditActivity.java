package com.thabelo.smartpantry.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.thabelo.smartpantry.R;
import com.thabelo.smartpantry.data.AppDatabase;
import com.thabelo.smartpantry.data.PantryItem;
import com.thabelo.smartpantry.util.IngredientNormalizer;

import java.text.DateFormat;
import java.util.Date;

// My Add/Edit screen. With no id in the Intent it adds, with an id it edits.
public class AddEditActivity extends AppCompatActivity {

    // the key I use to pass the item id in the Intent
    public static final String EXTRA_ITEM_ID = "item_id";

    // the units I allow, the recipes use the same ones
    private static final String[] UNITS = {"g", "kg", "ml", "l", "piece", "tsp", "tbsp"};

    private TextInputLayout layoutName, layoutQuantity, layoutUnit;
    private TextInputEditText editName, editQuantity, editExpiry;
    private AutoCompleteTextView editUnit;
    private Button btnDelete;

    private PantryItem current;   // the item I am editing, null when adding
    private Long expiryMillis;    // the chosen expiry date, null if none

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // I find all my views
        TextView header = findViewById(R.id.header);
        layoutName = findViewById(R.id.layout_name);
        layoutQuantity = findViewById(R.id.layout_quantity);
        layoutUnit = findViewById(R.id.layout_unit);
        editName = findViewById(R.id.edit_name);
        editQuantity = findViewById(R.id.edit_quantity);
        editUnit = findViewById(R.id.edit_unit);
        editExpiry = findViewById(R.id.edit_expiry);
        btnDelete = findViewById(R.id.btn_delete);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnClear = findViewById(R.id.btn_clear_date);

        // I push the header below the status bar
        int basePad = header.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), basePad + bars.top,
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // I fill the unit dropdown
        editUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, UNITS));

        editExpiry.setOnClickListener(v -> openDatePicker());
        btnClear.setOnClickListener(v -> {
            expiryMillis = null;
            editExpiry.setText("");
        });
        btnSave.setOnClickListener(v -> save());
        btnDelete.setOnClickListener(v -> confirmDelete());

        // if the Intent carries an id, I am editing, so I load that item
        int itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            header.setText(R.string.title_edit);
            btnDelete.setVisibility(View.VISIBLE);
            AppDatabase.dbExecutor.execute(() -> {
                PantryItem item = AppDatabase.getInstance(this).pantryDao().getById(itemId);
                runOnUiThread(() -> fillForm(item));
            });
        }
    }

    // I copy the saved item into the form fields
    private void fillForm(PantryItem item) {
        if (item == null) return;
        current = item;
        editName.setText(item.name);
        editQuantity.setText(item.quantity == Math.floor(item.quantity)
                ? String.valueOf((long) item.quantity)
                : String.valueOf(item.quantity));
        editUnit.setText(item.unit, false);   // false so the list is not filtered
        expiryMillis = item.expiryDate;
        showExpiry();
    }

    // I show the chosen date in the expiry box
    private void showExpiry() {
        if (expiryMillis == null) {
            editExpiry.setText("");
        } else {
            editExpiry.setText(DateFormat.getDateInstance().format(new Date(expiryMillis)));
        }
    }

    private void openDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker().build();
        picker.addOnPositiveButtonClickListener(selection -> {
            expiryMillis = selection;
            showExpiry();
        });
        picker.show(getSupportFragmentManager(), "expiry_picker");
    }

    // I check every field and return true only if all of them are fine
    private boolean validate() {
        boolean ok = true;

        String name = editName.getText().toString().trim();
        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_empty));
            ok = false;
        } else if (!name.matches("[\\p{L} '\\-]+")) {
            layoutName.setError(getString(R.string.error_name_letters));
            ok = false;
        } else {
            layoutName.setError(null);
        }

        String qtyText = editQuantity.getText().toString().trim();
        if (qtyText.isEmpty()) {
            layoutQuantity.setError(getString(R.string.error_qty_empty));
            ok = false;
        } else {
            try {
                if (Double.parseDouble(qtyText) <= 0) {
                    layoutQuantity.setError(getString(R.string.error_qty_invalid));
                    ok = false;
                } else {
                    layoutQuantity.setError(null);
                }
            } catch (NumberFormatException e) {
                layoutQuantity.setError(getString(R.string.error_qty_invalid));
                ok = false;
            }
        }

        if (editUnit.getText().toString().trim().isEmpty()) {
            layoutUnit.setError(getString(R.string.error_unit_empty));
            ok = false;
        } else {
            layoutUnit.setError(null);
        }

        return ok;
    }

    // I save a new item or update the one I am editing
    private void save() {
        if (!validate()) return;

        String name = editName.getText().toString().trim();
        boolean isNew = (current == null);
        PantryItem item = isNew ? new PantryItem() : current;

        item.name = name;
        item.normalizedName = IngredientNormalizer.normalize(name);
        item.quantity = Double.parseDouble(editQuantity.getText().toString().trim());
        item.unit = editUnit.getText().toString().trim();
        item.expiryDate = expiryMillis;

        AppDatabase.dbExecutor.execute(() -> {
            if (isNew) {
                AppDatabase.getInstance(this).pantryDao().insert(item);
            } else {
                AppDatabase.getInstance(this).pantryDao().update(item);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    // I ask first so nothing gets deleted by accident
    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_msg)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.btn_delete, (d, w) ->
                        AppDatabase.dbExecutor.execute(() -> {
                            AppDatabase.getInstance(this).pantryDao().delete(current);
                            runOnUiThread(() -> {
                                Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }))
                .show();
    }
}