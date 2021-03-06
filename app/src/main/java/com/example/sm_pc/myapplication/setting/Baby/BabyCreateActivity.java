package com.example.sm_pc.myapplication.setting.Baby;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class BabyCreateActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    private EditText babyName;
    private TextView dDayDate, leftDate;
    private RadioButton boy, girl, undecided;
    private Button buttonDate, buttonSave, buttonReturn, buttonChange;
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

        define();
        // If there's a existing data, it shows in advance
        displayInformation();
        // Making a completely new baby
        createBabyInformation();
        // Change baby Information
        changeBabyInformation();

        // Returns to previous page
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

    }

    private void define(){

        myDb = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();
        mRootref = FirebaseDatabase.getInstance().getReference();

        babyName = (EditText) findViewById(R.id.textBabyName);
        dDayDate = (TextView) findViewById(R.id.textDate);
        leftDate = (TextView) findViewById(R.id.showLeftDate);
        boy = (RadioButton) findViewById(R.id.buttonBoy);
        girl = (RadioButton) findViewById(R.id.buttonGirl);
        undecided = (RadioButton) findViewById(R.id.buttonUndecided);
        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonSave = (Button) findViewById(R.id.buttonSaveBaby);
        buttonReturn = (Button) findViewById(R.id.buttonReturnBaby);
        buttonChange = (Button) findViewById(R.id.buttonMendBaby);
    }

    private void displayInformation(){
        String userUID = mAuth.getCurrentUser().getUid();
        mRootref.child("Baby").child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Name")){
                        String name = dataSnapshot.child("Name").getValue().toString();
                        babyName.setText(name);
                    } else
                    if(dataSnapshot.hasChild("ExpectDate")){
                        String expectDate = dataSnapshot.child("ExpectDate").getValue().toString();
                        dDayDate.setText(expectDate);
                    }
                    if(dataSnapshot.hasChild("Gender")){
                        String gender = dataSnapshot.child("Gender").getValue().toString();
                        if(gender.equals("undecided")){undecided.setChecked(true);}
                        else if (gender.equals("boy")) {boy.setChecked(true);}
                        else {girl.setChecked(true);}
                    }
                    if (dataSnapshot.hasChild("LeftDate")){
                        String left;
                        if(resultNumber >= 0) {left = String.valueOf(resultNumber);}
                        else { left = "Born"; }
                        leftDate.setText(left);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createBabyInformation(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save in Firebase
                saveFirebase();
                // Save in SQLite
                GregorianCalendar calendar = new GregorianCalendar();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String compare = String.format(Locale.getDefault(), "%d%d%d", year, month, day);
                boolean isInserted = myDb.insertData(
                        dDayDate.getText().toString(),
                        compare,
                        compare,
                        leftDate.getText().toString()
                );
                if(isInserted){
                    Toast.makeText(BabyCreateActivity.this, "SUCCESSFULLY INSERTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BabyCreateActivity.this, MainActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(BabyCreateActivity.this, "FAILED TO INSERT", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void changeBabyInformation(){
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase Change Information
                saveFirebase();
                // SQLite Change Information
                GregorianCalendar calendar = new GregorianCalendar();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String compare = String.format(Locale.getDefault(), "%d%d%d", year, month, day);
                boolean isUpdated =
                        myDb.updateData(
                                "1",
                                dDayDate.getText().toString(),
                                compare,
                                compare,
                                leftDate.getText().toString()
                        );
                if (isUpdated){
                    Toast.makeText(BabyCreateActivity.this, "SUCCESSFULLY INSERTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BabyCreateActivity.this, MainActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(BabyCreateActivity.this, "FAILED TO INSERT", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveFirebase(){
        final String userUID = mAuth.getCurrentUser().getUid();
        String expectDate = dDayDate.getText().toString().trim();
        String Name = babyName.getText().toString().trim();
        String gender;
        String leftDate;

        // decide baby's gender (if not chosen, set them as 'undecided')
        if(boy.isChecked()){gender = "boy";}
        else if (girl.isChecked()){gender = "girl";}
        else {gender = "undecided";}

        // decide the input String according to left date
        if(resultNumber >= 0) {leftDate = String.valueOf(resultNumber);}
        else { leftDate = "Born"; }

        // Check if Name & Expect Date are all filled out
        if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(expectDate)){
            Toast.makeText(getApplicationContext(), "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make a Hashmap
        HashMap<String, String> babyMap = new HashMap<>();
            babyMap.put("Name", Name);
            babyMap.put("ExpectDate", expectDate);
            babyMap.put("LeftDate", leftDate);
            babyMap.put("Gender", gender);

        // Input Data into Firebase Realtime Database
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
            leftDate.setText(String.format(Locale.getDefault(), "%d", resultNumber));
        } else {
            leftDate.setText(String.format(Locale.getDefault(), "Born"));
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