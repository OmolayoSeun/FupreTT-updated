package com.omolayoseun.fuprett;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OnBoardingActivity extends AppCompatActivity {

    private static final String[] TUT1 = {"<!DOCTYPE html><head><title></title></head><body>" +
                    "<p>Click on </font><font color=\"purple\">Import course form</font> to toggle between " +
                    "form or Department selection.</p></body></html>",
            "<!DOCTYPE html><head><title></title></head><body>" +
                    "<p>Choose </font><font color=\"purple\">Department</font>, </font><font color=\"purple\">" +
                    "Level</font> and </font><font color=\"purple\">Semester</font> from the drop down menu containing" +
                    " the available departments, level and semester.</p></body></html>",
            "<!DOCTYPE html><head><title></title></head><body>" +
                    "<p>The <font color=\"purple\">Add</font> button is used to import files needed the only files " +
                    "needed are <font color=\"purple\">course form</font> and <font color=\"purple\">time-table</font> or <font color=\"purple\">time-table</font>" +
                    " only depending on the operation.</p></body></html>",
            "<!DOCTYPE html><head><title></title></head><body>" +
                    "Click <font color=\"purple\">Create time table</font> to start the operation, once operation starts it cannot be canceled until the operation finishes," +
                    " the operation runs in background.</p></body></html>"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        Button skip_btn = findViewById(R.id.skip);
        TextView t1 = findViewById(R.id.t1),
        t2 = findViewById(R.id.t2),
        t3 = findViewById(R.id.t3),
        t4 = findViewById(R.id.t4);

        t1.setText(Html.fromHtml(TUT1[0]));
        t2.setText(Html.fromHtml(TUT1[1]));
        t3.setText(Html.fromHtml(TUT1[2]));
        t4.setText(Html.fromHtml(TUT1[3]));


        skip_btn.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("tutorial-screen", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("has-shown-tutorial-screen", true);
            editor.apply();

            finish();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}