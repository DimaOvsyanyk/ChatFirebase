package com.dimaoprog.chat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyProgressDialog {

    private AlertDialog myProgressDialog;

    public MyProgressDialog(Context context) {
        LinearLayout lLayout = new LinearLayout(context);
        lLayout.setOrientation(LinearLayout.HORIZONTAL);
        lLayout.setPadding(30, 50, 30, 50);
        lLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lParams.gravity = Gravity.CENTER;
        lLayout.setLayoutParams(lParams);

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, 30 , 0);
        progressBar.setLayoutParams(lParams);

        TextView textView = new TextView(context);
        textView.setText("Loading......");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setLayoutParams(lParams);

        lLayout.addView(progressBar);
        lLayout.addView(textView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(lLayout);

        myProgressDialog = builder.create();



    }

    public void showMyProgressDialog(boolean show) {
        if (show) {
            myProgressDialog.show();
        } else {
            myProgressDialog.dismiss();
        }
    }
}
