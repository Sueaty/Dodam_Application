package com.example.sm_pc.myapplication.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.setting.CreateBaby.RegisterBabyActivity;

public class SettingActivity extends AppCompatActivity {

    private Button buttonBaby, buttonHow, buttonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        buttonBaby = (Button) findViewById(R.id.buttonRegisterBaby);
        buttonHow = (Button) findViewById(R.id.buttonHowTo);
        buttonUser = (Button) findViewById(R.id.buttonUserSetting);

        buttonBaby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SettingActivity.this, RegisterBabyActivity.class);
                startActivity(intent);
            }

        });

        buttonHow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SettingActivity.this, ChangeSettingActivity.class);
                startActivity(intent);
            }

        });

       buttonUser.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               Intent intent = new Intent(SettingActivity.this, UserViewActivity.class);
               startActivity(intent);
           }
       });
    }
}

