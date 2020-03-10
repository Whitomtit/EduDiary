package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.model.Category;
import com.example.myapplication.model.Record;
import com.example.myapplication.model.Item;
import com.example.myapplication.util.RecordDbManager;
import com.example.myapplication.util.Subjects;
import com.example.myapplication.util.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity {
    AutoCompleteTextView subjectPicker;
    TextInputEditText datePicker;
    ExtendedFloatingActionButton fabAddCategory;
    Record record;
    LinearLayout categoryList;
    Button buttonSave, buttonCancel;
    RecordDbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = new RecordDbManager(this);

        fabAddCategory = findViewById(R.id.fab_add_category);
        subjectPicker = findViewById(R.id.subject_picker);
        datePicker = findViewById(R.id.date_picker);
        categoryList = findViewById(R.id.category_list);
        buttonSave = findViewById(R.id.edit_button_save);
        buttonCancel = findViewById(R.id.edit_button_cancel);

        //if the activity was started with code 2, there need to be a record in intent
        Intent intent = getIntent();
        setData((Record) intent.getSerializableExtra("record"));

        //Adapter with subjects' names for a dropdown menu
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.menu.edit_dropdown_subjects_menu,
                        Subjects.getInstance().getSubjectsName());
        subjectPicker.setAdapter(adapter);

        //User is editing the subject name, there isn't need in the error message
        subjectPicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utils.getLayoutFromEditText(subjectPicker).setErrorEnabled(false);
            }
        });

        //If user press the date picker, a date picker dialog starts
        datePicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) return;
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(EditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                calendar.set(year, month,day, 0, 0);
                                datePicker.setText(Utils.dateToString(calendar.getTime()));
                            }
                        }, year, month, day);
                picker.show();
                //Date set, there's no need in the error message
                Utils.getLayoutFromEditText(datePicker).setErrorEnabled(false);

            }
        });

        //if user press an action button in the date picker and there's not any category's input it will create the first
        datePicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (record.isEmpty()) {
                    fabAddCategory.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        //if user press the fab, a new category will be created
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creates a dialog to enter category name
                final EditText input = new EditText(EditActivity.this);
                input.setSingleLine();
                input.setHint(getString(R.string.add_category_hint));
                final TextView title = new TextView(EditActivity.this);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(getString(R.string.add_category_title));
                final AlertDialog dialog = new MaterialAlertDialogBuilder(EditActivity.this)
                        .setTitle(R.string.add_category_title)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Category category = addCategory(input.getText().toString());
                                Utils.getEditTextFromLayout((TextInputLayout) category.getView()).requestFocus();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setView(input,
                                Utils.convertPixelsToDp(256, EditActivity.this),
                                0,
                                Utils.convertPixelsToDp(256, EditActivity.this),
                                0)
                        .create();
                dialog.show();

                //calls to a keyboard be started
                input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        input.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager inputMethodManager = (InputMethodManager) EditActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (inputMethodManager != null)
                                    inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                    }
                });
                input.requestFocus();

                //if the input is empty there is not a positive button (save)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!editable.toString().trim().isEmpty());
                    }
                });

                //prevent pressing an action button to save the category without name
                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        boolean handled = false;
                        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled() && dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()) {
                            handled = true;
                        }
                        return handled;
                    }
                });
            }
        });

        //if all inputs are filled properly, save the record to database and return to the previous activity
        //else show all errors
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;
                if (subjectPicker.getText().toString().isEmpty()) {
                    Utils.getLayoutFromEditText(subjectPicker).setError(getString(R.string.error_required));
                    error = true;
                } else {
                    record.setSubject(subjectPicker.getText().toString());
                }
                if (datePicker.getText().toString().isEmpty()) {
                    Utils.getLayoutFromEditText(datePicker).setError(getString(R.string.error_required));
                    error = true;
                } else {
                    try {
                        record.setDate(Utils.dateFromString(datePicker.getText().toString()));
                    } catch (ParseException e) {
                        Utils.getLayoutFromEditText(datePicker).setError(getString(R.string.error_date_format));
                        error = true;
                    }
                }
                if (record.isEmpty()) {
                    Snackbar.make(fabAddCategory, getString(R.string.error_no_categories), Snackbar.LENGTH_SHORT).show();
                    error = true;
                } else {
                    for (Category category : record.getCategoryList()) {
                        if (category.isEmpty()) {
                            ((TextInputLayout)category.getView()).setError(getString(R.string.error_empty_category));
                            error = true;
                        }
                    }
                }

                if (error) return;

                db.updateRecord(record);

                setResult(Activity.RESULT_OK);
                finish();

            }
        });

        //If cancel button pressed, finish activity and return to previous
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    //Generates and fills view according to record object
    private void setData(Record record) {
        if (record == null) {
            this.record = new Record();
            return;
        }
        this.record = new Record(record);

        subjectPicker.setText(record.getSubject());
        datePicker.setText(record.getDateAsString());

        for (Category category : record.getCategoryList()) {
            Category activityCategory = addCategory(category.getName());
            for (Item item : category.getItemList())
                addItem(item, activityCategory);
        }

    }

    private Category addCategory(String name) {
        final TextInputLayout categoryView = (TextInputLayout) getLayoutInflater().
                inflate(R.layout.edit_category_entry, categoryList, false);
        final Category category = new Category(name, (ChipGroup) categoryView.getChildAt(1), categoryView);

        categoryView.setHint(category.getName());
        //If end icon clicked category needs to be deleted
        categoryView.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record.removeCategory(category);
                categoryList.removeView(categoryView);
            }
        });

        //If user press action button (sent or enter) text from editor is added to chip
        Utils.getEditTextFromLayout(categoryView).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (!v.getText().toString().trim().isEmpty()) {
                    addItem(v, category);
                    v.setText("");

                    handled = true;

                    //Item was added, so there's no need in error message
                    categoryView.setErrorEnabled(false);
                }
                return handled;
            }
        });

        categoryList.addView(categoryView);
        record.addCategory(category);

        return category;
    }
    private void addItem(final Item item, final Category category) {
        final Chip itemView = (Chip) getLayoutInflater().inflate(R.layout.edit_category_chip,
                category.getItemBox(), false);

        itemView.setText(item.getContent());
        itemView.setChecked(item.isDone());
        //If end icon clicked item needs to be deleted
        itemView.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category.getItemBox().removeView(itemView);
                category.removeItem(item);
            }
        });

        category.getItemBox().addView(itemView);
        category.addItem(item);


    }
    private void addItem(TextView v, final Category category) {
        Item item = new Item(v.getText().toString());
        addItem(item, category);
    }
}
