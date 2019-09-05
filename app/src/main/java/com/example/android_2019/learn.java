package com.example.android_2019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import android.app.AlertDialog;
import android.view.Gravity;

public class learn extends AppCompatActivity implements Runnable, SensorEventListener{

    SensorManager sm;
    TextView tv, finish_tv;
    Handler h;
    float gx, gy, gz;
    double acc;
    String file_name = "data_run.csv";
    int num_sample = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);

        tv = new TextView(this);
        ll.addView(tv);

        finish_tv = new TextView(this);
        ll.addView(finish_tv);

        h = new Handler();
        h.postDelayed(this, 20);
    }

    @Override
    public void run() {
        acc = Math.sqrt(gx * gx + gy * gy + gz * gz);
        tv.setText("X-axis : " + gx + "\n"
                + "Y-axis : " + gy + "\n"
                + "Z-axis : " + gz + "\n"
                + "ACC : " + acc + "\n");

        //ここからファイルの書き込みの追加
        saveFile(file_name, acc);

        num_sample += 1;
        if (num_sample > 400) {
            finish_tv.setText("Finish collecting data");
            finish_tv.setTextSize(30.0f);
//            finish_tv.setGravity(Gravity.TOP|Gravity.CENTER);
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//            alertDialog.setTitle("Finish learning running")
//                    .setMessage("走り状態のデータ収集が完了")
//                    .setPositiveButton("OK", null)
//                    .show();
        }


        h.postDelayed(this, 20);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (0 < sensors.size()) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gx = event.values[0];
        gy = event.values[1];
        gz = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //csvファイルの書き込み
    //csvというか一応文字列の数値をカンマ区切りで書き込む感じにとりあえずした
    //ファイルの中身を見てみた感じ, いけてるとは思う
    //x軸だけでやってみるとちゃんと変化して書き込まれたけど, 合成加速度にすると全然変化がしないんだけど
    public void saveFile(String file_name, double data) {

        // try-with-resources
        try {
            FileOutputStream fileOutputstream = openFileOutput(file_name, MODE_APPEND);
            fileOutputstream.write(String.valueOf(data).getBytes());
            fileOutputstream.write(", ".getBytes());
            fileOutputstream.write("run".getBytes());
            fileOutputstream.write("\n".getBytes());

////             出力ファイルの作成
////             上の奴を使わないとfilesにファイルができない
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputstream));
////            FileWriter f = new FileWriter(file_name, true);
//            PrintWriter p = new PrintWriter(bw);
//
//            // 書き込み
//            p.print("stationary"); // クラスラベルのつもり
//            p.print(",");
//            p.println(data); // 合成加速度
//
//            // ファイルに書き出し閉じる
//            p.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}