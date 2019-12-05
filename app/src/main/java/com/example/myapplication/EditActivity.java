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

import com.example.myapplication.object.Category;
import com.example.myapplication.object.Homework;
import com.example.myapplication.object.Item;
import com.example.myapplication.utils.HomeworkDbManager;
import com.example.myapplication.utils.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity {
    AutoCompleteTextView subjectPicker;
    TextInputEditText datePicker;
    FloatingActionButton fabAddCategory;
    Homework homework;
    LinearLayout categoryList;
    Button buttonSave, buttonCancel;
    HomeworkDbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = new HomeworkDbManager(this);

        fabAddCategory = findViewById(R.id.fab_add_category);
        subjectPicker = findViewById(R.id.subject_picker);
        datePicker = findViewById(R.id.date_picker);
        categoryList = findViewById(R.id.category_list);
        buttonSave = findViewById(R.id.edit_button_save);
        buttonCancel = findViewById(R.id.edit_button_cancel);

        homework = new Homework();
        Intent intent = getIntent();
        setData((Homework) intent.getSerializableExtra("homeworkRecycler"));

        String[] SUBJECTS = new String[] {"Law", "Sport", "Literature", "Physics", "Chemistry",
            "Biology", "Programming", "Robophysics", "English", "Math"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.edit_dropdown_menu_subjects,
                        SUBJECTS);
        subjectPicker.setAdapter(adapter);

        subjectPicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utils.getLayoutFromEditText(subjectPicker).setErrorEnabled(false);
            }
        });

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
                Utils.getLayoutFromEditText(datePicker).setErrorEnabled(false);

            }
        });
        datePicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (homework.isEmpty()) {
                    fabAddCategory.performClick();
                    handled = true;
                }
                return handled;
            }
        });


        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(EditActivity.this);
                input.setSingleLine();
                input.setHint(getString(R.string.add_category_hint));
                final TextView title = new TextView(EditActivity.this);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(getString(R.string.add_category_title));
                final AlertDialog dialog = new MaterialAlertDialogBuilder(EditActivity.this)
                        .setTitle(R.string.add_category_title)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Category category = addCategory(input.getText().toString());
                                Utils.getEditTextFromLayout((TextInputLayout) category.getView()).requestFocus();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setView(input,
                                Utils.convertPixelsToDp(256, EditActivity.this),
                                0,
                                Utils.convertPixelsToDp(256, EditActivity.this),
                                0)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;
                if (subjectPicker.getText().toString().isEmpty()) {
                    Utils.getLayoutFromEditText(subjectPicker).setError("*Required");
                    error = true;
                } else {
                    homework.setSubject(subjectPicker.getText().toString());
                }
                if (datePicker.getText().toString().isEmpty()) {
                    Utils.getLayoutFromEditText(datePicker).setError("*Required");
                    error = true;
                } else {
                    try {
                        homework.setDate(Utils.dateFromString(datePicker.getText().toString()));
                    } catch (ParseException e) {
                        Utils.getLayoutFromEditText(datePicker).setError("Incorrect date format");
                        error = true;
                    }
                }
                if (homework.isEmpty()) {
                    Snackbar.make(fabAddCategory, "Add at least one category", Snackbar.LENGTH_SHORT).show();
                    error = true;
                } else {
                    for (Category category : homework.getCategoryList()) {
                        if (category.isEmpty()) {
                            ((TextInputLayout)category.getView()).setError("Add at least one item to category");
                            error = true;
                        }
                    }
                }

                if (error) return;

                db.updateHomework(homework);

                setResult(Activity.RESULT_OK);
                finish();

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setData(Homework homework) {
        if (homework == null) return;
        this.homework = new Homework(homework);

        subjectPicker.setText(homework.getSubject());
        datePicker.setText(homework.getDateAsString());

        for (Category category : homework.getCategoryList()) {
            Category activityCategory = addCategory(category.getName());
            activityCategory.setId(category.getId());
            for (Item item : category.getItemList())
                addItem(item, activityCategory);
        }

    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private Category addCategory(String name) {
        final TextInputLayout categoryView = (TextInputLayout) getLayoutInflater().
                inflate(R.layout.edit_category_entry, categoryList, false);
        final Category category = new Category(name, (ChipGroup) categoryView.getChildAt(1), categoryView);

        categoryView.setHint(category.getName());
        categoryView.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category.getId() != -1) db.deleteCategory(category);
                homework.removeCategory(category);
                categoryList.removeView(categoryView);
            }
        });


        Utils.getEditTextFromLayout(categoryView).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (!v.getText().toString().trim().isEmpty()) {
                    addItem(v, category);
                    handled = true;
                    categoryView.setErrorEnabled(false);
                }
                return handled;
            }
        });

        categoryList.addView(categoryView);
        homework.addCategory(category);

        return category;
    }
    private void addItem(final Item item, final Category category) {
        final Chip itemView = (Chip) getLayoutInflater().inflate(R.layout.edit_category_chip,
                category.getItemBox(), false);

        itemView.setText(item.getContent());
        itemView.setChecked(item.isDone());
        itemView.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getId() != -1) db.deleteItem(item);
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
        v.setText("");
    }
}
