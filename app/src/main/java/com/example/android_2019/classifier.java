package com.example.android_2019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;

public class classifier extends AppCompatActivity {

    String make_file_name = "data.arff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);

        make_arffHeader(make_file_name);
        make_arffFile(make_file_name, "data_stationary.csv");
        make_arffFile(make_file_name, "data_walking.csv");
        make_arffFile(make_file_name, "data_run.csv");
    }



    public void make_arffHeader(String make_file_name) {
        try {

            FileOutputStream fileOutputstream = openFileOutput(make_file_name, MODE_APPEND);
            fileOutputstream.write("@relation data\n".getBytes());
            fileOutputstream.write("\n".getBytes());
            fileOutputstream.write("@attribute label numeric\n".getBytes());
            fileOutputstream.write("@attribute acc numeric\n".getBytes());
            fileOutputstream.write("\n".getBytes());
            fileOutputstream.write("@data\n".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void make_arffFile(String write_file_name, String read_file_name) {
        String [] line = null;

        // try-with-resources
        try {

            FileOutputStream fileOutputstream = openFileOutput(write_file_name, MODE_APPEND);

            FileInputStream fileInputStream = openFileInput(read_file_name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));


            String lineBuffer;
            while ((lineBuffer = reader.readLine()) != null) {
                line = lineBuffer.split(",");
                fileOutputstream.write(line[0].getBytes());
                fileOutputstream.write(",".getBytes());
                fileOutputstream.write(line[1].getBytes());
                fileOutputstream.write("\n".getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}