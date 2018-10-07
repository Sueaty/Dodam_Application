
package com.example.sm_pc.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sm_pc.myapplication.BabyBot.BabyActivity;
import com.example.sm_pc.myapplication.DodamBot.BotActivity;
import com.example.sm_pc.myapplication.account.LoginActivity;
import com.example.sm_pc.myapplication.diary.DiaryMainActivity;
import com.example.sm_pc.myapplication.setting.Baby.BabyCreateActivity;
import com.example.sm_pc.myapplication.setting.Baby.DatabaseHelper;
import com.example.sm_pc.myapplication.setting.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    private TextView textName,textDdate, textToday;
    private ImageButton buttonSetting;
    private Button buttonDodam, buttonBaby, buttonDiary;
    private ImageView babyPic;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        define(); // define
        updateDday(); // update today's d-day
        updateTodaydate(); // update today's date
        textBabyName(); // set text of 'BABY NAME'

        // click and action methods
        dodamClick();
        babyClick();
        diaryClick();
        settingClick();
    }

    private void define(){
        myDb = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();
        mRootref = FirebaseDatabase.getInstance().getReference();
        textName = (TextView) findViewById(R.id.mainBabyName);
        textDdate = (TextView) findViewById(R.id.mainBabyDday);
        textToday = (TextView) findViewById(R.id.todayDate);
        buttonSetting = (ImageButton) findViewById(R.id.buttonSetting);
        buttonDodam = (Button) findViewById(R.id.buttonMainDodam);
        buttonBaby = (Button) findViewById(R.id.buttonMainBaby);
        buttonDiary = (Button) findViewById(R.id.buttonMainDiary);
        babyPic = (ImageView) findViewById(R.id.babyImageView);
    }

    private void updateDday(){
        String getExpectDate ="";
        String getCompareDate = "";
        String getDDay = "";
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0){

        } else{
            if(res.moveToFirst()){
                do{
                    getExpectDate = res.getString(1);
                    getCompareDate = res.getString(2);
                    getDDay = res.getString(4);
                } while(res.moveToNext());
            }
            // get today
            GregorianCalendar calendar = new GregorianCalendar();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String todayDate = String.format(Locale.getDefault(), "%d%d%d", year, month, day);

            int dDay = Integer.parseInt(getDDay);
            if (!getCompareDate.equals(todayDate)) {
                --dDay; // if compare date and today date is different, it means one day has passed, so tick off one day
                boolean isUpdatd = myDb.updateData("1", getExpectDate, todayDate, todayDate, String.valueOf(dDay)); // and update the baby_table
            }
            String setDday;
            if(dDay >= 0){ setDday = "D - " + dDay; }
            else { setDday = "Born"; }
            textDdate.setText(setDday);
            updateTodaypic(dDay);
        }
    }

    private void updateTodaydate(){
        Calendar calendar = Calendar.getInstance();
        int todayDay, todayMonth, todayYear;
        String updateDate;

        todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        todayMonth = calendar.get(Calendar.MONTH);
        todayYear = calendar.get(Calendar.YEAR);

        if((todayMonth + 1)  < 10 && todayDay < 10)
        { updateDate = String.format(Locale.getDefault(), "%d년" + 0 + "%d월" + 0 + "%d일", todayYear, todayMonth + 1, todayDay); }
        else if ( (todayMonth + 1)  < 10 )
        { updateDate = String.format(Locale.getDefault(), "%d년" + 0 + "%d월%d일", todayYear, todayMonth + 1, todayDay); }
        else if ( todayDay < 10 )
        { updateDate = String.format(Locale.getDefault(), "%d년%d월" + 0 + "%d일", todayYear, todayMonth + 1, todayDay); }
        else
        { updateDate = String.format(Locale.getDefault(), "%d년%d월%d일", todayYear, todayMonth + 1, todayDay); }
        textToday.setText(updateDate);
    }

    private void updateTodaypic(int dDay){
        if(0 <= dDay && dDay <= 41) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_eleven));}
        else if(42 <= dDay && dDay <= 83) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_ten));}
        else if(84 <= dDay && dDay <= 125) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_nine));}
        else if(126 <= dDay && dDay <= 153) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_eight));}
        else if(154 <= dDay && dDay <= 188) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_seven));}
        else if(189 <= dDay && dDay <= 216) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_six));}
        else if(217 <= dDay && dDay <= 237) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_five));}
        else if(238 <= dDay && dDay <= 244) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_four));}
        else if(245 <= dDay && dDay <= 251) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_three));}
        else if(252 <= dDay && dDay <= 265) {babyPic.setImageDrawable(getDrawable(R.drawable.baby_two));}
        else {babyPic.setImageDrawable(getDrawable(R.drawable.baby_one));}

    }

    private void textBabyName(){
        String userUID = mAuth.getCurrentUser().getUid();
        mRootref.child("Baby").child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    AlertDialog.Builder checkBaby = new AlertDialog.Builder(MainActivity.this);
                    checkBaby.setMessage("아기 등록을 반드시 해주세요");
                    checkBaby.setNegativeButton("등록하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, BabyCreateActivity.class);
                            startActivity(intent);
                        }
                    });
                    checkBaby.setPositiveButton("로그아웃", null);
                    AlertDialog alert = checkBaby.create();
                    alert.show();
                }
                else{
                    if(dataSnapshot.hasChild("Name")){
                        String getBabyName = dataSnapshot.child("Name").getValue().toString();
                        textName.setText(getBabyName);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Failed to Retrieve Data from Database", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // On CLICK ACTIVITIES //
    //=======================================================================================================================================================//
    private void dodamClick(){
        buttonDodam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, BotActivity.class);
                startActivity(intent);
            }
        });
    }

    private void babyClick(){
        buttonBaby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, BabyActivity.class);
                startActivity(intent);
            }
        });
    }

    private void diaryClick(){
        buttonDiary.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, DiaryMainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void settingClick(){
        buttonSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
    //=======================================================================================================================================================//
}
