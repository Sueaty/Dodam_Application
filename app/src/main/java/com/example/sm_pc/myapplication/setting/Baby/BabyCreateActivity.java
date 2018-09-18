package com.example.sm_pc.myapplication.setting.Baby;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class BabyCreateActivity extends AppCompatActivity {

    SharedPreferences SP_BABY_NAME;
    private EditText babyName;
    private TextView dDayDate;
    private RadioButton boy, girl;
    private Button buttonDate, buttonSave, buttonReturn;

    private int tYear, tMonth, tDay;
    private int dYear = 1, dMonth = 1, dDay = 1;

    private static final int DATE_DIALOG_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_create);

        babyName = (EditText) findViewById(R.id.textBabyName);
        dDayDate = (TextView) findViewById(R.id.textDate);
        boy = (RadioButton) findViewById(R.id.buttonBoy);
        girl = (RadioButton) findViewById(R.id.buttonGirl);
        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonSave = (Button) findViewById(R.id.buttonSaveBaby);
        buttonReturn = (Button) findViewById(R.id.buttonReturnBaby);

        buttonReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(BabyCreateActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });


        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String expectDate = dDayDate.getText().toString().trim();
                final String Name = babyName.getText().toString().trim();
                final String genderB = "Boy";
                final String genderG = "Girl";
                final String genderU = "Undecided";

                if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(expectDate)){
                    Toast.makeText(getApplicationContext(), "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference Baby = mRootRef.child("Baby");
                DatabaseReference user = Baby.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference name = user.child("name");
                DatabaseReference date = user.child("date");

                name.setValue(Name);
                date.setValue(expectDate);
                DatabaseReference gender = user.child("gender");
                if(boy.isChecked()){gender.setValue(genderB);}
                else if(girl.isChecked()){gender.setValue(genderG);}
                else{gender.setValue(genderU);}

                String FILENAME = "BABY_NAME_FILE";
                String FILEDATE = "BABY_DATE_FILE";
                String BABY_NAME = Name;
                String EXPECT_DATE = expectDate;

                FileOutputStream nameFile = null;
                FileOutputStream dateFile = null;

                try {
                    nameFile = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    nameFile.write(BABY_NAME.getBytes());

                    dateFile = openFileOutput(FILEDATE, Context.MODE_PRIVATE);
                    dateFile.write(EXPECT_DATE.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    if( (nameFile != null) && (dateFile != null)){
                        try {
                            nameFile.close();
                            dateFile.close();
                            Intent intent = new Intent(BabyCreateActivity.this, MainActivity.class);
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });


        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(DATE_DIALOG_ID).show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear, dMonth, dDay);
        updateDisplay();
    }




    private void updateDisplay() {
        if(dYear == 1){ dDayDate.setText("출산예정일");}
        else{dDayDate.setText(String.format("%d년 %d월 %d일", dYear, dMonth + 1, dDay));}
    }

    private DatePickerDialog.OnDateSetListener dDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            final Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(dYear, dMonth, dDay);
            updateDisplay();
        }
    };

    protected Dialog createDialog(int id){
        if(id==DATE_DIALOG_ID){
            return new DatePickerDialog(this, dDateSetListener,tYear,tMonth, tDay);
        }
        return null;
    }

}