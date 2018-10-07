
// Things to Improve //
// - Message of 'About 도담도담'
//      - ex) How to use the application
//      - ex) Major funtions of the application

// - Decide what to do with buttonSetting (overlaps with buttonUser)



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

    private Button buttonHome, buttonBaby, buttonSetting, buttonHow, buttonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        define(); // define

        // click and action methods
        homeClick();
        babyClick();
        settingClick();
        howClick();
        userClick();
    }

    private void define(){
        buttonHome = (Button) findViewById(R.id.buttonSettingMain);
        buttonBaby = (Button) findViewById(R.id.buttonSettingBaby);
        buttonHow = (Button) findViewById(R.id.buttonHowTo);
        buttonSetting = (Button) findViewById(R.id.buttonSettingSetting);
        buttonUser = (Button) findViewById(R.id.buttonSettingUserChange);
    }

    private void homeClick(){
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void babyClick(){
        buttonBaby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(SettingActivity.this);
                alert_confirm.setMessage("아기 신규등록이 아닐 경우, 작성을 완료한 후에 '수정 완료'를 눌러주시기 바랍니다.");
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
    }

    private void settingClick(){
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void howClick(){
        buttonHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setCancelable(true); //cancel after use (don't know what it means...)
                builder.setTitle("도담도담에 대하여");
                String Message = "도담도담은~";
                builder.setMessage(Message);
                builder.show();
            }
        });
    }

    private void userClick(){
        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}

