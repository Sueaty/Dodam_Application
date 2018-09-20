package com.example.sm_pc.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sm_pc.myapplication.BabyBot.BabyActivity;
import com.example.sm_pc.myapplication.DodamBot.BotActivity;
import com.example.sm_pc.myapplication.account.LoginActivity;
import com.example.sm_pc.myapplication.diary.DiaryMainActivity;
import com.example.sm_pc.myapplication.setting.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {


    private TextView textName,textDdate;
    private ImageButton buttonSetting;
    private Button signOut, buttonDodam, buttonBaby, buttonDiary;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textName = (TextView) findViewById(R.id.mainBabyName);
        textDdate = (TextView) findViewById(R.id.mainBabyDday);
        auth = FirebaseAuth.getInstance();
        buttonSetting = (ImageButton) findViewById(R.id.buttonSetting);
        signOut = (Button) findViewById(R.id.logOut);
        buttonDodam = (Button) findViewById(R.id.buttonMainDodam);
        buttonBaby = (Button) findViewById(R.id.buttonMainBaby);
        buttonDiary = (Button) findViewById(R.id.buttonMainDiary);

        buttonDodam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, BotActivity.class);
                startActivity(intent);
            }
        });

        buttonBaby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, BabyActivity.class);
                startActivity(intent);
            }
        });

        buttonDiary.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, DiaryMainActivity.class);
                startActivity(intent);
            }
        });

        String FILENAME = "BABY_NAME_FILE";
        String FILEDATE = "BABY_DATE_FILE";
        FileInputStream nameRead = null;
        FileInputStream dateRead = null;

        try {
            String textNameRead, textExpectDateRead;
            nameRead = openFileInput(FILENAME);
            dateRead = openFileInput(FILEDATE);
            InputStreamReader isr_name = new InputStreamReader(nameRead);
            InputStreamReader isr_date = new InputStreamReader(dateRead);
            BufferedReader br_name = new BufferedReader(isr_name);
            BufferedReader br_date = new BufferedReader(isr_date);
            StringBuilder sb_name = new StringBuilder();
            StringBuilder sb_date = new StringBuilder();

            while((textNameRead = br_name.readLine()) != null){
                sb_name.append(textNameRead);
            }
            textName.setText(sb_name.toString());

            while((textExpectDateRead = br_date.readLine()) != null){
                sb_date.append(textExpectDateRead);
            }
            textDdate.setText(sb_date.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        buttonSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    public void signOut() {
        auth.signOut();
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

}
