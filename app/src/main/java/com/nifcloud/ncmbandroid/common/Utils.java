package com.nifcloud.ncmbandroid.common;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.nifcloud.ncmbandroid.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean isEmailFormat(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static AlertDialog createOKDialog(Context context, String title, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null).create();
    }

    public static AlertDialog createErrorDialog(Context context, String title, String message) {
        return new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_launcher_background)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null).create();
    }
}
