package com.example.android_2019;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class start extends AppCompatActivity implements Runnable, SensorEventListener {

    Classifier clf;
    SensorManager sm;
    TextView tv;
    Handler h;
    float gx, gy, gz;
    double acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_start);

        try {
            // deserialize model
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("j48.model"));
            this.clf = (Classifier) ois.readObject();
            ois.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

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

        //make instance
        // ここは文字列の数字かな? ファイルではnumericってやったけど
        // indexは0なのかな？, インスタンスの作り方がよく分からん
        Attribute atri_acc = new Attribute("acc", 1);

        Instance instance = new DenseInstance(2);
        instance.setValue(atri_acc, acc);

        // これはinstancesというデータセットにinstanceを追加する?的なものみたいなので
        // とりあえずスルー
        //instance.setDataset(instances);

        // エラーが出るのでtry, catchしてみた
        double result = 0.0;
        try {
            // 予測, 0.0がstationary, 1.0がwalking, 2.0がrunのはず
            result = clf.classifyInstance(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ダイアログ表示
        if (result == 1.0 || result == 2.0) {
            AlertDialog.Builder alertDialog =new AlertDialog.Builder(this);
            alertDialog.setTitle("Alert")
                    .setMessage("歩きスマホをやめてください")
                    .setPositiveButton("OK", null)
                    .show();
        }

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

}
