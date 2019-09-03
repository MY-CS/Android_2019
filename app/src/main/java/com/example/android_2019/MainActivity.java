package com.example.android_2019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.learn).setOnClickListener(this);
        findViewById(R.id.learn_walking).setOnClickListener(this);
    }

    //ボタンが押された時の処理
    public void onClick(View view){
        switch(view.getId()){
            case R.id.learn:
                Intent intent = new Intent(this, learn.class);  //インテントの作成
                startActivity(intent);
                break;
            case R.id.learn_walking:
                Intent intent2 = new Intent(this, learn_walking.class);  //インテントの作成
                startActivity(intent2);
                break;
        }
    }
}
