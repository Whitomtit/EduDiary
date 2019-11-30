package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.object.Category;
import com.example.myapplication.object.Homework;
import com.example.myapplication.object.Item;
import com.example.myapplication.utils.HomeworkDbManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        homework = new Homework();
        fabAddCategory = findViewById(R.id.fab_add_category);
        subjectPicker = findViewById(R.id.subject_picker);
        datePicker = findViewById(R.id.date_picker);
        categoryList = findViewById(R.id.category_list);
        buttonSave = findViewById(R.id.edit_button_save);
        buttonCancel = findViewById(R.id.edit_button_cancel);


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
                getLayoutFromEditText(subjectPicker).setErrorEnabled(false);
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
                                String date = getString(R.string.date_format);
                                date = String.format(date, day, month + 1, year);
                                datePicker.setText(date);
                            }
                        }, year, month, day);
                picker.show();
                getLayoutFromEditText(datePicker).setErrorEnabled(false);

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
                input.setHint("Page number, book, etc");
                final TextView title = new TextView(EditActivity.this);
                title.setTypeface(null, Typeface.BOLD);
                title.setText("Chose a category name");
                final AlertDialog dialog = new MaterialAlertDialogBuilder(EditActivity.this)
                        .setTitle(R.string.add_category_title)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addCategory(input.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setView(input, convertPixelsToDp(256, EditActivity.this), 0, convertPixelsToDp(256, EditActivity.this), 0)
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
                    getLayoutFromEditText(subjectPicker).setError("*Required");
                    error = true;
                } else {
                    homework.setSubject(subjectPicker.getText().toString());
                }
                if (datePicker.getText().toString().isEmpty()) {
                    getLayoutFromEditText(datePicker).setError("*Required");
                    error = true;
                } else {
                    try {
                        homework.setDate(new SimpleDateFormat("d/M/yyyy").parse(datePicker.getText().toString()));
                    } catch (ParseException e) {
                        getLayoutFromEditText(datePicker).setError("Correct date format dd/mm/yyyy");
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

                db.addHomework(homework);

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

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void addCategory(String name) {
        final TextInputLayout categoryView = (TextInputLayout) getLayoutInflater().inflate(R.layout.edit_category_entry, null);
        final Category category = new Category(name, (ChipGroup) categoryView.getChildAt(1), categoryView);

        categoryView.setHint(category.getName());
        categoryView.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homework.removeCategory(category);
                categoryList.removeView(categoryView);
            }
        });


        getEditTextFromLayout(categoryView).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (!v.getText().toString().trim().isEmpty()) {
                    final Chip itemView = (Chip) getLayoutInflater().inflate(R.layout.edit_category_chip, null);
                    final Item item = new Item(v.getText().toString());

                    itemView.setText(item.getContent());
                    itemView.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            category.getItemBox().removeView(itemView);
                            category.removeItem(item);
                        }
                    });

                    category.getItemBox().addView(itemView);
                    category.addItem(item);

                    v.setText("");
                    handled = true;
                    categoryView.setErrorEnabled(false);
                }
                return handled;
            }
        });

        categoryList.addView(categoryView);
        homework.addCategory(category);

        categoryView.requestFocus();
    }

    private TextInputLayout getLayoutFromEditText(EditText v) {
        return ((TextInputLayout)((FrameLayout)v.getParent()).getParent());
    }

    private TextInputEditText getEditTextFromLayout(TextInputLayout layout) {
        return (TextInputEditText)((FrameLayout)layout.getChildAt(0)).getChildAt(0);
    }

    private static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return  (int) (px / (metrics.densityDpi / 160f));
    }
}
