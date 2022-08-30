package com.omolayoseun.fuprett;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int PERMISSION_REQUEST_CODE = 100;
    private final int PERMISSION_WRITE_CODE = 23;
    private static ProgressDialog progressDialog;
    private static boolean isCourseForm = false;

    String TAG = "MainActivity";
    String coursePath = null, timeTablePath = null;
    short choose;
    byte dept_num = 0, level_num = 0, semester_num = 0;

    @SuppressLint("StaticFieldLeak")
    static Context context;
    Button btn1, btn2, start, btn_switch;
    Spinner spin_dept, spin_level, spin_semester;
    LinearLayout layout_import, layout_select;
    TextView txt1, txt2;
    boolean[] permissions;

    ArrayAdapter<String> dept_adapter;
    ArrayAdapter<String> level_adapter;
    ArrayAdapter<String> semester_adapter;

    private final Handler messageHandler = new MessageHandler(new CallPDF() {
        @Override
        public void viewPDf() {
            try{
                File file = new File(Environment.getExternalStorageDirectory() + "/FupreTT/My time table.pdf");
                Intent intent = new Intent(Intent.ACTION_VIEW);

                /*
                *if (file.exists()) Toast.makeText(context, "Yes it exist", Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, "No it does not exist", Toast.LENGTH_SHORT).show();*/

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                else{
                    intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory() + "/FupreTT/My time table.pdf"), "application/pdf");
                    intent = Intent.createChooser(intent, "Open File");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                Log.w("Error intent", e.toString());
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        start = findViewById(R.id.start);
        btn_switch = findViewById(R.id.btn_switch);

        layout_import = findViewById(R.id.layout_import);
        layout_select = findViewById(R.id.layout_select);

        spin_dept = findViewById(R.id.spin_dept);
        spin_level = findViewById(R.id.spin_level);
        spin_semester = findViewById(R.id.spin_semester);

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);

        layout_import.setVisibility(View.GONE);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn_switch.setOnClickListener(this);
        start.setOnClickListener(this);

        spin_dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {dept_num = (byte) i;}
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spin_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {level_num = (byte) i;}
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spin_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {semester_num = (byte) i;}
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        context = getApplicationContext();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIcon(R.drawable.ic_baseline_info_24);
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Working on file");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(20);

        grantPermission();
        Department.initialiseDept(check -> {
            if(check) setSpinners();
            else new UpdateDept().execute();
        });

    }

    private void setSpinners() {
        dept_adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                Department.getArr("dep"));

        level_adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                Department.getArr("lev"));

        semester_adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                Department.getArr("sem"));

        spin_dept.setAdapter(dept_adapter);
        spin_level.setAdapter(level_adapter);
        spin_semester.setAdapter(semester_adapter);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    private class UpdateDept extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Department.initialiseDept(check -> {
                if (check) setSpinners();
                else doInBackground();
            });
            return null;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (permissions[0] && permissions[1]){
            switch (view.getId()) {
                case R.id.btn1:
                    choose = 1;
                    getForm();
                    break;
                case R.id.btn2:
                    choose = 2;
                    getForm();
                    break;
                case R.id.btn_switch:
                    if (layout_import.getVisibility() == View.GONE) {
                        layout_import.setVisibility(View.VISIBLE);
                        layout_select.setVisibility(View.GONE);
                        btn_switch.setText(R.string.text_select);
                        isCourseForm = true;
                    } else {
                        layout_import.setVisibility(View.GONE);
                        layout_select.setVisibility(View.VISIBLE);
                        btn_switch.setText(R.string.text_import);
                        isCourseForm = false;
                    }
                    break;
                case R.id.start:

                    if (isCourseForm) {
                        startService(new Intent(MainActivity.this, MyService.class)
                                .putExtra(IntentKey.MESSAGE.name(), new Messenger(messageHandler))
                                .putExtra(IntentKey.OK.name(), true)
                                .putExtra(IntentKey.COURSE_PATH.name(), coursePath)
                                .putExtra(IntentKey.TIME_TABLE_PATH.name(), timeTablePath));
                    }
                    else {
                        startService(new Intent(MainActivity.this, MyService.class)
                                .putExtra(IntentKey.MESSAGE.name(), new Messenger(messageHandler))
                                .putExtra(IntentKey.DEPT.name(), dept_num)
                                .putExtra(IntentKey.LEVEL.name(), level_num)
                                .putExtra(IntentKey.SEMESTER.name(), semester_num)
                                .putExtra(IntentKey.TIME_TABLE_PATH.name(), timeTablePath));
                    }
                    break;
                default:
            }
        }
        else grantPermission();
    }

    @SuppressWarnings("deprecation")
    public static class MessageHandler extends Handler {
        CallPDF callPDF;

        MessageHandler(CallPDF callPDF){
            this.callPDF = callPDF;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            int state = msg.arg1;
            switch (state) {
                case 0:
                    progressDialog.show();
                    break;
                case 1:
                    progressDialog.dismiss();
                    callPDF.viewPDf();
                    Toast.makeText(context, "Creating time table successful", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    progressDialog.dismiss();
                    Toast.makeText(context, "Failed to create time table", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public interface CallPDF{
        void viewPDf();
    }

    private void getForm() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        switch (choose){
            case 1:
                intent.setType("application/pdf");
                break;
            case 2:
                intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                break;
        }
        //noinspection deprecation
        startActivityForResult(intent, 1);
    }

    private void grantPermission(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            permissions = checkPermission();
            if (!permissions[0] || !permissions[1]) {
                Toast.makeText(this, "Grant permission else app won't work", Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        }
    }

    private boolean[] checkPermission() {
        int[] result = new int[2];
        result[0] = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        result[1] = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return new boolean[]{result[0] == PackageManager.PERMISSION_GRANTED, result[1] == PackageManager.PERMISSION_GRANTED};
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to writes files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_CODE);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Read External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_WRITE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultCode + 2) {
            Uri content_describer = data.getData();

            switch (choose) {
                case 1:
                    if (content_describer.getPath().endsWith(".pdf")) {
                        coursePath = content_describer.getPath();
                        txt1.setText(coursePath);
                        Log.w(TAG, coursePath);
                    } else {
                        alert("Notice", "File should be pdf");
                    }
                    break;
                case 2:
                    if (content_describer.getPath().endsWith(".docx")) {
                        timeTablePath = content_describer.getPath();
                        txt2.setText(timeTablePath);
                        Log.w(TAG, timeTablePath);
                    } else {
                        alert("Notice", "File should be docx");
                    }
                    break;
                default:
            }
        }
    }

    public void alert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}