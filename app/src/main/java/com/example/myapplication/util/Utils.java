package com.example.myapplication.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.widget.PopupMenu;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    //Shortcut to create a popup menu
    public static PopupMenu showMenu(View anchor) {
        PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.card_menu, popup.getMenu());
        popup.show();

        return popup;

    }

    public static TextInputLayout getLayoutFromEditText(EditText v) {
        return ((TextInputLayout)((FrameLayout)v.getParent()).getParent());
    }

    public static TextInputEditText getEditTextFromLayout(TextInputLayout layout) {
        return (TextInputEditText)((FrameLayout)layout.getChildAt(0)).getChildAt(0);
    }

    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return  (int) (px / (metrics.densityDpi / 160f));
    }

    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", new Locale("en", "UK"));
        return format.format(date);
    }

    public static Date dateFromString(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", new Locale("en", "UK"));
        return format.parse(s);
    }
}
