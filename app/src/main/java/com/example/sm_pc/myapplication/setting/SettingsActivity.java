package com.example.sm_pc.myapplication.setting;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm_pc.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private Button dateButton, saveButton;
    private RadioButton boyButton, girlButton;
    private TextView ddayText, babyName;

    private int tYear, tMonth, tDay;
    private int dYear = 1, dMonth = 1, dDay = 1;
    static final int DATE_DIALOG_ID = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dateButton = (Button) findViewById(R.id.dateButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        boyButton = (RadioButton) findViewById(R.id.buttonBoy);
        girlButton = (RadioButton) findViewById(R.id.buttonGirl);
        ddayText = (TextView) findViewById(R.id.ddaydate);
        babyName = (TextView) findViewById(R.id.textBabyName);


        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ddaydate = ddayText.getText().toString().trim();
                final String textBabyName = babyName.getText().toString().trim();
                final String genderB = "Boy";
                final String genderG = "Girl";
                final String genderU = "Undecided";
                if (TextUtils.isEmpty(textBabyName) || TextUtils.isEmpty(ddaydate)){
                    Toast.makeText(getApplicationContext(), "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference Baby = mRootRef.child("Baby");
                DatabaseReference user = Baby.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference name = user.child("name");
                DatabaseReference date = user.child("date");

                name.setValue(textBabyName);
                date.setValue(ddaydate);
                DatabaseReference gender = user.child("gender");
                if(boyButton.isChecked()){gender.setValue(genderB);}
                else if(girlButton.isChecked()){gender.setValue(genderG);}
                else{gender.setValue(genderU);}
            }
        });


        dateButton.setOnClickListener(new View.OnClickListener() {
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
        if(dYear == 1){ ddayText.setText("출산예정일");}
        else{ ddayText.setText(String.format("%d년 %d월 %d일", dYear, dMonth + 1, dDay));}
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
