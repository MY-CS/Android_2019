package com.example.android_2019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.io.IOException;
import androidx.appcompat.app.AlertDialog;

public class delete_classifier extends AppCompatActivity {

    // 実行できなかった

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_classifier);

        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("adb shell");
            runtime.exec("run-as com.example.android_2019");
            runtime.exec("cd /data/data/com.example.android_2019/files");
            runtime.exec("rm *");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete classifier")
                .setMessage("分類器を消去しました")
                .setPositiveButton("OK", null)
                .show();
    }
}
