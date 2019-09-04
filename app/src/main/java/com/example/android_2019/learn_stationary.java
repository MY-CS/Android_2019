package com.example.android_2019;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

//setContentView(R.layout.activity_learn);

public class learn_stationary extends AppCompatActivity implements Runnable, SensorEventListener {

    SensorManager sm;
    TextView tv;
    Handler h;
    float gx, gy, gz;
    double acc;
    String file_name = "data_stationary.csv";

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
//        acc = Math.sqrt(gx * gx + gy * gy + gz * gz);
        saveFile(file_name, acc);
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
    // ACCも表示してみた感じ変化してはいるが, 全然変化しないのはなんでなんだろ
    public void saveFile(String file_name, double data) {

        // 上のブロックの組み合わせで文字列でファイルができる
        // 一番上のfileoutputstreamと下の部分を組み合わせると数値で書き込みができるっぽい
        // try-with-resources
        try {
            FileOutputStream fileOutputstream = openFileOutput(file_name, MODE_APPEND);
//            fileOutputstream.write("stationary".getBytes());
//            fileOutputstream.write(",".getBytes());
//            fileOutputstream.write(String.valueOf(data).getBytes());
//            fileOutputstream.write("\n".getBytes());

//             出力ファイルの作成
//             上の奴を使わないとfilesにファイルができない
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputstream));
//            FileWriter f = new FileWriter(file_name, true);
            PrintWriter p = new PrintWriter(bw);

            // 書き込み
            p.print("stationary"); // クラスラベルのつもり
            p.print(",");
            p.println(data); // 合成加速度

            // ファイルに書き出し閉じる
            p.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
