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

import java.util.List;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

//setContentView(R.layout.activity_learn);

public class learn extends AppCompatActivity implements Runnable, SensorEventListener{

    SensorManager sm;
    TextView tv;
    Handler h;
    float gx, gy, gz;
    double acc;
    String file_name = "data_run.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ll = new LinearLayout(this);
        setContentView(ll);

        tv = new TextView(this);
        ll.addView(tv);

        h = new Handler();
        h.postDelayed(this, 500);
    }

    @Override
    public void run() {
        acc = Math.sqrt(gx * gx + gy * gy + gz * gz);
        tv.setText("X-axis : " + gx + "\n"
                + "Y-axis : " + gy + "\n"
                + "Z-axis : " + gz + "\n"
                + "ACCis : " + acc + "\n");

        //ここからファイルの書き込みの追加
        String str_data = String.valueOf(acc);
        saveFile(file_name, str_data);
        //

        h.postDelayed(this, 500);
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
    public void saveFile(String file_name, String str_data) {

        // try-with-resources
        try {
            FileOutputStream fileOutputstream = openFileOutput(file_name, MODE_APPEND);
            fileOutputstream.write("run".getBytes());
            fileOutputstream.write(",".getBytes());
            fileOutputstream.write(String.valueOf(str_data).getBytes());
            fileOutputstream.write("\n".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}