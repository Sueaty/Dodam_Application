package com.example.sm_pc.myapplication.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.sm_pc.myapplication.MainActivity;
import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.setting.Baby.BabyCreateActivity;

public class SettingActivity extends AppCompatActivity {

    private Button buttonHome, buttonBaby, buttonHow, buttonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        buttonHome = (Button) findViewById(R.id.buttonToHome);
        buttonBaby = (Button) findViewById(R.id.buttonRegisterBaby);
        buttonHow = (Button) findViewById(R.id.buttonHowTo);
        buttonUser = (Button) findViewById(R.id.buttonUserSetting);

        buttonHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonBaby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(SettingActivity.this);
                alert_confirm.setMessage("아예 바꾸는거야 알겠지?");
                alert_confirm.setNegativeButton("만들기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SettingActivity.this, BabyCreateActivity.class);
                        startActivity(intent);
                    }
                });
                alert_confirm.setPositiveButton("돌아가기", null);
                AlertDialog alert = alert_confirm.create();
                alert.show();

            }

        });

        buttonHow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SettingActivity.this, ChangeSettingActivity.class);
                startActivity(intent);
            }

        });
/*
       buttonUser.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               Intent intent = new Intent(SettingActivity.this, ??.class);
               startActivity(intent);
           }
       });
*/
    }
}

