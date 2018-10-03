package com.example.sm_pc.myapplication.setting.Baby;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm_pc.myapplication.MainActivity;
import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.setting.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class BabyCreateActivity extends AppCompatActivity {

    SharedPreferences SP_BABY_NAME;
    private EditText babyName;
    private TextView dDayDate, leftDate;
    private RadioButton boy, girl;
    private Button buttonDate, buttonSave, buttonReturn;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootref;

    private int tYear, tMonth, tDay;
    private int dYear = 1, dMonth = 1, dDay = 1;

    private long d, t,r;
    public int resultNumber = 0;

    private static final int DATE_DIALOG_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_create);

        mAuth = FirebaseAuth.getInstance();
        mRootref = FirebaseDatabase.getInstance().getReference();

        babyName = (EditText) findViewById(R.id.textBabyName);
        dDayDate = (TextView) findViewById(R.id.textDate);
        leftDate = (TextView) findViewById(R.id.showLeftDate);
        boy = (RadioButton) findViewById(R.id.buttonBoy);
        girl = (RadioButton) findViewById(R.id.buttonGirl);
        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonSave = (Button) findViewById(R.id.buttonSaveBaby);
        buttonReturn = (Button) findViewById(R.id.buttonReturnBaby);

        returnClick();

        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(DATE_DIALOG_ID).show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        Calendar dCalendar = Calendar.getInstance();

        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

        dCalendar.set(dYear, dMonth, dDay);
        t = calendar.getTimeInMillis();
        d = dCalendar.getTimeInMillis();
        r = (d - t) / (24 * 60 * 60 * 1000); // second -> minute

        resultNumber = (int) r + 1;
        updateDisplay();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userUID = mAuth.getCurrentUser().getUid();
                String expectDate = dDayDate.getText().toString().trim();
                String Name = babyName.getText().toString().trim();
                String gender;
                if(boy.isChecked()){gender = "boy";}
                else if (girl.isChecked()){gender = "girl";}
                else {gender = "undecided";}
                String leftDate;
                if(resultNumber >= 0) {leftDate = String.valueOf(resultNumber);}
                else {
                    leftDate ="아이가 태어났어요!";
                }

                if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(expectDate)){
                    Toast.makeText(getApplicationContext(), "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, String> babyMap = new HashMap<>();
                    babyMap.put("Name", Name);
                    babyMap.put("ExpectDate", expectDate);
                    babyMap.put("LeftDate", leftDate);
                    babyMap.put("Gender", gender);

                mRootref.child("Baby").child(userUID).setValue(babyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BabyCreateActivity.this, "Baby Information Updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BabyCreateActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else{
                            Toast.makeText(BabyCreateActivity.this, "Failed to save information. Try Again", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });

    }

    private void updateDisplay() {
        if(dYear == 1){ dDayDate.setText("출산예정일");}
        else{
            String stringDayForm;
            if((dMonth + 1)  < 10 && dDay < 10){
                stringDayForm = String.format(Locale.getDefault(), "%d" + 0 + "%d" + 0 + "%d", dYear, dMonth + 1, dDay);
            } else if ( (dMonth + 1)  < 10 ){
                stringDayForm = String.format(Locale.getDefault(), "%d" + 0 + "%d%d", dYear, dMonth + 1, dDay);
            } else if ( dDay < 10 ){
                stringDayForm = String.format(Locale.getDefault(), "%d%d" + 0 + "%d", dYear, dMonth + 1, dDay);
            } else{
                stringDayForm = String.format(Locale.getDefault(), "%d%d%d", dYear, dMonth + 1, dDay);
            }
            dDayDate.setText(stringDayForm);
        }

        if (resultNumber >= 0) {
            leftDate.setText(String.format(Locale.getDefault(), "D - %d", resultNumber));
        } else {
            int absR = Math.abs(resultNumber);
            leftDate.setText(String.format(Locale.getDefault(), "D + %d", absR));
        }
    }

    private DatePickerDialog.OnDateSetListener dDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            final Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(dYear, dMonth, dDay);

            d = dCalendar.getTimeInMillis();
            r = (d - t) / (24 * 60 * 60 * 1000);

            resultNumber = (int) r;
            updateDisplay();
        }
    };

    protected Dialog createDialog(int id){
        if(id==DATE_DIALOG_ID){
            return new DatePickerDialog(this, dDateSetListener,tYear,tMonth, tDay);
        }
        return null;
    }

    private void returnClick(){
        buttonReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(BabyCreateActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }


}